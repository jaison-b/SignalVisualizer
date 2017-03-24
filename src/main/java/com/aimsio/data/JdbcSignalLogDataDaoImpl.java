package com.aimsio.data;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.collect.*;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.ColumnListHandler;
import org.joda.time.DateTime;
import org.joda.time.Months;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class JdbcSignalLogDataDaoImpl implements SignalLogDataDao {

    private DataSource dataSource;

    private static final String QUERY_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    @Override
    public SignalLogDataRange getMinMaxSignalLogDataRange() {
        String query = "SELECT MIN(entry_date) min_date, MAX(entry_date) max_date FROM signal_log";
        ResultSetHandler<SignalLogDataRange> handler = new ResultSetHandler<SignalLogDataRange>() {
            @Override
            public SignalLogDataRange handle(ResultSet resultSet) throws SQLException {
                resultSet.next();
                return new SignalLogDataRange(resultSet.getDate("min_date"),
                                              resultSet.getDate("max_date"));
            }
        };
        return executeQuery(query, handler);
    }

    @Override
    public List<SignalLogData> getSignalLogData(SignalLogDataFilter signalLogDataFilter) {
        Preconditions.checkNotNull(signalLogDataFilter);
        if (signalLogDataFilter.getAssetUNs().isEmpty()) {
            return ImmutableList.of();
        }
        return executeQuery(getSignalLogDataQuery(signalLogDataFilter),
                            getSignalLogDataQueryResultHandler(signalLogDataFilter),
                            getSignalLogDataQueryParams(signalLogDataFilter));

    }

    SignalEntryDateResolution getResolution(SignalLogDataRange signalLogDataRange) {
        Date entryStartDate = signalLogDataRange.getEntryStartDate();
        Date entryEndDate = signalLogDataRange.getEntryEndDate();
        Months monthsBetween = Months.monthsBetween(new DateTime(entryStartDate), new DateTime(entryEndDate));
        if (monthsBetween.isLessThan(Months.THREE) || monthsBetween.equals(Months.THREE)) {
            return SignalEntryDateResolution.DAILY;
        }
        if (monthsBetween.isGreaterThan(Months.THREE) && monthsBetween.isLessThan(Months.TWELVE.multipliedBy(2))) {
            return SignalEntryDateResolution.MONTHLY;
        }
        return SignalEntryDateResolution.YEARLY;
    }

    private String getSignalLogDataQuery(SignalLogDataFilter signalLogDataFilter) {
        String grouping = getSignalLogDataQueryGrouping(signalLogDataFilter);
        return "SELECT status, entry_date, count(*) count FROM signal_log \n" +
                "WHERE asset_un IN (" + getJoinedParameterPlaceHolders(signalLogDataFilter.getAssetUNs()) + ") \n" +
                "AND LOWER(status) IN (" + getJoinedParameterPlaceHolders(signalLogDataFilter.getSignalStatusTypes()) + ") \n" +
                "AND entry_date >= ? AND entry_date <= ? \n" +
                "GROUP BY " + grouping + " \n" +
                "ORDER BY " + grouping + " \n";
    }

    private String getSignalLogDataQueryGrouping(SignalLogDataFilter signalLogDataFilter) {
        SignalEntryDateResolution resolution = getResolution(signalLogDataFilter.getSignalLogDataRange());
        StringBuilder groupingBuilder = new StringBuilder("status");
        switch (resolution) {
            case YEARLY:
                groupingBuilder.append(", YEAR(entry_date) ");
                break;
            case MONTHLY:
                groupingBuilder.append(", YEAR(entry_date), MONTH(entry_date) ");
                break;
            case DAILY:
                groupingBuilder.append(", entry_date ");
                break;
            default:
                break;
        }
        return groupingBuilder.toString();
    }

    private Object[] getSignalLogDataQueryParams(SignalLogDataFilter signalLogDataFilter) {
        String[] assetUNs = signalLogDataFilter.getAssetUNs().toArray(new String[signalLogDataFilter.getAssetUNs().size()]);
        String[] signalStatusTypeStrings = FluentIterable.from(signalLogDataFilter.getSignalStatusTypes())
                                                         .transform(toLowerCaseTypeString())
                                                         .toArray(String.class);
        Object[] queryParams = ObjectArrays.concat(assetUNs, signalStatusTypeStrings, String.class);
        SignalLogDataRange signalLogDataRange = signalLogDataFilter.getSignalLogDataRange();
        queryParams = ObjectArrays.concat(queryParams, toQueryDate(signalLogDataRange.getEntryStartDate()));
        queryParams = ObjectArrays.concat(queryParams, toQueryDate(signalLogDataRange.getEntryEndDate()));
        return queryParams;
    }

    private String toQueryDate(Date date) {
        return new SimpleDateFormat(QUERY_DATE_FORMAT).format(date);
    }

    private String getJoinedParameterPlaceHolders(List parameters) {
        return Joiner.on(", ").join(Iterables.transform(parameters, toQueryParamPlaceHolder()));
    }

    private <T> T executeQuery(String query, ResultSetHandler<T> resultSetHandler, Object... queryParams) {
        try {
            return getQueryRunner().query(query, resultSetHandler, queryParams);
        } catch (SQLException exp) {
            throw new RuntimeException("Error execution query: " + query + " for params" + Arrays.toString(queryParams),
                                       exp);
        }
    }

    private ResultSetHandler<List<SignalLogData>> getSignalLogDataQueryResultHandler(final SignalLogDataFilter signalLogDataFilter) {
        return new ResultSetHandler<List<SignalLogData>>() {
            @Override
            public List<SignalLogData> handle(ResultSet resultSet) throws SQLException {
                ArrayListMultimap<SignalStatusType, SignalLogData.SignalLogDataPoint> map = ArrayListMultimap.create();
                while (resultSet.next()) {
                    SignalStatusType signalStatusType = SignalStatusType.toSignalStatusType(resultSet.getString("status"));
                    DateTime entryDate = new DateTime(resultSet.getTimestamp("entry_date"));
                    SignalLogData.SignalLogDataPoint dataPoint = new SignalLogData.SignalLogDataPoint(entryDate.toDate(),
                                                                                                      resultSet.getInt("count"));
                    map.put(signalStatusType, dataPoint);
                }
                return toSignalLogData(map);
            }

            private List<SignalLogData> toSignalLogData(ArrayListMultimap<SignalStatusType, SignalLogData.SignalLogDataPoint> map) {
                SignalEntryDateResolution resolution = getResolution(signalLogDataFilter.getSignalLogDataRange());
                List<String> assetUNs = signalLogDataFilter.getAssetUNs();
                ImmutableList.Builder<SignalLogData> builder = ImmutableList.builder();
                for (SignalStatusType signalStatusType : map.keySet()) {
                    builder.add(new SignalLogData(assetUNs,
                                                  signalStatusType,
                                                  resolution,
                                                  map.get(signalStatusType)));
                }
                return builder.build();
            }
        };
    }

    private Function<Object, String> toQueryParamPlaceHolder() {
        return new Function<Object, String>() {
            @Override
            public String apply(Object s) {
                return "?";
            }
        };
    }

    private Function<SignalStatusType, String> toLowerCaseTypeString() {
        return new Function<SignalStatusType, String>() {
            @Override
            public String apply(SignalStatusType signalStatusType) {
                return signalStatusType.name().toLowerCase();
            }
        };
    }

    @Override
    public List<String> getAssetUNs() {
        try {
            List<String> assetUNs = getQueryRunner().query("SELECT DISTINCT asset_un FROM signal_log",
                                                           new ColumnListHandler<String>("asset_un"));
            return ImmutableList.<String>builder().addAll(assetUNs).build();
        } catch (SQLException e) {
            throw new RuntimeException("getAssetUNs Error", e);
        }

    }

    private QueryRunner getQueryRunner() {
        return new QueryRunner(getDataSource());
    }

    private DataSource getDataSource() {
        if (dataSource == null) {
            String dbName = System.getProperty("RDS_DB_NAME");
            String userName = System.getProperty("RDS_USERNAME");
            String password = System.getProperty("RDS_PASSWORD");
            String hostName = System.getProperty("RDS_HOSTNAME");
            String port = System.getProperty("RDS_PORT");
            MysqlDataSource mysqlDataSource = new MysqlDataSource();
            mysqlDataSource.setUrl("jdbc:mysql://" + hostName + ":" + port + "/" + dbName);
            mysqlDataSource.setUser(userName);
            mysqlDataSource.setPassword(password);
            dataSource = mysqlDataSource;
        }
        return dataSource;
    }
}

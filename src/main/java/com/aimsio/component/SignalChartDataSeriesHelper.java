package com.aimsio.component;

import com.aimsio.data.SignalLogData;
import com.aimsio.data.SignalLogDataDao;
import com.aimsio.data.SignalLogDataFilter;
import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.vaadin.addon.charts.model.DataSeries;
import com.vaadin.addon.charts.model.DataSeriesItem;

import java.util.List;

class SignalChartDataSeriesHelper {

    static List<DataSeries> getDataSeries(SignalLogDataFilter filter, SignalLogDataDao signalLogDataDao) {
        return FluentIterable.from(signalLogDataDao.getSignalLogData(filter))
                             .transform(toDataSeries())
                             .toList();
    }

    private static Function<SignalLogData, DataSeries> toDataSeries() {
        return new Function<SignalLogData, DataSeries>() {
            @Override
            public DataSeries apply(SignalLogData signalLogData) {
                DataSeries series = new DataSeries(signalLogData.getSignalStatusType().name());
                List<DataSeriesItem> seriesItems = FluentIterable.from(signalLogData.getDataPoints())
                                                                 .transform(toDataItem())
                                                                 .toList();
                for (DataSeriesItem seriesItem : seriesItems) {
                    series.add(seriesItem);
                }
                return series;
            }
        };
    }

    private static Function<SignalLogData.SignalLogDataPoint, DataSeriesItem> toDataItem() {
        return new Function<SignalLogData.SignalLogDataPoint, DataSeriesItem>() {
            @Override
            public DataSeriesItem apply(SignalLogData.SignalLogDataPoint signalLogDataPoint) {
                return new DataSeriesItem(signalLogDataPoint.getEntryDate(), signalLogDataPoint.getCount());
            }
        };
    }
}

package com.aimsio.data;

import com.google.common.collect.ImmutableList;
import org.joda.time.DateTime;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class JdbcSignalLogDataDaoImplTest {

    @Spy
    private JdbcSignalLogDataDaoImpl _signalLogDataDao;

    @Rule
    public ExpectedException _expectedException = ExpectedException.none();

    @Test
    public void getSignalLogData_withNullFilter_shouldThrowException() {
        _expectedException.expect(NullPointerException.class);
        _signalLogDataDao.getSignalLogData(null);
    }

    @Test
    public void getSignalLogData_withEmptyAssetUNs_shouldReturnEmptyResult() {
        List<SignalLogData> result = _signalLogDataDao.getSignalLogData(new SignalLogDataFilter()
                                                                                .setAssetUNs(ImmutableList.<String>of()));
        assertEquals(0, result.size());
    }

    @Test
    public void getResolution_whenDateRangeLessThan3Months_returnDailyResolution() {
        SignalLogDataRange range = new SignalLogDataRange(DateTime.now().toDate(),
                                                          DateTime.now().plusMonths(2).toDate());
        assertEquals(SignalEntryDateResolution.DAILY,
                     _signalLogDataDao.getResolution(range));
    }

    @Test
    public void getResolution_whenDateRangeBetween3MonthsTo24Months_returnMonthlyResolution() {
        SignalLogDataRange range = new SignalLogDataRange(DateTime.now().plusMonths(4).toDate(),
                                                          DateTime.now().plusMonths(12).toDate());
        assertEquals(SignalEntryDateResolution.MONTHLY,
                     _signalLogDataDao.getResolution(range));
    }

    @Test
    public void getResolution_whenDateRangeGreaterThank24Months_returnYearlyResolution() {
        SignalLogDataRange range = new SignalLogDataRange(DateTime.now().toDate(),
                                                          DateTime.now().plusYears(5).toDate());
        assertEquals(SignalEntryDateResolution.YEARLY,
                     _signalLogDataDao.getResolution(range));
    }
}
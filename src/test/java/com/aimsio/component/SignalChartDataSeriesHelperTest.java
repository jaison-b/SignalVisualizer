package com.aimsio.component;

import com.aimsio.data.*;
import com.google.common.collect.ImmutableList;
import com.vaadin.addon.charts.model.DataSeries;
import com.vaadin.addon.charts.model.DataSeriesItem;
import com.vaadin.addon.charts.util.Util;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SignalChartDataSeriesHelperTest {

    @Mock
    private SignalLogDataDao _signalLogDataDao;

    private static Date TEST_DATE = DateTime.now().toDate();


    @Test
    public void getDataSeries_returnChartDataSeries() {
        when(_signalLogDataDao.getSignalLogData(any(SignalLogDataFilter.class)))
                .thenReturn(ImmutableList.of(getSignalLogData()));
        List<DataSeries> seriesList = SignalChartDataSeriesHelper.getDataSeries(mock(SignalLogDataFilter.class),
                                                                                _signalLogDataDao);
        assertEquals(1, seriesList.size());
        DataSeries series = seriesList.get(0);
        assertEquals(SignalStatusType.ACTIVE.name(), series.getName());
        assertEquals(1, series.getData().size());
        DataSeriesItem seriesItem = series.get(0);
        assertEquals(TEST_DATE, Util.toServerDate(seriesItem.getX().doubleValue()));
        assertEquals(10, seriesItem.getY());
    }


    private SignalLogData getSignalLogData() {
        return new SignalLogData(ImmutableList.of("ASSET-1"),
                                 SignalStatusType.ACTIVE,
                                 SignalEntryDateResolution.YEARLY,
                                 ImmutableList.of(new SignalLogData.SignalLogDataPoint(TEST_DATE,
                                                                                       10)));
    }

}
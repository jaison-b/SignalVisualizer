package com.aimsio.component;

import com.aimsio.data.SignalLogDataFilter;
import com.aimsio.data.SignalLogDataRange;
import com.aimsio.data.SignalStatusType;
import com.google.common.collect.ImmutableList;
import com.google.common.eventbus.EventBus;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.spy;

@RunWith(MockitoJUnitRunner.class)
public class SignalVisualizerChartTest {

    private SignalVisualizerChart _signalVisualizerChart;
    private EventBus _eventBus;

    private static final List<String> TEST_ASSET_UN_LIST = ImmutableList.of("ASSET-1", "ASSET-2");
    private static final List<SignalStatusType> TEST_STATUS_TYPES = SignalStatusType.asList();
    private static final SignalLogDataRange TEST_DATA_RANGE = new SignalLogDataRange(DateTime.now().toDate(),
                                                                                     DateTime.now().plusDays(1).toDate());

    @Before
    public void setup() {
        _eventBus = new EventBus("TEST-BUS");
        _signalVisualizerChart = spy(new SignalVisualizerChart() {
            @Override
            void refreshChart() {
                //do-nothing
            }
        });
        _eventBus.register(_signalVisualizerChart);
    }

    @Test
    public void listen_onSignalLogDataFilterChangeEvent_shouldGetUpdateFilterData() {
        fireFilterChangeEvent();
        SignalLogDataFilter expected = new SignalLogDataFilter()
                .setAssetUNs(TEST_ASSET_UN_LIST)
                .setSignalStatusTypes(TEST_STATUS_TYPES)
                .setSignalLogDataRange(TEST_DATA_RANGE);
        assertEquals(expected, _signalVisualizerChart.getSignalLogDataFilter());
    }

    private void fireFilterChangeEvent() {
        SignalLogDataFilter filter = new SignalLogDataFilter()
                .setAssetUNs(TEST_ASSET_UN_LIST)
                .setSignalStatusTypes(TEST_STATUS_TYPES)
                .setSignalLogDataRange(TEST_DATA_RANGE);
        _eventBus.post(new SignalLogDataFilterChangeEvent(filter));
    }
}

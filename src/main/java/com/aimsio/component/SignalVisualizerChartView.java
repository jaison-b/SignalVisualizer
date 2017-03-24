package com.aimsio.component;

import com.aimsio.data.JdbcSignalLogDataDaoImpl;
import com.aimsio.data.SignalLogDataDao;
import com.aimsio.data.SignalLogDataFilter;
import com.aimsio.data.SignalStatusType;
import com.google.common.eventbus.EventBus;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

public class SignalVisualizerChartView extends VerticalLayout {

    private SignalLogDataDao _signalLogDataDao;
    private EventBus _eventBus;

    public SignalVisualizerChartView() {
        initDependencies();
        addComponent(getViewTitle());
        SignalLogDataFilter signalLogDataFilter = getSignalLogDataFilter();
        addComponent(new SignalLogDataFilterPanel(signalLogDataFilter, _eventBus));
        addComponent(new SignalVisualizerChart(signalLogDataFilter, _signalLogDataDao, _eventBus));
        setSpacing(true);
    }

    private void initDependencies() {
        _signalLogDataDao = new JdbcSignalLogDataDaoImpl();
        _eventBus = new EventBus("SIGNAL-CHART-VIEW-EVENT-BUS");
    }

    private Label getViewTitle() {
        return new Label("Signal Visualizer") {{
            setStyleName("h3");
        }};
    }

    private SignalLogDataFilter getSignalLogDataFilter() {
        return new SignalLogDataFilter()
                .setAssetUNs(_signalLogDataDao.getAssetUNs())
                .setSignalStatusTypes(SignalStatusType.asList())
                .setSignalLogDataRange(_signalLogDataDao.getMinMaxSignalLogDataRange());
    }
}

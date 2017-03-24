package com.aimsio.component;


import com.aimsio.data.SignalLogDataDao;
import com.aimsio.data.SignalLogDataFilter;
import com.google.common.collect.ImmutableList;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.*;
import com.vaadin.addon.charts.model.style.SolidColor;
import com.vaadin.data.Property;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.*;
import com.vaadin.ui.Label;

import java.util.List;

class SignalVisualizerChart extends CustomComponent {

    private SignalLogDataDao _signalLogDataDao;
    private SignalLogDataFilter _signalLogDataFilter;
    private Chart _chart;
    private static final String ASSET_SELECT_ALL_ID = "--Select All--";

    SignalVisualizerChart() {
        //only for testing
    }

    public SignalVisualizerChart(SignalLogDataFilter signalLogDataFilter, SignalLogDataDao signalLogDataDao, EventBus eventBus) {
        _signalLogDataDao = signalLogDataDao;
        _signalLogDataFilter = signalLogDataFilter;
        eventBus.register(this);
        _chart = getChart();
        Panel chartPanel = new Panel();
        setCompositionRoot(chartPanel);
        VerticalLayout layout = new VerticalLayout();
        chartPanel.setContent(layout);

        layout.addComponent(_chart);
        layout.addComponent(getCharFilter());
    }

    private Component getCharFilter() {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setSpacing(true);
        layout.setMargin(new MarginInfo(false, true, true, true));
        layout.addComponent(new Label("AssetUNs"));
        final List<String> allAssetUNs = _signalLogDataFilter.getAssetUNs();
        final ComboBox assetUnComboBox = new ComboBox();
        assetUnComboBox.setNewItemsAllowed(false);
        assetUnComboBox.setInvalidAllowed(false);
        assetUnComboBox.setNullSelectionAllowed(true);
        assetUnComboBox.setNullSelectionItemId(ASSET_SELECT_ALL_ID);
        assetUnComboBox.addItem(ASSET_SELECT_ALL_ID);
        assetUnComboBox.addItems(_signalLogDataFilter.getAssetUNs());
        assetUnComboBox.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                List<String> assetUNs = assetUnComboBox.getValue() != null
                        ? ImmutableList.of((String) assetUnComboBox.getValue())
                        : allAssetUNs;
                _signalLogDataFilter.setAssetUNs(assetUNs);
                refreshChart();
            }
        });
        layout.addComponent(assetUnComboBox);
        return layout;
    }


    private Chart getChart() {
        Chart chart = new Chart();
        Configuration chartConfiguration = getChartConfiguration();
        chart.setTimeline(true);
        chart.setConfiguration(chartConfiguration);
        return chart;
    }

    private Configuration getChartConfiguration() {
        Configuration configuration = new Configuration();
        configuration.setTitle("AssetUN Signal Count");
        configuration.getLegend().setEnabled(true);

        configuration.getNavigator().setEnabled(false);

        XAxis xAxis = configuration.getxAxis();
        xAxis.setType(AxisType.DATETIME);
        xAxis.setLineWidth(1);
        xAxis.setLineColor(SolidColor.DARKGREY);

        YAxis yAxis = configuration.getyAxis();
        yAxis.setTitle(new AxisTitle("No of Signals"));
        yAxis.getTitle().setAlign(VerticalAlign.MIDDLE);
        yAxis.setType(AxisType.LOGARITHMIC);
        yAxis.setLineWidth(1);
        yAxis.setLineColor(SolidColor.DARKGREY);
        yAxis.getLabels().setAlign(HorizontalAlign.LEFT);

        configuration.getRangeSelector().setInputEnabled(false);

        configuration.setSeries(getSeries());

        return configuration;
    }

    private List<Series> getSeries() {
        return ImmutableList.<Series>builder()
                .addAll(SignalChartDataSeriesHelper.getDataSeries(_signalLogDataFilter, _signalLogDataDao))
                .build();
    }

    @Subscribe
    void listen(SignalLogDataFilterChangeEvent event) {
        _signalLogDataFilter = event.getDataFilter();
        if (_signalLogDataFilter.getAssetUNs() == null) {
            _signalLogDataFilter.setAssetUNs(_signalLogDataDao.getAssetUNs());
        }
        refreshChart();
    }

    void refreshChart() {
        _chart.getConfiguration().setSeries(getSeries());
        _chart.drawChart();
    }

    SignalLogDataFilter getSignalLogDataFilter() {
        return _signalLogDataFilter;
    }
}

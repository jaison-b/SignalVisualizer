package com.aimsio.component;

import com.aimsio.data.SignalLogDataFilter;
import com.aimsio.data.SignalLogDataRange;
import com.aimsio.data.SignalStatusType;
import com.google.common.collect.ImmutableList;
import com.google.common.eventbus.EventBus;
import com.vaadin.ui.*;

import java.util.List;

class SignalLogDataFilterPanel extends CustomComponent {

    private final EventBus _eventBus;
    private final DateField _fromDateField;
    private final DateField _toDateField;
    private final ListSelect _statusTypeSelect;

    private static final String DATE_FORMAT = "yyyy-MM-dd";

    public SignalLogDataFilterPanel(SignalLogDataFilter signalLogDataFilter, EventBus eventBus) {
        _eventBus = eventBus;
        Panel panel = new Panel("Chart Parameters");
        setCompositionRoot(panel);

        GridLayout gridLayout = new GridLayout(7, 1);
        gridLayout.setSpacing(true);
        gridLayout.setMargin(true);

        Label signalType = new Label("Signal Type");
        addComponent(gridLayout, signalType, 0, 0);

        _statusTypeSelect = new ListSelect(null, signalLogDataFilter.getSignalStatusTypes());
        _statusTypeSelect.setNullSelectionAllowed(false);
        _statusTypeSelect.setMultiSelect(true);
        _statusTypeSelect.setRows(3);
        addComponent(gridLayout, _statusTypeSelect, 1, 0);


        Label from = new Label("From");
        addComponent(gridLayout, from, 2, 0);

        _fromDateField = new DateField(null, signalLogDataFilter.getSignalLogDataRange().getEntryStartDate());
        _fromDateField.setDateFormat(DATE_FORMAT);
        addComponent(gridLayout, _fromDateField, 3, 0);

        Label to = new Label("To");
        addComponent(gridLayout, to, 4, 0);

        _toDateField = new DateField(null, signalLogDataFilter.getSignalLogDataRange().getEntryEndDate());
        _toDateField.setDateFormat(DATE_FORMAT);
        addComponent(gridLayout, _toDateField, 5, 0);

        Button refreshButton = new Button("Refresh", new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                fireFilterChangeEvent();
            }
        });
        addComponent(gridLayout, refreshButton, 6, 0);

        panel.setContent(gridLayout);
    }

    private void fireFilterChangeEvent() {
        List<SignalStatusType> statuses = ImmutableList.<SignalStatusType>builder()
                .addAll((Iterable<SignalStatusType>) _statusTypeSelect.getValue())
                .build();
        SignalLogDataFilter signalLogDataFilter = new SignalLogDataFilter()
                .setSignalStatusTypes(statuses)
                .setSignalLogDataRange(new SignalLogDataRange(_fromDateField.getValue(), _toDateField.getValue()));
        _eventBus.post(new SignalLogDataFilterChangeEvent(signalLogDataFilter));
    }

    private static void addComponent(GridLayout layout, Component component, int column, int row) {
        layout.addComponent(component, column, row);
        layout.setComponentAlignment(component, Alignment.MIDDLE_CENTER);
    }
}

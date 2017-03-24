package com.aimsio.component;

import com.aimsio.data.SignalLogDataFilter;
import com.google.common.base.Objects;

class SignalLogDataFilterChangeEvent {

    private final SignalLogDataFilter _dataFilter;

    public SignalLogDataFilterChangeEvent(SignalLogDataFilter dataFilter) {
        _dataFilter = dataFilter;
    }

    public SignalLogDataFilter getDataFilter() {
        return _dataFilter;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SignalLogDataFilterChangeEvent that = (SignalLogDataFilterChangeEvent) o;
        return Objects.equal(_dataFilter, that._dataFilter);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(_dataFilter);
    }
}

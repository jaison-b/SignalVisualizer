package com.aimsio.data;

import com.google.common.base.Objects;

import java.util.List;

public class SignalLogDataFilter {
    private List<String> _assetUNs;
    private List<SignalStatusType> _signalStatusTypes;
    private SignalLogDataRange _signalLogDataRange;

    public List<String> getAssetUNs() {
        return _assetUNs;
    }

    public SignalLogDataFilter setAssetUNs(List<String> assetUNs) {
        _assetUNs = assetUNs;
        return this;
    }

    public List<SignalStatusType> getSignalStatusTypes() {
        return _signalStatusTypes;
    }

    public SignalLogDataFilter setSignalStatusTypes(List<SignalStatusType> signalStatusTypes) {
        _signalStatusTypes = signalStatusTypes;
        return this;
    }

    public SignalLogDataRange getSignalLogDataRange() {
        return _signalLogDataRange;
    }

    public SignalLogDataFilter setSignalLogDataRange(SignalLogDataRange signalLogDataRange) {
        _signalLogDataRange = signalLogDataRange;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SignalLogDataFilter that = (SignalLogDataFilter) o;
        return Objects.equal(_assetUNs, that._assetUNs) &&
                Objects.equal(_signalStatusTypes, that._signalStatusTypes) &&
                Objects.equal(_signalLogDataRange, that._signalLogDataRange);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(_assetUNs, _signalStatusTypes, _signalLogDataRange);
    }
}

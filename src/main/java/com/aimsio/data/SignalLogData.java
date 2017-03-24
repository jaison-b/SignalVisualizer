package com.aimsio.data;

import com.google.common.base.Objects;

import java.util.Date;
import java.util.List;

public class SignalLogData {
    private final List<String> _assetUNs;
    private final SignalStatusType _signalStatusType;
    private final SignalEntryDateResolution _entryDateResolution;
    private final List<SignalLogDataPoint> _dataPoints;

    public SignalLogData(List<String> assetUNs, SignalStatusType signalStatusType, SignalEntryDateResolution entryDateResolution, List<SignalLogDataPoint> dataPoints) {
        _assetUNs = assetUNs;
        _signalStatusType = signalStatusType;
        _entryDateResolution = entryDateResolution;
        _dataPoints = dataPoints;
    }

    public List<String> getAssetUNs() {
        return _assetUNs;
    }

    public SignalStatusType getSignalStatusType() {
        return _signalStatusType;
    }

    public SignalEntryDateResolution getEntryDateResolution() {
        return _entryDateResolution;
    }

    public List<SignalLogDataPoint> getDataPoints() {
        return _dataPoints;
    }

    public static class SignalLogDataPoint {
        private final Date _entryDate;
        private final int _count;

        public SignalLogDataPoint(Date entryDate, int count) {
            _entryDate = entryDate;
            _count = count;
        }

        public Date getEntryDate() {
            return _entryDate;
        }

        public int getCount() {
            return _count;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            SignalLogDataPoint that = (SignalLogDataPoint) o;
            return _count == that._count &&
                    Objects.equal(_entryDate, that._entryDate);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(_entryDate, _count);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SignalLogData that = (SignalLogData) o;
        return Objects.equal(_assetUNs, that._assetUNs) &&
                _signalStatusType == that._signalStatusType &&
                _entryDateResolution == that._entryDateResolution &&
                Objects.equal(_dataPoints, that._dataPoints);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(_assetUNs, _signalStatusType, _entryDateResolution, _dataPoints);
    }
}

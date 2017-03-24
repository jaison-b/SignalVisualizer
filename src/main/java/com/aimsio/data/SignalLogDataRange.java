package com.aimsio.data;

import com.google.common.base.Objects;

import java.util.Date;

public class SignalLogDataRange {
    private final Date _entryStartDate;
    private final Date _entryEndDate;

    public SignalLogDataRange(Date entryStartDate, Date entryEndDate) {
        _entryStartDate = entryStartDate;
        _entryEndDate = entryEndDate;
    }

    public Date getEntryStartDate() {
        return _entryStartDate;
    }

    public Date getEntryEndDate() {
        return _entryEndDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SignalLogDataRange that = (SignalLogDataRange) o;
        return Objects.equal(_entryStartDate, that._entryStartDate) &&
                Objects.equal(_entryEndDate, that._entryEndDate);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(_entryStartDate, _entryEndDate);
    }
}

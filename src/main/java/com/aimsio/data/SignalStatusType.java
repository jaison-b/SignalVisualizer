package com.aimsio.data;

import com.google.common.collect.ImmutableList;

import java.util.List;

public enum SignalStatusType {
    OVERRIDE, ENGAGED, ACTIVE, LOAD, UNPLUG;

    public static List<SignalStatusType> asList() {
        return ImmutableList.copyOf(SignalStatusType.values());
    }

    public static SignalStatusType toSignalStatusType(String signalStatusTypeStr) {
        for (SignalStatusType signalStatusType : SignalStatusType.values()) {
            if (signalStatusType.name().equalsIgnoreCase(signalStatusTypeStr)) {
                return signalStatusType;
            }
        }
        return null;
    }
}

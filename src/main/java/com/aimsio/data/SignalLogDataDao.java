package com.aimsio.data;

import java.util.List;

public interface SignalLogDataDao {

    List<String> getAssetUNs();

    List<SignalLogData> getSignalLogData(SignalLogDataFilter signalLogDataFilter);

    SignalLogDataRange getMinMaxSignalLogDataRange();

}

package com.example.a1;

import java.util.List;

public class CallHistoryResponse {
    private List<CallHistoryItem> callHistory;

    public CallHistoryResponse(List<CallHistoryItem> callHistory) {
        this.callHistory = callHistory;
    }

    public List<CallHistoryItem> getCallHistory() {
        return callHistory;
    }
}
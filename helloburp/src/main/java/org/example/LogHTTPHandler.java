package org.example;

import burp.api.montoya.http.handler.*;
import org.example.logtable.LogTableController;

import java.util.Date;

public class LogHTTPHandler implements HttpHandler {
    private LogTableController logTableController;
    LogHTTPHandler(LogTableController controller) {
        this.logTableController = controller;
    }

    public RequestToBeSentAction handleHttpRequestToBeSent(HttpRequestToBeSent requestToBeSent) {
        Date arrivalTime = new Date();
        LogEntry entry = new LogEntry(requestToBeSent, arrivalTime);
        this.logTableController.getLogTableModel().addEntry(entry);

        return RequestToBeSentAction.continueWith(requestToBeSent);
    }

    public ResponseReceivedAction handleHttpResponseReceived(HttpResponseReceived responseReceived) {
        // TODO: need to do something similar to Logger++ here
        return ResponseReceivedAction.continueWith(responseReceived);
    }
}

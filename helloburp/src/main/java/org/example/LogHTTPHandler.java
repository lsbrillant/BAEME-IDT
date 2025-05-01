package org.example;

import burp.api.montoya.http.handler.*;
import org.example.logtable.LogTableController;

import java.util.Date;
import java.util.List;

public class LogHTTPHandler implements HttpHandler {
    private LogTableController logTableController;
    private LogProcessor logProcessor;
    LogHTTPHandler(LogTableController controller, LogProcessor logProcessor) {
        this.logTableController = controller;
        this.logProcessor = logProcessor;
    }

    public RequestToBeSentAction handleHttpRequestToBeSent(HttpRequestToBeSent requestToBeSent) {
        Date arrivalTime = new Date();
        LogEntry entry = new LogEntry(requestToBeSent, arrivalTime);
        entry.setMessageId(requestToBeSent.messageId());
        this.logTableController.getLogTableModel().addEntry(entry);
        logProcessor.process(entry, requestToBeSent,null);
        return RequestToBeSentAction.continueWith(requestToBeSent);
    }

    public ResponseReceivedAction handleHttpResponseReceived(HttpResponseReceived responseReceived) {
        // TODO: can make this more efficient, probably store a Map in LogEntry that goes from messageId to entryIndex
        // but I don't wanna break this cuz it's working rn lol
        List<LogEntry> entries = logTableController.getLogTableModel().getData();
        for (LogEntry entry : entries) {
            if (entry.getMessageId() == responseReceived.messageId()) {
                entry.setResponse(responseReceived);
                logProcessor.process(entry,null, responseReceived);
                break;
            }
        }
        return ResponseReceivedAction.continueWith(responseReceived);
    }
}

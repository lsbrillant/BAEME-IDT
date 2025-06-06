package org.example;

import burp.api.montoya.http.handler.*;
import org.example.logtable.LogTableController;

import javax.swing.*;
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
        //if (!requestToBeSent.isInScope()) return RequestToBeSentAction.continueWith(requestToBeSent);
        if (!logTableController.isLoggingEnabled()) return RequestToBeSentAction.continueWith(requestToBeSent);
        Date arrivalTime = new Date();
        LogEntry entry = new LogEntry(requestToBeSent, arrivalTime);
        entry.setRequestSource(requestToBeSent.toolSource().toolType().toolName());
        entry.setMessageId(requestToBeSent.messageId());
        SwingUtilities.invokeLater(() -> {
            this.logTableController.getLogTableModel().addEntry(entry);
        }); // thread-safe so the table actually displays all requests :)
        logProcessor.processIfComplete(entry, requestToBeSent,null);
        return RequestToBeSentAction.continueWith(requestToBeSent);
    }

    public ResponseReceivedAction handleHttpResponseReceived(HttpResponseReceived responseReceived) {
        // TODO: can make this more efficient, probably store a Map in LogEntry that goes from messageId to entryIndex
        // but I don't wanna break this cuz it's working rn lol
        //if (!responseReceived.initiatingRequest().isInScope()) ResponseReceivedAction.continueWith(responseReceived);
        if (!logTableController.isLoggingEnabled()) return ResponseReceivedAction.continueWith(responseReceived);
        List<LogEntry> entries = logTableController.getLogTableModel().getData();
        for (LogEntry entry : entries) {
            if ((long) entry.getMessageId() == responseReceived.messageId()) {
                entry.setResponse(responseReceived);
                logProcessor.processIfComplete(entry, entry.getRequest(), responseReceived);
                break;
            }
        }
        return ResponseReceivedAction.continueWith(responseReceived);
    }
}

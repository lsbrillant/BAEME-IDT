package org.example;

import org.example.logtable.LogTableModel;
import org.example.LogEntry;
import java.util.UUID;

import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.http.message.responses.HttpResponse;

public class LogProcessor {
    //private final LogTableModel logTableModel;

    // Constructor
//    public LogProcessor(LogTableModel logTableModel) {
//        this.logTableModel = logTableModel;
//    }

    public void process(LogEntry entry, HttpRequest request, HttpResponse response) {
        if (response != null) {
            // If no existing entry is found, create a new one for the request
            entry.setResponse(response);
            entry.setStatus(LogEntry.Status.PROCESSED);
        }
        // Process the entry
        boolean processSuccess = entry.process();
        if (!processSuccess) {
            System.out.println("Log entry processing failed");
        }
    }

//    private LogEntry findEntryForRequest(UUID requestId) {
//        // Iterate over the existing log entries to find the corresponding LogEntry
//        for (LogEntry entry : logTableModel.getData()) {
//            if (entry.getRequestId().equals(requestId)) {
//                return entry;
//            }
//        }
//        return null;  // No existing entry found
//    }
}
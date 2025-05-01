package org.example;

import lombok.extern.java.Log;
import org.example.logtable.LogTableController;
import org.example.logtable.LogTableModel;
import org.example.LogEntry;
import java.util.UUID;

import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.http.message.responses.HttpResponse;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class LogProcessor {
    //private final Map<Integer, LogEntry> entriesById = new HashMap<>();
    //private final LogTableModel logTableModel;

    // Constructor
//    public LogProcessor(LogTableModel logTableModel) {
//        this.logTableModel = logTableModel;
//    }

    public void processIfComplete(LogEntry entry, HttpRequest request, HttpResponse response) {
//        if (request != null) {
//            entry.setRequest(request);
//            entriesById.put(entry.getMessageId(), entry);
//        }
//        if (response == null) {
//            entry.setStatus(LogEntry.Status.FAILED_PROCESS);
//        }
//        if (response != null) {
//            entry.setResponse(response);
//            if (entry.getStatus() == LogEntry.Status.UNPROCESSED) {
//                entry.setStatus(LogEntry.Status.AWAITING_RESPONSE);
//            }
//        }
////        else {
////            entry.setStatus(LogEntry.Status.NO_PROCESS);
////            return;
////        }
//        // Process the entry
//        boolean processSuccess = entry.process();
//        if (!processSuccess) {
//            System.out.println("Log entry processing failed");
//        }
//    }

        // Process request first
        if (request != null) {
            entry.setRequest(request);
            //entriesById.put(entry.getMessageId(), entry);
            if (entry.getStatus() == LogEntry.Status.UNPROCESSED) {
                //entry.setStatus(LogEntry.Status.AWAITING_RESPONSE);
                boolean requestProcessed = entry.processRequestOnly();
                if (requestProcessed) {
                    entry.setStatus(LogEntry.Status.AWAITING_RESPONSE);
                    entry.setDebugStatement(LogEntry.debugStatement.WORKING_REQUEST);
                } else {
                    entry.setStatus(LogEntry.Status.FAILED_PROCESS);
                    entry.setDebugStatement(LogEntry.debugStatement.NOT_WORKING_REQUEST);
                }
                return;
            }
        }

        // Process response
        if (response != null) {
            entry.setResponse(response);
            // Now process the full entry (with request + response)
            //boolean processSuccess = entry.process();
            boolean processSuccess = entry.processResponseOnly();
            if (processSuccess) { // process succeeded
                entry.setStatus(LogEntry.Status.PROCESSED);
                entry.setDebugStatement(LogEntry.debugStatement.WORKING_RESPONSE);
            } else { // processing response didn't work
                entry.setStatus(LogEntry.Status.FAILED_PROCESS);
                entry.setDebugStatement(LogEntry.debugStatement.NOT_WORKING_RESPONSE);

            }
        }
    }
}
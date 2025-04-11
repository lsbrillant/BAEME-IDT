package org.example;

import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.http.message.responses.HttpResponse;

import java.util.*;

public class LogEntry {
    private List<String> tags; // TODO: change type from String to our own class called Tag or something
    private HttpRequest request;
    private HttpResponse response;
    private Date requestDateTime = new Date(0);
    private Date responseDateTime = new Date(0);
    // TODO (Michaela?): fill in all relevant fields here
    // Look at loggerplusplus/logentry/LogEntry.java for reference

    // TODO: define process, processRequest, processResponse methods
    // These methods should extract and assign relevant fields

    private LogEntry() {
        this.tags = new ArrayList<>(); // TODO: change to HashSet for better lookup times?
    }

    public LogEntry(HttpRequest request) {
        this();
        this.request = request;
    }

    public LogEntry(HttpRequest request, Date requestDateTime) {
        this(request);
        this.requestDateTime = requestDateTime;
    }

    public LogEntry(HttpRequest request, HttpResponse response) {
        this(request);
        this.response = response;
    }

    public HttpRequest getRequest() {
        return this.request;
    }

    public HttpResponse getResponse() {
        return this.response;
    }

    // TODO: REPLACE.
    // temporary method to make the week 2 feature work, need to make LogEntry more like Logger++ probably
    public List<Object> getData() {
        return Arrays.asList(this.request.method(), this.request.url());
    }
}

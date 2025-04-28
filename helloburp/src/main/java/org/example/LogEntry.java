package org.example;


import burp.api.montoya.http.message.HttpHeader;
import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.http.message.responses.HttpResponse;
import burp.api.montoya.scanner.bchecks.BCheckImportResult;
import lombok.Getter;
import lombok.Setter;


import java.util.*;


public class LogEntry {
    private List<String> tags; // TODO: change type from String to our own class called Tag or something
    @Getter
    private HttpRequest request;
    @Getter
    @Setter
    private HttpResponse response;
    private Date requestDateTime = new Date(0);
    private Date responseDateTime = new Date(0);
    @Setter
    @Getter
    private int messageId;


    // Java Status class
    Status previousStatus;
    Status status = Status.UNPROCESSED;


    // Look at loggerplusplus/logentry/LogEntry.java for reference


    // from logtable
    private int number;
    private String tag;
    // Metadata for tagging
    private String hostname;
    private String urlString;
    private String httpMethod;
    private Short responseStatus;
    private String responseStatusText;
    private String comment; // use for the annotations for the tags(?)
    private int requestResponseDelay;  // is this useful for slow responses(?)
    private List<HttpHeader> requestHeaders;
    private List<HttpHeader> responseHeaders;
    private List<String> parameters;
    private boolean edited;
    private String statusCode;
    private int length;
    private String mimeType;
    private String extension;
    private String title;
    private String tls;
    private String ip;
    private String cookies;
    private String time;


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


    // TODO: REPLACE.
    // temporary method to make the week 2 feature work, need to make LogEntry more like Logger++ probably
    public List<Object> getData() {
        return Arrays.asList(this.request.method(), this.request.url());
    }


    // TODO: Processing data logic for requests


    // change some of this up from Logger++
    public boolean process() {
        previousStatus = this.status;
        switch (this.status) {
            case UNPROCESSED: {
                this.status = processRequest();
            }
            case AWAITING_RESPONSE: {
                if (this.response == null) {
                    this.status = Status.AWAITING_RESPONSE;
                    return false;
                }
                processResponse();
                this.status = Status.PROCESSED;
                return true;
            }
            case PROCESSED: {
                return true;
            }
            default: return false;
        }
    }


    // need reprocess(?)


    public Status getStatus() {
        return status;
    }


    public Status getPreviousStatus() {
        return previousStatus;
    }


    // similar to processRequest?
    // need java Status class - returns Status object
    private void processRequest() {
        requestHeaders = request.headers();


        // make the request tokens
        String[] httpRequestTokens = requestHeaders.get(0).value.split("");


        return
    }


    private void processResponse() {


    }


    public void setTag(String tag) {
        tags.clear();
        tags.add(tag);
    }


    public void addTag(String tag) {
        if (!tags.contains(tag)) {
            tags.add(tag);
        }
    }


    public String getFirstTag() {
        return tags.isEmpty() ? "" : tags.get(0);
    }


    public List<String> getTags() {
        return tags;
    }


    public void removeTag(String tag) {
        tags.remove(tag);
    }


    public void clearTags() {
        tags.clear();
    }
}
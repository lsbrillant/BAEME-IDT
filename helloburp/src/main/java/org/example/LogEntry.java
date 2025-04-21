package org.example;

import burp.api.montoya.http.message.HttpHeader;
import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.http.message.responses.HttpResponse;
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
    // TODO (Michaela?): fill in all relevant fields here
    // Look at loggerplusplus/logentry/LogEntry.java for reference

    // Metadata for tagging
    private String hostname;
    private String urlString;
    private String httpMethod;
    private Short responseStatus;
    private String comment; // use for the annotations for the tags(?)
    private int requestResponseDelay;  // is this useful for slow responses(?)
    private List<HttpHeader> requestHeaders;
    private List<HttpHeader> responseHeaders;
    private List<String> parameters;


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

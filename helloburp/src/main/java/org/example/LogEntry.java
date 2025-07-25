package org.example;


import burp.api.montoya.MontoyaApi;
import burp.api.montoya.ai.Ai;
import burp.api.montoya.burpsuite.BurpSuite;
import burp.api.montoya.collaborator.Collaborator;
import burp.api.montoya.comparer.Comparer;
import burp.api.montoya.http.message.Cookie; // added
import burp.api.montoya.decoder.Decoder;
import burp.api.montoya.extension.Extension;
import burp.api.montoya.http.Http;
import burp.api.montoya.http.message.HttpHeader;
import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.http.message.responses.HttpResponse;
import burp.api.montoya.http.message.params.HttpParameter; // added
import burp.api.montoya.http.message.params.HttpParameterType; // added
import burp.api.montoya.http.message.MimeType; // added
import burp.api.montoya.intruder.Intruder;
import burp.api.montoya.logging.Logging;
import burp.api.montoya.organizer.Organizer;
import burp.api.montoya.persistence.Persistence;
import burp.api.montoya.project.Project;
import burp.api.montoya.proxy.Proxy;
import burp.api.montoya.repeater.Repeater;
import burp.api.montoya.scanner.Scanner;
import burp.api.montoya.scope.Scope;
import burp.api.montoya.sitemap.SiteMap;
import burp.api.montoya.ui.UserInterface;
import burp.api.montoya.utilities.Utilities;
import burp.api.montoya.websocket.WebSockets;


import lombok.Getter;
import lombok.Setter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.stream.Collectors;
import java.util.UUID;


import java.util.*;

public class LogEntry {
    private List<String> tags; // TODO: change type from String to our own class called Tag or something
    @Getter
    @Setter
    private HttpRequest request;
    @Getter
    //@Setter
    private HttpResponse response;
    private Date requestDateTime = new Date(0);
    private Date responseDateTime = new Date(0);
    @Setter
    @Getter
    public int messageId;
    private String requestHttpVersion;

    // Java Status class
    public Status previousStatus;
    public Status status;

    //public debugStatement debug;

    private static final Pattern HTML_TITLE_PATTERN = Pattern.compile("<title>(.*?)</title>", Pattern.CASE_INSENSITIVE);

    // from LogTableColumnModel
    @Setter
    private int number;
    private String tag;

    // Metadata for tagging
    private String hostname; // w/o port number
    private String host; // w/ port number
    private String urlString; // url
    private URI uri;
    private String path;
    private String httpMethod; // method
    private Short responseStatus = -1; // status codes
    private String responseStatusText; // status code text
    private String redirectURL;
    private String comment; // use for the annotations for the tags(?)
    private int requestResponseDelay;  // is this useful for slow responses(?)
    @Getter
    private List<HttpHeader> requestHeaders;
    @Getter
    private List<HttpHeader> responseHeaders;
    private List<String> parameters;
    private Set<String> reflectedParameters;

    private String formattedResponseTime;

    private String sentCookies;

    private List<String> newCookies = new ArrayList<>();

    private boolean params;
    private boolean edited; // is it boolean(?)
    private int length;
    private MimeType responseMimeType;

    private MimeType responseInferredMimeType;

    private String responseContentType;
    private String extension;
    private String title;
    private boolean tls;
    @Setter
    private String requestSource;
    private String time;

    // may need
    private int targetPort;
    private URI parsedUrl;
    private String referrerURL;
    private String protocol;
    private boolean isSSL;
    private String origin;
    private boolean hasCookieParam;
    private String requestContentType;

    private String urlExtension;
    private boolean hasBodyParam;
    // private boolean hasCookieParam; // already defined
    private CookieJarStatus usesCookieJar;

    private boolean hasSetCookies = false;

    private int requestBodyLength;

    private int responseBodyLength;

    private boolean complete = false;

    private UUID requestId;

    private MontoyaApi montoya;

    public enum CookieJarStatus {
        YES("Yes"), NO("No"), PARTIALLY("Partially");

        private String value;

        CookieJarStatus(String value) {
            this.value = value;
        }
    }

    private Date responseArrivalTime;


    // TODO: define process, processRequest, processResponse methods
    // These methods should extract and assign relevant fields

    private LogEntry() {
        this.tags = new ArrayList<>(); // TODO: change to HashSet for better lookup times?
        this.status = Status.UNPROCESSED;
        this.montoya = null;
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

    // getter methods

    public UUID getRequestId() {
        return this.requestId;
    }

    public void setResponse(HttpResponse response) {
        this.response = response;
    }

    public int getNumber() {
        return number;
    }

    public String getHostname() {
        return hostname;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public String getTag() {
        return this.tag;
    }

    public boolean isEdited() {
        return edited;
    }

    public Date getResponseArrivalTime() {
        return responseArrivalTime;
    }

    public void setResponseArrivalTime(Date responseArrivalTime) {
        this.responseArrivalTime = responseArrivalTime;
    }

    public Short getResponseStatus() {
        return responseStatus;
    }

    public String getResponseStatusText() {
        return responseStatusText;
    }

    public int getLength() {
        return length;
    }

    public MimeType getResponseMimeType() {
        return responseMimeType;
    }

    public String getTitle() {
        return title;
    }

    public boolean getTls() {
        return tls;
    }

//    public String getIp() {
//        return ip;
//    }

    public String getFormattedResponseTime() {
        return formattedResponseTime;
    }

    // TODO: Processing data logic for requests
    // change some of this up from Logger++
    public boolean process() {
        previousStatus = this.status;
        switch (this.status) {
            case UNPROCESSED -> {
                this.status = processRequest();
            }
            case AWAITING_RESPONSE -> {
                if (this.response == null) {
                    this.status = Status.FAILED;
                    return false;
                }
                this.status = processResponse();
                //return this.status == Status.PROCESSED;
                // return true;
            }
            case PROCESSED -> {
                return true;
            }
            case FAILED -> {
                return false;
                //reprocess();
                // return this.status == Status.PROCESSED;
            }
            default -> {
                return false;
            }
        }
        return this.status == Status.PROCESSED;
    }

    public void reprocess() {
        this.status = Status.UNPROCESSED;
        process();
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }


//    public debugStatement getDebug() {
//        return debug;
//    }
//
//    public void setDebugStatement(debugStatement debug) {
//        this.debug = debug;
//    }

//    public void setResponse(HttpResponse response) {
//        this.response = response;
//    }

    public Status getPreviousStatus() {
        return previousStatus;
    }

    public void setMontoya(MontoyaApi montoya) {
        this.montoya = montoya;
    }

    // change to make relevant to tidyburp
    private Status processRequest() {
        if (this.request == null) {
            return Status.FAILED;
        }
        requestHeaders = request.headers();
        // retrieve basic request metadata
        if (this.requestHeaders != null && !this.requestHeaders.isEmpty()) {
            requestHeaders = request.headers();
        } else {
            System.out.print("Request headers is null or empty");
        }
        this.urlString = request.url();
        this.hostname = this.request.httpService().host();
        this.path = this.request.pathWithoutQuery();
        this.protocol = this.request.httpService().secure() ? "https" : "http";
        this.isSSL = this.request.httpService().secure();
        this.targetPort = this.request.httpService().port();
        this.httpMethod = request.method();
        this.requestDateTime = new Date();
        this.time = String.valueOf(requestDateTime.getTime());
        // or
        //this.time = System.currentTimeMillis(); // need to capture current time/date

        // http ports
        boolean isDefaultPort = (this.protocol.equals("https") && this.targetPort == 443)
                || (this.protocol.equals("http") && this.targetPort == 80);
        // hosts
        this.host = this.protocol + "://" + this.hostname + (isDefaultPort ? "" : ":" + this.targetPort);
        // Process whether we use TLS
        this.tls = this.request.httpService().secure();
        // Process the HTTP method
        this.httpMethod = request.method();

        //only needed if we care about HTTP version
        //parse for http version from request
        String[] httpRequestTokens = requestHeaders.get(0).value().split(" ");
        this.requestHttpVersion = httpRequestTokens[httpRequestTokens.length - 1];

        //extract non-cookie parameters
        this.parameters = request.parameters().stream()
                .map(HttpParameter::name)
                .collect(Collectors.toList());

        // Length of request body
        this.requestBodyLength = this.getRequestBytes().length - request.bodyOffset();
        this.hasBodyParam = requestBodyLength > 0;

        // url parsing and extension extraction
        if (isValidUri(this.urlString)) {
            this.uri = URI.create(this.urlString);
            // tags by file type
            String tempPath = uri.getPath().replaceAll("\\\\", "/");
            tempPath = tempPath.substring(tempPath.lastIndexOf("/"));
            int tempPathDotLocation = tempPath.lastIndexOf(".");
            if (tempPathDotLocation >= 0) {
                this.urlExtension = tempPath.substring(tempPathDotLocation + 1);
            }
            // param existence flag
            this.params = uri.getQuery() != null || this.hasBodyParam;
            // analyze HTTP headers
        }
//        for (HttpHeader header : requestHeaders) {
//            // handle cookies
//            if (header.name().equalsIgnoreCase("cookie")) {
//                this.sentCookies = header.value();
//                if (!this.sentCookies.isEmpty()) {
//                    this.hasCookieParam = true;
//                    this.sentCookies += ";";
//
//
//                    // split cookies
//                    String[] cookiesArray = this.sentCookies.split(";");
//
//                    // old logic
//                    // Check to see if it uses cookie Jars!
////                    List<Cookie> cookiesInJar = montoya.http().cookieJar().cookies();
////                    boolean oneNotMatched = false;
////                    boolean anyParamMatched = false;
////
////                    for (Cookie cookieItem : cookiesInJar) {
////                        if (cookieItem.domain().equals(this.hostname)) {
////                            String currentCookieJarParam = cookieItem.name() + "=" + cookieItem.value() + ";";
////                            if (this.sentCookies.contains(currentCookieJarParam)) {
////                                anyParamMatched = true;
////                            } else {
////                                oneNotMatched = true;
////                            }
////                            if (anyParamMatched && oneNotMatched) {
////                                break; // we do not need to analyse it more!
////                            }
////                        }
////                        // set cookie jar usage status
////                        if (oneNotMatched && anyParamMatched) {
////                            this.usesCookieJar = CookieJarStatus.PARTIALLY;
////                        } else if (!oneNotMatched && anyParamMatched) {
////                            this.usesCookieJar = CookieJarStatus.YES;
////                        }
//                    // old logic
//                    List<Cookie> cookiesInJar = montoya.http().cookieJar().cookies();
//                    boolean oneNotMatched = false;
//                    boolean anyParamMatched = false;
//
//                    for (String cookie: cookiesArray) {
//                        cookie = cookie.trim();
//                        if (cookie.isEmpty()) continue;
//
//                        String[] cookieParts = cookie.split("=");
//                        if (cookieParts.length == 2) {
//                            String cookieName = cookieParts[0].trim();
//                            String cookieValue = cookieParts[1].trim();
//                            String currentCookieJarParam = cookieName + "=" + cookieValue + ";";
//
//                            // Check if this cookie exists in the cookie jar
//                            for (Cookie cookieItem : cookiesInJar) {
//                                if (cookieItem.domain().equals(this.hostname) && currentCookieJarParam.equals(cookieItem.name() + "=" + cookieItem.value())) {
//                                    anyParamMatched = true;
//                                    break;  // Break if matched
//                                } else {
//                                    oneNotMatched = true;  // Mark as unmatched if not found
//                                }
//                            }
//                        }
//                    }
//
//                    // Set cookie jar usage status based on matching cookies
//                    if (oneNotMatched && anyParamMatched) {
//                        this.usesCookieJar = CookieJarStatus.PARTIALLY;
//                    } else if (!oneNotMatched && anyParamMatched) {
//                        this.usesCookieJar = CookieJarStatus.YES;
//                    }
//                }
//            }
            //handle different headers
//            if (header.name().equalsIgnoreCase("referer")) {
//                this.referrerURL = header.value();
//            }
//            if (header.name().equalsIgnoreCase("content-type")) {
//                this.requestContentType = header.value();
//            }
//            if (header.name().equalsIgnoreCase("origin")) {
//                this.origin = header.value();
//            }
//        }
        this.complete = true;
        return Status.AWAITING_RESPONSE;
    }

    public boolean processRequestOnly() {
        try{
            processRequest();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
        private Status processResponse () {
            if (response == null) {
                return Status.FAILED;
            }
            // resets the reflected parameters
            reflectedParameters = new HashSet<>();
            // basic response info
            this.responseStatus = response.statusCode();
            //this.responseStatusText = getStatusText(this.responseStatus);
            this.responseBodyLength = this.response.body().length();
            this.responseMimeType = response.statedMimeType();
            this.responseInferredMimeType = response.inferredMimeType();

            // 1. process headers
            Map<String, String> headers = response.headers().stream()
                    .collect(Collectors.toMap(HttpHeader::name, HttpHeader::value, (s, s2) -> {
                        s += ", " + s2;
                        return s;
                    }, () -> new TreeMap<>(String.CASE_INSENSITIVE_ORDER)));

            responseHeaders = response.headers();

            // finds headers that could affect processing
            if (headers.containsKey("Location")) {
                this.redirectURL = headers.get("Location");
            }

            // extract response content type
            if (headers.containsKey("content-type")) {
                this.responseContentType = headers.get("content-type");
            }

            // process cookies from response headers
            this.newCookies = response.cookies().stream().map(cookie -> String.format("%s=%s", cookie.name(), cookie.value())).collect(Collectors.toList());
            this.hasSetCookies = !newCookies.isEmpty();

            // parse date header
            if (this.responseDateTime == null && headers.containsKey("Date")) {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.ENGLISH);
                    this.responseDateTime = sdf.parse(headers.get("Date"));
                } catch (ParseException e) {
                    this.responseDateTime = null;
                }
            }

            if (responseDateTime != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                this.formattedResponseTime = sdf.format(responseDateTime);
            } else {
                this.formattedResponseTime = "";
            }

            // calculate response delay
            if (requestDateTime != null && responseDateTime != null) {
                this.requestResponseDelay = (int) (responseDateTime.getTime() - requestDateTime.getTime());
            }

            // 2. process body
            String responseBody = response.bodyToString();
            if (responseBody == null) {
                return Status.FAILED_PROCESS;
            }
            reflectedParameters = request.parameters().stream()
                    .filter((param -> isReflected(responseBody, param.value())))
                    .map(HttpParameter::name)
                    .collect(Collectors.toSet());

            if (this.responseMimeType != null && this.responseMimeType.toString().toLowerCase().contains("html")) {
                this.title = extractHtmlTitle(responseBody);
            }

            for (HttpParameter param : request.parameters()) {
                String value = param.value();
                for (HttpHeader header : response.headers()) {
                    if (header.value().contains(value)) {
                        reflectedParameters.add(param.name());
                        break;
                    }
                }
            }
                this.complete = true;
                return Status.PROCESSED;
            }


        public void setTag (String tag){
            tags.clear();
            tags.add(tag);
        }


        public void addTag (String tag){
            if (!tags.contains(tag)) {
                tags.add(tag);
            }
        }


        public String getFirstTag () {
            return tags.isEmpty() ? "" : tags.get(0);
        }


        public List<String> getTags () {
            return tags;
        }


        public void removeTag (String tag){
            tags.remove(tag);
        }

        public void renameTag (String oldTag, String newTag) {
            if (tags.contains(oldTag)) {
                tags.set(tags.indexOf(oldTag), newTag);
            }
        }

        public boolean hasTag(String tag) {
            return tags.contains(tag);
        }


        public void clearTags () {
            tags.clear();
        }

        public byte[] getRequestBytes () {
            return this.request.toByteArray().getBytes();
        }

        private boolean isValidUri (String uri){
            try {
                URI tempUri = new URI(uri);
                return tempUri.getHost() != null;
            } catch (URISyntaxException e) {
                return false;
            }
        }

        private boolean isReflected (String responseBody, String paramValue){
            return responseBody != null && responseBody.contains(paramValue);
        }

        private String extractHtmlTitle (String responseBody){
            if (responseBody == null) {
                return null;
            }
            Matcher titlePatternMatcher = HTML_TITLE_PATTERN.matcher(responseBody);
            if (titlePatternMatcher.find()) {
                return this.title = titlePatternMatcher.group(1);
            }
            return null;
        }

        // TODO: REPLACE.
        // temporary method to make the week 2 feature work, need to make LogEntry more like Logger++ probably
//    public List<Object> getData() {
//        return Arrays.asList(
//                this.number, // int
//                this.hostname != null ? this.hostname : "N/A", // String w/o port number
//                this.httpMethod != null? this.httpMethod: "UNKNOWN", // String
//                this.tag != null ? this.tag : "NO_TAG",// Request tag // String
//                this.request.url(), //
//                this.request.parameters(), // params - url is showing up here
//                // add request Mimetype(?)
//                this.edited, // parsed http [] is showing up here
//                this.statusCode, // response status code - showing up as either params or edited
//                this.length, // response length
//                this.responseMimeType, //  response mime type
//                this.extension, // response extension
//                this.title,
//                this.tls,
//                this.ip,
//                this.cookies,
//                this.requestDateTime, // showing up as status
//                this.status, // showing up as tags
//                // add response datetime
//                this.tags // Other tags
//        );
//    }

        public List<Object> getData () {
            return Arrays.asList(
                    this.number, // int
                    this.hostname != null ? this.hostname : "N/A", // String w/o port number
                    this.httpMethod != null ? this.httpMethod : "UNKNOWN", // String
                    //this.request != null ? this.request.url() : "N/A", // URL from request
                    this.path != null ? this.path : "N/A",
                    this.getParamCount(), // Parameters
                    // need to get the processed ones instead
                    //this.request.parameters(),
                    // everything past here is null
                    this.edited, // Boolean: edited status
                    this.responseStatus != -1 ? this.responseStatus : "UNKNOWN", // Response status code
                    //this.responseStatus = response.statusCode() != -1 ? response.statusCode() : 0
                    //this.response.statusCode(),
                    //this.responseStatus,
                    this.responseBodyLength, // Response length
                    this.responseMimeType != null ? this.responseMimeType : "UNKNOWN", // Response mime type
                    this.extension != null ? this.extension : "N/A", // File extension
                    this.title != null ? this.title : "Untitled", // Page title
                    this.tls, // TLS info
                    this.requestSource != null ? this.requestSource : "N/A", // IP address
                    this.sentCookies != null ? this.sentCookies : "N/A", // Cookies
                    // these work okay
                    this.requestDateTime != null ? this.requestDateTime.toString() : "N/A", // Request datetime
//                    this.status != null ? this.status.toString() : "N/A", // Status
                    //this.debug != null ? this.debug : "N/A",
                    //this.responseDateTime != null ? this.responseDateTime.toString() : "N/A", // Response datetime
                    this.tags != null ? this.tags : new ArrayList<>() // Tags
            );
        }

        public int getParamCount() {
            return parameters == null ? 0 : parameters.size();
        }


        public enum Status {
            UNPROCESSED, AWAITING_RESPONSE, PROCESSED, FAILED, FAILED_PROCESS, NO_PROCESS,
        }

//    public enum debugStatement {
//        WORKING_REQUEST, NOT_WORKING_REQUEST, WORKING_RESPONSE, NOT_WORKING_RESPONSE,
//    }
}
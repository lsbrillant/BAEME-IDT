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
import burp.api.montoya.scanner.bchecks.BCheckImportResult;
import burp.api.montoya.scope.Scope;
import burp.api.montoya.sitemap.SiteMap;
import burp.api.montoya.ui.UserInterface;
import burp.api.montoya.utilities.Utilities;
import burp.api.montoya.websocket.WebSockets;
import lombok.Getter;
import lombok.Setter;

import java.net.MalformedURLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.stream.Collectors;

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
    private String requestHttpVersion;

    // Java Status class
    Status previousStatus;
    Status status = Status.UNPROCESSED;

    private static final Pattern HTML_TITLE_PATTERN = Pattern.compile("<title>(.*?)</title>", Pattern.CASE_INSENSITIVE);

    // from LogTableColumnModel
    private int number;
    private String tag;

    // Metadata for tagging
    private String hostname; // w/o port number
    private String host; // w/ port number
    private String urlString; // url
    private URI uri;
    private String httpMethod; // method
    private Short responseStatus;
    private String responseStatusText;
    private String redirectURL;
    private String comment; // use for the annotations for the tags(?)
    private int requestResponseDelay;  // is this useful for slow responses(?)
    private List<HttpHeader> requestHeaders;
    private List<HttpHeader> responseHeaders;
    private List<String> parameters;
    private List<String> reflectedParameters;

    private String formattedResponseTime;

    private String sentCookies;

    private List<String> newCookies = new ArrayList<>();

    private boolean params;
    private boolean edited; // is it boolean(?)
    private String statusCode; // code
    private int length;
    private MimeType responseMimeType;

    private MimeType responseInferredMimeType;

    private String responseContentType;
    private String extension;
    private String title;
    private String tls;
    private String ip;
    private String cookies;
    private String time;

    // may need
    private int targetPort;
    private URI parsedUrl;
    private String referrerURL;
    private String protocol;
    private boolean isSSL;
    private String origin;
    // private String cookies; // already defined
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

    private final MontoyaApi montoya;

    public enum CookieJarStatus {
        YES("Yes"), NO("No"), PARTIALLY("Partially");

        private String value;

        CookieJarStatus(String value) {
            this.value = value;
        }
    }

    // TODO: define process, processRequest, processResponse methods
    // These methods should extract and assign relevant fields

    private LogEntry() {
        this.tags = new ArrayList<>(); // TODO: change to HashSet for better lookup times?
        montoya = new MontoyaApi() {
            @Override
            public Ai ai() {
                return null;
            }

            @Override
            public BurpSuite burpSuite() {
                return null;
            }

            @Override
            public Collaborator collaborator() {
                return null;
            }

            @Override
            public Comparer comparer() {
                return null;
            }

            @Override
            public Decoder decoder() {
                return null;
            }

            @Override
            public Extension extension() {
                return null;
            }

            @Override
            public Http http() {
                return null;
            }

            @Override
            public Intruder intruder() {
                return null;
            }

            @Override
            public Logging logging() {
                return null;
            }

            @Override
            public Organizer organizer() {
                return null;
            }

            @Override
            public Persistence persistence() {
                return null;
            }

            @Override
            public Project project() {
                return null;
            }

            @Override
            public Proxy proxy() {
                return null;
            }

            @Override
            public Repeater repeater() {
                return null;
            }

            @Override
            public Scanner scanner() {
                return null;
            }

            @Override
            public Scope scope() {
                return null;
            }

            @Override
            public SiteMap siteMap() {
                return null;
            }

            @Override
            public UserInterface userInterface() {
                return null;
            }

            @Override
            public Utilities utilities() {
                return null;
            }

            @Override
            public WebSockets websockets() {
                return null;
            }
        };
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
        return Arrays.asList(
                this.number,
                this.hostname,
                this.httpMethod,
                this.tag, // Request tag
                this.request.url(),
                this.request.parameters(), // params
                // add request Mimetype(?)
                this.edited,
                this.statusCode, // response status code
                this.length, // response length
                this.responseMimeType, //  response mime type
                this.extension, // response extension
                this.title,
                this.tls,
                this.ip,
                this.cookies,
                this.requestDateTime,
                // add response datetime
                this.tags // Other tags
        );
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
            default:
                return false;
        }
    }

    public void reprocess() {
        this.status = Status.UNPROCESSED;
        process();
    }

    public Status getStatus() {
        return status;
    }

    public Status getPreviousStatus() {
        return previousStatus;
    }

    // change to make relevant to tidyburp
    private Status processRequest() {
        // retrieve basic request metadata
        requestHeaders = request.headers();
        this.urlString = request.url();
        this.hostname = this.request.httpService().host();
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
        // Process the HTTP method
        this.httpMethod = request.method();

        //only needed if we care about HTTP version
        //parse for http version from request
        String[] httpRequestTokens = requestHeaders.get(0).value().split(" ");
        this.requestHttpVersion = httpRequestTokens[httpRequestTokens.length - 1];

        //extract non-cookie parameters
        this.parameters = request.parameters().stream()
                .filter(param -> param.type() != HttpParameterType.COOKIE)
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
        for (HttpHeader header : requestHeaders) {
            // handle cookies
            if (header.name().equalsIgnoreCase("cookie")) {
                this.sentCookies = header.value();
                if (!this.sentCookies.isEmpty()) {
                    this.hasCookieParam = true;
                    this.sentCookies += ";";

                    // Check to see if it uses cookie Jars!
                    List<Cookie> cookiesInJar = montoya.http().cookieJar().cookies();
                    boolean oneNotMatched = false;
                    boolean anyParamMatched = false;

                    for (Cookie cookieItem : cookiesInJar) {
                        if (cookieItem.domain().equals(this.hostname)) {
                            String currentCookieJarParam = cookieItem.name() + "=" + cookieItem.value() + ";";
                            if (this.sentCookies.contains(currentCookieJarParam)) {
                                anyParamMatched = true;
                            } else {
                                oneNotMatched = true;
                            }
                            if (anyParamMatched && oneNotMatched) {
                                break; // we do not need to analyse it more!
                            }
                        }
                        // set cookie jar usage status
                        if (oneNotMatched && anyParamMatched) {
                            this.usesCookieJar = CookieJarStatus.PARTIALLY;
                        } else if (!oneNotMatched && anyParamMatched) {
                            this.usesCookieJar = CookieJarStatus.YES;
                        }
                    }
                    //handle different headers
                } else if (header.name().equalsIgnoreCase("referer")) {
                    this.referrerURL = header.value();
                } else if (header.name().equalsIgnoreCase("content-type")) {
                    this.requestContentType = header.value();
                } else if (header.name().equalsIgnoreCase("origin")) {
                    this.origin = header.value();
                }
            }
        }

        return Status.AWAITING_RESPONSE;
    }

    // ill make this work eventually
//    // change to make relevant to tidyburp
    private Status processResponse() {
        // resets the reflected parameters
        reflectedParameters = new ArrayList<>();
        // basic response info
        this.responseStatus = response.statusCode();
        this.responseBodyLength = response.body().length();
        this.responseMimeType = response.statedMimeType();
        //this.responseInferredMimeType = response.inferredMimeType();

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

        reflectedParameters = request.parameters().stream()
                .filter((param -> isReflected(responseBody, param.value())))
                .map(HttpParameter::name)
                .collect(Collectors.toList());

        if (this.responseMimeType != null && this.responseMimeType.toString().toLowerCase().contains("html")) {
            this.title = extractHtmlTitle(responseBody);
        }

        for (HttpParameter param: request.parameters()) {
            String value = param.value();
            for (HttpHeader header: response.headers()) {
                if (header.value().contains(value)) {
                    reflectedParameters.add(param.name());
                    break;
                }
            }
        }

        this.complete = true;
        return Status.PROCESSED;
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

    public byte[] getRequestBytes() {
        return this.request.toByteArray().getBytes();
    }

    private boolean isValidUri(String uri) {
        try {
            URI tempUri = new URI(uri);
            return tempUri.getHost() != null;
        } catch (URISyntaxException e){
            return false;
        }
    }

    private boolean isReflected (String responseBody, String paramValue) {
        return responseBody != null && responseBody.contains(paramValue);
    }

    private String extractHtmlTitle(String responseBody) {
        if (responseBody == null) {
            return null;
        }
        Matcher titlePatternMatcher = HTML_TITLE_PATTERN.matcher(responseBody);
        if (titlePatternMatcher.find()) {
            return this.title = titlePatternMatcher.group(1);
        }
        return null;
    }
}
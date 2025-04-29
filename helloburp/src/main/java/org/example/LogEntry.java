package org.example;


import burp.api.montoya.http.message.HttpHeader;
import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.http.message.responses.HttpResponse;
import burp.api.montoya.http.sessions.CookieJar;
import burp.api.montoya.scanner.bchecks.BCheckImportResult;
import lombok.Getter;
import lombok.Setter;
import java.net.MalformedURLException;
import java.net.URL;


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

    // from LogTableColumnModel
    private int number;
    private String tag;

    // Look at loggerplusplus/logentry/LogEntry.java for reference

    // from logtable
    private int number;
    private String tag;
    // Metadata for tagging
    private String hostname; // w/o port number
    private String host; // w/ port number
    private String urlString; // url
    private URL url;
    private String httpMethod; // method
    private Short responseStatus;
    private String responseStatusText;
    private String comment; // use for the annotations for the tags(?)
    private int requestResponseDelay;  // is this useful for slow responses(?)
    private List<HttpHeader> requestHeaders;
    private List<HttpHeader> responseHeaders;
    private List<String> parameters; // params
    private boolean edited; // is it boolean(?)
    private String statusCode; // code
    private int length;
    private String mimeType; // MIME
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

    // may need
    private int targetPort;
    private URL parsedUrl;
    private String referrerURL;
    private String protocol;
    private boolean isSSL;
    private String origin;
    private String cookies;
    private boolean hasCookieParam;
    private String requestContentType;
    private boolean hasBodyParam;
    private boolean hasCookieParam;
    private CookieJarStatus usesCookieJar;

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
                this.tag,
                this.request.url(),
                this.request.parameters(), // params
                this.edited,
                this.statusCode,
                this.length,
                this.mimeType,
                this.extension,
                this.title,
                this.tls,
                this.ip,
                this.cookies,
                this.requestDateTime,
                this.tags
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
        // extract request headers - keep
        requestHeaders = request.headers();
        this.urlString = request.url();
        this.hostname = this.request.httpService().host();
        this.protocol = this.request.httpService().secure() ? "https" : "http";
        this.isSSL = this.request.httpService().secure();
        this.targetPort = this.request.httpService().port();
        this.httpMethod = request.method();
        this.requestDateTime = new Date();
        // is this different from requestDateTime(?)
        // this.time = System.currentTimeMillis(); // need to capture current time/date

        // only needed if we care about HTTP version
        // parse for http version from request
//        String[] httpRequestTokens = requestHeaders.get(0).value().split(" ");
//        this.requestHttpVersion = httpRequestTokens[httpRequestTokens.length - 1];

        // extract non-cookie parameters
        this.parameters = request.parameters().stream()
                .filter(param -> param.type() != HttpParameterType.COOKIE)
                .map(HttpParameter::name)
                .collect(Collectors.toList());

        // http ports
        boolean isDefaultPort = (this.protocol.equals("https") && this.targetPort == 443)
                || (this.protocol.equals("http") && this.targetPort == 80);
        // hosts
        this.host = this.protocol + "://" + this.hostname + (isDefaultPort ? "" : ":" + this.targetPort);

        // Process the HTTP method
        this.method = request.method();

        // Length of request body
        this.requestBodyLength = this.getRequestBytes().length - request.bodyOffset();
        this.hasBodyParam = requestBodyLength > 0;

        // url parsing and extension extraction
        try {
            this.url = new URL(request.url());

            // I don't want to delete special characters such as ; or : from the extension
            // as it may really be part of the extension! (burp proxy log ignores them)

            // tags by file type - keep
            String tempPath = url.getPath().replaceAll("\\\\", "/");
            tempPath = tempPath.substring(tempPath.lastIndexOf("/"));
            int tempPathDotLocation = tempPath.lastIndexOf(".");
            if (tempPathDotLocation >= 0) {
                this.urlExtension = tempPath.substring(tempPathDotLocation + 1);
            }
            // param existence flag
            this.params = url.getQuery() != null || this.hasBodyParam;
        } catch (MalformedURLException ignored) {
        }

        // analyze HTTP headers

        // reading request headers like a boss!
        for (HttpHeader header : requestHeaders) {
            // simplify the cookie logic if needed
////			if (header.contains(":")) {
//            // handle cookies
//            if (header.name().equalsIgnoreCase("cookie")) {
//                this.sentCookies = header.value();
//                if (!this.sentCookies.isEmpty()) {
//                    this.hasCookieParam = true;
//                    this.sentCookies += ";"; // we need to ad this to search it in cookie Jar!
//
//                    // Check to see if it uses cookie Jars!
//                    List<Cookie> cookiesInJar = montoya.http().cookieJar().cookies();
//                    boolean oneNotMatched = false;
//                    boolean anyParamMatched = false;
//
//                    for (Cookie cookieItem : cookiesInJar) {
//                        if (cookieItem.domain().equals(this.hostname)) {
//                            // now we want to see if any of these cookies have been set here!
//                            String currentCookieJarParam = cookieItem.name() + "=" + cookieItem.value() + ";";
//                            if (this.sentCookies.contains(currentCookieJarParam)) {
//                                anyParamMatched = true;
//                            } else {
//                                oneNotMatched = true;
//                            }
//                        if (anyParamMatched && oneNotMatched) {
//                            break; // we do not need to analyse it more!
//                        }
//                    }
//                        // set cookie jar usage status
//                    if (oneNotMatched && anyParamMatched) {
//                        this.usesCookieJar = CookieJarStatus.PARTIALLY;
//                    } else if (!oneNotMatched && anyParamMatched) {
//                        this.usesCookieJar = CookieJarStatus.YES;
//                    }
//                }
            // handle different headers
//        } else if (header.name().equalsIgnoreCase("referer")) {
//            this.referrerURL = header.value();
//        } else if (header.name().equalsIgnoreCase("content-type")) {
//            this.requestContentType = header.value();
//        } else if (header.name().equalsIgnoreCase("origin")) {
//            this.origin = header.value();
//        }
//			}
    }

        return Status.AWAITING_RESPONSE;
    }

    // change to make relevant to tidyburp
    private void processResponse() {
        // what are reflected parameters(?)
        reflectedParameters = new ArrayList<>();
//		IResponseInfo tempAnalyzedResp = LoggerPlusPlus.montoya.getHelpers()
//				.analyzeResponse(response);

        this.responseStatus = response.statusCode();
        this.responseBodyLength = response.body().length();
        this.responseMimeType = response.statedMimeType();
        this.responseInferredMimeType = response.inferredMimeType();

        /**************************************
         ************HEADER PROCESSING*********
         **************************************/

        Map<String, String> headers = response.headers().stream()
                .collect(Collectors.toMap(HttpHeader::name, HttpHeader::value, (s, s2) -> {
                    s += ", " + s2;
                    return s;
                }, () -> new TreeMap<>(String.CASE_INSENSITIVE_ORDER)));

        responseHeaders = response.headers();

        if (headers.containsKey("Location")) {
            this.redirectURL = headers.get("Location");
        }

        // Extract HTTP Status message
        HttpHeader httpStatusTokens = response.headers().get(0);
        //TODO FixMe
//		this.responseStatusText = httpStatusTokens[httpStatusTokens.length - 1];
//		this.responseHttpVersion = httpStatusTokens[0];


        if (headers.containsKey("content-type")) {
            this.responseContentType = headers.get("content-type");
        }

        //Cookies
        this.newCookies = response.cookies().stream().map(cookie -> String.format("%s=%s", cookie.name(), cookie.value())).collect(Collectors.toList());
        this.hasSetCookies = !newCookies.isEmpty();


        if (this.responseDateTime == null) {
            // If it didn't have an arrival time set, parse the response for it.
            if (headers.get("date") != null && !StringUtils.isBlank(headers.get("date"))) {
                try {
                    synchronized (LogProcessor.SERVER_DATE_FORMAT) {
                        this.responseDateTime = LogProcessor.SERVER_DATE_FORMAT.parse(headers.get("date"));
                    }
                } catch (ParseException e) {
                    this.responseDateTime = null;
                }
            } else {
                // No date header...
                this.responseDateTime = null;
            }
        }
        if (responseDateTime != null) {
            this.formattedResponseTime = LogProcessor.LOGGER_DATE_FORMAT.format(responseDateTime);
        } else {
            this.formattedResponseTime = "";
        }

        if (requestDateTime != null && responseDateTime != null) {
            this.requestResponseDelay = (int) (responseDateTime.getTime() - requestDateTime.getTime());
        }

        /**************************************
         *************BODY PROCESSING**********
         **************************************/

        Long maxRespSize = ((Integer) LoggerPlusPlus.instance.getPreferencesController().getPreferences().getSetting(Globals.PREF_MAX_RESP_SIZE)) * 1000000L;
        int bodyOffset = response.bodyOffset();
        if (responseBodyLength < maxRespSize) {
            //Only title match HTML files. Prevents expensive regex running on e.g. binary downloads.
            if (this.responseInferredMimeType == MimeType.HTML) {
                Matcher titleMatcher = Globals.HTML_TITLE_PATTERN.matcher(response.bodyToString());
                if (titleMatcher.find()) {
                    this.title = titleMatcher.group(1);
                }
            }

            ReflectionController reflectionController = LoggerPlusPlus.instance.getReflectionController();
            reflectedParameters = request.parameters().parallelStream()
                    .filter(parameter -> !reflectionController.isParameterFiltered(parameter) && reflectionController.validReflection(response.bodyToString(), parameter))
                    .map(HttpParameter::name).collect(Collectors.toList());

//			this.requestResponse = LoggerPlusPlus.montoya.saveBuffersToTempFiles(requestResponse);
        } else {
            //Just look for reflections in the headers.
            ReflectionController reflectionController = LoggerPlusPlus.instance.getReflectionController();
            reflectedParameters = request.parameters().parallelStream()
                    .filter(parameter -> !reflectionController.isParameterFiltered(parameter)
                            && reflectionController.validReflection(response.bodyToString(), parameter))
                    .map(HttpParameter::name).collect(Collectors.toList());

            //Trim the response down to a maximum size, but at least keep the headers!
            //TODO Fix response trimming?
//			this.response = (new String(this.response, 0, bodyOffset) + "Response body trimmed by Logger++. To prevent this, increase \"Maximum Response Size\" in the Logger++ options.").getBytes(StandardCharsets.UTF_8);
        }

        this.complete = true;
        return Status.PROCESSED;
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
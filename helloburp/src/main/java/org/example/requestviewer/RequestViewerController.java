package org.example.requestviewer;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.ui.editor.EditorOptions;
import burp.api.montoya.ui.editor.HttpRequestEditor;
import burp.api.montoya.ui.editor.HttpResponseEditor;
import lombok.Getter;
import org.example.LogEntry;

public class RequestViewerController {
    @Getter
    private final HttpRequestEditor requestEditor;
    @Getter
    private final HttpResponseEditor responseEditor;
    @Getter
    private final RequestViewerPanel requestViewerPanel;

    private LogEntry currentEntry;

    public RequestViewerController(MontoyaApi montoya) {
        this.requestEditor = montoya.userInterface().createHttpRequestEditor(EditorOptions.READ_ONLY);
        this.responseEditor = montoya.userInterface().createHttpResponseEditor(EditorOptions.READ_ONLY);
        this.requestViewerPanel = new RequestViewerPanel(this, montoya);
    }

    public void setDisplayedEntity(LogEntry logEntry) {
        if (this.currentEntry == logEntry) { return; }

        this.currentEntry = logEntry;

        if (logEntry == null || logEntry.getRequest() == null) {
            requestEditor.setRequest(null);
        }else{
            requestEditor.setRequest(logEntry.getRequest());
        }

        if (logEntry == null || logEntry.getResponse() == null) {
            responseEditor.setResponse(null);
        }else{
            responseEditor.setResponse(logEntry.getResponse());
        }
    }
}

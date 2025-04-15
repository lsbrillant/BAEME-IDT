package org.example.requestviewer;

import burp.api.montoya.MontoyaApi;
import com.coreyd97.BurpExtenderUtilities.PopOutPanel;
import com.coreyd97.BurpExtenderUtilities.VariableViewPanel;
import lombok.Getter;

public class RequestViewerPanel extends PopOutPanel {

    private final RequestViewerController controller;
    @Getter
    private final VariableViewPanel variableViewPanel;

    public RequestViewerPanel(RequestViewerController controller, MontoyaApi montoya) {
        super(montoya);
        this.controller = controller;

        this.variableViewPanel = new VariableViewPanel(null, "msgviewlayout",
                controller.getRequestEditor().uiComponent(), "Request",
                controller.getResponseEditor().uiComponent(), "Response",
                VariableViewPanel.View.HORIZONTAL);

        this.setComponent(variableViewPanel);
        this.setTitle("Request/Response Viewer");
    }
}
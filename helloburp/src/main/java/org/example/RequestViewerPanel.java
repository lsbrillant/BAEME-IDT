package org.example;

import burp.api.montoya.MontoyaApi;
import com.coreyd97.BurpExtenderUtilities.PopOutPanel;
import com.coreyd97.BurpExtenderUtilities.VariableViewPanel;

public class RequestViewerPanel extends PopOutPanel {

    private final RequestViewerController controller;
    private final VariableViewPanel variableViewPanel;

    public RequestViewerPanel(RequestViewerController controller, MontoyaApi montoya) {
        super(montoya);
        this.controller = controller;

        this.variableViewPanel = new VariableViewPanel(null, null,
                controller.getRequestEditor().uiComponent(), "Request",
                controller.getResponseEditor().uiComponent(), "Response",
                VariableViewPanel.View.HORIZONTAL);

        this.setComponent(variableViewPanel);
        this.setTitle("Request/Response Viewer");
    }

    public VariableViewPanel getVariableViewPanel() {
        return variableViewPanel;
    }
}
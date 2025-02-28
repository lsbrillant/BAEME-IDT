package org.example;
import burp.api.montoya.BurpExtension;
import burp.api.montoya.MontoyaApi;
import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.proxy.ProxyHttpRequestResponse;
//import burp.api.montoya.ui.UserInterface;

import java.util.ArrayList;
import java.util.List;

public class HelloBurp implements BurpExtension {
    @Override
    public void initialize(MontoyaApi montoyaApi) {
        montoyaApi.extension().setName("Hello Burp");
        montoyaApi.logging().logToOutput("Hello Burp!");

//        ExtensionUI ui = new ExtensionUI();
//        montoyaApi.userInterface().registerSuiteTab("BAEME", ui.getUi());
        List<ProxyHttpRequestResponse> history = montoyaApi.proxy().history();
        List<List<Object>> data = new ArrayList<>();
        for (ProxyHttpRequestResponse hItem : history) {
            HttpRequest req = hItem.request();
            List<Object> reqInfo = new ArrayList<>();
            reqInfo.add(req.method());
            reqInfo.add(req.url());
            reqInfo.add(req.pathWithoutQuery());
            reqInfo.add(req.query());
            data.add(reqInfo);
        }
        AnnotationsTab annotationsTab = new AnnotationsTab(data);
        montoyaApi.userInterface().registerSuiteTab(annotationsTab.name(), annotationsTab.getPanel());
    }
}

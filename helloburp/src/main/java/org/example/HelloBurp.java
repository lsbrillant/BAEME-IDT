package org.example;

import burp.api.montoya.BurpExtension;
import burp.api.montoya.MontoyaApi;

public class HelloBurp implements BurpExtension {
    @Override
    public void initialize(MontoyaApi montoyaApi) {
        montoyaApi.extension().setName("BAEME");
        montoyaApi.logging().logToOutput("BAEME extension loaded!");

        AnnotationsTab annotationsTab = new AnnotationsTab();
        montoyaApi.userInterface().registerSuiteTab(annotationsTab.name(), annotationsTab.getPanel());
    }
}

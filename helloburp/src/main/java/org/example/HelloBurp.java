package org.example;
import burp.api.montoya.BurpExtension;
import burp.api.montoya.MontoyaApi;

public class HelloBurp implements BurpExtension {
    @Override
    public void initialize(MontoyaApi montoyaApi) {
        montoyaApi.extension().setName("Hello Burp");
        montoyaApi.logging().logToOutput("Hello Burp!");
    }
}

package org.example;

public class MyHTTPHandler {
    private String hash = "";
    private ExtensionUI ui;

    public MyHTTPHandler(String hash, ExtensionUI ui) {
        this.hash = hash;
        this.ui = ui;
    }

    public String getHash() {
        return this.hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

}
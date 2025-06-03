package org.example;

import burp.api.montoya.http.message.HttpRequestResponse;
import burp.api.montoya.ui.contextmenu.ContextMenuEvent;
import burp.api.montoya.ui.contextmenu.ContextMenuItemsProvider;
import org.example.logtable.LogTableController;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class TBContextMenu implements ContextMenuItemsProvider {
    private LogTableController controller;

    TBContextMenu(LogTableController controller) {
        super();
        this.controller = controller;
    }

    @Override
    public List<Component> provideMenuItems(ContextMenuEvent event) {
        // TODO: send with tags
        // TODO: send to specific tab
        List<Component> menuItems = new ArrayList<>();
        List<HttpRequestResponse> contextInfo = event.selectedRequestResponses();
        if (event.messageEditorRequestResponse().isPresent()) {
            contextInfo.add(event.messageEditorRequestResponse().get().requestResponse());
        }
        JMenuItem sendButton = new JMenuItem("Send to TidyBurp");
        sendButton.addActionListener(e -> {
            for (HttpRequestResponse requestResponse : contextInfo) {
                LogEntry entry = new LogEntry(requestResponse.request(), requestResponse.response());
                while(!entry.process()) {
                    entry.process();
                } // this is kinda weird; keep processing until we have status PROCESSED
                entry.setRequestSource(event.toolType().toString());
                controller.getLogTableModel().addEntry(entry);
            }
        });
        menuItems.add(sendButton);

        return menuItems;
    }
}

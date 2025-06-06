// Adapted closely from LoggerPlusPlus/logview/MultipleLogEntryMenu.java
package org.example;

import burp.api.montoya.core.BurpSuiteEdition;
import burp.api.montoya.http.message.HttpRequestResponse;
import burp.api.montoya.scanner.AuditConfiguration;
import burp.api.montoya.scanner.BuiltInAuditConfiguration;
import burp.api.montoya.scanner.Crawl;
import burp.api.montoya.scanner.CrawlConfiguration;
import burp.api.montoya.scanner.audit.Audit;
import org.example.logtable.LogTable;
import org.example.logtable.LogTableController;

import javax.swing.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class MultipleLogEntryMenu extends JPopupMenu {

    public MultipleLogEntryMenu(final LogTableController logTableController, final List<LogEntry> selectedEntries){
        final LogTable logTable = logTableController.getLogTable();

        JMenuItem sendToRepeater = new JMenuItem(new AbstractAction("Send " + selectedEntries.size() + " selected items to Repeater") {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                for (LogEntry entry : selectedEntries) {
                    TidyBurp.montoya.repeater().sendToRepeater(entry.getRequest());
                }
            }
        });
        this.add(sendToRepeater);

        JMenuItem sendToIntruder = new JMenuItem(new AbstractAction("Send " + selectedEntries.size() + " selected items to Intruder") {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                for (LogEntry entry : selectedEntries) {
                    TidyBurp.montoya.intruder().sendToIntruder(entry.getRequest());
                }
            }
        });
        this.add(sendToIntruder);

        JMenu sendToComparer = new JMenu("Send " + selectedEntries.size() + " selected items to Comparer");
        JMenuItem comparerRequest = new JMenuItem(new AbstractAction("Requests") {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                for (LogEntry entry : selectedEntries) {
                    TidyBurp.montoya.comparer().sendToComparer(entry.getRequest().toByteArray());
                }
            }
        });
        sendToComparer.add(comparerRequest);
        JMenuItem comparerResponse = new JMenuItem(new AbstractAction("Responses") {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                for (LogEntry entry : selectedEntries) {
                    if (entry.getStatus() == LogEntry.Status.PROCESSED) { //Do not add entries without a response
                        TidyBurp.montoya.comparer().sendToComparer(entry.getResponse().toByteArray());
                    }
                }
            }
        });
        sendToComparer.add(comparerResponse);
        this.add(sendToComparer);

        this.add(new Separator());

        JMenuItem removeItem = new JMenuItem(new AbstractAction("Remove " + selectedEntries.size() + " selected items") {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                //If we don't clear the selection, the table will select the next entry after row is deleted
                //This causes the request response viewer to change after each and slow the process.
                logTable.getSelectionModel().clearSelection();
                for (LogEntry entry : selectedEntries) {
                    logTableController.getLogTableModel().removeEntry(entry);
                }
            }
        });
        this.add(removeItem);
    }
}

// Adapted closely from LoggerPlusPlus/logview/SingleLogEntryMenu.java
package org.example;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.scanner.Crawl;
import burp.api.montoya.scanner.CrawlConfiguration;
import org.example.logtable.LogTable;
import org.example.logtable.LogTableController;
import org.example.tabfeature.Tab;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class SingleLogEntryMenu extends JPopupMenu {

    public SingleLogEntryMenu(final LogTableController logTableController, final LogEntry entry) {
        final LogTable logTable = logTableController.getLogTable();

        JMenuItem sendToRepeater = new JMenuItem(new AbstractAction("Send to Repeater") {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                TidyBurp.montoya.repeater().sendToRepeater(entry.getRequest());
            }
        });
        this.add(sendToRepeater);

        JMenuItem sendToIntruder = new JMenuItem(new AbstractAction("Send to Intruder") {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                TidyBurp.montoya.intruder().sendToIntruder(entry.getRequest());
            }
        });
        this.add(sendToIntruder);

        JMenu sendToComparer = new JMenu("Send to Comparer");
        JMenuItem comparerRequest = new JMenuItem(new AbstractAction("Request") {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                TidyBurp.montoya.comparer().sendToComparer(entry.getRequest().toByteArray());
            }
        });
        sendToComparer.add(comparerRequest);
        JMenuItem comparerResponse = new JMenuItem(new AbstractAction("Response") {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                TidyBurp.montoya.comparer().sendToComparer(entry.getResponse().toByteArray());
            }
        });
        sendToComparer.add(comparerResponse);
        this.add(sendToComparer);

        this.add(new JPopupMenu.Separator());

        List<Tab> allTabs = logTableController.getTabController().getModel().getAllTabs();
        JMenu sendToTab = new JMenu("Send to Tab");

        for (Tab tab : allTabs) {
            JMenuItem sendToTabItem = new JMenuItem(new AbstractAction(tab.getName()) {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    logTableController.getTabController().getModel().addEntryToTab(tab, entry);
                }
            });
            sendToTab.add(sendToTabItem);
        }

        if (!allTabs.isEmpty()) {
            this.add(sendToTab);
            this.add(new JPopupMenu.Separator());
        }

        // Doesn't remove item but hides it from Dashboard view
        JMenuItem hideItem = new JMenuItem(new AbstractAction("Hide item") {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                entry.addTag("Hidden");
                logTableController.getLogTableModel().fireTableDataChanged();
            }
        });

        // Doesn't remove item but hides it from non-Dashboard tab view
        JMenuItem removeFromTab = new JMenuItem(new AbstractAction("Remove from Tab") {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                Tab currTab = logTableController.getTabController().getView().getCurrentTab();
                logTableController.getTabController().getModel().removeEntryFromTab(currTab, entry);
            }
        });
        // Remove item from tab only makes sense when a non-Dashboard tab is selected
        if (!logTableController.getTabController().getView().getCurrentTab().getName().equals("Dashboard")) {
            this.add(removeFromTab);
        } else { // if we're on the Dashboard, give them the "Hide item" option instead
            this.add(hideItem);
        }


        JMenuItem deleteItem = new JMenuItem(new AbstractAction("Delete item") {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                logTableController.getLogTableModel().removeEntry(entry);
            }
        });
        this.add(deleteItem);
    }
}
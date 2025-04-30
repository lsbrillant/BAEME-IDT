package org.example;

import org.example.logtable.LogTable;
import org.example.logtable.LogTableController;
import com.coreyd97.BurpExtenderUtilities.HistoryField;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;

public class FilterFeature extends JPanel {
    JLabel searchLabel;
    JLabel filterLabel;
    JPanel activeFilterPanel; // TODO: for now, there will only be one filter at a time. eventually, want both active filter and selected filter panels
    HistoryField searchField;
    JComboBox filterField;
    LogTableController logTableController;

    FilterFeature(LogTableController logTableController) {
        super();
        this.logTableController = logTableController;
        this.searchLabel = new JLabel("Search:" );
        this.filterLabel = new JLabel("Filter:" );
        this.activeFilterPanel = new JPanel();

        // Create search field UI element and add interactivity to it
        this.searchField = new HistoryField(null, "filterHistory", 15);
        searchField.getEditor().getEditorComponent().addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyChar() == KeyEvent.VK_ENTER) {
                    // Update only when pressing enter after typing
                    setFilter((String) searchField.getEditor().getItem());
                    searchField.getRootPane().requestFocus(true); // ?
                }
            }
        });

        // Create filter field UI element and add interactivity to it
        ArrayList<String> columnNames = new ArrayList<>();
        columnNames.add("Header");
        columnNames.add("Code"); // TODO: remove once this just becomes a column itself
        columnNames.addAll(Arrays.asList(logTableController.getLogTableModel().getColumnNames()));
        this.filterField = new JComboBox<>(columnNames.toArray());
        filterField.addActionListener(e -> {
            JComboBox<String> cb = (JComboBox<String>) e.getSource();
            String s = cb != null ? (String) cb.getSelectedItem() : null;
            this.activeFilterPanel.removeAll(); // clear active filter panel
            if (s != null && s.equals("Code")) {
                this.activeFilterPanel.add(new JLabel("matches"));
                JTextField codeField = new JTextField(5);
                // TODO: add enter key listener to execute filter method
                this.activeFilterPanel.add(codeField);
            } else if (s != null && s.equals("Header")) {
                String[] filterOptionsArr = {"present", "matches"};
                JComboBox<String> filterOptionsField = new JComboBox<>(filterOptionsArr);
                // TODO: above needs an action listener to check which one is checked
                // if present, create JCheckBox with JLabel that says "inverted" or smth
                // if absent, create JTextField like above
                this.activeFilterPanel.add(filterOptionsField);
            }
            this.activeFilterPanel.revalidate();
            this.activeFilterPanel.repaint();
            // above two lines force the JPanel to update
        });

        this.add(filterLabel);
        this.add(filterField);
        this.add(activeFilterPanel);
        this.add(searchLabel);
        this.add(searchField);
    }

    public void clearFilter() {
        this.logTableController.getLogTable().setFilter("");
        formatFilter("");
    }

    public void formatFilter(String string) {
        if (!string.equalsIgnoreCase("")) {
            ((HistoryField.HistoryComboModel) searchField.getModel()).addToHistory(string);
            searchField.setSelectedItem(string);
        } else {
            searchField.setSelectedItem(null);
        }
    }

    public void setFilter(String filterString) {
        if (filterString == null || filterString.equals("") || filterString.matches(" +")) {
            clearFilter();
        } else {
            formatFilter(filterString);
            logTableController.getLogTable().setFilter(filterString);
        }

        LogTable logTable = logTableController.getLogTable();
        if (logTable.getSelectedRow() != -1) {
            logTable.scrollRectToVisible(logTable.getCellRect(logTable.getSelectedRow(), 0, true));
        }
    }
}

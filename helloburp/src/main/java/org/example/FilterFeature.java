package org.example;

import org.example.logtable.LogTable;
import org.example.logtable.LogTableController;
import com.coreyd97.BurpExtenderUtilities.HistoryField;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;

import java.io.File;
import java.io.IOException;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;


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
                    setFilter("search", (String) searchField.getEditor().getItem());
                    searchField.getRootPane().requestFocus(true); // ?
                }
            }
        });

        // Create filter field UI element and add interactivity to it
        ArrayList<String> columnNames = new ArrayList<>();
        columnNames.add(""); // default to empty
        columnNames.add("Header");
        columnNames.add("Code"); // TODO: remove once this just becomes a column itself
        columnNames.addAll(Arrays.asList(logTableController.getLogTableModel().getColumnNames()));
        this.filterField = new JComboBox<>(columnNames.toArray());
        filterField.addActionListener(e -> {
            String s = (String) filterField.getSelectedItem();
            this.activeFilterPanel.removeAll(); // clear active filter panel
            this.activeFilterPanel.repaint();
            this.activeFilterPanel.revalidate();
            if (s != null && s.equals("Code")) {
                this.activeFilterPanel.add(new JLabel("matches"));
                JTextField codeField = new JTextField(5);

                codeField.addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyPressed(KeyEvent e) {
                        if (e.getKeyChar() == KeyEvent.VK_ENTER) {
                            setFilter("code", codeField.getText());
                        }
                    }
                });
                this.activeFilterPanel.add(codeField);
            } else if (s != null && s.equals("Header")) {
                JTextField headerField = new JTextField(15);
                JTextField headerFilterField = new JTextField(5);
                headerFilterField.addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyPressed(KeyEvent e) {
                        if (e.getKeyChar() == KeyEvent.VK_ENTER) {
                            setFilter("header", headerField.getText(), headerFilterField.getText(),
                                    false);
                        }
                    }
                });
                String[] filterOptionsArr = {"", "present", "not present", "matches"};
                JComboBox<String> filterOptionsField = new JComboBox<>(filterOptionsArr);

                headerField.addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyPressed(KeyEvent e) {
                        if (e.getKeyChar() == KeyEvent.VK_ENTER) {
                            if (headerField.getText().isEmpty()) {
                                clearFilter("header");
                                return;
                            }
                            String selectedFilterOption = filterOptionsField.getSelectedItem().toString(); // maybe unsafe?
                            if (selectedFilterOption.contains("present")) {
                                setFilter("header", headerField.getText(), null,
                                        selectedFilterOption.equals("not present"));
                            } else if (selectedFilterOption.equals("matches")) {
                                setFilter("header", headerField.getText(), headerFilterField.getText(),
                                        false);
                            }
                        }
                    }
                });

                filterOptionsField.addActionListener(e1 -> {
                    String selectedFilterOption = (String) filterOptionsField.getSelectedItem();

                    if (selectedFilterOption != null && selectedFilterOption.equals("matches")) {
                        this.activeFilterPanel.add(headerFilterField);
                    } else if (selectedFilterOption != null && !selectedFilterOption.isEmpty()) {
                        if (this.activeFilterPanel.getComponent(this.activeFilterPanel.getComponentCount() - 1) instanceof JTextField) {
                            this.activeFilterPanel.remove(this.activeFilterPanel.getComponentCount() - 1);
                        }
                        // headerName, filterString, inverted
                        setFilter("header", headerField.getText(), null,
                                selectedFilterOption.equals("not present"));
                    }
                    this.activeFilterPanel.revalidate();
                    this.activeFilterPanel.repaint();
                });
                this.activeFilterPanel.add(headerField);
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

        JButton exportButton = new JButton("Export Visible Requests");
        exportButton.setToolTipText("Download all currently visible rows as a CSV file");
        exportButton.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                File file = chooser.getSelectedFile();
                if (!file.getName().toLowerCase().endsWith(".csv")) {
                    file = new File(file.getAbsolutePath() + ".csv");
                }

                try {
                    ExportFeature.exportVisibleRowsToCSV(logTableController.getLogTable(), file);
                    JOptionPane.showMessageDialog(this, "Export successful!");
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "Export failed: " + ex.getMessage());
                }
            }
        });
        this.add(exportButton);
    }

    public void clearFilter(String filterType) {
        switch (filterType) {
            case "search": {
                this.logTableController.getLogTable().setFilter("");
                formatFilter("");
            }
            case "code": {
                this.logTableController.getLogTable().setCodeFilter("");
            }
            case "header": {
                this.logTableController.getLogTable().setHeaderFilter("", "", false);
            }
        }

    }

    public void formatFilter(String string) {
        if (!string.equalsIgnoreCase("")) {
            ((HistoryField.HistoryComboModel) searchField.getModel()).addToHistory(string);
            searchField.setSelectedItem(string);
        } else {
            searchField.setSelectedItem(null);
        }
    }

    public void setFilter(String filterType, Object... args) {
        switch (filterType) {
            case "search": {
                String filterString = (String) args[0];
                if (filterString == null || filterString.equals("") || filterString.matches(" +")) {
                    clearFilter(filterType);
                } else {
                    formatFilter(filterString);
                    logTableController.getLogTable().setFilter(filterString);
                }

                LogTable logTable = logTableController.getLogTable();
                if (logTable.getSelectedRow() != -1) {
                    logTable.scrollRectToVisible(logTable.getCellRect(logTable.getSelectedRow(), 0, true));
                }
                break;
            }
            case "header": {
                if (args[1] == null) { // if filterString is null (this means we're looking for existence of a header)
                    logTableController.getLogTable().setHeaderFilter((String) args[0], null, (boolean) args[2]);
                } else if (((String) args[1]).equals("")) {
                    clearFilter(filterType);
                } else { // filterString not null (we're looking for if a header matches something)
                    logTableController.getLogTable().setHeaderFilter((String) args[0], (String) args[1], (boolean) args[2]);
                }
                LogTable logTable = logTableController.getLogTable();
                if (logTable.getSelectedRow() != -1) {
                    logTable.scrollRectToVisible(logTable.getCellRect(logTable.getSelectedRow(), 0, true));
                }
                break;
            }
            case "code": {
                String codeFilterString = (String) args[0];
                if (codeFilterString.equals("") || codeFilterString.matches(" +")) {
                    clearFilter(filterType);
                } else {
                    logTableController.getLogTable().setCodeFilter(codeFilterString);
                }
                LogTable logTable = logTableController.getLogTable();
                if (logTable.getSelectedRow() != -1) {
                    logTable.scrollRectToVisible(logTable.getCellRect(logTable.getSelectedRow(), 0, true));
                }
                break;
            }
            default: {
                break;
            }
        }
    }
}

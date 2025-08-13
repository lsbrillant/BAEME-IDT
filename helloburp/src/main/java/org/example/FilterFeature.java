package org.example;

import org.example.logtable.LogTable;
import org.example.logtable.LogTableController;
import com.coreyd97.BurpExtenderUtilities.HistoryField;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
    JCheckBox regexEnabledBox;
    JCheckBox loggingEnabledBox;
    LogTableController logTableController;

    FilterFeature(LogTableController logTableController) {
        super();
        this.logTableController = logTableController;
        this.searchLabel = new JLabel("Search:" );
        this.filterLabel = new JLabel("Filter:" );
        this.activeFilterPanel = new JPanel();
        this.loggingEnabledBox = new JCheckBox("Logging Enabled");

        // Set whether user wants all requests to be logged or not
        // We will get this from the LogTableController in LogHttpHandler
        this.logTableController.setLoggingEnabled(loggingEnabledBox.isSelected());
        this.loggingEnabledBox.addActionListener(e -> {
            this.logTableController.setLoggingEnabled(loggingEnabledBox.isSelected());
        });

        this.regexEnabledBox = new JCheckBox("Regex");
        this.regexEnabledBox.setToolTipText("Enable/disable regex filtering GLOBALLY");
//        regexEnabledBox.setPreferredSize(new Dimension(22, 22));
//        try {
//            Image img = ImageIO.read(getClass().getResource("/images/regex.png"));
//            img = img.getScaledInstance(16, 16, Image.SCALE_SMOOTH);
//            regexEnabledBox.setIcon(new ImageIcon(img));
//            regexEnabledBox.setToolTipText("Enable/disable regex GLOBALLY");
//        } catch (Exception ex) {
//            throw new RuntimeException(ex);
//        }
        regexEnabledBox.addActionListener(e -> {
            logTableController.setRegexEnabled(regexEnabledBox.isSelected());
//            if (logTableController.isRegexEnabled()) { // add icon if regex is enabled
//                try {
//                    Image img = ImageIO.read(getClass().getResource("/images/regex.png"));
//                    img.getScaledInstance(16, 16, Image.SCALE_SMOOTH);
//                    regexEnabledBox.setIcon(new ImageIcon(img));
//                    regexEnabledBox.setToolTipText("blah");
//                } catch (IOException ex) {
//                    throw new RuntimeException(ex);
//                }
//            } else { // remove icon if regex is disabled
//                try {
//                    Image img = ImageIO.read(getClass().getResource("/images/blank.png"));
//                    img.getScaledInstance(16, 16, Image.SCALE_SMOOTH);
//                    regexEnabledBox.setIcon(new ImageIcon(img));
//                    regexEnabledBox.setToolTipText("bleh");
//                } catch (IOException ex) {
//                    throw new RuntimeException(ex);
//                }
//            }
        });

        // Create search field UI element and add interactivity to it
        this.searchField = new HistoryField(null, "filterHistory", 15);
        searchField.setPreferredSize(new Dimension(200, searchField.getPreferredSize().height));
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

                JButton codeButton = new JButton("Enter");
                codeButton.addActionListener(e1 -> setFilter("code", codeField.getText()));
                this.activeFilterPanel.add(codeField);
                this.activeFilterPanel.add(codeButton);
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

                JButton headerButton = new JButton("Enter");
                headerButton.addActionListener(e1 -> {
                    String selectedFilter = (String) filterOptionsField.getSelectedItem();
                    if (selectedFilter == null) {
                        return;
                    }
                    if (selectedFilter.contains("present")) {
                        setFilter("header", headerField.getText(), null, selectedFilter.equals("not present"));
                    } else if (selectedFilter.equals("matches")) {
                        setFilter("header", headerField.getText(), headerFilterField.getText(), false);
                    }
                });

                filterOptionsField.addActionListener(e1 -> {
                    String selectedFilterOption = (String) filterOptionsField.getSelectedItem();

                    if (selectedFilterOption != null && selectedFilterOption.equals("matches")) {
                        if (this.activeFilterPanel.getComponent(this.activeFilterPanel.getComponentCount() - 1) instanceof JButton) {
                            this.activeFilterPanel.remove(this.activeFilterPanel.getComponentCount() - 1);
                        }
                        this.activeFilterPanel.add(headerFilterField);
                        this.activeFilterPanel.add(headerButton);
                    } else if (selectedFilterOption != null && !selectedFilterOption.isEmpty()) {
                        if (this.activeFilterPanel.getComponent(this.activeFilterPanel.getComponentCount() - 2) instanceof JTextField) {
                            this.activeFilterPanel.remove(this.activeFilterPanel.getComponentCount() - 2);
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
                this.activeFilterPanel.add(headerButton);
            } else if (s != null && s.equals("Tags")) {
                this.activeFilterPanel.add(new JLabel("has tag"));
                JTextField tagField = new JTextField(10);

                tagField.addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyPressed(KeyEvent e) {
                        if (e.getKeyChar() == KeyEvent.VK_ENTER) {
                            setFilter("tags", tagField.getText());
                        }
                    }
                });

                JButton tagButton = new JButton("Enter");
                tagButton.addActionListener(e1 -> setFilter("tags", tagField.getText()));
                this.activeFilterPanel.add(tagField);
                this.activeFilterPanel.add(tagButton);
            } else if (s != null && !s.isEmpty()) {
                this.activeFilterPanel.add(new JLabel("matches"));
                JTextField genericField = new JTextField(10);
                int colIndex = Arrays.asList(logTableController.getLogTableModel().getColumnNames()).indexOf(s);

                genericField.addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyPressed(KeyEvent e) {
                        if (e.getKeyChar() == KeyEvent.VK_ENTER) {
                            setFilter("generic", genericField.getText(), colIndex);
                        }
                    }
                });
                JButton genericButton = new JButton("Enter");
                genericButton.addActionListener(e1 -> setFilter("generic", genericField.getText(), colIndex));

                this.activeFilterPanel.add(genericField);
                this.activeFilterPanel.add(genericButton);
            }
            this.activeFilterPanel.revalidate();
            this.activeFilterPanel.repaint();
            this.activeFilterPanel.requestFocus(); // fixes small UI bug where top bar doesn't resize until user interacts with it
            // above two lines force the JPanel to update
        });

        // UNIVERSAL BUTTON - save the states
//        JButton enterButton = new JButton("Enter");
//        enterButton.addActionListener(e -> {
//            setFilter("search", (String) searchField.getEditor().getItem());
//            searchField.getRootPane().requestFocus(true);
//        });

        JButton resetButton = new JButton("Reset");
        resetButton.addActionListener(e1 -> {
            this.activeFilterPanel.removeAll();
            this.activeFilterPanel.revalidate();
            this.activeFilterPanel.repaint();
            this.filterField.setSelectedItem(null);
            logTableController.getLogTable().setFilter(null); // resets to filter that still ignores entries with Hidden tag
            logTableController.getTabController().getView().resetFilter(); // set selected tab to Dashboard
        });
        this.add(resetButton);

        this.add(filterLabel);
        this.add(filterField);
        this.add(activeFilterPanel);
        this.add(searchLabel);
        this.add(searchField);
        this.add(regexEnabledBox);
        this.add(loggingEnabledBox);

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
                break;
            }
            case "code": {
                this.logTableController.getLogTable().setCodeFilter("");
                break;
            }
            case "header": {
                this.logTableController.getLogTable().setHeaderFilter("", "", false);
                break;
            }
            case "generic": {
                this.logTableController.getLogTable().setGenericFilter("",  0);
                break;
            }
            case "tags": {
                this.logTableController.getLogTable().setTagFilter("");
                break;
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
            } case "tags": {
                String tagFilter = (String) args[0];
                if (tagFilter == null || tagFilter.isBlank()) {
                    clearFilter(filterType);
                } else {
                    logTableController.getLogTable().setTagFilter(tagFilter);
                }

                LogTable logTable = logTableController.getLogTable();
                if (logTable.getSelectedRow() != -1) {
                    logTable.scrollRectToVisible(logTable.getCellRect(logTable.getSelectedRow(), 0, true));
                }
                break;
            }
            case "generic": {
                String filterString = (String) args[0];
                int columnIndex = (int) args[1];
                if (filterString.equals("") || filterString.matches(" +")) {
                    clearFilter(filterType);
                } else {
                    logTableController.getLogTable().setGenericFilter(filterString, columnIndex);
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

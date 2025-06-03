package org.example.logtable;

import burp.api.montoya.http.message.HttpHeader;
import org.example.LogEntry;
import org.example.requestviewer.RequestViewerController;

import javax.swing.*;
import javax.swing.table.TableRowSorter;
import java.util.List;

import lombok.Getter;

public class LogTable extends JTable {
    private LogTableController controller;
    @Getter
    private TableRowSorter<LogTableModel> sorter;
    @Getter
    private String currentFilterName;

    LogTable(LogTableController controller) {
        super(controller.getLogTableModel());
        this.controller = controller;
        this.sorter = new TableRowSorter<>(controller.getLogTableModel());
        this.setRowSorter(sorter);
        this.currentFilterName = "";

        this.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        this.getSelectionModel().addListSelectionListener(e -> {
            if(e.getValueIsAdjusting()) return;
            RequestViewerController requestViewerController = this.controller.getRequestViewerController();
            int selectedRow = getSelectedRow();
            if(selectedRow == -1){
                requestViewerController.setDisplayedEntity(null);
            }else {
                // Use a relative instead of an absolute index (This prevents an issue when a filter is set)
                LogEntry logEntry = controller.getLogTableModel().getData().get(convertRowIndexToModel(selectedRow));
                if (logEntry != null) {
                    requestViewerController.setDisplayedEntity(logEntry);
                }
            }
        });
        for (int i = 0; i < getColumnCount(); i++) {
            if ("Tags".equalsIgnoreCase(getColumnName(i))) {
                getColumnModel().getColumn(i).setCellRenderer(new MultiTagCellRenderer());
                break;
            }
        }

        for (int i = 0; i < getColumnCount(); i++) {
            String colName = getColumnName(i).toLowerCase();
            int width;

            switch (colName) {
                case "number" -> width = 40;
                case "method" -> width = 60;
                case "host" -> width = 150;
                case "url" -> width = 200;
                case "params" -> width = 120;
                case "code" -> width = 50;
                case "length" -> width = 70;
                case "mime" -> width = 80;
                case "extension" -> width = 80;
                case "title" -> width = 100;
                case "edited" -> width = 70;
                case "tls" -> width = 50;
                case "ip" -> width = 100;
                case "cookies" -> width = 80;
                case "time" -> width = 100;
                case "status" -> width = 100;
                case "tags" -> width = 350;
                default -> width = 100;
            }
            getColumnModel().getColumn(i).setPreferredWidth(width);
        }
    }

    public void setFilter(String filter) {
        // TODO: verify the regex is valid via Pattern.compile in FilterFeature
        if (filter == null) {
            this.sorter.setRowFilter(null);
        } else {
            this.currentFilterName = "Search: " + filter;
            this.sorter.setRowFilter(RowFilter.regexFilter(filter));
            ((JScrollPane) this.getParent().getParent()).getVerticalScrollBar().setValue(0); // maybe don't need this?
        }
    }

    public void setHeaderFilter(String headerName, String filterString, boolean inverted) {
        if (headerName.isEmpty() && filterString != null && filterString.isEmpty()) {
            this.sorter.setRowFilter(null);
            return;
        }

        RowFilter<LogTableModel, Integer> headerFilter = new RowFilter<LogTableModel, Integer>() {
            public boolean include(Entry<? extends LogTableModel, ? extends Integer> entry) {
                LogEntry e = entry.getModel().getRow(entry.getIdentifier());
                List<HttpHeader> headers = e.getResponseHeaders();
                if (filterString == null) {
                    for (HttpHeader header : headers) {
                        if (header.name().equalsIgnoreCase(headerName)) {
                            return !inverted;
                        }
                    }
                    return inverted;
                } else {
                    for (HttpHeader header : headers) {
                        if (header.name().equalsIgnoreCase(headerName) && header.value().matches(filterString)) {
                            return true;
                        }
                    }
                    return false;
                }
            }
        };
        // Set the automatic name for the tab
        if (filterString == null) {
            this.currentFilterName = "Header: " + headerName + (inverted ? " doesn't exist" : " exists");
        } else {
            this.currentFilterName = "Header: " + headerName + " matches " + filterString;
        }

        this.sorter.setRowFilter(headerFilter);
    }

    public void setCodeFilter(String filterString) {
        if (filterString.isEmpty()) {
            this.sorter.setRowFilter(null);
            return;
        }
        RowFilter<LogTableModel, Integer> codeFilter = new RowFilter<LogTableModel, Integer>() {
            public boolean include(Entry<? extends LogTableModel, ? extends Integer> entry) {
                LogEntry e = entry.getModel().getRow(entry.getIdentifier());
                if (filterString.toLowerCase().matches("^[12345]xx")) { // of form 1xx, 2xx, etc.
                    return e.getResponseStatus().toString().matches(filterString.charAt(0) + ".*");
                }
                return e.getResponseStatus().toString().matches(filterString);
            }
        };
        this.currentFilterName = "Code matches " + filterString;
        this.sorter.setRowFilter(codeFilter);
    }

    public void setGenericFilter(String filterString, int columnIndex) {
        if (filterString.isEmpty()) {
            this.sorter.setRowFilter(null);
            return;
        }
        RowFilter<LogTableModel, Integer> genericFilter = new RowFilter<LogTableModel, Integer>() {
            public boolean include(Entry<? extends LogTableModel, ? extends Integer> entry) {
                LogEntry e = entry.getModel().getRow(entry.getIdentifier());
                String data = e.getData().get(columnIndex).toString();
                return data.matches(filterString);
            }
        };
        this.currentFilterName = getModel().getColumnName(columnIndex) + " matches " + filterString;
        this.sorter.setRowFilter(genericFilter);
    }

    public void setTagFilter(String tagFilter) {
        if (tagFilter == null || tagFilter.isBlank()) {
            this.sorter.setRowFilter(null);
            return;
        }

        RowFilter<LogTableModel, Integer> tagFilterLogic = new RowFilter<>() {
            public boolean include(Entry<? extends LogTableModel, ? extends Integer> entry) {
                LogEntry logEntry = entry.getModel().getRow(entry.getIdentifier());
                List<String> tags = logEntry.getTags();
                return tags.stream().anyMatch(tag -> tag.equalsIgnoreCase(tagFilter));
            }
        };

        this.currentFilterName = "Tags: " + tagFilter;
        this.sorter.setRowFilter(tagFilterLogic);
    }
}

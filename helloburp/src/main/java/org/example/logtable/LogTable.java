package org.example.logtable;

import org.example.LogEntry;
import org.example.requestviewer.RequestViewerController;

import javax.swing.*;
import javax.swing.table.TableRowSorter;

public class LogTable extends JTable {
    private LogTableController controller;
    private TableRowSorter<LogTableModel> sorter;

    LogTable(LogTableController controller) {
        super(controller.getLogTableModel());
        this.controller = controller;
        this.sorter = new TableRowSorter<>(controller.getLogTableModel());
        this.setRowSorter(sorter);

        this.getSelectionModel().addListSelectionListener(e -> {
            if(e.getValueIsAdjusting()) return;
            RequestViewerController requestViewerController = this.controller.getRequestViewerController();
            int selectedRow = getSelectedRow();
            if(selectedRow == -1){
                requestViewerController.setDisplayedEntity(null);
            }else {
                // Use a relative instead of an absolute index (This prevents an issue when a filter is set)
                LogEntry logEntry = controller.getLogTableModel().getRow(selectedRow);
                if (logEntry != null) {
                    requestViewerController.setDisplayedEntity(logEntry);
                }
            }
        });
    }

    public void setFilter(String filter) {
        // TODO: verify that filter is valid regex first (?)
        if (filter == null) {
            this.sorter.setRowFilter(null);
        } else {
            this.sorter.setRowFilter(RowFilter.regexFilter(filter));
            ((JScrollPane) this.getParent().getParent()).getVerticalScrollBar().setValue(0); // maybe don't need this?
        }
    }
}

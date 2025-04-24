package org.example.logtable;

import lombok.Setter;
import org.example.requestviewer.RequestViewerController;

public class LogTableController {
    private final LogTable logTable;
    private final LogTableModel logTableModel;
    @Setter
    private RequestViewerController requestViewerController;

    public LogTableController() {
        this.logTableModel = new LogTableModel(this);
        this.logTable = new LogTable(this);
    }

    public LogTableModel getLogTableModel() {
        return this.logTableModel;
    }

    public LogTable getLogTable() {
        return this.logTable;
    }

    public RequestViewerController getRequestViewerController() {
        return this.requestViewerController;
    }

    public void tagSelectedRequest(String tag) {
        int selectedRow = logTable.getSelectedRow();
        if (selectedRow >= 0) {
            int modelRow = logTable.convertRowIndexToModel(selectedRow);
            logTableModel.setValueAt(tag, modelRow, logTableModel.getColumnCount() - 1);
        }
    }
}

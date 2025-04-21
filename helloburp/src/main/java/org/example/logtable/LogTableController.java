package org.example.logtable;

import lombok.Setter;
import org.example.requestviewer.RequestViewerController;

public class LogTableController {
    private final LogTable logTable;
    private final LogTableModel logTableModel;
    private final LogTableColumnModel logTableColumnModel;
    private final TableHeader tableHeader;
    @Setter
    private RequestViewerController requestViewerController;

    public LogTableController() {
        this.logTableColumnModel = new LogTableColumnModel();
        this.logTableModel = new LogTableModel(this, logTableColumnModel);
        this.logTable = new LogTable(this);
//        this.logTable.setAutoResizeMode(JTable.AUTO);
        this.tableHeader = new TableHeader(this);
    }

    public TableHeader getTableHeader() {
        return this.tableHeader;
    }

    public LogTableModel getLogTableModel() {
        return this.logTableModel;
    }

    public LogTableColumnModel getLogTableColumnModel() {
        return this.logTableColumnModel;
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

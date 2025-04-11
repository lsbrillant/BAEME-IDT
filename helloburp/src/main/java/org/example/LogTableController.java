package org.example;

public class LogTableController {
    private final LogTable logTable;
    private final LogTableModel logTableModel;
    private final LogTableColumnModel logTableColumnModel;
    private final TableHeader tableHeader;
    private RequestViewerController requestViewerController;

    LogTableController() {
        this.logTableColumnModel = new LogTableColumnModel();
        this.logTableModel = new LogTableModel(this, logTableColumnModel);
        this.logTable = new LogTable(this);
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

    public void setRequestViewerController(RequestViewerController requestViewerController) {
        this.requestViewerController = requestViewerController;
    }

    public RequestViewerController getRequestViewerController() {
        return this.requestViewerController;
    }
}

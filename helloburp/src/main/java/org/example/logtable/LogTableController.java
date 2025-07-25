package org.example.logtable;

import lombok.Getter;
import lombok.Setter;
import org.example.LogEntry;
import org.example.requestviewer.RequestViewerController;
import org.example.tabfeature.TabController;

import java.util.List;


public class LogTableController {
    private final LogTable logTable;
    private final LogTableModel logTableModel;
    @Setter
    private RequestViewerController requestViewerController;
    @Setter
    @Getter
    private boolean isLoggingEnabled;
    @Setter
    @Getter
    private TabController tabController;

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
        int[] selectedRows = logTable.getSelectedRows();
        if (selectedRows.length == 0) return;

        int tagColumnIndex = getTagsColumnIndex();

        for (int viewRow : selectedRows) {
            int modelRow = logTable.convertRowIndexToModel(viewRow);
            LogEntry entry = logTableModel.getRow(modelRow);

            if (entry.hasTag(tag)) {
                entry.removeTag(tag);
            } else {
                entry.addTag(tag);
            }

            logTableModel.fireTableCellUpdated(modelRow, tagColumnIndex);
        }
    }

    public int getTagsColumnIndex() {
        for (int i = 0; i < logTableModel.getColumnCount(); i++) {
            if ("Tags".equalsIgnoreCase(logTableModel.getColumnName(i))) {
                return i;
            }
        }
        return -1;
    }
}

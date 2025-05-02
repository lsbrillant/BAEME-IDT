package org.example.logtable;

import lombok.Setter;
import org.example.LogEntry;
import org.example.requestviewer.RequestViewerController;
import java.util.List;


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
            int tagColumnIndex = getTagsColumnIndex();

            LogEntry entry = logTableModel.getRow(modelRow);
            List<String> currentTags = entry.getTags();

            if (currentTags.contains(tag)) {
                entry.removeTag(tag);
            } else {
                entry.addTag(tag);
            }

            logTableModel.fireTableCellUpdated(modelRow, tagColumnIndex);
        }
    }

    private int getTagsColumnIndex() {
        for (int i = 0; i < logTableModel.getColumnCount(); i++) {
            if ("Tags".equalsIgnoreCase(logTableModel.getColumnName(i))) {
                return i;
            }
        }
        return -1;
    }
}

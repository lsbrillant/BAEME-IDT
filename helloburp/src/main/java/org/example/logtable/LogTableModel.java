package org.example.logtable;

import org.example.LogEntry;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

public class LogTableModel extends AbstractTableModel {
    private final LogTableController controller;
    @Getter
    private final String[] columnNames;
    private final List<LogEntry> entries;

    public LogTableModel(LogTableController controller) {
        this.entries = new ArrayList<>(); // Collections.synchronizedList?
        this.columnNames = new String[]{"Number", "Host", "Method", "Tag", "URL", "Params", "Edited", "Code", "Length", "MIME", "Extension", "Title", "TLS", "IP", "Cookies", "Time", "Tags"}; // TODO: add all of them here
        this.controller = controller;
//        this.entries.add(Arrays.asList(1, "http://dhruviscool.com", "GET"));
//        this.entries.add(Arrays.asList(2, "http://sheaminiscool.com", "POST"));
    }

    public int getRowCount() {
        return this.entries.size();
    }

    public int getColumnCount() {
        return this.columnNames.length;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public Object getValueAt(int row, int column) {
        if (column == 0) { // request number column
            return row + 1;
        }

        String columnName = columnNames[column];
        if ("Tags".equalsIgnoreCase(columnName)) {
            return this.entries.get(row).getTags();
        }

        return this.entries.get(row).getData().get(column - 1);
//        return String.format("%d %d", row, column);
    }

    public List<LogEntry> getData() {
        return this.entries;
    }

    public LogEntry getRow(int row) {
        return this.entries.get(row);
    }

    public void addEntry(LogEntry entry) {
        this.entries.add(entry);
        fireTableRowsInserted(this.entries.size() - 1, this.entries.size() - 1);
    }

    @Override
    public void setValueAt(Object aValue, int row, int column) {
        String columnName = columnNames[column];
        if ("Tags".equalsIgnoreCase(columnName) && aValue instanceof String tag) {
            LogEntry entry = this.entries.get(row);
            entry.addTag(tag);
            fireTableCellUpdated(row, column);
        }
    }

    // TODO: a lot more methods (as relevant), see LoggerPlusPlus as reference
}

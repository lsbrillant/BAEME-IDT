package org.example.logtable;

import org.example.LogEntry;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class LogTableModel extends AbstractTableModel {
    private final LogTableController controller;
    private final LogTableColumnModel columnModel;
    private final List<LogEntry> entries;

    public LogTableModel(LogTableController controller, LogTableColumnModel columnModel) {
        this.entries = new ArrayList<>(); // Collections.synchronizedList?
        this.columnModel = columnModel;
        this.controller = controller;
//        this.entries.add(Arrays.asList(1, "http://dhruviscool.com", "GET"));
//        this.entries.add(Arrays.asList(2, "http://sheaminiscool.com", "POST"));
    }

    public int getRowCount() {
        return this.entries.size();
    }

    public int getColumnCount() {
        return this.columnModel.getColumnCount();
    }

    public Object getValueAt(int row, int column) {
        if (column == 0) { // request number column
            return row + 1;
        }
        return this.entries.get(row).getData().get(column - 1);
    }

    public List<LogEntry> getData() {
        return this.entries;
    }

    public LogEntry getRow(int row) {
        return this.entries.get(row);
    }

    public String getColumnName(int column) {
        //return this.columnModel.getColumn(column).getName();
        String[] names = {"Number", "Host", "Method"};
        return names[column];
    }

    public boolean isCellEditable(int row, int column) {
        return !((LogTableColumn) this.columnModel.getColumn(column)).isReadOnly();
    }

    public void addEntry(LogEntry entry) {
        this.entries.add(entry);
        fireTableRowsInserted(this.entries.size() - 1, this.entries.size() - 1);
    }

    // TODO: isCellEditable (should come from the LogTableColumnModel)
    // TODO: a lot more methods (as relevant), see LoggerPlusPlus as reference
}

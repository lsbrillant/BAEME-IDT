package org.example.logtable;

import org.example.LogEntry;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import lombok.Getter;

public class LogTableModel extends AbstractTableModel {
    private final LogTableController controller;
    @Getter
    private final String[] columnNames;
    private final List<LogEntry> entries;
    private int currNumber; // tracks what should be the number for the next request

    public LogTableModel(LogTableController controller) {
        this.entries = Collections.synchronizedList(new ArrayList<>());
        this.columnNames = new String[]{"Number", "Host", "Method", "URL", "Param count", "Edited", "Code", "Length", "MIME", "Extension", "Title", "TLS", "Request Source", "Cookies", "Time", "Tags"};
        this.controller = controller;
        this.currNumber = 1;
    }

    public void removeEntry(LogEntry entry) {
        int index = entries.indexOf(entry);
        entries.remove(entry);
        fireTableRowsDeleted(index, index);
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
        LogEntry entry = entries.get(row);
//        if (column == 0) { // request number column
//            return row + 1;
//        }

        String columnName = columnNames[column];
        if ("Tags".equalsIgnoreCase(columnName)) {
            return this.entries.get(row).getTags();
        }

        if ("Param Count".equalsIgnoreCase(columnName)) {
            return entry.getParamCount();
        }
        List<Object> data = entry.getData();
        return data.get(column);
//
//        if (column == getColumnCount() - 1) {
//            return entry.getTags();
//           // return this.entries.get(row).getFirstTag();
//        }
//        return data.get(column - 1);
//

//        return this.entries.get(row).getData().get(column - 1);
////        return String.format("%d %d", row, column);
//        if (column == 0) {
//            return row + 1;
//        } else {
//            return data.get(column - 1);
//        }
    }

    public List<LogEntry> getData() {
        return this.entries;
    }

    public LogEntry getRow(int row) {
        return this.entries.get(row);
    }

    public void addEntry(LogEntry entry) {
        entry.setNumber(currNumber++);
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

    @Override
    public Class<?> getColumnClass(int column) {
        List<String> integerColumns = Arrays.asList(new String[]{"Number", "Param Count", "Code", "Length"});
        if (integerColumns.contains(getColumnName(column))) {
            return Integer.class;
        }
        return String.class;
    }

    // TODO: a lot more methods (as relevant), see LoggerPlusPlus as reference
}

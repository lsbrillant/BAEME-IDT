package org.example;

import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;
import java.util.*;

public class LogTableColumnModel extends DefaultTableColumnModel {
    private List<LogTableColumn> columns;

    public LogTableColumnModel() {
        super();
        this.columns = new ArrayList<>();
        // Load the default columns (those that are seen in the Proxy view)
        // this kinda sucks but i can't figure out a better way rn
        // ideally, we load this in from some external config file
        List<List<Object>> defaultColumns = new ArrayList<>();
        int order = 1;
        defaultColumns.add(Arrays.asList("Number", order++, true, "", "#"));
        defaultColumns.add(Arrays.asList("Host", order++, true, "", "Host"));
        defaultColumns.add(Arrays.asList("Method", order++, true, "", "Method"));
//        defaultColumns.add(Arrays.asList("URL", order++, true, "", "URL"));
//        defaultColumns.add(Arrays.asList("Params", order++, true, "", "Params"));
//        defaultColumns.add(Arrays.asList("Edited", order++, false, "", "Edited"));
//        defaultColumns.add(Arrays.asList("Code", order++, true, "", "Status code"));
//        defaultColumns.add(Arrays.asList("Length", order++, true, "", "Length"));
//        defaultColumns.add(Arrays.asList("MIME", order++, true, "", "MIME type"));
//        defaultColumns.add(Arrays.asList("Extension", order++, true, "", "Extension"));
//        defaultColumns.add(Arrays.asList("Title", order++, true, "", "Title"));
//        defaultColumns.add(Arrays.asList("TLS", order++, true, "", "TLS"));
//        defaultColumns.add(Arrays.asList("IP", order++, true, "", "IP"));
//        defaultColumns.add(Arrays.asList("Cookies", order++, true, "", "Cookies"));
//        defaultColumns.add(Arrays.asList("Time", order++, true, "", "Time"));
//        defaultColumns.add(Arrays.asList("Tags", order, true, "", "Tags"));
        for (int i = 0; i < defaultColumns.size(); i++) {
            LogTableColumn column = new LogTableColumn();
            column.setName((String) defaultColumns.get(i).get(0));
            column.setOrder((int) defaultColumns.get(i).get(1));
            column.setReadOnly((boolean) defaultColumns.get(i).get(2));
            column.setDescription((String) defaultColumns.get(i).get(3));
            column.setVisibleName((String) defaultColumns.get(i).get(4));
            this.columns.add(column);
        }
    }

    public TableColumn getColumn(int columnIndex) {
        return this.columns.get(columnIndex);
    }

    public int getColumnCount() {
        return this.columns.size();
    }

    public void addColumn(TableColumn column) {
        int newPosition = -1;
        for (int i = 0; i < this.columns.size(); i++) {
            if (this.columns.get(i).compareTo((LogTableColumn) column) > 0) {
                newPosition = i;
                break;
            }
        }
        if (newPosition == -1) {
            newPosition = this.columns.size();
        }
        this.columns.add(newPosition, (LogTableColumn) column);
        // Adjust model indices of subsequent columns
        for (int i = newPosition; i < this.columns.size(); i++) {
            this.columns.get(i).setOrder(i);
        }
    }

    public void removeColumn(TableColumn column) {
        int columnIndex = this.columns.indexOf((LogTableColumn) column);
        if (columnIndex != -1) {
            this.columns.remove(columnIndex);
            // Adjust all subsequent column model indices
            for (int i = columnIndex; i < this.columns.size(); i++) {
                this.columns.get(i).setOrder(i);
            }
        }
    }
}
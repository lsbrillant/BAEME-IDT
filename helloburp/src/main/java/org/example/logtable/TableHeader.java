package org.example.logtable;

import javax.swing.table.JTableHeader;

public class TableHeader extends JTableHeader {
    TableHeader(LogTableController controller) {
        super(controller.getLogTableColumnModel());
        this.setTable(controller.getLogTable());
    }
}

package org.example;

import burp.api.montoya.proxy.ProxyHttpRequestResponse;

import javax.swing.*;
import javax.swing.event.TableColumnModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.util.Enumeration;
import java.util.List;

public class AnnotationsTab {
    private JPanel panel;

    public AnnotationsTab(List<List<Object>> data) {
        panel = new JPanel();
        AbstractTableModel dataModel = new AbstractTableModel() {
            public int getColumnCount() { return getRowCount() == 0 ? 0 : data.getFirst().size(); }
            public int getRowCount() { return data.size();}
            public Object getValueAt(int row, int col) { return data.get(row).get(col); }
        };
        // need a TableColumnModel here?

        if (data.isEmpty()) {
            panel.add(new JLabel("No requests yet!"));
        } else {
            panel.add(new JTable(dataModel));
        }
    }

    public String name() {
        return "Organizer++";
    }

    public JPanel getPanel() {
        return panel;
    }
}

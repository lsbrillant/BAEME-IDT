package org.example;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.proxy.ProxyHttpRequestResponse;
import com.coreyd97.BurpExtenderUtilities.VariableViewPanel;

import javax.lang.model.element.VariableElement;
import javax.swing.*;
import javax.swing.event.TableColumnModelListener;
import javax.swing.table.*;
import java.awt.*;
import java.util.Enumeration;
import java.util.List;

public class AnnotationsTab {
    private JPanel panel;
    private VariableViewPanel tableViewerSplitPanel;
    private RequestViewerPanel requestViewerPanel;

    public AnnotationsTab(List<List<Object>> dat, LogTableController logTableController, MontoyaApi montoya) {
        this.panel = new JPanel();
        Object[][] data = {
                {1, "John", 25},
                {2, "Sarah", 30},
                {3, "Mike", 35}
        };
//        AbstractTableModel dataModel = new AbstractTableModel() {
//            public int getColumnCount() { return getRowCount() == 0 ? 0 : data.getFirst().size(); }
//            public int getRowCount() { return data.size();}
//            public Object getValueAt(int row, int col) { return data.get(row).get(col); }
//        };
//        String[] columnNames = {"ID", "Name", "Age"};
//        DefaultTableModel model = new DefaultTableModel(data, columnNames);
//        JTable logTable = new JTable(model);

//        LogTableController logTableController = new LogTableController();
        LogTable logTable = logTableController.getLogTable();
        JScrollPane scrollPane = new JScrollPane(logTable, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        RequestViewerController requestViewerController = new RequestViewerController(montoya);
        logTableController.setRequestViewerController(requestViewerController); // hack to avoid creating LogViewController

        VariableViewPanel bottomPanel = new VariableViewPanel(null, null,
                requestViewerController.getRequestViewerPanel(), "Request/Response",
                new JScrollPane(new JLabel("TAG STUFF GOES HERE!")), "Tags",
                VariableViewPanel.View.HORIZONTAL);

        tableViewerSplitPanel = new VariableViewPanel(null, null, scrollPane, "Log Table",
                bottomPanel, "Bottom", VariableViewPanel.View.VERTICAL);
        this.panel.add(tableViewerSplitPanel);
    }

    public String name() {
        return "TidyBurp";
    }

    public JPanel getPanel() {
        return this.panel;
    }
}

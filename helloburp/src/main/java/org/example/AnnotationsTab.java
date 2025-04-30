package org.example;

import burp.api.montoya.MontoyaApi;
import com.coreyd97.BurpExtenderUtilities.VariableViewPanel;
import lombok.Getter;
import org.example.logtable.LogTable;
import org.example.logtable.LogTableController;
import org.example.requestviewer.RequestViewerController;
import org.example.requestviewer.RequestViewerPanel;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class AnnotationsTab {
    @Getter
    private JPanel panel;
    private VariableViewPanel tableViewerSplitPanel;
    private RequestViewerPanel requestViewerPanel;

    public AnnotationsTab(LogTableController logTableController, MontoyaApi montoya) {
        this.panel = new JPanel(new BorderLayout());
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

        LogTable logTable = logTableController.getLogTable();
        JScrollPane scrollPane = new JScrollPane(logTable, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        RequestViewerController requestViewerController = new RequestViewerController(montoya);
        logTableController.setRequestViewerController(requestViewerController); // hack to avoid creating LogViewController

        VariableViewPanel bottomPanel = new VariableViewPanel(null, "msgviewlayout",
                requestViewerController.getRequestViewerPanel(), "Request/Response",
                new TaggingFeature(logTableController), "Tags",
                VariableViewPanel.View.HORIZONTAL);

        tableViewerSplitPanel = new VariableViewPanel(null, "msgviewlayout", scrollPane, "Log Table",
                bottomPanel, "Bottom", VariableViewPanel.View.VERTICAL);
        this.panel.add(new FilterFeature(logTableController), BorderLayout.NORTH);
        this.panel.add(tableViewerSplitPanel, BorderLayout.CENTER);
    }

    public String name() {
        return "TidyBurp";
    }
}

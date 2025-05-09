package org.example;

import burp.api.montoya.MontoyaApi;
import com.coreyd97.BurpExtenderUtilities.VariableViewPanel;
import lombok.Getter;
import org.example.logtable.LogTable;
import org.example.logtable.LogTableController;
import org.example.requestviewer.RequestViewerController;
import org.example.requestviewer.RequestViewerPanel;
import org.example.tabfeature.TabController;

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

        LogTable logTable = logTableController.getLogTable();
        JScrollPane scrollPane = new JScrollPane(logTable, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED); // TODO: change column width defaults

        RequestViewerController requestViewerController = new RequestViewerController(montoya);
        logTableController.setRequestViewerController(requestViewerController); // hack to avoid creating LogViewController

        TabController tabController = new TabController(logTableController);

        VariableViewPanel bottomPanel = new VariableViewPanel(null, "msgviewlayout",
                requestViewerController.getRequestViewerPanel(), "Request/Response",
                new TaggingFeature(logTableController), "Tags",
                VariableViewPanel.View.HORIZONTAL);

        VariableViewPanel centerPanel = new VariableViewPanel(null, "msgviewlayout",
                tabController.getView().getSidePanel(), "Tab Side Panel", scrollPane, "Log Table",
                VariableViewPanel.View.HORIZONTAL);
        ((JSplitPane) centerPanel.getComponent(0)).setResizeWeight((double) 0.1F); // hack to make table take up most of the space

        tableViewerSplitPanel = new VariableViewPanel(null, "msgviewlayout", centerPanel,
                "Center Panel", bottomPanel, "Bottom Panel", VariableViewPanel.View.VERTICAL);

        VariableViewPanel aboveTablePanel = new VariableViewPanel(null, "msgviewlayout",
                new FilterFeature(logTableController), "Filter Feature", tabController.getView().getTopPanel(),
                "Active Tabs Panel", VariableViewPanel.View.VERTICAL);

        this.panel.add(aboveTablePanel, BorderLayout.NORTH);
//        this.panel.add(new FilterFeature(logTableController), BorderLayout.NORTH);
//        this.panel.add(tabController.getView().getTopPanel(), BorderLayout.NORTH);
//        this.panel.add(tabController.getView().getSidePanel(), BorderLayout.WEST);
        this.panel.add(tableViewerSplitPanel, BorderLayout.CENTER);
    }

    public String name() {
        return "TidyBurp";
    }
}

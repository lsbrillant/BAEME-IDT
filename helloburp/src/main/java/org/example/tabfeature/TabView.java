package org.example.tabfeature;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;

import lombok.Getter;
import org.example.logtable.LogTableModel;

import java.awt.*;

public class TabView {
    private TabController controller;
    @Getter
    private JPanel topPanel;
    @Getter
    private JPanel sidePanel;

    TabView(TabController controller) {
        this.controller = controller;
        this.topPanel = new JPanel(new BorderLayout());
        this.sidePanel = new JPanel(new BorderLayout());
        buildTopPanel();
        buildSidePanel();
    }

    private void buildTopPanel() {
        JTable topTable = new JTable(controller.getModel().getTopPanelModel());
        topTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        topTable.setTableHeader(null);
        topTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JButton newButton = new JButton("Create Tab");
        newButton.addActionListener(e -> {
            RowFilter<?, ?> f = controller.getLogTableController().getLogTable().getSorter().getRowFilter();
            if (f != null) {
                // TODO: make there be a popup so you can set the name if you want (look at JR's tab code for this)
                Tab newTab = new Tab((RowFilter<LogTableModel, Integer>) f, "New Tab");
                controller.getModel().addTab(newTab, "side");
            }
        });
        this.topPanel.add(newButton, BorderLayout.WEST);

        JScrollPane topScrollPane = new JScrollPane(topTable);
        topScrollPane.setPreferredSize(new Dimension(topScrollPane.getPreferredSize().width, topTable.getRowHeight()));
        this.topPanel.add(topScrollPane);
        // TODO: list last N opened tabs
    }

    private void buildSidePanel() {
        JTable sideTable = new JTable(controller.getModel().getSidePanelModel());
//        sideTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        sideTable.setTableHeader(null);
        sideTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        sideTable.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            int selectedRow = sideTable.convertRowIndexToModel(sideTable.getSelectedRow());
            if (selectedRow < 0) return;
            Tab tab = controller.getModel().getTab(selectedRow);
            controller.getLogTableController().getLogTable().getSorter().setRowFilter(null); // clears any existing filter
            controller.getLogTableController().getLogTable().getSorter().setRowFilter(tab.getFilter());
            controller.getModel().addTab(tab, "top"); // add tab to top
        });

        JScrollPane sideScrollPane = new JScrollPane(sideTable);
        sideScrollPane.setBorder(BorderFactory.createEmptyBorder());

        this.sidePanel.add(sideTable);
//        sideScrollPane.setPreferredSize(new Dimension(sideScrollPane.getParent().getPreferredSize().width / 2,
//                sideScrollPane.getParent().getPreferredSize().height));
        // TODO: GET RID OF ANNOYING PADDING TO THE RIGHT OF TABLE

        // TODO: show dashboard tab above the table
        // TODO: list ALL tabs below this
    }
}

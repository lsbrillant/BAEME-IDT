package org.example.tabfeature;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;

import lombok.Getter;
import org.example.TidyBurp;
import org.example.logtable.LogTable;
import org.example.logtable.LogTableModel;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.stream.IntStream;

public class TabView {
    private TabController controller;
    @Getter
    private JPanel topPanel;
    @Getter
    private JPanel sidePanel;
    private JTable topTable;
    private JTable sideTable;

    TabView(TabController controller) {
        this.controller = controller;
        this.topPanel = new JPanel(new BorderLayout());
        this.sidePanel = new JPanel(new BorderLayout());
        buildTopPanel();
        buildSidePanel();
    }

    private void buildTopPanel() {
        this.topTable = new JTable(controller.getModel().getTopPanelModel());
        topTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        topTable.setTableHeader(null);
        topTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        topTable.setCellSelectionEnabled(true);

//        topTable.getSelectionModel().addListSelectionListener(e -> {
//            if (e.getValueIsAdjusting()) return;
//            int selectedColumn = topTable.convertColumnIndexToModel(topTable.getSelectedColumn());
//            if (selectedColumn < 0) return;
//            Tab tab = (Tab) controller.getModel().getTopPanelModel().getValueAt(0, selectedColumn);
//            controller.getLogTableController().getLogTable().getSorter().setRowFilter(tab.getFilter());
//            this.sideTable.clearSelection();
//        });

        JButton newButton = new JButton("Create Tab");
        newButton.addActionListener(e -> {
            RowFilter<?, ?> f = controller.getLogTableController().getLogTable().getSorter().getRowFilter();
            Tab newTab;
            if (f != null) {
                String input = JOptionPane.showInputDialog(this.topPanel, "Enter tab name:",
                        this.controller.getLogTableController().getLogTable().getCurrentFilterName());
                if (input != null && !input.trim().isEmpty()) {
                    newTab = new Tab((RowFilter<LogTableModel, Integer>) f, input);
                    controller.getModel().addTab(newTab, "side");
                } else if (input != null) {
                    newTab = new Tab((RowFilter<LogTableModel, Integer>) f,
                            this.controller.getLogTableController().getLogTable().getCurrentFilterName());
                    controller.getModel().addTab(newTab, "side");
                }
            } else {
                f = RowFilter.regexFilter("$^"); // empty, won't return any matches
                String input = JOptionPane.showInputDialog(this.topPanel, "Enter tab name:");
                newTab = new Tab((RowFilter<LogTableModel, Integer>) f, input);
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
        this.sideTable = new JTable(controller.getModel().getSidePanelModel());
//        sideTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        sideTable.setTableHeader(null);
        sideTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        sideTable.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            int selectedRow = sideTable.convertRowIndexToModel(sideTable.getSelectedRow());
            if (selectedRow < 0) return;
            Tab tab = controller.getModel().getTab(selectedRow);
            controller.getLogTableController().getLogTable().getSorter().setRowFilter(tab.getFilter());
            controller.getModel().addTab(tab, "top"); // add tab to top
        });
        sideTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                onMouseEvent(e);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                onMouseEvent(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                onMouseEvent(e);
            }

            private void onMouseEvent(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    Point p = e.getPoint();
                    int rowAtPoint = sideTable.rowAtPoint(p);
                    if (rowAtPoint == -1 || rowAtPoint == 0) return; // don't allow right click on Dashboard
                    if (sideTable.getSelectedRow() != rowAtPoint) {
                        // We right-clicked an unselected row. Set it as the selected row and update our selected
                        sideTable.setRowSelectionInterval(rowAtPoint, rowAtPoint);
                    }
                    int tabIndex = sideTable.convertRowIndexToModel(rowAtPoint);

                    JPopupMenu popupMenu = new JPopupMenu();
                    JMenuItem renameTab = new JMenuItem(new AbstractAction("Rename Tab") {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            String currName = controller.getModel().getTab(tabIndex).getName();
                            String input = JOptionPane.showInputDialog("Enter new name:", currName);
                            if (input != null && !input.trim().isEmpty()) {
                                controller.getModel().renameTab(tabIndex, input);
                                controller.getModel().getSidePanelModel().fireTableCellUpdated(rowAtPoint, 0);
                            }
                        }
                    });
                    JMenuItem deleteTab = new JMenuItem(new AbstractAction("Delete Tab") {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            controller.getModel().removeTab(controller.getModel().getTab(tabIndex), "side");
                            controller.getModel().getSidePanelModel().fireTableRowsDeleted(rowAtPoint, rowAtPoint);
                        }
                    });

                    // TODO: view/edit filter for a tab
                    popupMenu.add(renameTab);
                    popupMenu.add(deleteTab);
                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });

        JScrollPane sideScrollPane = new JScrollPane(sideTable);
        sideScrollPane.setBorder(BorderFactory.createEmptyBorder());

        this.sidePanel.add(sideTable);
//        sideScrollPane.setPreferredSize(new Dimension(sideScrollPane.getParent().getPreferredSize().width / 2,
//                sideScrollPane.getParent().getPreferredSize().height));
        // TODO: GET RID OF ANNOYING PADDING TO THE RIGHT OF TABLE
    }

    public Tab getCurrentTab() {
        return controller.getModel().getTab(this.sideTable.convertRowIndexToModel(this.sideTable.getSelectedRow()));
    }

    public void resetFilter() {
        this.sideTable.setRowSelectionInterval(0, 0);
    }

    public void refreshView() {
        // Programmatically switch view to Dashboard and then back to current tab
        int currTab = this.sideTable.getSelectedRow();
        resetFilter();
        this.sideTable.setRowSelectionInterval(currTab, currTab);
    }
}

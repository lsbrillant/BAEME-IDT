package org.example.tabfeature;

import lombok.Getter;
import lombok.Setter;
import org.example.LogEntry;
import org.example.logtable.LogTableModel;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.Getter;

public class TabModel {
    private TabController controller;
    @Getter
    private List<Tab> allTabs; // these get displayed in the side panel
    @Getter
    private List<Tab> activeTabs; // these get displayed in the top panel as a user opens them
    @Getter
    private AbstractTableModel topPanelModel;
    @Getter
    private AbstractTableModel sidePanelModel;

    TabModel(TabController controller) {
        this.controller = controller;
        this.allTabs = new ArrayList<>();
        this.activeTabs = new ArrayList<>();
        allTabs.add(new Tab(RowFilter.regexFilter(".*"), "Dashboard"));
//        allTabs.add(new Tab(RowFilter.regexFilter("204"), "dsfhusdhfiuhsduifhdsuifhuidshfuidshfiudshufi"));

        this.sidePanelModel = new AbstractTableModel() {
            @Override
            public int getRowCount() {
                return allTabs.size();
            }

            @Override
            public int getColumnCount() {
                return 1;
            }

            @Override
            public Object getValueAt(int rowIndex, int columnIndex) {
                return allTabs.get(rowIndex).getName();
            }
        };

        this.topPanelModel = new AbstractTableModel() {
            @Override
            public int getRowCount() {
                return 1;
            }

            @Override
            public int getColumnCount() {
                return activeTabs.size();
            }

            @Override
            public Object getValueAt(int rowIndex, int columnIndex) {
                return activeTabs.get(columnIndex).getName();
            }
        };
    }

    public Tab getTab(int index) {
        return this.allTabs.get(index);
    }

    public void addTab(Tab tab, String location) {
        switch (location) {
            case "side": {
                this.allTabs.add(tab);
                sidePanelModel.fireTableRowsUpdated(this.allTabs.size() - 1, this.allTabs.size() - 1);
                // also add to top when we first add to side
                this.activeTabs.add(tab);
                topPanelModel.fireTableStructureChanged();
            }
            case "top": {
                if (!this.activeTabs.contains(tab)) {
                    this.activeTabs.add(tab);
                    topPanelModel.fireTableStructureChanged();
                }
            }
        }
        // we do this to force the side panel to update and display the new tab
//        TabView v = this.controller.getView();
//        v.getSidePanel().revalidate();
//        v.getSidePanel().repaint();
    }

    public void removeTab(Tab tab, String location) {
        switch (location) {
            case "side": {
                this.allTabs.remove(tab);
                sidePanelModel.fireTableRowsUpdated(0, this.allTabs.size() - 1);
                if (this.activeTabs.contains(tab)) {
                    this.activeTabs.remove(tab);
                    topPanelModel.fireTableStructureChanged();
                }
            }
            case "top": {
                if (this.activeTabs.contains(tab)) {
                    this.activeTabs.remove(tab);
                    topPanelModel.fireTableStructureChanged();
                }
            }
        }
        // we do this to force the side panel to update and display the new tab
//        TabView v = this.controller.getView();
//        v.getSidePanel().revalidate();
//        v.getSidePanel().repaint();
    }

    public void addEntryToTab(Tab tab, LogEntry otherEntry) {
        RowFilter<LogTableModel, Integer> currFilter = tab.getFilter();
        RowFilter<LogTableModel, Integer> exactNumberFilter = new RowFilter<LogTableModel, Integer>() {
            @Override
            public boolean include(Entry<? extends LogTableModel, ? extends Integer> entry) {
                LogEntry e = entry.getModel().getRow(entry.getIdentifier());
                return e.getNumber() == otherEntry.getNumber();
            }
        };
        tab.setFilter(RowFilter.orFilter(Arrays.asList(currFilter, exactNumberFilter)));
    }
}

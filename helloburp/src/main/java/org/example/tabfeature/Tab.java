package org.example.tabfeature;

import lombok.Getter;
import lombok.Setter;
import org.example.logtable.LogTableModel;

import javax.swing.*;

public class Tab {
    @Getter
    @Setter
    private String name;
    @Getter
    @Setter
    private RowFilter<LogTableModel, Integer> filter;

    Tab(RowFilter<LogTableModel, Integer> filter, String name) {
        this.filter = filter;
        this.name = name;
    }



}

package org.example;

import javax.swing.*;

public class FilterFeature extends JPanel {
    JLabel filterLabel;
    JTextField searchField;
    public FilterFeature() {
        super();
        this.filterLabel = new JLabel("Filter:" );
        this.searchField = new JTextField(20);
        this.add(filterLabel);
        this.add(searchField);
    }
}

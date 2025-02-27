package org.example;

import javax.swing.*;

public class AnnotationsTab {
    private JPanel panel;

    public AnnotationsTab() {
        panel = new JPanel();
        panel.add(new JLabel("Awesomeness BAEME tab created by J.R. Lim and soon to be improved upon by Dhruv Ashok and Michaela Tran :D"));
    }

    public String name() {
        return "BAEME";
    }

    public JPanel getPanel() {
        return panel;
    }
}

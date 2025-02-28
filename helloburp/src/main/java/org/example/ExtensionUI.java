package org.example;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ExtensionUI {
    private JTextField hashField;
    private JButton saveHash;
    private JPanel ui;

    private MyHTTPHandler handler;

    public ExtensionUI() {
        saveHash.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                handler.setHash(hashField.getText());
            }
        });
    }

    public JPanel getUi() {
        return this.ui;
    }

    public void setHash(String hash) {
        this.hashField.setText(hash);
    }
}

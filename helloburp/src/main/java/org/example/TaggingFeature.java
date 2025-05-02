package org.example;

import org.example.logtable.LogTable;
import org.example.logtable.LogTableController;

import javax.swing.*;
import java.awt.*;

public class TaggingFeature extends JPanel {
    private final LogTableController logTableController;

    public TaggingFeature(LogTableController logTableController) {
        super();
        this.logTableController = logTableController;
        setLayout(new BorderLayout());

        JLabel instructions = new JLabel("Select a tag to attach to a request:");
        instructions.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        add(instructions, BorderLayout.NORTH);

        JPanel tagPanel = new JPanel(new GridLayout(2, 5, 5, 5)); // rows, cols, hgap, vgap
        tagPanel.setBorder(BorderFactory.createEmptyBorder(10, 5, 5, 5));

        // TODO: need to update tag names still
        String[] tags = {
                "Tag 1", "Tag 2", "Tag 3", "Tag 4", "Tag 5",
                "Tag 6", "Tag 7", "Tag 8", "Tag 9", "[Custom Tag]"
        };

        // TODO: temp colors for now, might update based on user feedback
        Color[] colors = {
                new Color(0xFFCDD2), new Color(0xC8E6C9), new Color(0xBBDEFB),
                new Color(0xFFF9C4), new Color(0xD1C4E9), new Color(0xB2EBF2),
                new Color(0xFFE0B2), new Color(0xDCEDC8), new Color(0xF8BBD0),
                new Color(0xFF0000)
        };

        for (int i = 0; i < tags.length; i++) {
            String tag = tags[i];
            JButton tagButton = new JButton(tag);
            tagButton.setBackground(colors[i]);
            tagButton.setOpaque(true);
            tagButton.setBorderPainted(false);
            if (i >= 9) {
                tagButton.addActionListener(e -> {
                    String input = JOptionPane.showInputDialog(this, "Enter a custom tag:", tag);
                    if (input != null && !input.trim().isEmpty()) {
                        logTableController.tagSelectedRequest(input.trim());
                    }
                });
            } else {
                tagButton.addActionListener(e -> {
                    logTableController.tagSelectedRequest(tag);
                });
            }
            tagPanel.add(tagButton);
        }

        add(tagPanel, BorderLayout.CENTER);
    }
}
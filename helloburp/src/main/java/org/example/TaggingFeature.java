package org.example;

import org.example.logtable.LogTableController;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedHashSet;
import java.util.Set;

public class TaggingFeature extends JPanel {
    private final LogTableController logTableController;
    private final JPanel tagPanel;
    private final Set<String> customTags = new LinkedHashSet<>();
    private final Color[] premadeColors = {
            new Color(0xBBDEFB), new Color(0xFFCDD2), new Color(0xC8E6C9)
    };
    private final String[] premadeTags = {"Baseline", "Retest", "Tested"}; // Still can update if tags don't seem relevant

    public TaggingFeature(LogTableController logTableController) {
        super();
        this.logTableController = logTableController;
        setLayout(new BorderLayout());

        JLabel instructions = new JLabel("Select a tag to attach to a request:");
        instructions.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        add(instructions, BorderLayout.NORTH);

        tagPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        tagPanel.setBorder(BorderFactory.createEmptyBorder(10, 5, 5, 5));

        addPremadeTags();
        addCustomTagButton();

        add(tagPanel, BorderLayout.CENTER);
    }

    private void addPremadeTags() {
        for (int i = 0; i < premadeTags.length; i++) {
            String tag = premadeTags[i];
            JButton tagButton = new JButton(tag);
            tagButton.setBackground(premadeColors[i]);
            tagButton.setOpaque(true);
            tagButton.setBorderPainted(false);
            tagButton.addActionListener(e -> logTableController.tagSelectedRequest(tag));
            tagPanel.add(tagButton);
        }
    }

    private void addCustomTagButton() {
        JButton customTagButton = new JButton("[Custom Tag]");
        customTagButton.setBackground(new Color(0xFFAB91));
        customTagButton.setOpaque(true);
        customTagButton.setBorderPainted(false);
        customTagButton.addActionListener(e -> {
            String input = JOptionPane.showInputDialog(this, "Enter a custom tag:");
            if (input != null && !input.trim().isEmpty()) {
                String tag = input.trim();
                if (!customTags.contains(tag)) {
                    customTags.add(tag);
                    JButton tagButton = new JButton(tag);
                    tagButton.setBackground(new Color(0xFF8A65)); // Deeper orange
                    tagButton.setOpaque(true);
                    tagButton.setBorderPainted(false);
                    tagButton.addActionListener(evt -> logTableController.tagSelectedRequest(tag));
                    tagPanel.add(tagButton, tagPanel.getComponentCount() - 1);
                    tagPanel.revalidate();
                    tagPanel.repaint();
                }
                logTableController.tagSelectedRequest(tag);
            }
        });
        tagPanel.add(customTagButton);
    }
}

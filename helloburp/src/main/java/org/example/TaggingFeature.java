package org.example;

import lombok.Getter;
import lombok.Setter;
import org.example.logtable.LogTableController;
import org.example.logtable.MultiTagCellRenderer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.stream.IntStream;

public class TaggingFeature extends JPanel {
    private final LogTableController logTableController;
    private final JPanel tagPanel;
    private final Set<String> customTags;
    private final Color[] premadeColors = {
            new Color(0xBBDEFB), new Color(0xFFCDD2), new Color(0xC8E6C9)
    };
    private final String[] premadeTags = {"Baseline", "Retest", "Tested"}; // Still can update if tags don't seem relevant

    public TaggingFeature(LogTableController logTableController) {
        super();
        this.logTableController = logTableController;
        setLayout(new BorderLayout());
        this.customTags = new HashSet<>();

        JLabel instructions = new JLabel("Select a tag to attach to a request:");
        instructions.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        add(instructions, BorderLayout.NORTH);

        tagPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        tagPanel.setBorder(BorderFactory.createEmptyBorder(10, 5, 5, 5));

        addCustomTagButton();
        addPremadeTags();

        add(tagPanel, BorderLayout.CENTER);
    }

    private void addPremadeTags() { // eventually, this will import tags by default from file or default to premade
        for (int i = 0; i < premadeTags.length; i++) {
            addTag(premadeTags[i], premadeColors[i]);
        }
    }

    private void addTag(String tagName, Color tagColor) {
        customTags.add(tagName);
        MultiTagCellRenderer.colorMap.put(tagName, tagColor);
        JButton tagButton = new JButton(tagName);
        tagButton.setBackground(tagColor);
        tagButton.setOpaque(true);
        tagButton.setBorderPainted(false);
        tagButton.setToolTipText("Right click to modify");
        tagButton.addActionListener(e -> logTableController.tagSelectedRequest(tagButton.getText()));
        // Configure right click actions
        tagButton.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    JPopupMenu popupMenu = new JPopupMenu();
                    JMenuItem renameTag = new JMenuItem(new AbstractAction("Rename tag") {
                        @Override
                        public void actionPerformed(ActionEvent actionEvent) {
                            String currName = ((JButton) e.getComponent()).getText();
                            String input = JOptionPane.showInputDialog("Enter new name:", currName);
                            if (input != null && !input.trim().isEmpty()) {
                                ((JButton) e.getComponent()).setText(input);
                                for (LogEntry entry : logTableController.getLogTableModel().getData()) {
                                    entry.renameTag(currName, input);
                                }
                                // Remove existing color mapping to old tag name and create mapping to new tag name
                                MultiTagCellRenderer.colorMap.put(input, MultiTagCellRenderer.colorMap.get(currName));
                                MultiTagCellRenderer.colorMap.remove(currName);
                            }
                        }
                    });
                    JMenuItem changeTagColor = new JMenuItem(new AbstractAction("Change tag color") {
                        @Override
                        public void actionPerformed(ActionEvent actionEvent) {
                            Color newColor = JColorChooser.showDialog(tagPanel, "Change tag color", tagButton.getBackground());
                            if (newColor != null) {
                                tagButton.setBackground(newColor);
                            }
                            MultiTagCellRenderer.colorMap.put(tagName, newColor);
                            logTableController.getLogTableModel().fireTableDataChanged();
                        }
                    });
                    JMenuItem deleteTag = new JMenuItem(new AbstractAction("Delete tag") {
                        @Override
                        public void actionPerformed(ActionEvent actionEvent) {
                            String tagName = ((JButton) e.getComponent()).getText();
                            // Remove tag from log table entries that have it
                            int tagColumnIndex = logTableController.getTagsColumnIndex();
                            int index = 0;
                            for (LogEntry entry : logTableController.getLogTableModel().getData()) {
                                if (entry.hasTag(tagName)) {
                                    entry.removeTag(tagName);
                                    logTableController.getLogTableModel().fireTableCellUpdated(index, tagColumnIndex);
                                }
                                index++;
                            }

                            // Remove tag from tag panel
                            customTags.remove(tagName);
                            MultiTagCellRenderer.colorMap.remove(tagName);
                            tagPanel.remove(e.getComponent());
                            tagPanel.revalidate();
                            tagPanel.repaint();
                        }
                    });
                    popupMenu.add(renameTag);
                    popupMenu.add(changeTagColor);
                    popupMenu.add(deleteTag);
                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
                    tagButton.getModel().setPressed(false);
                }
            }
        });
        tagPanel.add(tagButton, tagPanel.getComponentCount() - 1);
        tagPanel.repaint();
        tagPanel.revalidate();
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
                    Color tagColor = JColorChooser.showDialog(tagPanel, "Set tag color", tagPanel.getBackground());
                    addTag(tag, tagColor != null ? tagColor : new Color(0xFF8A65));
                }
                logTableController.tagSelectedRequest(tag);
            }
        });
        tagPanel.add(customTagButton);
    }
}

package org.example.logtable;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.List;

public class MultiTagCellRenderer extends JPanel implements TableCellRenderer {

    public MultiTagCellRenderer() {
        setOpaque(true);
        setLayout(new FlowLayout(FlowLayout.LEFT, 5, 3));
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus,
                                                   int row, int column) {
        removeAll();

        if (value instanceof List<?> tagList) {
            for (Object obj : tagList) {
                if (obj instanceof String tag) {
                    JLabel label = new JLabel(tag);
                    label.setOpaque(true);
                    label.setBackground(getColorForTag(tag));
                    label.setForeground(Color.BLACK);
                    label.setFont(label.getFont().deriveFont(Font.PLAIN, 12f));
                    label.setBorder(BorderFactory.createEmptyBorder(2, 6, 2, 6));
                    add(label);
                }
            }
        }

        setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
        return this;
    }

    private Color getColorForTag(String tag) {
        return switch (tag.toLowerCase()) {
            case "tag 1" -> new Color(0xFFCDD2);
            case "tag 2" -> new Color(0xC8E6C9);
            case "tag 3" -> new Color(0xBBDEFB);
            case "tag 4" -> new Color(0xFFF9C4);
            case "tag 5" -> new Color(0xD1C4E9);
            case "tag 6" -> new Color(0xB2EBF2);
            case "tag 7" -> new Color(0xFFE0B2);
            case "tag 8" -> new Color(0xDCEDC8);
            case "tag 9" -> new Color(0xF8BBD0);
            case "tag 10" -> new Color(0xC5CAE9);
            default -> new Color(0xFF0000); // default to dark red for custom tags (might change still)
        };
    }
}

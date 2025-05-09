package org.example;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class ExportFeature {
    public static void exportVisibleRowsToCSV(JTable table, File file) throws IOException {
        try (FileWriter fw = new FileWriter(file)) {
            TableModel model = table.getModel();

            for (int i = 0; i < model.getColumnCount(); i++) {
                fw.write(model.getColumnName(i));
                if (i < model.getColumnCount() - 1) fw.write(",");
            }
            fw.write("\n");

            for (int i = 0; i < table.getRowCount(); i++) {
                int modelRow = table.convertRowIndexToModel(i);
                for (int j = 0; j < model.getColumnCount(); j++) {
                    Object value = model.getValueAt(modelRow, j);
                    String cellValue;

                    if (value instanceof List) {
                        List<?> list = (List<?>) value;
                        cellValue = String.join(" | ", list.stream()
                                .map(Object::toString)
                                .map(s -> s.replace(",", ";").replace("\n", " ").replace("\r", " "))
                                .toList());
                    } else {
                        cellValue = value != null ? value.toString() : "";
                        cellValue = cellValue.replace(",", ";").replace("\n", " ").replace("\r", " ");
                    }

                    fw.write(cellValue);
                    if (j < model.getColumnCount() - 1) fw.write(",");
                }
                fw.write("\n");
            }

            fw.flush();
        }
    }
}

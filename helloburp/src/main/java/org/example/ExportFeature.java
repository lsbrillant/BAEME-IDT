package org.example;

import org.example.logtable.LogTableModel;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Base64;

public class ExportFeature {
    public static void exportVisibleRowsToCSV(JTable table, File file) throws IOException {
        try (FileWriter fw = new FileWriter(file)) {
            TableModel model = table.getModel();

            for (int i = 0; i < model.getColumnCount(); i++) {
                fw.write(model.getColumnName(i));
                fw.write(",");
            }
            fw.write("Request,Response"); // add CSV headers for request and response
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
                    fw.write(",");
                }
                // write the b64 encoded request
                LogEntry e = ((LogTableModel) model).getRow(modelRow);
                String encodedRequest = Base64.getEncoder().encodeToString(e.getRequestBytes());
                fw.write(encodedRequest);

                fw.write(",");

                // write the b64 encoded response
                String encodedResponse = Base64.getEncoder().encodeToString(e.getResponseBytes());
                fw.write(encodedResponse);
                fw.write("\n");
            }

            fw.flush();
        }
    }
}

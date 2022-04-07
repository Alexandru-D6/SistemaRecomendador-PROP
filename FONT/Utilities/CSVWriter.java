package Utilities;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class CSVWriter {
    private BufferedWriter writer;
    
    public void open(String filename) throws IOException {
        writer = new BufferedWriter(new FileWriter(filename));
    }
    
    public void close() throws IOException {
        writer.close();
    }
    
    public void write(String[] fields) throws IOException {
        for (int i = 0; i < fields.length; i++) {
            if (i > 0) {
                writer.write(",");
            }
            writer.write(process(fields[i]));
        }
        writer.write("\n");
    }
    
    private String process(String field) {
        // Insert quotes around the field if it contains a potentially problematic character
        if (field.contains("\"") || field.contains(",") || field.contains("\n")) {
            return "\"" + field.replace("\"", "\"\"") + "\"";
        }
        return field;
    }
}

package Utilities;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class ConfigFileWriter {
    private ArrayList<String> keys = new ArrayList<>();
    private ArrayList<String> values = new ArrayList<>();
    private String filename;
    
    public ConfigFileWriter(String filename) {
        this.filename = filename;
    }
    
    public void add(String key, String value) {
        keys.add(key);
        values.add(value);
    }
    
    public void addBlank() {
        keys.add(null);
        values.add(null);
    }
    
    public void write() throws IOException {        
        BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
        
        for (int i = 0; i < keys.size(); i++) {
            if (keys.get(i) != null)
                writer.write(keys.get(i) + "=" + values.get(i));
            writer.write("\n");
        }
        
        writer.close();
    }
}

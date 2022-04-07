package Utilities;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.TreeMap;

public class ConfigFileReader {
    private TreeMap <String, String> map = new TreeMap<>();
    
    public ConfigFileReader(String filename) throws FileNotFoundException {
        Scanner scanner = new Scanner(new File(filename));
        
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (line.length() == 0) continue;
            String[] parts = line.split("=");
            
            if (parts.length != 2) {
                throw new IllegalArgumentException("Invalid line in config file: " + line);
            }
            map.put(parts[0].trim(), parts[1].trim());
        }
        
        scanner.close();
    }
    
    // Returns the value of the given key, or null if the key does not exist
    public String get(String key) {
        return map.get(key);
    }
}

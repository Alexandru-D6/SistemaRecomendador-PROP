package Utilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;

import Exceptions.ImplementationError;


public class CSVReader {
    private BufferedReader reader = null;
    private String nextString;
    private final static String QUOTE_REPLACEMENT = "«";
    
    
    private void updateNextString() {
        try {
            // reader.readLine() returns null if the end of the file is reached
            nextString = reader.readLine();
        }
        catch (IOException e) {
            throw new ImplementationError("Error reading CSV file");
        }
    }
    
    private String[] readFile() {
        // nextString becomes the current input line
        String line = nextString;
        
        // Read the next line in order to know if EOF has been reached
        updateNextString();
        
        // Split the line into individual fields, limit is -1 (don't delete trailing empty fields)
        return line.split(",", -1);
    }
    
    public void open(String filename) throws FileNotFoundException {
        if (reader != null) {
            close();
        }
        Charset utf8 = Charset.forName("UTF-8");
        FileReader fr;
        try {
            fr = new FileReader(new File(filename), utf8);
        }
        catch (IOException e) {
            throw new ImplementationError("IOException while creating FileReader: " + e.getMessage());
        }
        reader = new BufferedReader(fr);
        updateNextString();
    }
    
    public void close() {
        try {
            reader.close();
        }
        catch (IOException e) {
            throw new ImplementationError("Error closing CSV file");
        }
    }
    
    public String[] readNext() {
        String[] tokens = readFile();
        return process(tokens);
    }
    
    public boolean endReached() {
        return nextString == null;
    }
    
    
    
    // Code below is a modified version of https://stackoverflow.com/questions/60790347/read-a-csv-that-has-double-quotes-with-comma-inside
    
    
    // @brief This function specifically deal with the issue of commas within the quotation marks
    // @detail it gets the index number of the 2 elements containing the quotation marks, then concats them all. It works with multiple quotation marks on the same line
    private String[] process(String[] data) {
        for (int i = 0; i < data.length; i++) {
            // Use QUOTE_REPLACEMENT to replace the TRUE quotation marks, so that the loop below does not get confused
            
            // Check edge case where the first 3 characters are "
            if (data[i].length() > 2 && data[i].substring(0, 3).equals("\"\"\"")) {
                data[i] = "\"" + QUOTE_REPLACEMENT + data[i].substring(3);
            }
            
            data[i] = data[i].replace("\"\"", QUOTE_REPLACEMENT);
        }
        
        int index1 = -1; //initialize the index of the first ", -1 for empty
        int index2 = 0;  //initialize the index of the second ", 0 for empty
        for (int i = 0; i < data.length; i++) {
            if (data[i].length() == 0)
                continue;
            
            if (String.valueOf(data[i].charAt(0)).equals("\"") && index1 == -1) { //if index1 is not empty and the first char of current element is "
                index1 = i; //set index1 to current index number
            }
            
            if (String.valueOf(data[i].charAt(data[i].length()-1)).equals("\"") && index1 != -1) { //if index1 is not empty and the last char of current element is "
                index2 = i; //set index2 to current index number
                multiconcat(index1, index2, data); //concat the elements between index1 and index2 
                data = multidelet(index1+1, index2, data); //delete the elements that were copied (index1+1:index2)
                i -= (index2-index1); //this is to reset the cursor back to index1 (could be replaced with i = index1)
                index1 = -1; //set index1 to empty

            }
        }
        
        // String with a newline in the middle
        if (index1 != -1) {
            data[data.length-1] += "\n";
            String[] extraTokens = readFile();
            String[] concat = new String[data.length + extraTokens.length];
            
            // Copy the first part of the array (old line)
            System.arraycopy(data, 0, concat, 0, data.length);
            // Copy the second part of the array (new line)
            System.arraycopy(extraTokens, 0, concat, data.length, extraTokens.length);
            
            return process(concat);
        }


        for (int i = 0; i < data.length; i++) {
            // Restore the TRUE quotation marks
            data[i] = data[i].replace(QUOTE_REPLACEMENT, "\"");
        }
        return data;
    }

    // @brief Copy all elements between index1 and index2 to index1, doesn't return anything
    private static void multiconcat(int index1, int index2, String[] data) {
        for (int i=index1+1; i<=index2; i++) {
            String temp = data[index1];
            if (temp.length() == 0 || temp.charAt(temp.length()-1) != '\n'){
                data[index1] += ",";
            }
            data[index1] += data[i];
        }
        data[index1] = data[index1].substring(1, data[index1].length()-1); //remove the quotation marks
    }

    // @brief Deletes the elements between index1+1 and index2 
    private static String[] multidelet(int index1, int index2, String[] data) {
        String[] newarr = new String[data.length-(index2-index1+1)];
        int n = 0;
        for (int i=0; i<data.length; i++) {
            if (index1 <= i && i <= index2) continue;
            newarr[n] = data[i];
            n++;
        }
        return newarr;

    }
}

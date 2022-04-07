package Domain.Value;

import java.util.ArrayList;
import Utilities.CSVReader;

public abstract class Attribute {
    private String name;
    private double weight = 1.0;
    
    // For createAttributes()
    private static enum AtrType {
        BOOLEAN, NUMERIC, CATEGORICAL
    }
    private static boolean isBool(String s) {
        // Boolean.parseBoolean does not throw an exception, check booleans manually
        String lower = s.toLowerCase();
        return lower.equals("true") || lower.equals("false");
    }
    private static boolean isNumeric(String s) {
        // If s is a numeric, returns true and sets parsedNum to the numeric value
        try {
            Double.parseDouble(s);
        }
        catch (NumberFormatException e) {
            return false;
        }
        return true;
    }
    
    
    
    public Attribute(String name) {
        this.name = name;
    }
    
    public abstract Value parseValue(String str);
    
    public String getName() {
        return name;
    }
    public double getWeight() {
        return weight;
    }
    public void setWeight(double weight) {
        this.weight = weight;
    }
    
    
    
    public static ArrayList<Attribute> parseAttributes(CSVReader reader) {
        // Read the header (name of each attribute)
        String[] attrNames = reader.readNext();
        // Store the type of each attribute
        ArrayList<AtrType> attrTypes = new ArrayList<>(attrNames.length);
        
        // Read the first line of values
        String[] values = reader.readNext();
        
        // Initialize attrTypes with a guess based on the first line
        for (String s : values) {
            if (isBool(s)) {
                attrTypes.add(AtrType.BOOLEAN);
            }
            else if (isNumeric(s)) {
                attrTypes.add(AtrType.NUMERIC);
            }
            else {
                attrTypes.add(AtrType.CATEGORICAL);
            }
        }
        
        // Read the rest of lines in order to validate the guesses (some numeric and boolean
        // attributes may become categorical if one of the values is not correct)
        while (!reader.endReached()) {
            values = reader.readNext();
            
            if (values.length != attrTypes.size()) {
                throw new IllegalArgumentException("The header size (" + attrTypes.size() + ") does not match the items in the data (" + values.length + ")");
            }
            
            for (int i = 0; i < values.length; i++) {
                String s = values[i];
                
                if (attrTypes.get(i) == AtrType.BOOLEAN && !isBool(s)) {
                    // Attribute was marked as boolean but a non-boolean value has been received
                    attrTypes.set(i, AtrType.CATEGORICAL);
                }
                else if (attrTypes.get(i) == AtrType.NUMERIC && !isNumeric(s)) {
                    // Attribute was marked as numeric but a non-numeric value has been received
                    attrTypes.set(i, AtrType.CATEGORICAL);
                }
            }
        }
        
        // Create the attributes
        ArrayList<Attribute> attrs = new ArrayList<>(attrNames.length);
        
        // Initialize the attributes
        for (int i = 0; i < values.length; i++) {
            AtrType type = attrTypes.get(i);
            String name = attrNames[i];
            
            if (type == AtrType.BOOLEAN) {
                attrs.add(new AttributeBoolean(name));
            }
            else if (type == AtrType.NUMERIC) {
                attrs.add(new AttributeNumeric(name));
            }
            else {
                attrs.add(new AttributeCategorical(name));
            }
        }
        
        return attrs;
    }
}

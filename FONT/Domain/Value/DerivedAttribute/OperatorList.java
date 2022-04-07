package Domain.Value.DerivedAttribute;

import java.util.ArrayList;

import Domain.Value.AttributeNumeric;

// Utility class for passing arrays of operators

public class OperatorList {
    public ArrayList<Operator> ops;
    public double defaultVal;
    public AttributeNumeric newAttribute;
    
    public OperatorList(ArrayList<Operator> ops, double defaultVal, AttributeNumeric newAttribute) {
        this.ops = ops;
        this.defaultVal = defaultVal;
        this.newAttribute = newAttribute;
    }
}

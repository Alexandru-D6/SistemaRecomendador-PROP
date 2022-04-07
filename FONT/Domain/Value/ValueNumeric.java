// Author: Pol Rivero

package Domain.Value;

import Domain.Value.DerivedAttribute.NumericOperator;
import Domain.Value.DerivedAttribute.Operator;
import Exceptions.ImplementationError;

public class ValueNumeric extends Value {
    
    double val;
    
    public ValueNumeric(Attribute attribute, double val) {
        super(attribute);
        
        if (attribute.getClass() != AttributeNumeric.class)
            throw new ImplementationError("Creating numeric value on a non-numeric attribute");
        
        this.val = val;
    }
    
    // Pre: other is a ValueNumeric
    protected double normalizedDistance(Value other) {
        double otherVal = ((ValueNumeric) other).val;
        AttributeNumeric atr = (AttributeNumeric) attribute;
        
        double delta = Math.abs(val - otherVal);
        double maximumDelta = atr.getMaxVal() - atr.getMinVal();
        
        if (maximumDelta == 0) {
            if (delta > 0) throw new ImplementationError("Invalid numeric delta");
            return 0.0;
        }
        
        return delta / maximumDelta;
    }
    
    protected Double evaluateOperator(Operator op) {
        NumericOperator cOp;
        try {
            cOp = (NumericOperator) op;
        }
        catch (ClassCastException e) {
            throw new ImplementationError("Trying to use a non-numeric operator on a numeric attribute");
        }
        return cOp.evaluate(val);
    }
    
    @Override
    public String toString() {
        // Check if stored value is actually an integer and print without .0
        int i = (int) val;
        if (i == val) return Integer.toString(i);
        return Double.toString(val);
    }
}

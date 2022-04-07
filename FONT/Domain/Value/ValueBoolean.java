// Author: Pol Rivero

package Domain.Value;

import Domain.Value.DerivedAttribute.Operator;
import Exceptions.ImplementationError;

public class ValueBoolean extends Value {
    
    boolean val;
    
    public ValueBoolean(Attribute attribute, boolean val) {
        super(attribute);
        
        if (attribute.getClass() != AttributeBoolean.class)
            throw new ImplementationError("Creating boolean value on a non-boolean attribute");
        
        this.val = val;
    }
    
    // Pre: other is a ValueBoolean
    protected double normalizedDistance(Value other) {        
        boolean otherVal = ((ValueBoolean) other).val;
        
        if (val == otherVal) return 0.0;
        return 1.0;
    }

    protected Double evaluateOperator(Operator op) {
        throw new ImplementationError("A boolean attribute cannot be derived!");
    }
    
    public String toString() {
        return val ? "true" : "false";
    }
}

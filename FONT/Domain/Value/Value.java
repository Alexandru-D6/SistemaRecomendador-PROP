// Author: Pol Rivero

package Domain.Value;

import Domain.Value.DerivedAttribute.Operator;
import Domain.Value.DerivedAttribute.OperatorList;
import Exceptions.ImplementationError;

public abstract class Value {
    protected boolean invalid = false;
    protected Attribute attribute;
    
    public Value(Attribute attribute) {
        this.attribute = attribute;
    }
    
    protected abstract double normalizedDistance(Value other);
    
    public double distance(Value other) {
        // Comparing values that belong to different attributes (ex: duration and year) doesn't make any sense
        // This also ensures that both values are of the same type
        if (this.attribute != other.attribute)
            throw new ImplementationError("Comparing values of different attributes");
        
        // If any of the values has been marked as invalid by the user, don't use them in the algorithms
        if (this.invalid || other.invalid)
            return Double.NaN;
        
        return normalizedDistance(other) * attribute.getWeight();
    }
    
    public Attribute getAttribute() {
        return attribute;
    }
    
    public boolean isInvalid() {
        return invalid;
    }
    public void setInvalid(boolean invalid) {
        this.invalid = invalid;
    }
    
    public ValueNumeric derivedValue(OperatorList list) {
        // If this value is invalid, the derived value will also invalid
        if (invalid) return invalidValue(list.newAttribute);
        
        // Keep trying operators until one works
        for (Operator op : list.ops) {
            // Call primitive in subclass
            Double result = evaluateOperator(op);
            if (result != null) {
                // Success! Construct new value and return it
                return constructResult(result, list.newAttribute);
            }
        }
        // If none of the operators worked, return the default value
        return constructResult(list.defaultVal, list.newAttribute);
    }
    
    public void checkCanBeDerived(OperatorList list) {
        // Error checking
        if (getClass() == ValueBoolean.class) {
            throw new RuntimeException("Cannot derive a value from a boolean");
        }
        if (getClass() != ValueNumeric.class) {
            if (list.defaultVal == Operator.SAME) {
                throw new RuntimeException("SAME can only be used when deriving numeric attributes");
            }
            for (Operator op : list.ops) {
                if (op.getResultIfTrue() == Operator.SAME) {
                    throw new RuntimeException("SAME can only be used when deriving numeric attributes");
                }
            }
        }
    }
    
    // Helper functions for derivedValue:
    private ValueNumeric constructResult(double result, AttributeNumeric newAttribute) {
        // If the result is SAME, the derived value is the same as the original value
        if (result == Operator.SAME) result = ((ValueNumeric) this).val;
        // If the result is INVALID, the derived value is invalid
        if (result == Operator.INVALID) return invalidValue(newAttribute);
        return newAttribute.createValue(result);
    }
    private ValueNumeric invalidValue(AttributeNumeric newAttribute) {
        final double NULL_VAL = 0.0;
        ValueNumeric val = newAttribute.createValue(NULL_VAL);
        val.invalid = true;
        return val;
    }
    
    protected abstract Double evaluateOperator(Operator op);
    
    
    @Override
    public abstract String toString();
}

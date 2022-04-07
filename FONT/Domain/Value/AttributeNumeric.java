// Author: Pol Rivero

package Domain.Value;

public class AttributeNumeric extends Attribute {
    private double maxVal, minVal;
    
    public AttributeNumeric(String name, double maxVal, double minVal) {
        super(name);
        this.maxVal = maxVal;
        this.minVal = minVal;
    }
    
    public AttributeNumeric(String name) {
        super(name);
        maxVal = Double.NEGATIVE_INFINITY;
        minVal = Double.POSITIVE_INFINITY;
    }
    
    public ValueNumeric parseValue(String str) {
        double val = Double.parseDouble(str);
        return createValue(val);
    }
    
    public ValueNumeric createValue(double val) {
        maxVal = Math.max(maxVal, val);
        minVal = Math.min(minVal, val);
        return new ValueNumeric(this, val);
    }
    
    
    public double getMaxVal() {
        return maxVal;
    }
    
    public double getMinVal() {
        return minVal;
    }
}

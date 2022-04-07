package Domain.Value.DerivedAttribute;

public abstract class Operator {
    // Arbitrary constants
    public static final double SAME = Double.NEGATIVE_INFINITY;
    public static final double INVALID = Double.POSITIVE_INFINITY;
    
    protected double resultIfTrue;
    
    public double getResultIfTrue() {
        return resultIfTrue;
    }
    
    public Operator(double resultIfTrue) {
        this.resultIfTrue = resultIfTrue;
    }
}

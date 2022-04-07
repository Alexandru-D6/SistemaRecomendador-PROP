package Domain.Value.DerivedAttribute;

public abstract class NumericOperator extends Operator {
    // userArg is the argument that the user entered
    protected double userArg;
    protected final double TOLERANCE = 0.0001;
    
    public NumericOperator(double userArg, double resultIfTrue) {
        super(resultIfTrue);
        this.userArg = userArg;
    }
        
    // arg comes from each value being evaluated
    public abstract Double evaluate(double arg);
}

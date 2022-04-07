package Domain.Value.DerivedAttribute;

public class OperatorGT extends NumericOperator {
    
    public OperatorGT(double argument, double resultIfTrue) {
        super(argument, resultIfTrue);
    }
    
    public Double evaluate(double arg) {
        // GT operator: true if the value is greater than the argument entered by the user
        if (arg > userArg) {
            return resultIfTrue;
        }
        return null;
    }
}

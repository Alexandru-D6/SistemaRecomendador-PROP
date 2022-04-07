package Domain.Value.DerivedAttribute;

public class OperatorGE extends NumericOperator {
    
    public OperatorGE(double argument, double resultIfTrue) {
        super(argument, resultIfTrue);
    }

    public Double evaluate(double arg) {
        // GE operator: true if the value is greater or equal than the argument entered by the user
        if (arg > userArg || Math.abs(arg - userArg) < TOLERANCE) {
            return resultIfTrue;
        }
        return null;
    }
}

package Domain.Value.DerivedAttribute;

public class OperatorLE extends NumericOperator {
    
    public OperatorLE(double argument, double resultIfTrue) {
        super(argument, resultIfTrue);
    }

    public Double evaluate(double arg) {
        // LE operator: true if the value is less or equal than the argument entered by the user
        if (arg < userArg || Math.abs(arg - userArg) < TOLERANCE) {
            return resultIfTrue;
        }
        return null;
    }
}

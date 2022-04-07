package Domain.Value.DerivedAttribute;

public class OperatorLT extends NumericOperator {
    
    public OperatorLT(double argument, double resultIfTrue) {
        super(argument, resultIfTrue);
    }

    public Double evaluate(double arg) {
        // LT operator: true if the value is less than the argument entered by the user
        if (arg < userArg) {
            return resultIfTrue;
        }
        return null;
    }
}

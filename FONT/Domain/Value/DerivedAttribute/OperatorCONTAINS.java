package Domain.Value.DerivedAttribute;

public class OperatorCONTAINS extends CategoricalOperator {
    
    public OperatorCONTAINS(String argument, double resultIfTrue) {
        // Tags are stored in lower case, so we need to convert the argument to lower case
        super(argument.toLowerCase(), resultIfTrue);
    }

    public Double evaluate(String[] arg) {
        // CONTAINS operator: true if the value contains the user argument (case insensitive)
        for (String s : arg) {
            if (s.equals(userArg)) {
                return resultIfTrue;
            }
        }
        return null;
    }
}

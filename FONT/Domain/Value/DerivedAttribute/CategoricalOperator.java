package Domain.Value.DerivedAttribute;

public abstract class CategoricalOperator extends Operator {
    // userArg is the argument that the user entered
    protected String userArg;
    
    public CategoricalOperator(String userArg, double resultIfTrue) {
        super(resultIfTrue);
        this.userArg = userArg;
    }
    
    // arg comes from each value being evaluated
    public abstract Double evaluate(String[] arg);
}

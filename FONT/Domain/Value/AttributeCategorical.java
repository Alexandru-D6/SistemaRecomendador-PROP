// Author: Pol Rivero

package Domain.Value;

public class AttributeCategorical extends Attribute {
    public AttributeCategorical(String name) {
        super(name);
    }
    
    public ValueCategorical parseValue(String str) {
        return new ValueCategorical(this, str);
    }
}

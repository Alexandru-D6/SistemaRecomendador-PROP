// Author: Pol Rivero

package Domain.Value;

public class AttributeBoolean extends Attribute {
    public AttributeBoolean(String name) {
        super(name);
    }
    
    public ValueBoolean parseValue(String str) {
        return new ValueBoolean(this, Boolean.parseBoolean(str));
    }
}

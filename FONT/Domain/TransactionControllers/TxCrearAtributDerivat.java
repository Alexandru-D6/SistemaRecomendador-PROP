package Domain.TransactionControllers;

import java.util.ArrayList;

import Domain.ModelCtrl;
import Domain.Value.AttributeNumeric;
import Domain.Value.DerivedAttribute.Operator;
import Domain.Value.DerivedAttribute.OperatorFactory;
import Domain.Value.DerivedAttribute.OperatorList;

public class TxCrearAtributDerivat {
    
    public static final double SAME = Operator.SAME;
    public static final double INVALID = Operator.INVALID;
    
    public static class Operation {
        public String operatorName;
        public String argument;
        public double valueIfTrue;
        
        public Operation(String operatorName, String argument, double valueIfTrue) {
            this.operatorName = operatorName;
            this.argument = argument;
            this.valueIfTrue = valueIfTrue;
        }
    }
    
    // List of operations to be executed sequentially
    private ArrayList<Operation> operations;
    // Value to assign if none of the operations return true
    private double defaultValue;
    private int attributeIndex;
    private String newAttrName;
    
    public TxCrearAtributDerivat(ArrayList<Operation> operations, double defaultValue, int attributeIndex, String newAttrName) {
        this.operations = operations;
        this.defaultValue = defaultValue;
        this.attributeIndex = attributeIndex;
        this.newAttrName = newAttrName;
    }
    
    public void execute() {
        AttributeNumeric newAttribute = new AttributeNumeric(newAttrName);
        OperatorList opList = OperatorFactory.getOperatorList(operations, defaultValue, newAttribute);
        
        ModelCtrl.getInstance().createDerivedAttr(attributeIndex, opList);
    }
}

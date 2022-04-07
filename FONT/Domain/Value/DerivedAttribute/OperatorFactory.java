package Domain.Value.DerivedAttribute;

import java.util.ArrayList;

import Domain.TransactionControllers.TxCrearAtributDerivat.Operation;
import Domain.Value.AttributeNumeric;

public class OperatorFactory {
    
    public static OperatorList getOperatorList(ArrayList<Operation> operations, double defaultValue, AttributeNumeric newAttribute) {
        ArrayList<Operator> ops = new ArrayList<Operator>(operations.size());
        for (Operation op : operations) {
            double numeric_arg;
            switch (op.operatorName.toUpperCase()) {
                case "LT":
                case "<":
                    try {
                        numeric_arg = Double.parseDouble(op.argument);
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException("The argument of a LT (<) must be numeric!");
                    }
                    ops.add(new OperatorLT(numeric_arg, op.valueIfTrue));
                    break;
                    
                case "LE":
                case "<=":
                    try {
                        numeric_arg = Double.parseDouble(op.argument);
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException("The argument of a LE (<=) must be numeric!");
                    }
                    ops.add(new OperatorLE(numeric_arg, op.valueIfTrue));
                    break;
                    
                case "GT":
                case ">":
                    try {
                        numeric_arg = Double.parseDouble(op.argument);
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException("The argument of a GT (>) must be numeric!");
                    }
                    ops.add(new OperatorGT(numeric_arg, op.valueIfTrue));
                    break;
                    
                case "GE":
                case ">=":
                    try {
                        numeric_arg = Double.parseDouble(op.argument);
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException("The argument of a GE (>=) must be numeric!");
                    }
                    ops.add(new OperatorGE(numeric_arg, op.valueIfTrue));
                    break;
                    
                case "EQ":
                case "=":
                    try {
                        numeric_arg = Double.parseDouble(op.argument);
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException("The argument of an EQ (=) must be numeric!");
                    }
                    ops.add(new OperatorEQ(numeric_arg, op.valueIfTrue));
                    break;
                    
                case "CONTAINS":
                    ops.add(new OperatorCONTAINS(op.argument, op.valueIfTrue));
                    break;
                
                default:
                    throw new IllegalArgumentException("Operator \"" + op.operatorName + "\" not supported!");
            }
        }
        
        
        return new OperatorList(ops, defaultValue, newAttribute);
    }
}

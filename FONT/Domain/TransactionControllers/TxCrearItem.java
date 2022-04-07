package Domain.TransactionControllers;

import java.util.ArrayList;

import Domain.ModelCtrl;
import Domain.DataInterface.DBFactory;
import Domain.DataInterface.ItemDB;
import Domain.Value.Attribute;
import Domain.Value.Value;
import Exceptions.ImplementationError;
import Exceptions.ObjectAlreadyExistsException;
import Utilities.RandomNumber;

public class TxCrearItem {
    
    ArrayList<String> values;
    int result;
    
    public TxCrearItem(ArrayList<String> values) {
        this.values = values;
    }
    
    public void execute() {
        ItemDB itemDB = DBFactory.getInstance().getItemDB();
        ModelCtrl mc = ModelCtrl.getInstance();
               
        ArrayList<Attribute> attrs = mc.getAttributes();
        
        if (values.size() != attrs.size()) {
            throw new IllegalArgumentException("The number of provided values (" + values.size() +
                ") does not match the number of attributes in the model (" + attrs.size() + ")");
        }
        
        // PARSE VALUES
        ArrayList<Value> vals = new ArrayList<Value>(attrs.size());
        // For each string in values, parse it to the appropriate Value type
        for (int i = 0; i < attrs.size(); i++) {
            String val = values.get(i);
            Attribute attr = attrs.get(i);
            try {
                vals.add(attr.parseValue(val));
            }
            catch (NumberFormatException e) {
                throw new IllegalArgumentException("The value at index " + i + " (\"" + val + "\") has to be numeric");
            }
        }
        
        // GENERATE ID
        int newId = -1;
        boolean newIdValid = false;
        // Try random guesses until an ID doesn't exist
        while (!newIdValid) {
            newId = RandomNumber.randomInt(1, Integer.MAX_VALUE);
            newIdValid = !itemDB.exist(newId);
        }
        
        try {
            mc.createItem(newId, vals);
        }
        catch (ObjectAlreadyExistsException e) {
            throw new ImplementationError("The generated ID " + newId + " already existed");
        }
        
        result = newId;
    }
    
    public int getResult() {
        return result;
    }
}

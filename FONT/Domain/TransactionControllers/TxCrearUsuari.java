package Domain.TransactionControllers;

import java.util.ArrayList;

import Domain.DataInterface.*;
import Exceptions.ImplementationError;
import Exceptions.ObjectAlreadyExistsException;
import Utilities.RandomNumber;
import Utilities.Pair;
import Domain.Item;
import Domain.User;

public class TxCrearUsuari {
    
    int result;
    
    public void execute() {
        UserDB userDB = DBFactory.getInstance().getUserDB();
        
        // GENERATE ID
        int newId = -1;
        boolean newIdValid = false;
        // Try random guesses until an newId doesn't exist
        while (!newIdValid) {
            newId = RandomNumber.randomInt(1, Integer.MAX_VALUE);
            newIdValid = !userDB.exist(newId);
        }
        
        try {
            // Create the new user: it's a regular (non-active) user without ratings
            new User(newId, false, new ArrayList<Pair<Item,Double>>());
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

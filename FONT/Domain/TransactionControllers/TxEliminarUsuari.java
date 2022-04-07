package Domain.TransactionControllers;

import Domain.User;
import Domain.DataInterface.DBFactory;
import Domain.DataInterface.UserDB;
import Exceptions.ObjectDoesNotExistException;

public class TxEliminarUsuari {
    private int id;
    
    public TxEliminarUsuari(int id) {
        this.id = id;
    }
    
    public void execute() {
        UserDB userDB = DBFactory.getInstance().getUserDB();
        
        try {
            User user = userDB.get(id);
            user.delete();
        }
        catch (ObjectDoesNotExistException e) {
            throw new RuntimeException("There is no User with the provided ID!");
        }
    }
}

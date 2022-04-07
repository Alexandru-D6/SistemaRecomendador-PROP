package Domain.TransactionControllers;

import Domain.Item;
import Domain.ModelCtrl;
import Domain.DataInterface.DBFactory;
import Domain.DataInterface.ItemDB;
import Exceptions.ObjectDoesNotExistException;

public class TxEliminarItem {
    private int id;
    
    public TxEliminarItem(int id) {
        this.id = id;
    }
    
    public void execute() {
        ItemDB itemDB = DBFactory.getInstance().getItemDB();
        
        try {
            Item item = itemDB.get(id);
            //item.delete();
            ModelCtrl.getInstance().deleteItem(item);
        }
        catch (ObjectDoesNotExistException e) {
            throw new RuntimeException("There is no Item with the provided ID!");
        }
    }
}

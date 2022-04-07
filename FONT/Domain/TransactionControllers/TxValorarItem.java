package Domain.TransactionControllers;

import Domain.Item;
import Domain.Rating;
import Domain.User;
import Domain.DataInterface.DBFactory;
import Domain.DataInterface.ItemDB;
import Domain.DataInterface.UserDB;
import Exceptions.ObjectAlreadyExistsException;
import Exceptions.ObjectDoesNotExistException;

public class TxValorarItem {
    private int idUser;
    private int idItem;
    private double rating;
    
    public TxValorarItem(int idUser, int idItem, double rating){
        this.idUser = idUser;
        this.idItem = idItem;
        this.rating = rating;
    }
    
    public void execute(){
        ItemDB itemDB = DBFactory.getInstance().getItemDB();
        UserDB userDB = DBFactory.getInstance().getUserDB();
        User user;
        Item ratedItem;
        
        try {
            user = userDB.get(idUser);
        }
        catch (ObjectDoesNotExistException e) {
            throw new RuntimeException("The user " + idUser + " does not exist");
        }
        
        try {
            ratedItem = itemDB.get(idItem);
        }
        catch (ObjectDoesNotExistException e) {
            throw new RuntimeException("The Item " + idItem + " does not exist");
        }
        
        try {
            new Rating(rating, user, ratedItem);
        }
        catch (ObjectAlreadyExistsException e) {
            throw new RuntimeException("The user had already rated this item");
        }
    }
}

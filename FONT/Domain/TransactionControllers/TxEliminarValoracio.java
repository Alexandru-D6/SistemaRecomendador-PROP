package Domain.TransactionControllers;

import Domain.Rating;
import Domain.DataInterface.DBFactory;
import Domain.DataInterface.RatingDB;
import Exceptions.ObjectDoesNotExistException;

public class TxEliminarValoracio {
    private int idUser;
    private int idItem;
    
    public TxEliminarValoracio(int idUser, int idItem){
        this.idUser = idUser;
        this.idItem = idItem;
    }
    
    public void execute(){
        RatingDB ratingDB = DBFactory.getInstance().getRatingDB();
        
        try {
            Rating rating = ratingDB.get(idItem, idUser);
            rating.delete();
        }catch (ObjectDoesNotExistException e) {
            throw new RuntimeException("The user has not rated this item.");
        }
    }
}

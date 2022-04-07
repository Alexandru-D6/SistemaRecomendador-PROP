package Domain.TransactionControllers;

import java.util.ArrayList;

import Domain.Item;
import Domain.ModelCtrl;
import Domain.User;
import Domain.DataInterface.*;
import Exceptions.ObjectDoesNotExistException;

public class TxObtenirRecomanacions {
    private int idUsuari;
    private ArrayList<Integer> idItemsQuery;
    private int Q;
    
    private ArrayList<Integer> result;
    
    
    public TxObtenirRecomanacions(int idUsuari, ArrayList<Integer> idItemsQuery, int Q) {
        this.idUsuari = idUsuari;
        this.idItemsQuery = idItemsQuery;
        this.Q = Q;
    }
    
    public void execute() {
        ArrayList<Item> query = new ArrayList<>(idItemsQuery.size());
        ItemDB itemDB = DBFactory.getInstance().getItemDB();
        UserDB userDB = DBFactory.getInstance().getUserDB();
        
        User u;
        try {
            u = userDB.get(idUsuari);
        }
        catch (ObjectDoesNotExistException e) {
            throw new RuntimeException("The user " + idUsuari + " does not exist");
        }
        
        for (Integer idItem : idItemsQuery) {
            try {
                Item it = itemDB.get(idItem);
                query.add(it);
                
                if (u.hasRated(it)) {
                    throw new RuntimeException("The specified user has already rated one of the items of the query");
                }
            }
            catch (ObjectDoesNotExistException e) {
                throw new RuntimeException("An item of the query does not exist");
            }
        }
        
        ArrayList<Item> recommendations = ModelCtrl.getInstance().getRecommendation(u, query, Q);
        
        result = new ArrayList<>(recommendations.size());
        
        for (Item it : recommendations) {
            result.add(it.getId());
        }
    }
    
    public ArrayList<Integer> getResult() {
        return result;
    }

    public void printResult() {
        for (int i = 0; i < result.size(); ++i) {
            System.out.println("  " + i + ". " + result.get(i));
        }
        System.out.println();
    }
}

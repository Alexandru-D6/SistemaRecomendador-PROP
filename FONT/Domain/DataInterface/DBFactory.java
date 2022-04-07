package Domain.DataInterface;

import Data.*;

public class DBFactory {
    private static DBFactory instance = null;
    
    private ItemDB itemDB = null;
    private RatingDB ratingDB = null;
    private UserDB userDB = null;
    
    private DBFactory() { }
    
    public static DBFactory getInstance() {
        if (instance == null) instance = new DBFactory();
        return instance;
    }
    
    public ItemDB getItemDB() {
        if (itemDB == null) itemDB = new ItemDBCtrl();
        return itemDB;
    }
    public RatingDB getRatingDB() {
        if (ratingDB == null) ratingDB = new RatingDBCtrl();
        return ratingDB;
    }
    public UserDB getUserDB() {
        if (userDB == null) userDB = new UserDBCtrl();
        return userDB;
    }
}

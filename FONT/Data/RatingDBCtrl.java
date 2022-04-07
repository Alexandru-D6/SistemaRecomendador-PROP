package Data;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import Domain.DataInterface.DBFactory;
import Domain.DataInterface.RatingDB;
import Domain.Item;
import Domain.Rating;
import Domain.User;
import Exceptions.ObjectDoesNotExistException;
import Exceptions.ObjectAlreadyExistsException;

public class RatingDBCtrl implements RatingDB {
    // Access Ratings, primary key is userId+itemId.
    
    public Rating get(int itemId, int userId) throws ObjectDoesNotExistException {
        User user = DBFactory.getInstance().getUserDB().get(userId);
        Item item = DBFactory.getInstance().getItemDB().get(itemId);
        Rating result = user.getRating(item);
        if (result == null) {
            throw new ObjectDoesNotExistException(Rating.class);
        }
        return result;
    }
    
    public Collection<Rating> getAll() {
        Collection<User> allUsers = DBFactory.getInstance().getUserDB().getAll();
        ArrayList<Rating> allRatings = new ArrayList<>();
        for (User u : allUsers) {
            allRatings.addAll(u.getAllRatings());
        }
        return allRatings;
    }
    
    public void add(Rating rating) throws ObjectAlreadyExistsException {
        // Don't need to do anything
    }
    
    public void remove(Rating rating) throws ObjectDoesNotExistException {
        // Don't need to do anything
    }

    public Boolean exist(int itemId, int userId) {
        try {
            User user = DBFactory.getInstance().getUserDB().get(userId);
            Item item = DBFactory.getInstance().getItemDB().get(itemId);
            return user.hasRated(item);
        }
        catch (ObjectDoesNotExistException e) {
            return false;
        }
    }
    
    class FunctsSave implements GenericDBCtrl.PersistentFunctions<Rating> {
        public Collection<Rating> getAllObjects() {
            return getAll();
        }
        public String[] getFields(Rating obj) {
            return Rating.getFields();
        }
        public String[] getValues(Rating obj) {
            return obj.getValues();
        }
    }
    
    public void save(String filename) throws IOException {
        GenericDBCtrl<Rating, Object> dummyDB = new GenericDBCtrl<>(Rating.class, 0);
        dummyDB.save(filename, new FunctsSave());
    }
}

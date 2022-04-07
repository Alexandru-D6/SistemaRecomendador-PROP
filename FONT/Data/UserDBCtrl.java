package Data;

import java.util.Collection;
import Domain.DataInterface.UserDB;
import Domain.User;
import Exceptions.ObjectDoesNotExistException;
import Exceptions.ObjectAlreadyExistsException;

public class UserDBCtrl implements UserDB {
    // Store Item, primary key is id (int)
    GenericDBCtrl<User, Integer> db = new GenericDBCtrl<>(User.class);
    
    public User get(int id) throws ObjectDoesNotExistException {
        return db.get(id);
    }
    
    public Collection<User> getAll() {
        return db.getAll();
    }
    
    public void add(User user) throws ObjectAlreadyExistsException {
        db.add(user, user.getId());
    }
    
    public void remove(User user) throws ObjectDoesNotExistException {
        db.remove(user.getId());
    }

    public Boolean exist(int id) {
        return db.exist(id);
    }
}

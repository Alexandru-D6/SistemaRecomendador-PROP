package Domain.DataInterface;

import java.util.Collection;
import Domain.User;
import Exceptions.ObjectDoesNotExistException;
import Exceptions.ObjectAlreadyExistsException;

public interface UserDB {
    // Returns the object identified by its primary key.
    // Throws an ObjectDoesNotExistException if the object doesn't exist.
    User get(int id) throws ObjectDoesNotExistException;
    
    // Returns all objects of this type
    Collection<User> getAll();
    
    // Store a new object to the database.
    // Throws an ObjectAlreadyExistsException if the object already exists.
    void add(User user) throws ObjectAlreadyExistsException;
    
    // Delete an object from the database.
    // Throws an ObjectDoesNotExistException if the object doesn't exist.
    void remove(User user) throws ObjectDoesNotExistException;

    //Return according to whether the object indentified by id exists or not.
    Boolean exist(int id);
}

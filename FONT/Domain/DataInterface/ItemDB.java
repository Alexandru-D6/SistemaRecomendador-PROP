package Domain.DataInterface;

import java.io.IOException;
import java.util.Collection;
import Domain.Item;
import Exceptions.ObjectDoesNotExistException;
import Exceptions.ObjectAlreadyExistsException;

public interface ItemDB {
    // Returns the object identified by its primary key.
    // Throws an ObjectDoesNotExistException if the object doesn't exist.
    Item get(int id) throws ObjectDoesNotExistException;
    
    // Returns all objects of this type
    Collection<Item> getAll();
    
    // Store a new object to the database.
    // Throws an ObjectAlreadyExistsException if the object already exists.
    void add(Item item) throws ObjectAlreadyExistsException;
    
    // Delete an object from the database.
    // Throws an ObjectDoesNotExistException if the object doesn't exist.
    void remove(Item item) throws ObjectDoesNotExistException;

    //Return according to whether the object indentified by id exists or not.
    Boolean exist(int id);
    
    // Write data to file
    void save(String filename) throws IOException;
    
    // Write weights + invalid to file
    void saveWeightsInvalid(String filename) throws IOException;
}

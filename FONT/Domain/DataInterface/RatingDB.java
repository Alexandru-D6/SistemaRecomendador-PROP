package Domain.DataInterface;

import java.io.IOException;
import java.util.Collection;
import Domain.Rating;
import Exceptions.ObjectDoesNotExistException;
import Exceptions.ObjectAlreadyExistsException;

public interface RatingDB {
    // Returns the object identified by its primary key.
    // Throws an ObjectDoesNotExistException if the object doesn't exist.
    Rating get(int itemId, int userId) throws ObjectDoesNotExistException;
    
    // Returns all objects of this type
    Collection<Rating> getAll();
    
    // Store a new object to the database.
    // Throws an ObjectAlreadyExistsException if the object already exists.
    void add(Rating item) throws ObjectAlreadyExistsException;
    
    // Delete an object from the database.
    // Throws an ObjectDoesNotExistException if the object doesn't exist.
    void remove(Rating item) throws ObjectDoesNotExistException;

    //Return according to whether the object indentified by itemId and userId exists or not.
    Boolean exist(int itemId, int userId);
    
    // Write data to file
    void save(String filename) throws IOException;
}

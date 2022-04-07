package Data;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

import Exceptions.ObjectAlreadyExistsException;
import Exceptions.ObjectDoesNotExistException;
import Utilities.CSVWriter;

// T is the type to be stored. K is the type of the primary key
public class GenericDBCtrl<T, K> {
    
    private final Class<T> TClass;
    private static final int INITIAL_CAPACITY = 1000;
    
    // Actual storage. Implemented as a map
    Map<K, T> storage;
    
    
    public GenericDBCtrl(Class<T> TClass) {
        this(TClass, INITIAL_CAPACITY);
    } 
    public GenericDBCtrl(Class<T> TClass, int initialCapacity) {
        this.TClass = TClass;
        storage = new TreeMap<>();
    } 
    
    
    // Returns the object identified by its primary key.
    // Throws an ObjectDoesNotExistException if the object doesn't exist.
    T get(K pk) throws ObjectDoesNotExistException {
        T result = storage.get(pk);
        
        if (result == null)
            throw new ObjectDoesNotExistException(TClass);
            
        return result;
    }
    
    
    // Returns all objects of this type
    Collection<T> getAll() {
        return storage.values();
    }
    
    
    // Store a new object to the database.
    // Throws an ObjectAlreadyExistsException if the object already exists.
    void add(T item, K pk) throws ObjectAlreadyExistsException {
        T oldValue = storage.putIfAbsent(pk, item);
        
        if (oldValue != null)
            throw new ObjectAlreadyExistsException(TClass);
    }
    
    
    // Delete an object from the database.
    // Throws an ObjectDoesNotExistException if the object doesn't exist.
    void remove(K pk) throws ObjectDoesNotExistException {
        T oldValue = storage.remove(pk);
        
        if (oldValue == null)
            throw new ObjectDoesNotExistException(TClass);
    }


    // Return according to whether the object indentified by its primary key exists or not.
    Boolean exist(K pk) {
        T result = storage.get(pk);
        return (result != null);
    }
    
    
    interface PersistentFunctions<T> {
        Collection<T> getAllObjects();
        String[] getFields(T obj);
        String[] getValues(T obj);
    }
    
    // Save the database to a CSV file
    void save(String filename, PersistentFunctions<T> funct) throws IOException {
        Collection<T> allObjects = funct.getAllObjects();
        if (allObjects.size() == 0) {
            throw new IllegalStateException("Storage is empty");
        }
        
        CSVWriter writer = new CSVWriter();
        writer.open(filename);
        
        boolean first = true;
        // for each object in the storage
        for (T obj : allObjects) {
            // write header
            if (first) {
                writer.write(funct.getFields(obj));
                first = false;
            }
            // write data
            writer.write(funct.getValues(obj));
        }
        
        writer.close();
    }
}

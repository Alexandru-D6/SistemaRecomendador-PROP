package Data;

import java.io.IOException;
import java.util.Collection;
import Domain.DataInterface.ItemDB;
import Domain.Item;
import Exceptions.ObjectDoesNotExistException;
import Exceptions.ObjectAlreadyExistsException;

public class ItemDBCtrl implements ItemDB {
    // Store Item, primary key is id (int)
    GenericDBCtrl<Item, Integer> db = new GenericDBCtrl<>(Item.class);
    
    public Item get(int id) throws ObjectDoesNotExistException {
        return db.get(id);
    }
    
    public Collection<Item> getAll() {
        return db.getAll();
    }
    
    public void add(Item item) throws ObjectAlreadyExistsException {
        db.add(item, item.getId());
    }
    
    public void remove(Item item) throws ObjectDoesNotExistException {
        db.remove(item.getId());
    }

    public Boolean exist(int id) {
        return db.exist(id);
    }
    
    
    class FunctsSave implements GenericDBCtrl.PersistentFunctions<Item> {
        public Collection<Item> getAllObjects() {
            return getAll();
        }
        public String[] getFields(Item obj) {
            return obj.getFields();
        }
        public String[] getValues(Item obj) {
            return obj.getValues();
        }
    }
    class FunctsWeightsInvalid implements GenericDBCtrl.PersistentFunctions<Item> {
        public Collection<Item> getAllObjects() {
            return getAll();
        }
        public String[] getFields(Item obj) {
            return obj.getValuesWeights();
        }
        public String[] getValues(Item obj) {
            return obj.getValuesInvalid();
        }
    }
    
    public void save(String filename) throws IOException {
        db.save(filename, new FunctsSave());
    }
    
    public void saveWeightsInvalid(String filename) throws IOException {
        db.save(filename, new FunctsWeightsInvalid());
    }
}

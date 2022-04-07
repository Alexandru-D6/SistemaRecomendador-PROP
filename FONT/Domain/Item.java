// Author: Pol Rivero

package Domain;

import java.util.ArrayList;

import Domain.Recommendation.KNN.KNNItem;
import Domain.DataInterface.DBFactory;
import Domain.Distance.DistanceStrategy;
import Domain.Value.Attribute;
import Domain.Value.Value;
import Domain.Value.DerivedAttribute.OperatorList;
import Exceptions.ImplementationError;
import Exceptions.ObjectAlreadyExistsException;
import Exceptions.ObjectDoesNotExistException;

public class Item extends KNNItem implements Comparable<Item> {
    private int id;
    private int index;
    private ArrayList<Value> values = new ArrayList<>();
    private ArrayList<Rating> ratings = new ArrayList<>();
    
    public final static String DEFAULT_ID_NAME = "id";

    private static String titleName = null;
    private static String overviewName = null;
    private static String avgRatingName = null;
    private static String imageName = null;
    
    // Pre: other is an Item
    protected double distance(KNNItem other) {
        return distance(this, (Item) other);
    }
    
    
    
    public Item(int id, int index) throws ObjectAlreadyExistsException {
        if (id < 0 || index < 0) {
            throw new ImplementationError("Invalid id or index");
        }
        this.id = id;
        this.index = index;
        
        DBFactory.getInstance().getItemDB().add(this);
    }

    public static void setColumnNames(String _titleName, String _overviewName, String _avgRatingName, String _imageName) {
        if (_titleName == null || _overviewName == null || _avgRatingName == null || _imageName == null) {
            throw new ImplementationError("Trying to set the names of the columns to null");
        }

        Item.titleName = _titleName;
        Item.overviewName = _overviewName;
        Item.avgRatingName = _avgRatingName;
        Item.imageName = _imageName;
        
        ArrayList<Attribute> attrs = ModelCtrl.getInstance().getAttributes();
        for (var str : getcolumnNames()) {
            boolean found = false;
            for (int i = 0; (i < attrs.size()) && !found; ++i) {
                if (attrs.get(i).getName().equals(str)) {
                    found = true;
                }
            }
            if (!found) {
                throw new RuntimeException("The config file references the attribute \"" + str + "\", but it does not exist!");
            }
        }
    }

    public static String[] getcolumnNames() {
        if (titleName == null || overviewName == null || avgRatingName == null || imageName == null) {
            throw new RuntimeException("The file .info doesn't initialize correctly.");
        }
        return new String[]{titleName, overviewName, avgRatingName, imageName};
    }
    
    public void delete() {
        for (int i = ratings.size()-1; i >= 0; --i) {
            ratings.get(i).delete();
        }
        
        try {
            DBFactory.getInstance().getItemDB().remove(this);
        }
        catch (ObjectDoesNotExistException e) {
            throw new ImplementationError("Deleting an Item that did not exist in the DB");
        }
    }
    
        
    public static double distance(Item a, Item b) {
        DistanceStrategy ds = ModelCtrl.getInstance().getDistanceStrategy();
        ArrayList<Double> vector = new ArrayList<Double>();
        
        if (a.values.size() != b.values.size())
            throw new ImplementationError("Comparing items with different attibutes");
        
        for (int i = 0; i < a.values.size(); i++) {
            Value va = a.values.get(i);
            Value vb = b.values.get(i);
            vector.add(va.distance(vb));
        }
        
        return ds.computeDistance(vector);
    }
    
    public int getId() {
        return id;
    }
    
    public int getIndex() {
        return index;
    }
    public void setIndex(int index) {
        if (index < 0) {
            throw new ImplementationError("The new index can't be negative");
        }
        for (Rating rating : ratings) {
            rating.updateItemIndex(this.index, index);
        }
        this.index = index;
    }
    
    // Only for reading, do not modify!
    
    void addRating(Rating rating) {
        ratings.add(rating);
    }
    
    void removeRating(Rating rating) {
        boolean wasRemoved = ratings.remove(rating);
        if (!wasRemoved) {
            throw new ImplementationError("Trying to remove a rating that does not exist");
        }
    }
    
    public void addValue(Value value) {
        values.add(value);
    }
    
    public ArrayList<Attribute> getAttributes() {
        ArrayList<Attribute> result = new ArrayList<>();
        for (Value v : values) {
            result.add(v.getAttribute());
        }
        return result;
    }
    
    public void deleteAttribute(int index) {
        // Removing the value from all items also deletes the attribute
        values.remove(index);
    }
    
    public void setInvalidAttributes(ArrayList<Boolean> invalid) {
        if (invalid.size() != values.size()) {
            throw new RuntimeException("Number of invalid bools does not match number of attributes of item " + id);
        }
        
        for (int i = 0; i < invalid.size(); i++) {
            values.get(i).setInvalid(invalid.get(i));
        }
    }
    
    public void createDerivedAttr(int index, OperatorList opList) {
        // Removing the value from all items also deletes the attribute
        Value v = values.get(index);
        // The new attribute is appended to the end of the list
        values.add(v.derivedValue(opList));
    }
    
    public void checkCanBeDerived(int index, OperatorList opList) {
        values.get(index).checkCanBeDerived(opList);
    }
    
    // Implement comparable interface
    public int compareTo(Item other) {
        return id - other.id;
    }
    
    
    // Functions for persistance
    
    public String[] getFields() {
        String[] result = new String[values.size() + 1];
        result[0] = DEFAULT_ID_NAME;
        for (int i = 0; i < values.size(); i++) {
            result[i + 1] = values.get(i).getAttribute().getName();
        }
        return result;
    }

    public String[] getValues() {
        String[] result = new String[values.size() + 1];
        result[0] = Integer.toString(id);
        for (int i = 0; i < values.size(); i++) {
            result[i + 1] = values.get(i).toString();
        }
        return result;
    }
    
    public String[] getValuesWeights() {
        String[] result = new String[values.size()];
        for (int i = 0; i < values.size(); i++) {
            double weight = values.get(i).getAttribute().getWeight();
            result[i] = Double.toString(weight);
        }
        return result;
    }

    public String[] getValuesInvalid() {
        String[] result = new String[values.size() + 1];
        result[0] = Integer.toString(id);
        for (int i = 0; i < values.size(); i++) {
            result[i + 1] = values.get(i).isInvalid() ? "1" : "0";
        }
        return result;
    }
}

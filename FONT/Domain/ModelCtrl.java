// Author: Pol Rivero

package Domain;

import java.util.ArrayList;
import java.util.Collection;

import Domain.Distance.*;
import Domain.Recommendation.*;
import Domain.Recommendation.KNN.KNearestNeighbours;
import Domain.Value.Attribute;
import Domain.Value.Value;
import Domain.Value.DerivedAttribute.OperatorList;
import Exceptions.ImplementationError;
import Exceptions.ObjectAlreadyExistsException;

public class ModelCtrl {
    private static ModelCtrl instance;
    private DistanceStrategy distanceStrategy;
    private RecommendationStrategy recommendationStrategy;
    private ArrayList<Item> items = new ArrayList<>();
    
    
    // Singleton pattern: make constructor private
    private ModelCtrl() {
        // Default values for model
        distanceStrategy = new EuclideanDistance();
        recommendationStrategy = new KNearestNeighbours();
    }
    
    public static ModelCtrl getInstance() {
        if (instance == null) instance = new ModelCtrl();
        return instance;
    }
    
    public DistanceStrategy getDistanceStrategy() {
        return distanceStrategy;
    }
    public void setDistanceStrategy(DistanceStrategy distanceStrategy) {
        this.distanceStrategy = distanceStrategy;
    }
    public RecommendationStrategy getRecommendationStrategy() {
        return recommendationStrategy;
    }
    public void setRecommendationStrategy(RecommendationStrategy recommendationStrategy) {
        this.recommendationStrategy = recommendationStrategy;
    }
    
    // Warning: Only for reading, do not modify!
    public Collection<Item> getItems() {
        return items;
    }
    
    
    public Item createItem(int id, Collection<Value> values) throws ObjectAlreadyExistsException {
        int newIndex = items.size();
        Item item = new Item(id, newIndex);
        for (Value v : values) {
            item.addValue(v);
        }
        items.add(item);
        return item;
    }
    
    public void deleteItem(Item item) {
        int removeFromIndex = item.getIndex();
        if (removeFromIndex >= items.size() || items.get(removeFromIndex) != item) {
            throw new ImplementationError("Deleting an Item that had not been inserted in items");
        }
        
        // Remove the item
        items.remove(removeFromIndex).delete();
        
        // Update the index of the remaining items
        for (int i = removeFromIndex; i < items.size(); i++) {
            items.get(i).setIndex(i);
        }
    }
    
    
    public ArrayList<Item> getRecommendation(RecommendationQuery q) {
        User u;
        try {
            // Create new active user with the id and ratings from the query
            u = new User(q.getUserId(), true, q.getRatings());
        }
        catch (ObjectAlreadyExistsException e) {
            throw new RuntimeException("Active user id already exists!");
        }
        
        ArrayList<Item> result = getRecommendation(u, q.getQuery(), q.getQ());
        
        u.delete();
        return result;
    }
    
    public ArrayList<Item> getRecommendation(User u, ArrayList<Item> query, int Q) {
        return recommendationStrategy.execute(u, query, Q);
    }
    
    public ArrayList<Attribute> getAttributes() {
        if (items.size() == 0) {
            throw new RuntimeException("Model has not been initialized yet");
        }
        return items.get(0).getAttributes();
    }
    
    
    public void deleteAttribute(int index) {
        if (items.size() == 0) {
            throw new RuntimeException("Model has not been initialized yet");
        }
        if (index < 0 || index >= items.get(0).getAttributes().size()) {
            throw new RuntimeException("The index is invalid");
        }
        
        String name = getAttributes().get(index).getName();
        String[] infoNames = Item.getcolumnNames();
        for (String infoName : infoNames) {
            if (infoName.equals(name)) {
                throw new RuntimeException("The attribute \"" + infoName + "\" is used as info in the config file!\n"
                    + "Consider setting its weight to 0 instead of deleting it.");
            }
        }
        
        for (Item i : items) {
            i.deleteAttribute(index);
        }
    }
    
    public void createDerivedAttr(int index, OperatorList opList) {
        if (items.size() == 0) {
            throw new RuntimeException("Model has not been initialized yet");
        }
        if (index < 0 || index >= items.get(0).getAttributes().size()) {
            throw new RuntimeException("The index is invalid");
        }
        // Only do error checking on the first item (all items should have the same attributes)
        items.get(0).checkCanBeDerived(index, opList);
        for (Item i : items) {
            i.createDerivedAttr(index, opList);
        }
    }
    
}

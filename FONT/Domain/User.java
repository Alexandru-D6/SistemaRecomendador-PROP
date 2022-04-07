// Author: Pol Rivero

package Domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import Domain.DataInterface.DBFactory;
import Domain.Recommendation.KMeansSlope1.ClusterCtrl;
import Domain.Recommendation.KMeansSlope1.ClusterMember;
import Exceptions.ImplementationError;
import Exceptions.ObjectAlreadyExistsException;
import Exceptions.ObjectDoesNotExistException;
import Utilities.Pair;

public class User extends ClusterMember implements Comparable<User> {
    private int id;
    private boolean isActiveUser;
    private Map<Item, Rating> ratings = new HashMap<>();
    // Same as ratings but with the coordinates as key
    private Map<Integer, Double> coordinates = new TreeMap<>();
    
    public User(int id, boolean isActiveUser, ArrayList< Pair<Item, Double> > ratings) throws ObjectAlreadyExistsException {
        this.id = id;
        this.isActiveUser = isActiveUser;
        
        for (Pair<Item, Double> rt : ratings) {
            // First element is rated item, second element is the rating
            try {
                Item ratedItem = rt.first;
                double rating = rt.second;
                new Rating(rating, this, ratedItem);
            }
            catch (ObjectAlreadyExistsException e) {
                throw new RuntimeException("Array of ratings contained 2 ratings for the same Item");
            }
        }
        
        if (isActiveUser) {
            if (ratings.size() == 0) {
                throw new RuntimeException("Active user must have at least 1 rating");
            }
            newActiveClusterMember();
        }
        
        DBFactory.getInstance().getUserDB().add(this);
    }
    
    public void delete() {
        
        this.delete_clustermember();

        Collection<Rating> allRatings = new ArrayList<>(ratings.values());
        for (Rating r : allRatings) {
            r.delete();
        }
        
        try {
            DBFactory.getInstance().getUserDB().remove(this);
        }
        catch (ObjectDoesNotExistException e) {
            throw new ImplementationError("Deleting a User that did not exist in the DB");
        }
    }
    
    
    public int getId() {
        return id;
    }
    
    
    // Implement ClusterMember
    protected Map<Integer,Double> getCoordinates() {
        if (coordinates.size() == 0) {
            throw new RuntimeException("User " + id + " cannot be clustered because it has no ratings");
        }
        
        // Warning: do not modify!
        return coordinates;
    }
    
    public static Collection<ClusterMember> getAllMembers() {
        Collection<User> allUsers = DBFactory.getInstance().getUserDB().getAll();
        
        // Cast from collection of User to collection of ClusterMember
        @SuppressWarnings("unchecked")
        Collection<ClusterMember> allMembers = (Collection<ClusterMember>)(Collection<? extends ClusterMember>) allUsers;
        
        return allMembers;
    }
    
    protected double distance(ClusterMember other) {
        return distance(this, (User) other);
    }
    
    public static double distance(User u1, User u2) {
        Map<Integer, Double> a = u1.coordinates;
        Map<Integer, Double> b = u2.coordinates;
        ArrayList<Double> parcial = new ArrayList<Double>();
        
        for (var str : a.entrySet()) {
            int index = str.getKey();
            if (b.get(index) != null) parcial.add(str.getValue() - b.get(index));
        }
        
        double dist = ModelCtrl.getInstance().getDistanceStrategy().computeDistance(parcial);
        
        if (Double.isNaN(dist)) {
            return 0.0;
        }
        return dist;
    }
    
    
    // Warning: Only for reading, do not modify!
    public Collection<Rating> getRatings() {
        return ratings.values();
    }
    
    void addRating(Rating rating) {
        ratings.put(rating.getItem(), rating);
        coordinates.put(rating.getItem().getIndex(), rating.getNormalizedRating());
        
        if (!isActiveUser) {
            // Clusters may need to be recomputed
            ClusterCtrl.markDirty();
        }
    }
    
    void removeRating(Item ratedItem) {
        ratings.remove(ratedItem);
        coordinates.remove(ratedItem.getIndex());
        
        if (!isActiveUser) {
            // Clusters may need to be recomputed
            ClusterCtrl.markDirty();
        }
    }
    
    public void updateItemIndex(int oldIndex, int newIndex) {
        Double old = coordinates.remove(oldIndex);
        if (old == null) {
            throw new ImplementationError("Trying to update an item index that does not exist");
        }
        Double existing = coordinates.put(newIndex, old);
        if (existing != null) {
            throw new ImplementationError("Trying to update an item index to a value that already exists");
        }
    }
    
    public boolean hasRated(Item i) {
        return ratings.containsKey(i);
    }
    
    public Rating getRating(Item i) {
        return ratings.get(i);
    }
    
    public Collection<Rating> getAllRatings() {
        return ratings.values();
    }
    public Collection<Item> getAllRatedItems() {
        return ratings.keySet();
    }

    public static boolean exist(int id) {
        return DBFactory.getInstance().getUserDB().exist(id);
    }
    
    // Implement comparable interface
    public int compareTo(User other) {
        return id - other.id;
    }
}

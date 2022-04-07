// Author: Pol Rivero

package Domain;

import Domain.DataInterface.DBFactory;
import Exceptions.ImplementationError;
import Exceptions.ObjectAlreadyExistsException;
import Exceptions.ObjectDoesNotExistException;

public class Rating implements Comparable<Rating> {
    private static final double NOT_INITIALIZED = -1.0;
    private static double maxRating = NOT_INITIALIZED;
    
    private double normalizedRating;
    private User user;
    private Item item;
    
    public Rating(double rating, User user, Item item) throws ObjectAlreadyExistsException {
        if (maxRating == NOT_INITIALIZED) {
            throw new ImplementationError("maxRating not initialized");
        }
        
        this.normalizedRating = rating/maxRating;
        this.user = user;
        this.item = item;
        
        if (normalizedRating < 0 || normalizedRating > 1) {
            throw new RuntimeException("Attempting to create a rating of " + rating + ". Max rating is " + maxRating);
        }
        
        user.addRating(this);
        item.addRating(this);
        
        DBFactory.getInstance().getRatingDB().add(this);
    }
    
    public void delete() {
        user.removeRating(item);
        item.removeRating(this);
        
        
        try {
            DBFactory.getInstance().getRatingDB().remove(this);
        }
        catch (ObjectDoesNotExistException e) {
            throw new ImplementationError("Deleting a Rating that did not exist in the DB");
        }
    }
    
    public static void setMaxRating(double max) {
        if (maxRating != NOT_INITIALIZED) {
            throw new ImplementationError("Max rating can only be set once");
        }
        maxRating = max;
    }
    public static double getMaxRating() {
        return maxRating;
    }
    
    public void updateItemIndex(int oldIndex, int newIndex) {
        user.updateItemIndex(oldIndex, newIndex);
    }
    
    
    public Item getItem() {
        return item;
    }
    
    public User getUser() {
        return user;
    }
    
    public double getNormalizedRating() {
        return normalizedRating;
    }
    
    public static String[] getFields() {
        return UserRatings.getColumnNames();
    }

    public String[] getValues() {
        return new String[] {
            Integer.toString(user.getId()),
            Integer.toString(item.getId()),
            Double.toString(normalizedRating * maxRating)
        };
    }

    // Sort by normalized rating, in descending order
    @Override
    public int compareTo(Rating o) {
        return Double.compare(o.normalizedRating, normalizedRating);
    }
}

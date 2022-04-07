// Author: Pol Rivero

package Domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeMap;

import Domain.DataInterface.DBFactory;
import Domain.DataInterface.ItemDB;
import Exceptions.ObjectDoesNotExistException;
import Utilities.CSVReader;
import Utilities.Pair;


// Utility class to store the user ratings

public class UserRatings {
    private int userId;
    private ArrayList< Pair<Item, Double> > ratings;
    private static String userIdName = null;
    private static String itemIdName = null;
    private static String ratingName = null;
    
    public UserRatings(int userId) {
        if (userIdName == null) {
            throw new RuntimeException("Trying to instantiate UserRatings without setting the names of the columns");
        }
        
        this.userId = userId;
        this.ratings = new ArrayList<>();
    }
    
    public static void setColumnNames(String userIdName, String itemIdName, String ratingName) {
        if (userIdName == null || itemIdName == null || ratingName == null) {
            throw new RuntimeException("Trying to set the names of the columns to null");
        }
        if (UserRatings.userIdName != null) {
            throw new RuntimeException("Trying to set the names of the columns twice");
        }
        UserRatings.userIdName = userIdName;
        UserRatings.itemIdName = itemIdName;
        UserRatings.ratingName = ratingName;
    }
    
    public static String[] getColumnNames() {
        if (userIdName == null) {
            throw new RuntimeException("Trying to get the names of the columns without setting them");
        }
        return new String[] {userIdName, itemIdName, ratingName};
    }
    
    public int getUserId() {
        return userId;
    }
    
    public ArrayList<Pair<Item, Double>> getRatings() {
        return ratings;
    }
    
    public void addRating(Item item, double rating) {
        ratings.add(new Pair<Item, Double>(item, rating));
    }
    
    
    public static Collection<UserRatings> parseRatings(CSVReader reader) {
        ItemDB itemDB = DBFactory.getInstance().getItemDB();
    
        String[] line = reader.readNext(); // Skip header
        if (line.length != 3) {
            throw new RuntimeException("Incorrect number of columns in CSV");
        }
        int userIdIndex = -1;
        int itemIdIndex = -1;
        int ratingIndex = -1;
        for (int i = 0; i < line.length; i++) {
            if (line[i].equals(userIdName)) {
                userIdIndex = i;
            }
            else if (line[i].equals(itemIdName)) {
                itemIdIndex = i;
            }
            else if (line[i].equals(ratingName)) {
                ratingIndex = i;
            }
        }
        if (userIdIndex == -1 || itemIdIndex == -1 || ratingIndex == -1) {
            throw new RuntimeException("The header of the ratings CSV does not match the config file");
        }
        
        TreeMap<Integer, UserRatings> userRatings = new TreeMap<>();
        
        // For each line of the file, create a new Rating (and possibly a new User)
        while (!reader.endReached()) {
            line = reader.readNext();
            
            int userId, itemId;
            double rating;
            try {
                userId = Integer.parseInt(line[userIdIndex]);
                itemId = Integer.parseInt(line[itemIdIndex]);
                rating = Double.parseDouble(line[ratingIndex]);
            }
            catch (NumberFormatException e) {
                throw new RuntimeException("Could not parse numeric rating (" + e.getMessage() + ")");
            }
            
            // New user id: create new group of ratings
            if (!userRatings.containsKey(userId)) {
                userRatings.put(userId, new UserRatings(userId));
            }
            
            try {
                Item item = itemDB.get(itemId);
                // Create the new rating
                userRatings.get(userId).addRating(item, rating);
            }
            catch (ObjectDoesNotExistException e) {
                throw new RuntimeException("The CSV references the item " + itemId + ", which doesn't exist");
            }
        }
        
        return userRatings.values();
    }
}

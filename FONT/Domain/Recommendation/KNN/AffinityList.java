package Domain.Recommendation.KNN;

import Domain.Item;
import Domain.Rating;
import Domain.User;
import Exceptions.ImplementationError;
import Utilities.Pair;
import java.util.ArrayList;
import java.util.Collections;

/**
 *
 * @author Pablo Jose Galvan Calderon
 */
public class AffinityList {
    
    private enum Method {
        NORMAL, LOG, ORDERED 
    }
    // METHOD = NORMAL: Unweighted (pure KNN)
    // METHOD = LOG: Divide by log(10+dist)
    // METHOD = ORDERED: Ratings R are ordered decreasingly.
    //                   For every neighbour j of each rated item i, a new affinity
    //                   within the interval [R[i], R[i+1]] determined by the distance
    //                   between i and j is added to affinity[j] (is this distance
    //                   is the maximum distance out of i's neighbours', the new
    //                   affinity is R[i+1] (or TRESHOLD if R[i+1] does not exist),
    //                   but if this distance is 0 the new affinity is R[i], and
    //                   otherwise the new affinity is interpolated between these
    //                   two values depending on the distance).
    
    private ArrayList<ItemAffinity> affinities;
    private ArrayList<Item> query;
    private final Method METHOD = Method.LOG;

    
    public AffinityList(ArrayList<Item> items) {
        query = new ArrayList<>(items);
        affinities = new ArrayList<>(items.size());
        for (Item item : items) {
            affinities.add(new ItemAffinity(item));
        }
    }
    
    public void accumulate(AffinityList other) {
        if (affinities.size() != other.affinities.size())
            throw new ImplementationError("AffinityLists must have the same size");
        
        for (int i = 0; i < affinities.size(); i++) {
            affinities.get(i).accumulateAffinity(other.affinities.get(i));
        }
    }
    
    private double getNewAffinity(ArrayList<Rating> ratings, int ratingIndex, double dist, double max_dist, double treshold) {
        Rating rating = ratings.get(ratingIndex);
        double newaff = rating.getNormalizedRating(); // New affinity to be added to the average.
        switch (METHOD) {
            case LOG: {
                // The new affinity is divided by log(10+dist), where dist is the distance between the rated item and the query item. 
                newaff /= Math.log10(10 + dist);
                break;
            }
            case ORDERED: {
                // i is the rated item's index in the ratings list.
                // R is the ratings list.
                // d is the distance between the rated item and the query item.
                // Dmax is the maximum distance out of the rated item's neighbours'.
                // r = d/Dmax
                // x = (R[i] - R[i+1])*r
                // New affinity = R[i] - x
                double current_rating = rating.getNormalizedRating(); // R[i]
                // R[i+1]
                double next_rating = 0;
                if (ratingIndex == ratings.size() - 1 || ratings.get(ratingIndex + 1).getNormalizedRating() <= treshold)
                    next_rating = treshold;
                else
                    next_rating = ratings.get(ratingIndex + 1).getNormalizedRating();

                double aff_subtr = (current_rating - next_rating)*dist/max_dist; // x(id)

                newaff -= aff_subtr; // New affinity.
                break;
            }
            default:
        }
        return newaff;
    }
    
    private ArrayList<Rating> getRatings(User user, boolean sort) {
        ArrayList<Rating> ratings = new ArrayList<>(user.getRatings());
        if (sort) Collections.sort(ratings);
        return ratings;
    }
    
    private double getMaxDist(ArrayList<Pair<Integer, Double> > list) {
        int n = list.size();
        double max_dist = 0;
        // The maximum distance out of the rated item's neighbours' is calculated.
        for (int j = 0; j < n; ++j) {
            double dist = list.get(j).second;
            if (dist > max_dist)
                max_dist = dist;
        }
        return max_dist;
    }
    
    public void updateAffinities(double weight, User user, double kFrac, double treshold) {        
        // K = floor(K fraction * size of ratings)
        int K = Math.max((int)(kFrac * query.size()), 1);

        // Create a list with the active user's ratings.
        boolean needToSort = (METHOD == Method.ORDERED);
        ArrayList<Rating> ratings = getRatings(user, needToSort);
        
        for (int i = 0; i < ratings.size(); ++i) {
            Rating rating = ratings.get(i);
            // If the rating is below the specified treshold, it is discarded.
            if (rating.getNormalizedRating() < treshold) {
                ratings.remove(i);
                --i;
            }
            else {
                // Returns a list with the indices of the item within queries and the distance to the rated item (ordered increasingly by the distance). 
                ArrayList<Pair<Integer, Double> > nn = ((KNNItem) rating.getItem()).getNearestItems(query, K);
                double max_dist = 0;
                
                if (METHOD == Method.ORDERED)
                    max_dist = getMaxDist(nn);
                
                for (Pair<Integer, Double> neighbour : nn) {
                    int index = neighbour.first; // Index of the j nearest neighbour within queries.
                    
                    double newaff = getNewAffinity(ratings, i, neighbour.second, max_dist, treshold);
                    
                    ItemAffinity affinity = affinities.get(index);
                    affinity.addAffinity(newaff, weight);
                }
            }
        }
    }
    
    // Sorts the affinities decreasingly.
    public void sortAffinities() {
        Collections.sort(affinities);
    }
    
    // q := maximum size of the returned list.
    public ArrayList<Item> toItemList(int q) {
        int n = Math.min(q, affinities.size());
        ArrayList<Item> list = new ArrayList<>(n);
        
        for (int i = 0; i < n; ++i) {
            ItemAffinity ia = affinities.get(i);
            list.add(ia.getItem());
        }
        return list;
    }
}

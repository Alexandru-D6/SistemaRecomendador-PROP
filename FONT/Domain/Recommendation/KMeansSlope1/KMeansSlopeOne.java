package Domain.Recommendation.KMeansSlope1;

import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.Collection;
import java.util.Comparator;
import Domain.Recommendation.RecommendationStrategy;
import Domain.User;
import Domain.Item;
import Utilities.Pair;

//Author Ferran De La Varga

public class KMeansSlopeOne implements RecommendationStrategy {
    
    private static Lock update_sum_lock = new ReentrantLock();
    
    //retorna la diferència de les valoracions de l'usuari us del itemId_to_discover i el item id. 
    private double slope_items(User us, Item item_to_discover, Item i) {
        return us.getRating(item_to_discover).getNormalizedRating() - us.getRating(i).getNormalizedRating();
    }

    //users_cluster contains minimum a user
    //returns the average slope
    private double average_slope(Collection<User> users_cluster, Item item_to_discover, Item i) {
        double n = 0;
        double sum_slopes = 0;
        for (User us : users_cluster) {
            if (us.hasRated(item_to_discover) && us.hasRated(i)) {
                sum_slopes += slope_items(us, item_to_discover, i);
                ++n;
            }
        }


        if (n == 0) return 0;
        else return sum_slopes/n;
    }

    //Returns the possible rating that will put the user u to the item item_to_discover
    double _sum_predictions = 0;
    int _n = 0;
    private double slope_one(User u, Item item_to_discover) {
        _sum_predictions = 0;
        _n = 0;
        
        // Cast Collection<ClusterMember> -> Collection<User>
        @SuppressWarnings("unchecked")
        Collection<User> users = (Collection<User>)(Collection<? extends ClusterMember>) u.get_cluster().get_members();
        
        if (users.size() == 0) {
            throw new RuntimeException("There's a cluster of users that doesn't have any users.");
        }
        
        Collection<Item> allRatedItems = u.getAllRatedItems();
        
        allRatedItems.parallelStream().forEach(item -> {
            double prediction = u.getRating(item).getNormalizedRating() + average_slope(users, item_to_discover, item);
            update_sum_lock.lock();
            _sum_predictions += prediction;
            ++_n;
            update_sum_lock.unlock();
        });
        
        if (_n == 0) {
            throw new RuntimeException("The active user " + u.getId() + " hasn't rated any item.");
        }

        return _sum_predictions/_n;
    }
    
     
    //user u has not rated the items of the query
    //user u has rated at least one item otherwise an exception will be thrown
    // Q <= query.size() 
    //Returns a priority queue ordered by the posible rating (from most to least)
    public PriorityQueue< Pair<Double, Item> > get_recommendations(User u, ArrayList<Item> query, int Q) {
        PriorityQueue< Pair<Double, Item> > result = new PriorityQueue<Pair <Double, Item> > (
            // Only store best Q items, discard the others
            Q, 
            // Order by first element (distance), in ascending order
            Comparator.comparing((Pair<Double,Item> p) -> p.first).reversed()
        );
        
        for (Item q : query) {
            Pair<Double, Item> p = new Pair<>(slope_one(u, q), q);
            result.add(p); 
        }
        
        return result;
    }
    
    public void updateKmeans() {
        ClusterCtrl.getInstance().updateKmeans(User.getAllMembers());
    }
    
    // Implement interface
    public ArrayList<Item> execute(User u, ArrayList<Item> query, int Q) {
        
        // RUN KMEANS IF NEEDED
        updateKmeans();
        
        // RUN SLOPEONE
        PriorityQueue< Pair<Double, Item> > recommendations = get_recommendations(u, query, Q);
        ArrayList<Item> result = new ArrayList<>(Q);
        
        // Convert from priority queue to sorted array list
        while (!recommendations.isEmpty() && result.size() < Q) {
            result.add(recommendations.poll().second);
        }
        
        return result;
    }
}

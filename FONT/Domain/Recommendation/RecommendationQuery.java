package Domain.Recommendation;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.TreeMap;

import Domain.Item;
import Domain.UserRatings;
import Exceptions.ImplementationError;
import Utilities.Pair;

// Utility class for storing queries to the recommendation algorithm

public class RecommendationQuery {
    private UserRatings known;
    private ArrayList<Item> query;
    private int Q;
    
    private TreeMap<Item, Double> unknownRatings = null;
    
    public int getUserId() {
        return known.getUserId();
    }
    
    public ArrayList<Pair<Item, Double>> getRatings() {
        return known.getRatings();
    }
    
    public ArrayList<Item> getQuery() {
        return query;
    }
    
    public int getQ() {
        return Q;
    }
    
    public double normalizedDCG(ArrayList<Item> result) {
        ArrayList<Item> idealResult = bestQueryResult();
        if (result.size() != idealResult.size()) {
            throw new ImplementationError("Incorrect length of recommendation result");
        }
        double normalizedDCG = DCG(result) / DCG(idealResult);
        if (normalizedDCG > 1 || normalizedDCG < 0) {
            throw new ImplementationError("Incorrect DCG computation");
        }
        return normalizedDCG;
    }
    
    public static RecommendationQuery buildQuery(UserRatings known, UserRatings unknown, int Q) {       
        if (known.getUserId() != unknown.getUserId()) {
            throw new IllegalArgumentException("In order to build the queries, known and unknown must have the same user id");
        }
        int unkn_size = unknown.getRatings().size();
        if (Q > unkn_size) {
            throw new IllegalArgumentException("The number of expected results Q is bigger than the query size");
        }
        
        RecommendationQuery recQuery = new RecommendationQuery(known, unknown, new ArrayList<>(unkn_size), Q);
        
        for (Pair<Item, Double> p : unknown.getRatings()) {
            // The first element of the pair is the Item that needs to be predicted, the second is the correct (unknown) rating
            recQuery.query.add(p.first);
        }
        
        return recQuery;
    }
    
    public static RecommendationQuery buildQuery(int userId, ArrayList<Pair<Item,Double>> known, ArrayList<Item> unknown, int Q) {
        UserRatings knownRatings = new UserRatings(userId);
        for (Pair<Item,Double> k : known) {
            knownRatings.addRating(k.first, k.second);
        }
        
        RecommendationQuery recQuery = new RecommendationQuery(knownRatings, null, unknown, Q);
        return recQuery;
    }
    
    
    
    private RecommendationQuery(UserRatings known, UserRatings unknown, ArrayList<Item> query, int Q) {
        this.known = known;
        this.query = query;
        this.Q = Q;
        
        // If unknown ratings haven't been provided (debug query), leave unknownRatings as null
        if (unknown != null) {
            unknownRatings = new TreeMap<>();
            for (Pair<Item, Double> p : unknown.getRatings()) {
                unknownRatings.put(p.first, p.second);
            }
        }
    }
    
    private ArrayList<Item> bestQueryResult() {
        if (unknownRatings == null) {
            throw new ImplementationError("Attempting to compute best result of a DEBUG query without unknowns!");
        }
        
        if (Q > unknownRatings.size()) {
            throw new IllegalArgumentException("The number of expected results Q is bigger than the query size");
        }
        
        PriorityQueue< Pair<Double, Item> > pq = new PriorityQueue<Pair <Double, Item> > (
            // Only store best Q items, discard the others
            Q, 
            // Order by first element (rating), in reverse order (first element has the greatest rating)
            Comparator.comparingDouble((Pair<Double,Item> p) -> p.first).reversed()
        );
        
        // For each pair (Item, true_rating) in the unknown ratings, we add it to the priority queue
        for (Map.Entry<Item, Double> e : unknownRatings.entrySet()) {
            pq.add(new Pair<Double, Item>(e.getValue(), e.getKey()));
        }
        
        // Convert from priority queue to sorted array list
        ArrayList<Item> result = new ArrayList<>(Q);
        while (!pq.isEmpty() && result.size() < Q) {
            result.add(pq.poll().second);
        }
        
        return result;
    }
    
    private double DCG(ArrayList<Item> permutation) {
        double dcg = 0;
        
        for (int i = 0; i < permutation.size(); i++) {
            // True rating of the item in the ith position of the permutation
            double unknownRating = unknownRatings.get(permutation.get(i));
            // Math.log returns the natural log instead of the base 2 log, but it should not matter
            dcg += ((Math.pow(2, unknownRating) - 1) / Math.log(i + 2));
        }
        
        return dcg;
    }
}


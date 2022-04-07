package Domain.Recommendation;
import java.util.ArrayList;
import Domain.*;

// Interface for a generic recommendation algorithm.
// Inputs: The active user u and a query of Items that u has not rated
// Result: The best Q items from the query, ordered according to the result of the algorithm
public interface RecommendationStrategy {
    ArrayList<Item> execute(User u, ArrayList<Item> query, int Q);
}

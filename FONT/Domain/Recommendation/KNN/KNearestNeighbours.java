package Domain.Recommendation.KNN;

import Domain.Item;
import Domain.User;
import Domain.Recommendation.RecommendationStrategy;
import java.util.ArrayList;
/**
 *
 * @author Pablo Jose Galvan Calderon
 */

public class KNearestNeighbours implements RecommendationStrategy {
    
    private final double TRESHOLD = 0.5;
    private final double K_FRAC = 0.25; // (0; 1]
    
    public KNearestNeighbours() {
    }
    
    public ArrayList<Item> execute(User activeUser, ArrayList<Item> query, int q) {
        AffinityList affinities = new AffinityList(query);
        affinities.updateAffinities(1, activeUser, K_FRAC, TRESHOLD);

        // Gets the q items with largest affinities.
        affinities.sortAffinities();
        ArrayList<Item> recom = affinities.toItemList(q);
        return recom;
    }
}

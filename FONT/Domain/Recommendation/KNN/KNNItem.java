package Domain.Recommendation.KNN;

import Domain.Item;
import java.util.ArrayList;
import Utilities.Pair;
import java.util.Comparator;
import java.util.PriorityQueue;

/**
 *
 * @author Pablo Jose Galvan Calderon
 */

public abstract class KNNItem {
    
    protected abstract double distance(KNNItem other);
    
    public ArrayList<Pair<Integer, Double> > getNearestItems(ArrayList<Item> query, int k) {
        // neighbours has pairs with the indexes of the items from query and their distances to the implicit item. These pairs are sorted increasingly by their distances.
        PriorityQueue<Pair<Integer, Double> > neighbours = new PriorityQueue<>(k, Comparator.comparingDouble((Pair<Integer, Double> p) -> p.second));
        int nq = query.size();
        for (int i = 0; i < nq; ++i) {
            Item it = query.get(i);
            neighbours.add(new Pair<>(i, distance(it)));
        }
        ArrayList<Pair<Integer, Double> > ret = new ArrayList<>(neighbours.size());
        
        k = Math.min(k, neighbours.size());
        for (int i = 0; i < k; ++i) {
            ret.add(neighbours.poll());
        }
        
        return ret;
    }
}

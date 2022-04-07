package Domain.Recommendation.Hybrid;

import Domain.Item;
import Domain.Recommendation.KMeansSlope1.ClusterCtrl;
import Domain.Recommendation.KMeansSlope1.ClusterMember;
import Domain.Recommendation.KNN.AffinityList;
import Domain.Recommendation.RecommendationStrategy;
import Domain.User;
import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * @author Pablo Jose Galvan Calderon
 */
public class Hybrid implements RecommendationStrategy {
    
    private enum Method {
        NORMAL, LOG
    }
    
    private final double TRESHOLD = 0.5;
    private final double K_FRAC = 0.25; // (0; 1]
    
    private Method METHOD = Method.LOG;
    
    public Hybrid() {
    }
    
    public void updateKmeans() {
        ClusterCtrl.getInstance().updateKmeans(User.getAllMembers());
    }
    
    public ArrayList<Item> execute(User activeUser, ArrayList<Item> query, int q) {
        AffinityList affinities = new AffinityList(query);
        
        // RUN KMEANS IF NEEDED
        updateKmeans();
        
        // Cast Collection<ClusterMember> -> Collection<User>
        @SuppressWarnings("unchecked")
        Collection<User> users = (Collection<User>)(Collection<? extends ClusterMember>) activeUser.get_cluster().get_members();
        
        if (users.size() == 0) {
            throw new RuntimeException("There's a cluster of users that doesn't have any users.");
        }
        
        users.parallelStream().forEach(user -> {
            AffinityList localAffinityList = new AffinityList(query);
            double weight = 1;
            if (METHOD == Method.LOG)
                weight /= Math.log10(10 + User.distance(activeUser, user));
            localAffinityList.updateAffinities(weight, user, K_FRAC, TRESHOLD);
            
            affinities.accumulate(localAffinityList);
        });
        
        // Gets the q items with largest affinities.
        affinities.sortAffinities();
        
        ArrayList<Item> recom = affinities.toItemList(q);
        return recom;
    }
}


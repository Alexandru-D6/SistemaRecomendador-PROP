package Domain.Recommendation.KMeansSlope1;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.Collection;

import Domain.ModelCtrl;
import Domain.Distance.DistanceStrategy;
import Domain.Recommendation.RecommendationStrategy;
import Domain.Recommendation.Hybrid.Hybrid;
import Exceptions.ImplementationError;

//Author Alexandru Dumitru Maroz

public abstract class ClusterMember {
    
    Cluster cluster;
    private static Lock add_member_lock = new ReentrantLock();
    
    protected abstract Map<Integer, Double> getCoordinates();
    
    public ClusterMember() {
        cluster = null;
    }

    public void delete_clustermember() {
        if (cluster != null) cluster.remove_member(this);
    }
    
    protected void newActiveClusterMember() {
        // If the current strategy is Kmeans, then assign the active member to a cluster
        RecommendationStrategy recommendationStrategy = ModelCtrl.getInstance().getRecommendationStrategy();
        if (recommendationStrategy instanceof KMeansSlopeOne) {
            // Run KMeans if needed
            ((KMeansSlopeOne) recommendationStrategy).updateKmeans();
            // Assign the active member to a cluster
            updateCluster(ClusterCtrl.getInstance().getClusters());
        }
        else if (recommendationStrategy instanceof Hybrid) {
            // Run KMeans if needed
            ((Hybrid) recommendationStrategy).updateKmeans();
            // Assign the active member to a cluster
            updateCluster(ClusterCtrl.getInstance().getClusters());
        }
    }

    public Cluster get_cluster() {
        if (cluster == null)
            throw new RuntimeException("ClusterMember not assigned to any cluster");
            
        return cluster;
    }

    public void set_cluster(Cluster _cluster) {
        this.cluster = _cluster;
    }
        
    public void updateCluster(Collection<Cluster> clusters) {
        if (clusters.size() == 0) {
            throw new ImplementationError("Calling updateCluster with 0 clusters (has ClusterCtrl been initialized?)");
        }
        
        double bestDist = Double.POSITIVE_INFINITY;
        Map<Integer, Double> myCoord = getCoordinates();
        
        for (var c : clusters) {
            
            ArrayList<Double> clusterCoord = c.getCoordinates();
            ArrayList<Double> distanciasParciales = new ArrayList<Double>(myCoord.size());
            
            /*if (myCoord.size() != clusterCoord.size()) {
                throw new ImplementationError("Dimensions of myCoord and clusterCoord do not match, updateCluster");
            }*/
            
            for (var str : myCoord.entrySet()) {
                int index = str.getKey();
                distanciasParciales.add(str.getValue()-clusterCoord.get(index));
            }
            
            DistanceStrategy ds = ModelCtrl.getInstance().getDistanceStrategy();
            //Computing the distance from this member into cluster c;
            double dist = ds.computeDistance(distanciasParciales);
            
            if (dist < bestDist) {
                bestDist = dist;
                set_cluster(c);
            }
        }
        
        add_member_lock.lock();
        cluster.add_member(this);
        add_member_lock.unlock();
    }

    //Compute the square variation of this point from the cluster centroid
    public Double compute_wss() {
        double temp_wss = 0.0;
        Map<Integer, Double> myCoord = getCoordinates();
        ArrayList<Double> clusterCoord = cluster.getCoordinates();

        for (var str : myCoord.entrySet()) {
            temp_wss += Math.pow((str.getValue() - clusterCoord.get(str.getKey())), 2.0);
        }

        return temp_wss;
    }

    public double compute_silhouette() {
        Collection<ClusterMember> clustermembers = cluster.get_members();
        double si = 0.0;

        //precondition for calculate ci
        if (clustermembers.size() > 1) {
            //a(i) = 1/(|Ci|-1) * Sum(d(i,j)) being j all other points from the same cluster
            double ai = 1.0/(clustermembers.size() - 1.0);
            double parcial_sum = 0.0;
            for (var str1 : clustermembers) {
                if (str1 != this) parcial_sum += distance(str1);
            }
            ai *= parcial_sum;

            //b(i) = min (1/|Ck|) * Sum(d(i,j)) being j all points from all the other cluster
            double bi = Double.POSITIVE_INFINITY;

            for (var str1 : ClusterCtrl.getInstance().getClusters()) {
                double b_temp = 0.0;
                if (str1 != cluster) {
                    b_temp = 1.0/str1.get_members().size();

                    parcial_sum = 0.0;
                    for (var str2 : str1.get_members()) {
                        parcial_sum += distance(str2);
                    }
                    b_temp *= parcial_sum;

                    if (b_temp < bi) bi = b_temp;
                }
            }

            si = (bi-ai)/Double.max(ai, bi);
        }
        
        if (Double.isNaN(si)) si = -1.0;

        return si;
    }
    
    protected abstract double distance(ClusterMember member);
}

package Domain.Recommendation.KMeansSlope1;

import java.util.ArrayList;
import java.util.Collection;

import Exceptions.ImplementationError;

//Author Alexandru Dumitru Maroz

public class ClusterCtrl {
    private static ClusterCtrl instance;
    private ArrayList<Cluster> clusters;
    private int k;

    private int k_forTesting = -1;

    static boolean initialized = false;

    private void create_clusters(ArrayList<Double> _origin) {
        clusters.clear();
        for (int i = 0; i < k; ++i) clusters.add(new Cluster(_origin));
    }
    
    private Collection<ClusterMember> get_all_members() {
        ArrayList<ClusterMember> temp = new ArrayList<ClusterMember>();

        for (var str : clusters) {
            temp.addAll(str.get_members());
        }

        return temp;
    }
    
    //Recalculate the centroid coordinates based of the mean of all his current members.
    private boolean recalculate_centroids() {
        boolean state = false;
        for (var str : clusters) state = state || str.recalculate_centroid();
        return state;
    }

    private void clear_clusters_members() {
        for (var str : clusters) {
            str.remove_all_members();
        }
    }

    //Updates the centroid to which each member, from the parameter, belongs. Aka. 1 iteration of kmeans.
    private void iterate_kmeans_algorithm(Collection<ClusterMember> _members) {
        if (clusters.size() == 0) {
            throw new ImplementationError("Calling updateCluster with 0 clusters (has ClusterCtrl been initialized?)");
        }
        
        _members.parallelStream().forEach(member -> {
            member.updateCluster(clusters);
        });
    }
    
    //Main code to ejecute Kmeans
    private void run_kmeans(int _max_iterations, Collection<ClusterMember> _members) {
        Boolean recalculated = true;

        while (_max_iterations > 0 && recalculated) {
            clear_clusters_members();
            iterate_kmeans_algorithm(_members);
            recalculated = recalculate_centroids();
            --_max_iterations;
        }
    }

    private ClusterCtrl() {
        //populating all default instances
        k = 2;
        clusters = new ArrayList<Cluster>();
    }

    public static ClusterCtrl getInstance() {
        if (instance == null) instance = new ClusterCtrl();
        return instance;
    }

    public Collection<Cluster> getClusters() { return (Collection<Cluster>)this.clusters; }

    public void set_k(Integer _k) { this.k = _k; }

    public void set_k_forTesting(Integer _k) { this.k_forTesting = _k; }

    public Integer get_k() { return this.k; }

    public double compute_silhouette() {
        //https://en.wikipedia.org/wiki/Silhouette_(clustering)
        //s(i) = (b(i)-a(i))/max{a(i),b(i)}, if |Ci| > 1
        //avgSI = mean of all si() member
        double avgSi = 0.0;

        Collection<ClusterMember> _members = get_all_members();

        if (clusters.size() == 0) {
            throw new ImplementationError("Calling compute_silhouette with 0 clusters (has ClusterCtrl been initialized?)");
        }else if (_members.size() == 0) {
            throw new ImplementationError("Calling compute_silhouette with 0 memmbers");
        }

        avgSi = _members.parallelStream()
                        .mapToDouble(member -> member.compute_silhouette())
                        .average()
                        .getAsDouble();

        return avgSi;
    }

    //Compute the total WSS from the current state of the clusters
    public double compute_wss() {
        double WSS = 0.0;

        Collection<ClusterMember> _members = get_all_members();

        if (clusters.size() == 0) {
            throw new ImplementationError("Calling compute_wss with 0 clusters (has ClusterCtrl been initialized?)");
        }else if (_members.size() == 0) {
            throw new ImplementationError("Calling compute_wss with 0 memmbers");
        }

        WSS = _members.parallelStream()
                        .mapToDouble(member -> member.compute_wss())
                        .sum();

        return WSS;
    }

    public void rebuildClusters(Collection<ClusterMember> _members, int _k) {
        set_k(_k);

        //Getting the center of coordinate from all the members based of the mean of all of them.
        Cluster temp = new Cluster();
        temp.add_all(_members);
        temp.recalculate_centroid();

        create_clusters(temp.coord);
        run_kmeans(15, _members);
        initialized = true;
    }

    public void updateClusters() {
        Collection<ClusterMember> members = get_all_members();
        run_kmeans(15, members);
    }
    
    public static void markDirty() {
        initialized = false;
    }
    
    public void updateKmeans(Collection<ClusterMember> members) {
        // In the future, this may be replaced with a smarter way to compute k
        final int MEMBERS_PER_CLUSTER = 2000;
        if (!initialized) {
            int k = Math.max(2, members.size()/MEMBERS_PER_CLUSTER);
            if (k_forTesting == -1) rebuildClusters(members, k);
            else rebuildClusters(members, k_forTesting);
        }
    }
}

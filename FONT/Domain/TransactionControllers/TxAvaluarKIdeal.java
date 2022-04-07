package Domain.TransactionControllers;

import java.util.ArrayList;
import java.util.Collection;
import Domain.Recommendation.KMeansSlope1.*;
import Domain.User;

public class TxAvaluarKIdeal {

    ArrayList<Double> result = new ArrayList<Double>();
    ArrayList<Double> times = new ArrayList<Double>();

    long time1 = 0;
    long time2 = 0;

    public TxAvaluarKIdeal() {

    }

    public void executeElbow() {
        result.clear();
        times.clear();
        Collection<ClusterMember> members = User.getAllMembers();
        ClusterCtrl cc = ClusterCtrl.getInstance();

        for (int i = 1; i <= 10; ++i) {
            cc.rebuildClusters(members, i);

            time1 = System.currentTimeMillis();
            result.add(cc.compute_wss());
            time2 = System.currentTimeMillis();

            times.add((double)(time2-time1));
        }
    }

    public void executeSilhoutte() {
        result.clear();
        times.clear();
        result.add(-1.0);
        times.add(0.0);
        Collection<ClusterMember> members = User.getAllMembers();
        ClusterCtrl cc = ClusterCtrl.getInstance();

        for (int i = 2; i <= 10; ++i) {
            cc.rebuildClusters(members, i);
            
            time1 = System.currentTimeMillis();
            result.add(cc.compute_silhouette());
            time2 = System.currentTimeMillis();

            times.add((double)(time2-time1));
        }
    }

    public ArrayList<Double> getResult() { return result; }
    public ArrayList<String> getResultString() { 
        ArrayList<String> temp = new ArrayList<String>();
        for (var str : result) temp.add(str.toString());
        return temp; 
    }

    public ArrayList<Double> getTimes() { return times; }
    public ArrayList<String> getTimesString() { 
        ArrayList<String> temp = new ArrayList<String>();
        for (var str : times) temp.add(str.toString());
        return temp; 
    }
}

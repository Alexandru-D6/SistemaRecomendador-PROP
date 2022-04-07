package Domain.TransactionControllers;

import java.util.ArrayList;
import java.util.Collection;

import Domain.User;
import Domain.Recommendation.KMeansSlope1.*;

public class TxTestPerformance {
    ArrayList<Double> result = new ArrayList<Double>();

    long time1 = 0;
    long time2 = 0;

    public TxTestPerformance() {
    }

    public void executeClusters() {
        result.clear();
        Collection<ClusterMember> members = User.getAllMembers();
        ClusterCtrl cc = ClusterCtrl.getInstance();

        for (int i = 1; i <= 10; ++i) {

            time1 = System.currentTimeMillis();
            cc.rebuildClusters(members, i);
            time2 = System.currentTimeMillis();

            result.add((double)(time2-time1));
        }
    }

    public ArrayList<Double> getResult() {
        return result;
    }

    public ArrayList<String> getResultString() {
        ArrayList<String> temp = new ArrayList<String>();
        for (var str : result) temp.add(String.valueOf(str));
        return temp;
    }
}

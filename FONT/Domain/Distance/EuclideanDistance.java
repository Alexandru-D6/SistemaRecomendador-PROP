// Author: Pol Rivero

package Domain.Distance;
import java.util.ArrayList;

public class EuclideanDistance implements DistanceStrategy {
    public double computeDistance(ArrayList<Double> vector) {
        double sum = 0.0;
        boolean valid = false;
        // Compute sum of squares
        for (Double d : vector) {
            if (!Double.isNaN(d)) {
                sum += d*d;
                valid = true;
            }
        }
        
        if (valid) return sum; // sqrt() could be added here
        return Double.NaN; // No valid item, return NaN
    }
}

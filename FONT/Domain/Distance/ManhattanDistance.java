// Author: Pol Rivero

package Domain.Distance;
import java.util.ArrayList;

public class ManhattanDistance implements DistanceStrategy {
    public double computeDistance(ArrayList<Double> vector) {
        double sum = 0.0;
        boolean valid = false;
        // Compute sum of distances
        for (Double d : vector) {
            if (!Double.isNaN(d)) {
                sum += Math.abs(d);
                valid = true;
            }
        }
        
        if (valid) return sum;
        return Double.NaN; // No valid item, return NaN
    }
}

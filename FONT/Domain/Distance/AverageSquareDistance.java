// Author: Pol Rivero

package Domain.Distance;
import java.util.ArrayList;

public class AverageSquareDistance implements DistanceStrategy {
    public double computeDistance(ArrayList<Double> vector) {
        double sum = 0.0;
        int size = 0;
        // Compiute sum of squares
        for (Double d : vector) {
            if (!Double.isNaN(d)) {
                sum += d*d;
                size++;
            }
        }
        
        if (size > 0) return sum / size;
        return Double.NaN; // No valid item, return NaN
    }
}

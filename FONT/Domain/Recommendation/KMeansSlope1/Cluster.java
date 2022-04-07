package Domain.Recommendation.KMeansSlope1;

import java.util.ArrayList;
import java.util.Map;
import java.util.Collection;

import Domain.ModelCtrl;
import Exceptions.ImplementationError;
import Utilities.RandomNumber;

//Author Alexandru Dumitru Maroz

public class Cluster {
    ArrayList<ClusterMember> members;
    ArrayList<Double> coord;

    public Cluster() {
        int size = ModelCtrl.getInstance().getItems().size();
        members = new ArrayList<ClusterMember>();
        coord = new ArrayList<Double>(size);
        
        // Randomize staring values
        for (int i = 0; i < size; i++) {
            coord.add(Double.NaN);
        }
        randomize_centroid(0.0, 1.0);
    }

    public Cluster(ArrayList<Double> _origin) {
        int size = ModelCtrl.getInstance().getItems().size();
        members = new ArrayList<ClusterMember>();
        coord = new ArrayList<Double>(size);

        for (int i = 0; i < size; ++i) coord.add(Double.NaN);

        randomize_centroid(-0.05, 0.05);

        Normalized_addBy(coord, _origin);
    }

    public Collection<ClusterMember> get_members() { return members; }

    public ArrayList<Double> getCoordinates() { return coord; }

    public void setCoordinates(ArrayList<Double> _coord) { this.coord = _coord; }

    public void remove_all_members() { members.clear(); }

    public int size() { return members.size(); }

    public void add_member(ClusterMember _member) {
        members.add(_member);
    }

    public void add_all(Collection<ClusterMember> _members) {
        members.clear();
        members.addAll(_members);
    }

    public boolean remove_member(ClusterMember _member) { return members.remove(_member); }
    
    //Divide the content of _suma by _divisor
    public static void divideBy(ArrayList<Double> _suma, ArrayList<Double> _divisor) {
        if (_suma.size() != _divisor.size()) {
            throw new ImplementationError("Dimensions of partial sum and divisor do not match");
        }
        for (int i = 0; i < _suma.size(); ++i) {
            Double divisor = _divisor.get(i);
            if (divisor == 0.0) _suma.set(i, Double.NaN);
            else _suma.set(i, _suma.get(i) / divisor);
        }
    }

    //Sum the content of sumando_b to sumando_a, ensuring that the values remains between [0.0-1.0]
    public static void Normalized_addBy(ArrayList<Double> _sumando_a, ArrayList<Double> _sumando_b) {
        if (_sumando_a.size() != _sumando_b.size()) {
            throw new ImplementationError("Dimensions of _sumando_a and _sumando_b do not match");
        }
        for (int i = 0; i < _sumando_b.size(); ++i) {
            _sumando_a.set(i, _sumando_a.get(i)+_sumando_b.get(i));

            double sumando = _sumando_a.get(i);

            if (sumando < 0.0) _sumando_a.set(i, 0.0);
            else if (sumando > 1.0) _sumando_a.set(i, 1.0);
        }
    }

    // Compute sum of all coordinares and pop
    public ArrayList<Double> sum(ArrayList<ClusterMember> _members, ArrayList<Double> _divisor) {
        ArrayList<Double> parcial_sum = new ArrayList<Double>(_divisor.size());
        for (int i = 0; i < _divisor.size(); ++i) {
            parcial_sum.add(0.0);
        }
        
        // Initialize the partial sum to 0
        for (int i = 0; i < parcial_sum.size(); ++i) {
            parcial_sum.set(i, 0.0);
        }
        
        for (var str : _members) {
            Map<Integer, Double> coords = str.getCoordinates();
            
            // For each coordinate, if the value is not NaN, add it to the partial sum and increment the divisor
            for (var str2 : coords.entrySet()) {
                int index = str2.getKey();
                parcial_sum.set(index, parcial_sum.get(index)+str2.getValue());
                _divisor.set(index, _divisor.get(index)+1.0);
            }
        }
        return parcial_sum;
    }

    //Compute the mean of members coordinates
    public ArrayList<Double> media_coordenadas() {
        int size = ModelCtrl.getInstance().getItems().size();
        ArrayList<Double> divisor = new ArrayList<Double>(size);
        for (int i = 0; i < size; ++i) {
            divisor.add(0.0);
        }

        ArrayList<Double> suma_parcial = sum(members, divisor);
        divideBy(suma_parcial, divisor);

        return suma_parcial;
    }

    private static Boolean check_coords(ArrayList<Double> centroid, ArrayList<Double> centroid_b, Double tolerancia) {
        for (int i = 0; i < centroid.size(); ++i) {
            // For each component of the centroid, if the difference is greater than the tolerance, return true
            if (Math.abs(centroid.get(i) - centroid_b.get(i)) > tolerancia) return true;
        }
        return false;
    }

    //
    boolean recalculate_centroid() {
        ArrayList<Double> coord_b = new ArrayList<Double>(coord);
        double tolerancia = 5.0/100.0; //5% room to avoid noise from recalculating kmeans and 1 member is at the border of two clusters.

        coord = media_coordenadas();
        
        for (int i = 0; i < coord.size(); ++i) {
            // If the new computed value is NaN, use the old value
            // (do not leave NaN, since members of other clusters may have rated this item)
            if (Double.isNaN(coord.get(i)))
                coord.set(i, coord_b.get(i));
        }

        return check_coords(coord, coord_b, tolerancia);
    }


    //Setting all NaN value to random values.
    public void randomize_centroid(double _min, double _max) {
        for (int i = 0; i < coord.size(); ++i) {
            if (Double.isNaN(coord.get(i)))
                coord.set(i, RandomNumber.randomDouble(_min, _max));
        }
    }

}

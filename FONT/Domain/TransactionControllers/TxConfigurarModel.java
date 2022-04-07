package Domain.TransactionControllers;

import java.util.ArrayList;

import Domain.ModelCtrl;
import Domain.Distance.*;
import Domain.Recommendation.RecommendationStrategy;
import Domain.Recommendation.Hybrid.Hybrid;
import Domain.Recommendation.KMeansSlope1.KMeansSlopeOne;
import Domain.Recommendation.KNN.KNearestNeighbours;
import Domain.Value.Attribute;
import Utilities.Pair;

// Tecnicament no es un controlador transaccio, pero mantinc el nom perque sigui coherent amb els altres

public class TxConfigurarModel {
    
    public String getDistanceFunction() {
        DistanceStrategy distanceStrategy = ModelCtrl.getInstance().getDistanceStrategy();
        
        if (distanceStrategy instanceof EuclideanDistance) {
            return "Euclidean";
        }
        else if (distanceStrategy instanceof ManhattanDistance) {
            return "Manhattan";
        }
        else if (distanceStrategy instanceof AverageDistance) {
            return "Average";
        }
        else if (distanceStrategy instanceof AverageSquareDistance) {
            return "AverageSquared";
        }
        else {
            throw new RuntimeException("Unknown distance strategy");
        }
    }
    
    public String getRecommendationAlgorithm() {
        RecommendationStrategy recommendationStrategy = ModelCtrl.getInstance().getRecommendationStrategy();
        
        if (recommendationStrategy instanceof KMeansSlopeOne) {
            return "KMeansSlope1";
        }
        else if (recommendationStrategy instanceof KNearestNeighbours) {
            return "KNearestNeighbours";
        }
        else if (recommendationStrategy instanceof Hybrid) {
            return "Hybrid";
        }
        else {
            throw new RuntimeException("Unknown recommendation strategy");
        }
    }
    
    public void setDistanceFunction(String distanceFunction) {
        ModelCtrl mc = ModelCtrl.getInstance();
        
        switch (distanceFunction.toLowerCase()) {
            case "euclidean":
                mc.setDistanceStrategy(new EuclideanDistance());
                break;
            case "manhattan":
                mc.setDistanceStrategy(new ManhattanDistance());
                break;
            case "average":
            case "avg":
                mc.setDistanceStrategy(new AverageDistance());
                break;
            case "averagesquared":
            case "averagesquare":
                mc.setDistanceStrategy(new AverageSquareDistance());
                break;
                
            default:
                throw new IllegalArgumentException("Unknown distance function: " + distanceFunction);
        }
    }
    
    public void setRecommendationAlgorithm(String recommendationAlgorithm) {
        ModelCtrl mc = ModelCtrl.getInstance();
        
        switch (recommendationAlgorithm.toLowerCase()) {
            case "kmeansslopeone":
            case "kmeansslope1":
            case "kmso":
            case "kms1":
                mc.setRecommendationStrategy(new KMeansSlopeOne());
                break;
            case "knearestneighbours":
            case "knearestneighbors":
            case "knn":
                mc.setRecommendationStrategy(new KNearestNeighbours());
                break;
            case "hybrid":
                mc.setRecommendationStrategy(new Hybrid());
                break;
                
            default:
                throw new IllegalArgumentException("Unknown recommendation algorithm: \"" + recommendationAlgorithm + "\"");
        }
    }
    
    
    // Return pair (name, weight) for each attribute
    public ArrayList< Pair<String,Double> > getAttributes() {
        ArrayList<Attribute> attributeList = ModelCtrl.getInstance().getAttributes();
        ArrayList< Pair<String,Double> > result = new ArrayList< Pair<String,Double> >(attributeList.size());
        
        for (Attribute attribute : attributeList) {
            result.add(new Pair<String,Double>(attribute.getName(), attribute.getWeight()));
        }
        return result;
    }
    
    public void setWeights(ArrayList<Double> weights) {
        ArrayList<Attribute> attributeList = ModelCtrl.getInstance().getAttributes();
        if (attributeList.size() != weights.size()) {
            throw new RuntimeException("Number of weights does not match number of attributes");
        }
        
        // iterate weights and set weights
        for (int i = 0; i < weights.size(); i++) {
            Double weight = weights.get(i);
            if (weight < 0) {
                throw new RuntimeException("Weights must be positive");
            }
            attributeList.get(i).setWeight(weight);
        }
    }
}

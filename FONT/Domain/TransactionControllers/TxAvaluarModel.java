package Domain.TransactionControllers;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import Domain.Item;
import Domain.ModelCtrl;
import Domain.UserRatings;
import Domain.Recommendation.RecommendationQuery;
import Exceptions.ImplementationError;
import Utilities.CSVReader;
import Utilities.RandomNumber;

public class TxAvaluarModel {
    
    static String pathCSVKnown = null;
    static String pathCSVUnknown = null;
    private boolean randomizeQ;
    
    private double result;
    
    public TxAvaluarModel() {
        this(true);
    }
    
    public TxAvaluarModel(boolean randomizeQ) {
        this.randomizeQ = randomizeQ;
    }
    
    public void execute() throws FileNotFoundException {
        if (pathCSVKnown == null) {
            throw new RuntimeException("The config file does not contain KNOWN (path to the CSV file with the known ratings)");
        }
        if (pathCSVUnknown == null) {
            throw new RuntimeException("The config file does not contain UNKNOWN (path to the CSV file with the unknown ratings)");
        }
        
        CSVReader reader = new CSVReader();
        ModelCtrl mc = ModelCtrl.getInstance();
        
        reader.open(pathCSVKnown);
        Collection<UserRatings> knowns = UserRatings.parseRatings(reader);
        
        reader.open(pathCSVUnknown);
        Collection<UserRatings> unknowns = UserRatings.parseRatings(reader);
        
        if (knowns.size() != unknowns.size()) {
            throw new RuntimeException("The known and unknown files contain a different number of queries");
        }
        
        double averageAccuracySum = 0;
        int averageAccuracyDivisor = 0;
        
        Iterator<UserRatings> knownsIt = knowns.iterator();
        Iterator<UserRatings> unknownsIt = unknowns.iterator();
        while (knownsIt.hasNext() && unknownsIt.hasNext()) {
            UserRatings known = knownsIt.next();
            UserRatings unknown = unknownsIt.next();
            
            if (unknown.getRatings().size() == 0) {
                throw new ImplementationError("One of the queries has size 0 (no items in query)!");
            }
            
            // If randomizeQ is false, the size of the query (Q) is the number of items in the query (number of items in unknown)
            int Q = unknown.getRatings().size();
            // Otherwise, Compute a random Q between 1 and the size of the query, both included
            if (randomizeQ) Q = RandomNumber.randomInt(1, Q);
            
            RecommendationQuery query = RecommendationQuery.buildQuery(known, unknown, Q);
            ArrayList<Item> result = mc.getRecommendation(query);
            
            averageAccuracySum += query.normalizedDCG(result);
            averageAccuracyDivisor++;
        }
        
        result = averageAccuracySum / averageAccuracyDivisor;
        
        System.gc();
    }
    
    public double getResult() {
        return result;
    }
}

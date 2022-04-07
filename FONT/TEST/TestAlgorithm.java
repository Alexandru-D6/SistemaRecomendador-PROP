package TEST;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import Domain.TransactionControllers.TxLlegirCSV;
import Exceptions.ObjectDoesNotExistException;
import Utilities.Pair;
import Domain.Item;
import Domain.ModelCtrl;
import Domain.DataInterface.DBFactory;
import Domain.Recommendation.RecommendationQuery;

public class TestAlgorithm {
    public static void main(String[] args)  {
        if (args.length != 2) {
            System.err.println("ERROR. Expected 2 arguments (see readme):");
            System.err.println("dataset.info fileQueries.txt");
            System.exit(0);
        }
        
        TxLlegirCSV txlc = new TxLlegirCSV(args[0]);
        
        System.err.println("Initializing model, please wait...");
        try {
            txlc.execute();
        } catch (FileNotFoundException e) {
            System.err.println("ERROR: File could not be opened: " + e.getMessage());
            System.exit(0);
        }
        catch (RuntimeException e) {
            System.err.println("There has been an error:");
            System.err.println(e.getMessage());
            System.exit(0);
        }
        
        Scanner sc = init_scanner(args[1]);
        
        int nrQuerry = -1;
        nrQuerry = sc.nextInt();
        
        System.err.println("Computing recommendations...");
        for (int i = 0; i < nrQuerry; i++) {
            read_query_txt(sc);
        }
        System.err.println("Done.");
    }
    
    private static void print_arraylist(ArrayList<Item> a) {
        for (var str : a) {
            System.out.print(str.getId() + " ");
        }
        System.out.println();
    }
    
    private static Scanner init_scanner(String query) {
        Scanner sc = null;
        try {
            sc = new Scanner(new File(query));
        } catch (FileNotFoundException e) {
            System.err.println("ERROR: File could not be opened: " + e.getMessage());
            System.exit(0);
        }
        return sc;
    }

    private static void read_query_txt(Scanner sc) {
        int userId = sc.nextInt();
        int nrknown = sc.nextInt();
        int nrunkown = sc.nextInt();
        int Q = sc.nextInt();
        
        ArrayList<Pair<Item,Double>> known = new ArrayList<Pair<Item,Double>>();
        ArrayList<Item> unknown = new ArrayList<Item>();
        
        for (int i = 0; i < nrknown; ++i) {
            int itemId = sc.nextInt();
            Item item = null;
            try {
                item = DBFactory.getInstance().getItemDB().get(itemId);
            } catch (ObjectDoesNotExistException e) {
                System.err.println("ERROR: Queries file references an Item that does not exist: id=" + itemId);
                System.exit(0);
            }
            double val = sc.nextDouble();
            known.add(new Pair<Item, Double>(item, val));
        }
        
        for (int i = 0; i < nrunkown; ++i) {
            int itemId = sc.nextInt();
            Item item = null;
            try {
                item = DBFactory.getInstance().getItemDB().get(itemId);
            } catch (ObjectDoesNotExistException e) {
                System.err.println("ERROR: Queries file references an Item that does not exist: id=" + itemId);
                System.exit(0);
            }
            unknown.add(item);
        } 
        
        RecommendationQuery query = RecommendationQuery.buildQuery(userId, known, unknown, Q);
        ArrayList<Item> result = ModelCtrl.getInstance().getRecommendation(query);
        
        print_arraylist(result);
    }
}

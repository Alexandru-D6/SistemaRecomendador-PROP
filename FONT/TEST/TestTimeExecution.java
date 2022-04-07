package TEST;

//Author Alexandru Dumitru Maroz

import java.io.FileNotFoundException;
import Domain.TransactionControllers.*;
import java.util.Collection;

import Domain.User;
import java.util.ArrayList;
import Domain.Recommendation.KMeansSlope1.ClusterMember;

//Tester per observar els temps de executió de tots els algoritmes.
//Els resultats seran la mitjana de temps per ejecutar el algoritme complet per tots els Items y Users.

// Objecte de la prova: KmeansSlopeOne, KNN, Hybrid.
// Altres elements integrats: TxLectorCSV, TxObtenirRecomanacions.
// Fitxers de dades necessaris: Qualsevol DataSet (items y ratings).
// Valors estudiats: Son probes de caixa negra, ja que el que són interessa és el valor final del que hauria de retornar.
// Efectes estudiats: Velocitat dels algoritmes.
// Operativa: Executar aquest programa a través del comando make.

public class TestTimeExecution {

    public static void main(String[] args) {

        if (args.length != 2) {
            System.err.println("ERROR. Expected 2 arguments (see readme):");
            System.err.println("dataset.info testSize");
            System.exit(0);
        }

        int TestSize = Integer.valueOf(args[1]);

        long time1 = System.currentTimeMillis();
        TxLlegirCSV txlcsv = new TxLlegirCSV(args[0]);
        try {
            txlcsv.execute();
            System.out.println("Dataset loaded!");
            System.out.println();
        }
        catch (FileNotFoundException e) {
            System.out.println("Error!");
            System.out.println("Could not open the file " + e.getMessage());
            System.out.println("Aborting...");
            System.exit(1);
        }
        long time2 = System.currentTimeMillis();
        System.out.println("Time elapsed to charge the dataset: " + (time2-time1) + "ms");

        String[] algorithms = new String[]{"KMeansSlope1", "KNearestNeighbours", "Hybrid"};
        String[] distances = new String[]{"Euclidean", "Manhattan", "Average", "AverageSquared"};
        ArrayList<Long> results = new ArrayList<>();
        for (int i = 0; i < 3*4; ++i) results.add(0L);

        Collection<ClusterMember> members = new ArrayList<ClusterMember>();
        ArrayList<Integer> userIds = new ArrayList<>();
        members = User.getAllMembers();

        for (var str : members) {
            User u = (User)str;
            if (u.getRatings().size() > 0) userIds.add(u.getId());
        }

        
        for (int i = 0; i < 3; ++i) {
            TxConfigurarModel txcm = new TxConfigurarModel();
            txcm.setRecommendationAlgorithm(algorithms[i]);

            for (int j = 0; j < 4; ++j) {
                txcm.setDistanceFunction(distances[j]);

                try {
                    runRecommendation(results, i*4+j, userIds, TestSize);
                } catch (Exception e) {
                    System.out.println(e.getStackTrace());
                }
            }
        }

        System.out.println("Time elapsed to execute all the algorithms and distances:");
        for (int i = 0; i < 3; ++i) {
            System.out.println("  " + algorithms[i] + ": ");
            for (int j = 0; j < 4; ++j) {
                System.out.println("    - " + distances[j] + ": " + results.get(i*4+j) + " ms.");
            }
        }
        System.out.println("\n(*)This times represents the mean of time taken to compute the recomendations for\n" +
                            "all the items that the user doesn't rated.\n");
    }

    public static void runRecommendation(ArrayList<Long> results, int index, ArrayList<Integer> members, int TestSize){
        for (int i = 0; i < TestSize; ++i) {
            System.out.println((index+1) + "/" + results.size() + " --- " + (i+1) + "/" + TestSize);
            
            TxGetterPresentation txgp = new TxGetterPresentation();
            ArrayList<Integer> idItemsQuery = txgp.getItemsNotValorated(members.get(i));

            TxObtenirRecomanacions txor = new TxObtenirRecomanacions(members.get(i), idItemsQuery, idItemsQuery.size());

            long time1 = System.currentTimeMillis();
            txor.execute();
            long time2 = System.currentTimeMillis();

            results.set(index, results.get(index) + (time2-time1));
            
        };

        results.set(index, results.get(index)/TestSize);
    }
}

package TEST;
import Domain.Recommendation.KMeansSlope1.ClusterCtrl;
import Domain.TransactionControllers.*;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

//Tester que comprova el correcte funcionament dels algorismes, 
//utilitza un dataset generat especialment per a aquesta prova, concretament el Kmeans2D db3.

// Objecte de la prova: KmeansSlopeOne, KNN, Hybrid.
// Altres elements integrats: TxLectorCSV, TxObtenirRecomanacions.
// Fitxers de dades necessaris: DataSet Kmeans2D construit per aquestes proves (items y ratings).
// Valors estudiats: Son probes de caixa negra, ja que el que són interessa és el valor final del que hauria de retornar.
// Efectes estudiats: Precisió y correct funcionament dels algoritmes.
// Operativa: Executar aquest programa a través del comando make.

public class Main_TestingAllAlgorithms {
    public static void main(String[] args)  {
        TxLlegirCSV txlcsv = new TxLlegirCSV("Kmeans2D/dataset3.info");
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

        ArrayList<ArrayList<Integer>> rawResults = new ArrayList<>();
        ArrayList<Integer> results = new ArrayList<>();
        boolean test = false;

        rawResults = new ArrayList<>();
        for (int i = 0; i < 20; ++i) rawResults.add(testKmeansSlopeOne());
        results = getResults(rawResults);
        test = (results.get(0) == 3 && results.get(1) == 4 && results.get(2) == 5 && results.get(3) == 6);
        System.out.println("KmeansSlopeOne test passed: " + (test ? "true" : "false") + ".");

        rawResults = new ArrayList<>();
        for (int i = 0; i < 20; ++i) rawResults.add(testKNN());
        results = getResults(rawResults);
        test = (results.get(0) == 3 && results.get(1) == 5);
        System.out.println("KNN test passed: " + (test ? "true" : "false") + ".");

        rawResults = new ArrayList<>();
        for (int i = 0; i < 20; ++i) rawResults.add(testHybrid());
        results = getResults(rawResults);
        test = (results.get(0) == 3 && results.get(1) == 4 && results.get(2) == 5 && results.get(3) == 6);
        System.out.println("Hybrid test passed: " + (test ? "true" : "false") + ".");
    }

    private static ArrayList<Integer> getResults(ArrayList<ArrayList<Integer>> a) {
        ArrayList<Integer> results = new ArrayList<>();

        Map<Integer, Integer> temp = new HashMap<Integer, Integer>();
        for (int i = 0; i < a.get(0).size(); ++i) {
            for (int j = 0; j < 10; ++j) {
                int key = a.get(j).get(i);
                if (temp.get(key) != null) temp.put(key, temp.get(key) + 1);
                else temp.put(key, 1);
            }

            int maxId = 0;
            int max = -1;

            for (var str : temp.entrySet()) {
                if (str.getValue() > max) {
                    maxId = str.getKey();
                    max = str.getValue();
                }
            }

            results.add(maxId);
            temp.clear();
        }

        return results;
    }

    private static ArrayList<Integer> testHybrid() {
        ArrayList<Integer> res = new ArrayList<>();
        TxConfigurarModel txcm = new TxConfigurarModel();
        txcm.setRecommendationAlgorithm("Hybrid");
        ClusterCtrl.getInstance().set_k_forTesting(4);

        TxCrearUsuari txcu = new TxCrearUsuari();
        TxObtenirRecomanacions txor;
        TxEliminarUsuari txeu;

        ArrayList<Integer> query = new ArrayList<>();
        query.add(3);
        query.add(4);
        query.add(5);
        query.add(6);

        txcu.execute();
        int idUser = txcu.getResult();
        //For this user, the algorithm should recommend the item 3
        TxValorarItem txvi = new TxValorarItem(idUser, 1, 1.0);
        txvi.execute();
        txvi = new TxValorarItem(idUser, 2, 4.0);
        txvi.execute();

        txor = new TxObtenirRecomanacions(idUser, query, 1);
        txor.execute();
        res.add(txor.getResult().get(0));
        txeu = new TxEliminarUsuari(idUser);
        txeu.execute();

        
        txcu.execute();
        idUser = txcu.getResult();
        //For this user, the algorithm should recommend the item 4
        txvi = new TxValorarItem(idUser, 1, 1.0);
        txvi.execute();
        txvi = new TxValorarItem(idUser, 2, 1.0);
        txvi.execute();

        txor = new TxObtenirRecomanacions(idUser, query, 1);
        txor.execute();
        res.add(txor.getResult().get(0));
        txeu = new TxEliminarUsuari(idUser);
        txeu.execute();


        txcu.execute();
        idUser = txcu.getResult();
        //For this user, the algorithm should recommend the item 5
        txvi = new TxValorarItem(idUser, 1, 4.0);
        txvi.execute();
        txvi = new TxValorarItem(idUser, 2, 4.0);
        txvi.execute();

        txor = new TxObtenirRecomanacions(idUser, query, 1);
        txor.execute();
        res.add(txor.getResult().get(0));
        txeu = new TxEliminarUsuari(idUser);
        txeu.execute();

        txcu.execute();
        idUser = txcu.getResult();
        //For this user, the algorithm should recommend the item 6
        txvi = new TxValorarItem(idUser, 1, 4.0);
        txvi.execute();
        txvi = new TxValorarItem(idUser, 2, 1.0);
        txvi.execute();

        txor = new TxObtenirRecomanacions(idUser, query, 1);
        txor.execute();
        res.add(txor.getResult().get(0));
        txeu = new TxEliminarUsuari(idUser);
        txeu.execute();

        ClusterCtrl.getInstance().set_k_forTesting(-1);

        return res;
    }

    private static ArrayList<Integer> testKNN() {
        ArrayList<Integer> res = new ArrayList<>();
        TxConfigurarModel txcm = new TxConfigurarModel();
        txcm.setRecommendationAlgorithm("knn");

        TxCrearUsuari txcu = new TxCrearUsuari();
        TxObtenirRecomanacions txor;
        TxEliminarUsuari txeu;

        ArrayList<Integer> query = new ArrayList<>();
        query.add(3);
        query.add(4);
        query.add(5);
        query.add(6);

        txcu.execute();
        int idUser = txcu.getResult();
        //For this user, the algorithm should recommend the item 3
        TxValorarItem txvi = new TxValorarItem(idUser, 1, 4.0);
        txvi.execute();

        txor = new TxObtenirRecomanacions(idUser, query, 1);
        txor.execute();
        res.add(txor.getResult().get(0));
        txeu = new TxEliminarUsuari(idUser);
        txeu.execute();


        txcu.execute();
        idUser = txcu.getResult();
        //For this user, the algorithm should recommend the item 5
        txvi = new TxValorarItem(idUser, 2, 5.0);
        txvi.execute();

        txor = new TxObtenirRecomanacions(idUser, query, 1);
        txor.execute();
        res.add(txor.getResult().get(0));
        txeu = new TxEliminarUsuari(idUser);
        txeu.execute();

        return res;
    }

    private static ArrayList<Integer> testKmeansSlopeOne() {
        ArrayList<Integer> res = new ArrayList<>();
        TxConfigurarModel txcm = new TxConfigurarModel();
        txcm.setRecommendationAlgorithm("kmeansslopeone");
        ClusterCtrl.getInstance().set_k_forTesting(4);

        TxCrearUsuari txcu = new TxCrearUsuari();
        TxObtenirRecomanacions txor;
        TxEliminarUsuari txeu;

        ArrayList<Integer> query = new ArrayList<>();
        query.add(3);
        query.add(4);
        query.add(5);
        query.add(6);

        txcu.execute();
        int idUser = txcu.getResult();
        //For this user, the algorithm should recommend the item 3
        TxValorarItem txvi = new TxValorarItem(idUser, 1, 0.0);
        txvi.execute();
        txvi = new TxValorarItem(idUser, 2, 0.0);
        txvi.execute();

        txor = new TxObtenirRecomanacions(idUser, query, 1);
        txor.execute();
        res.add(txor.getResult().get(0));
        txeu = new TxEliminarUsuari(idUser);
        txeu.execute();


        txcu.execute();
        idUser = txcu.getResult();
        //For this user, the algorithm should recommend the item 4
        txvi = new TxValorarItem(idUser, 1, 0.0);
        txvi.execute();
        txvi = new TxValorarItem(idUser, 2, 5.0);
        txvi.execute();

        txor = new TxObtenirRecomanacions(idUser, query, 1);
        txor.execute();
        res.add(txor.getResult().get(0));
        txeu = new TxEliminarUsuari(idUser);
        txeu.execute();


        txcu.execute();
        idUser = txcu.getResult();
        //For this user, the algorithm should recommend the item 5
        txvi = new TxValorarItem(idUser, 1, 5.0);
        txvi.execute();
        txvi = new TxValorarItem(idUser, 2, 0.0);
        txvi.execute();

        txor = new TxObtenirRecomanacions(idUser, query, 1);
        txor.execute();
        res.add(txor.getResult().get(0));
        txeu = new TxEliminarUsuari(idUser);
        txeu.execute();


        txcu.execute();
        idUser = txcu.getResult();
        //For this user, the algorithm should recommend the item 6
        txvi = new TxValorarItem(idUser, 1, 5.0);
        txvi.execute();
        txvi = new TxValorarItem(idUser, 2, 5.0);
        txvi.execute();

        txor = new TxObtenirRecomanacions(idUser, query, 1);
        txor.execute();
        res.add(txor.getResult().get(0));
        txeu = new TxEliminarUsuari(idUser);
        txeu.execute();

        ClusterCtrl.getInstance().set_k_forTesting(-1);

        return res;
    }
}

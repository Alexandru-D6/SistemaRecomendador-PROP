package TEST;
import Domain.User;
import Domain.Recommendation.KMeansSlope1.ClusterCtrl;
import Domain.TransactionControllers.*;
import Utilities.Pair;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import Domain.Recommendation.KMeansSlope1.Cluster;
import Domain.Recommendation.KMeansSlope1.ClusterMember;

//Tester per a observar si el clúster agrupa els usuaris de manera correcta.
//El resultat és l'agrupacions dels usuaris entre els diferents clúster i grandàries d'aquests.
//Destacar que per la naturalesa de l'algorisme, per utilitzar coordenades random al principi i 
//entre execucions pot succeir que el clúster no l'agrupi correctament. Per a comprovar el 
//correcte funcionament caldria executar-lo diverses vegades per a corroborar la decisió final.

// Objecte de la prova: Kmeans.
// Altres elements integrats: Cap.
// Fitxers de dades necessaris: DataSet Kmeans2D (items, ratings.db, ratings.db2).
// Valors estudiats: És una prova Grisa, on treballem amb un dataset reduït amb el 
//                   propòsit de saber com serà l'agrupació després de l'execució.
// Efectes estudiats: Agrupació del dataset.
// Operativa: Executar aquest programa a través del comando make.

public class Main_TestingClusterAlgorithm {

    static void upload_4C_database() {
        TxLlegirCSV txlcsv = new TxLlegirCSV("Kmeans2D/dataset1.info");
        try {
            txlcsv.execute();
            System.out.println("Kmeans2D loaded!");
            System.out.println();
        }
        catch (FileNotFoundException e) {
            System.out.println("Error!");
            System.out.println("Could not open the file " + e.getMessage());
            System.out.println("Aborting...");
            System.exit(1);
        }
    }

    static void upload_5C_database() {
        TxLlegirCSV txlcsv = new TxLlegirCSV("Kmeans2D/dataset2.info");
        try {
            txlcsv.execute();
            System.out.println("Kmeans2D loaded!");
            System.out.println();
        }
        catch (FileNotFoundException e) {
            System.out.println("Error!");
            System.out.println("Could not open the file " + e.getMessage());
            System.out.println("Aborting...");
            System.exit(1);
        }
    }

    private static void print_members(Collection<ClusterMember> members) {
        for (var str : members) {
            User u = (User) str;
            System.out.print(u.getId() + " ");
        }
        System.out.println();
    }

    private static void print_clusters(Collection<Cluster> cs, int _k) {
        int i = 0;
        System.out.println("Clustering by " + _k + " centroids:");
        for (var str : cs) {
            System.out.print("Cluster " + i + " has: ");
            print_members(str.get_members());
            ++i;
        }
        System.out.println();
    }

    static void test_Kmeans(int groups) {
        ClusterCtrl cc = ClusterCtrl.getInstance();
        Collection<ClusterMember> members = User.getAllMembers();
        
        ArrayList<Pair<Double,Double>> result = new ArrayList<Pair<Double,Double>>();
        
        cc.rebuildClusters(members, 1);
        result.add(new Pair<Double,Double>(-1.0, cc.compute_wss()));
        print_clusters(cc.getClusters(), 1);

        for (int i = 2; i <= 10; ++i) {
            cc.rebuildClusters(members, i);
            double silh = cc.compute_silhouette();
            double wss = cc.compute_wss();
            result.add(new Pair<Double,Double>(silh, wss));
            print_clusters(cc.getClusters(), i);
            
        }

        System.out.println("iteration -- Silhouette -- Elbow");
        for (int i = 0; i < result.size(); ++i) {
            System.out.println((i+1) + " -- " + result.get(i).first + " -- " + result.get(i).second );
        }

        System.out.println();
        System.out.println("Check that the silhouette parameter start to be maximum at " + groups + " with this DataSet.");
        System.out.println("Also check that the elbow parameter is minimum at " + groups + ", or represent with a graphic.");
        System.out.println("You can also see how all the members are consecutive, in other words, in the same cluster would be the members named from 1 to " + groups + ".");
        System.out.println();

    }
    public static void main(String[] args)  {

        System.out.println();
        System.out.println("What do you want to do?");
        System.out.println(" > Check for clustering 4 group. Option clustering4Group");
        System.out.println(" > Check for clustering 5 group. Option clustering5Group");
        System.out.println(" > Exit. Option Exit");
        System.out.println();
        System.out.print(">");

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String terminal = new String();
        try {
            terminal = reader.readLine();
        } catch (Exception e) {}

        while (!terminal.toUpperCase().equals("EXIT")) {
            switch(terminal.toUpperCase()) {
                case "CLUSTERING4GROUP": {
                    upload_4C_database();
                    test_Kmeans(4);
                    return;
                }
                
                case "CLUSTERING5GROUP": {
                    upload_5C_database();
                    test_Kmeans(5);
                    return;
                }

                default: {
                    System.out.println("Uknown command, try again.");
                    System.out.println();
                    System.out.print(">");
                    break;
                }
            }

            try {
                terminal = reader.readLine();
            } catch (Exception e) {}
        }
    }
}

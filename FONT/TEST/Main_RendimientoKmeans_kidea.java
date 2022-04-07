package TEST;
//Author Alexandru Dumitru Maroz

import java.io.FileNotFoundException;
import Domain.TransactionControllers.*;
import java.util.Collection;
import Exceptions.ObjectAlreadyExistsException;
import Exceptions.ObjectDoesNotExistException;

import Domain.User;
import java.util.ArrayList;
import Domain.Recommendation.KMeansSlope1.ClusterCtrl;
import Domain.Recommendation.KMeansSlope1.ClusterMember;

//Tester para observar los tiempos de ejecucion del Kmeans, Elbow and Silhouette method.
//El resultado sera por una parte el tiempo de ejecucion y por la otra la salida de los dos metodos 
//para averiguar la k ideal para el data set 2250 (aunque se le puede aplicar a cualquier data set)

// Objecte de la prova: Kmeans
// Altres elements integrats: TxLectorCSV, Cluster
// Fitxers de dades necessaris: Cualquier DataSet (items y ratings)
// Valors estudiats: Son pruebas de caja negra, ya que lo que son interesa es el valor final de lo que tendria que devolver
//                      (en cuanto a elbow y silhouette) .
// Efectes estudiats: Velocidad de los algoritmos
// Operativa: Ejecutar este programa a traves del comando make.

class Main_RendimientoKmeans_kidea {
    public static void main(String[] args) throws FileNotFoundException, ObjectAlreadyExistsException, ObjectDoesNotExistException {
        long time1 = System.currentTimeMillis();
        TxLlegirCSV tx = new TxLlegirCSV("Movielens/2250/dataset.info");
        tx.execute();
        long time2 = System.currentTimeMillis();
        System.out.println("Time elapsed: " + (time2-time1) + "ms");
        runKmeans();
    }

    public static void runKmeans() throws FileNotFoundException, ObjectAlreadyExistsException, ObjectDoesNotExistException {
        Collection<ClusterMember> members = new ArrayList<ClusterMember>();

        members = User.getAllMembers();

        ClusterCtrl cc = ClusterCtrl.getInstance();

        long time1 = System.currentTimeMillis();
        cc.rebuildClusters(members, 1);
        long time2 = System.currentTimeMillis();
        System.out.println(1 + " -1.0 " + cc.compute_wss());
        System.out.println("Time elapsed: " + (time2-time1) + "ms");

        for (int i = 2; i <= 10; ++i) {
            time1 = System.currentTimeMillis();
            cc.rebuildClusters(members, i);
            time2 = System.currentTimeMillis();
            long algorithm = time2-time1;

            //comentar este fragmento de codigo, junto a la linea 68, para dejar de calcular silhouette y de esta manera ejecutar el algoritmo mas rapido
            time1 = System.currentTimeMillis();
            // System.out.print(i + " " + cc.compute_silhouette());
            time2 = System.currentTimeMillis();
            long silhouette = time2-time1;

            time1 = System.currentTimeMillis();
            System.out.println(i +" " + cc.compute_wss());
            time2 = System.currentTimeMillis();
            long wss = time2-time1;

            System.out.println("Time elapsed for the algorithm: " + algorithm + "ms");
            System.out.println("Time elapsed for the silhouette: " + silhouette + "ms");
            System.out.println("Time elapsed for the wss: " + wss + "ms");
        }
        System.out.println("hey");
    }
}

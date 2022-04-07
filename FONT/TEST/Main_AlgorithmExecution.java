package TEST;
import java.util.Scanner;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import Domain.TransactionControllers.*;

import Domain.User;
import Domain.ModelCtrl;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import Exceptions.ImplementationError;
import Exceptions.ObjectAlreadyExistsException;
import Domain.Recommendation.KMeansSlope1.*;
import Domain.Recommendation.KNN.KNearestNeighbours;
import Utilities.CSVReader;
import Domain.UserRatings;

import java.io.BufferedReader;
import java.io.InputStreamReader;

//Tester para ejecutar de manera rapida los algoritmos de recomendacion.
//El resultado puede ser tanto un recomendacion para el usuario como 
//el valor DCG de los dos algoritmos para un DataSet concreto.

// Objecte de la prova: KmeansSlopeOne y KNearestNeighbours
// Altres elements integrats: TxLectorCSV, Cluster, SlopeOne, KNN
// Fitxers de dades necessaris: Cualquier DataSet (items y ratings) y el commandsAE
// Valors estudiats: Son pruebas de caja negra, ya que estamos comprobando el output final de cada clase.
// Efectes estudiats: Ejecucion de los algoritmos
// Operativa: Ejecutar este programa a traves del comando make.

public class Main_AlgorithmExecution {

    static String dataset = "Movielens/2250"; //or Series/2250
    static Scanner capt;
    static String idName = "id";
    static double maxRating = 5.0; //or 10.0 in the case of series

    static void evaluate() {
        System.out.println("Evaluating model...");
        ModelCtrl.getInstance().setRecommendationStrategy(new KMeansSlopeOne());
        TxAvaluarModel txam = new TxAvaluarModel(false);
        try {
            txam.execute();
            System.out.println();
        }
        catch (FileNotFoundException e) {
            System.out.println("Error!");
            System.out.println("Could not open the file " + e.getMessage());
            System.out.println("Aborting...");
            System.exit(1);
        }

        System.out.println("DCG for KmeansSlopeOne: " + txam.getResult());

        ModelCtrl.getInstance().setRecommendationStrategy(new KNearestNeighbours());

        try {
            txam.execute();
            System.out.println();
        }
        catch (FileNotFoundException e) {
            System.out.println("Error!");
            System.out.println("Could not open the file " + e.getMessage());
            System.out.println("Aborting...");
            System.exit(1);
        }

        System.out.println("DCG for KNearestNeighbours: " + txam.getResult());
    }

    static void getRecommendation() {
        CSVReader csvreader = new CSVReader();
        try {
            csvreader.open("DATA/" + dataset + "/ratings.test.unknown.csv");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e.getMessage());
        }
        Collection<UserRatings> unknowns = UserRatings.parseRatings(csvreader);

        try {
            csvreader.open("DATA/" + dataset + "/ratings.test.known.csv");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e.getMessage());
        }
        Collection<UserRatings> knowns = UserRatings.parseRatings(csvreader);

        System.out.println();
        System.out.println("Select an user:");

        Set<Integer> allUsers = new HashSet<Integer>();
        for (var str : unknowns) {
            System.out.print(str.getUserId() + " ");
            allUsers.add(str.getUserId());
        }

        System.out.println();
        System.out.print(">");

        int userId = -1;

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        while (userId == -1) {
            try {
                userId = Integer.parseInt(reader.readLine());
            } catch (Exception e) {}

            if (!allUsers.contains(userId)) {
                userId = -1;
                System.out.println("Incorrect userId, try again.");
                System.out.println();
                System.out.print(">");
            }
        }

        ModelCtrl.getInstance().setRecommendationStrategy(new KMeansSlopeOne());
        
        if (!User.exist(userId)) {
            for (var str : knowns) {
                if (str.getUserId() == userId) {
                    // Now create the user with the list of items
                    try {
                        // The new user is not an active user
                        new User(userId, true, str.getRatings());
                    }
                    catch (ObjectAlreadyExistsException e) {
                        throw new RuntimeException("The User with ID " + userId + " aleady exists (is the CSV grouped by userId?)");
                    }
                }
            }
        }

        ArrayList<Integer> query = new ArrayList<Integer>();
        for (var str : unknowns) {
            if (str.getUserId() == userId) {
                for (var str2 : str.getRatings()) query.add(str2.first.getId());
            }
        }

        TxObtenirRecomanacions txor = new TxObtenirRecomanacions(userId, query, query.size());
        txor.execute();
        System.out.println("Recommended items by KmeansSlopeOne");
        txor.printResult();

        ModelCtrl.getInstance().setRecommendationStrategy(new KNearestNeighbours());
        txor.execute();
        System.out.println("Recommended items by KNearestNeighbours");
        txor.printResult();
    }


    public static void main(String[] args) {

        TxLlegirCSV txlc = new TxLlegirCSV(dataset + "/dataset.info");
        try {
            System.out.println("Loading DataSet...");
            txlc.execute();
        }catch (FileNotFoundException e) {
            throw new ImplementationError(e.getMessage());
        }
        System.out.println("Done!");

        evaluate();

        while (true) {
            getRecommendation();
        }
    }
}

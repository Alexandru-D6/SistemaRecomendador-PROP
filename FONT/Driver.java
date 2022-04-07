import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;

import Domain.TransactionControllers.*;
import Utilities.Pair;

//Author Ferran De La Varga and verified and retouched by the rest of the team

public class Driver  {
        
    static String dataset = null;
    static Scanner capt;
    static String idName = null;
    static double maxRating = -1;
    
    static private void init() {
        System.out.println("Welcome to the driver!");
        String[] line;
        
        System.out.println("Please enter the name of your dataset file (relative to DATA):");
        while (dataset == null) {
            System.out.print(">");
            line = getLine();
            if (line.length > 0) {
                dataset = line[0];
            }
        }
        
        System.out.println("Initializing model, please wait... ");
        TxLlegirCSV txlcsv = new TxLlegirCSV(dataset);
        try {
            long startTime = System.nanoTime();
            txlcsv.execute();
            long stopTime = System.nanoTime();
            System.out.println("Done! Elapsed: " + (stopTime - startTime) / 1000000000.0 + " seconds");
            System.out.println();
        }
        catch (FileNotFoundException e) {
            System.out.println("Error!");
            System.out.println("Could not open the file " + e.getMessage());
            System.out.println("Aborting...");
            System.exit(1);
        }
        catch (RuntimeException e) {
            System.out.println("\nThere has been an error:");
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }
    
    
    static private void print_attrs() {
        TxConfigurarModel tx = new TxConfigurarModel();
        ArrayList<Pair<String, Double>> attr = tx.getAttributes();
        System.out.println("INDEX. NAME: WEIGHT");
        for (int i = 0; i < attr.size(); i++) {
            System.out.println(i + ". " + attr.get(i).first + ": " + attr.get(i).second);
        }
    }
    
    static private void print_commands() {
        System.out.println("Commands (case-insensitive):");
        System.out.println();
        System.out.println("CONFIGURE_MODEL");
        System.out.println("EVALUATE");
        System.out.println("SEE_ATTR");
        System.out.println("NEW_ATTR oldAttrIndex newName");
        System.out.println("DELETE_ATTR index");
        System.out.println("SET_WEIGHT attrIndex newWeight");
        System.out.println("NEW_ITEM value[0]..value[n-1]");
        System.out.println("DELETE_ITEM id");
        System.out.println("NEW_USER");
        System.out.println("DELETE_USER id");
        System.out.println("RATE_ITEM idUser idItem rating");
        System.out.println("SET_DISTANCE {Euclidean, Manhattan, Average, AverageSquare}");
        System.out.println("SET_ALGORITHM {KNN, KMS1, Hybrid}");
        System.out.println("RECOMMEND idUsuari idItemsQuery[0]..idItemsQuery[n-1] Q");
        System.out.println("SAVE_DATA datasetName");
        System.out.println("HELP");
        System.out.println("EXIT");
    }
    
    private static Scanner cmdFile = null;
    static private String[] getLine() {
        String input;
        if (cmdFile != null && cmdFile.hasNextLine()) {
            input = cmdFile.nextLine();
            // If first character of input (ignoring whitespace) is '#', ignore the line
            String temp = input.trim();
            if (temp.length() == 0 || temp.charAt(0) == '#') {
                return getLine();
            }
            // Print the command
            System.out.println(input);
        }
        else {
            input = capt.nextLine();
        }
        
        String[] result = Arrays.stream(input.split(" ")).filter(e -> e.trim().length() > 0).toArray(String[]::new);
        return result;
    }
    
    static private String getNextString() {
        String[] line = getLine();
        while (line.length == 0) {
            line = getLine();
        }
        return line[0];
    }
    
    
    public static void main(String[] args) {
        
        capt = new Scanner(System.in);
        if (cmdFile == null && args.length > 0) {
            try {
                cmdFile = new Scanner(new File(args[0]));
            }
            catch (FileNotFoundException e) {
                System.out.println("Error: Could not open the command file " + e.getMessage());
                System.exit(1);
            }
        }
        init();
        
        print_commands();
        
        
        String[] line;
        System.out.println();
        do {
            System.out.print(">");
            line = getLine();
        }
        while (line.length == 0);

        while (true) {
            switch (line[0].toUpperCase()) {

                case "DELETE_ATTR": {
                    int indexAttr;
                    try {
                        indexAttr = Integer.parseInt(line[1]);
                    } catch (RuntimeException e) {
                        System.out.println("Please enter the INDEX of the attribute");
                        break;
                    }
                    TxEliminarAtribut txea = new TxEliminarAtribut(indexAttr);
                    try {
                        txea.execute();
                        System.out.println("Attribute at index " + indexAttr + " deleted!");
                        System.out.println("New list of attributes:");
                        print_attrs();
                    }
                    catch (RuntimeException e) {
                        System.out.println("There has been an error:");
                        System.out.println(e.getMessage());
                    }
                    break;
                }

                case "CONFIGURE_MODEL": {
                    TxConfigurarModel txcm = new TxConfigurarModel();
                    
                    ArrayList< Pair<String,Double> > attributes = txcm.getAttributes();
                    ArrayList<Double> new_weights = new ArrayList<Double>();

                    System.out.println("We are going to determine the weights for all the attributes.");
                        
                    for (int i = 0; i < attributes.size(); ++i) {
                        System.out.print("Introduce the new weight for the attribute " + attributes.get(i).first + ": ");
                        String weight_str = getNextString();
                        new_weights.add(Double.parseDouble(weight_str));
                    }
                    txcm.setWeights(new_weights);
                    System.out.println("Weights added correctly" + "\n");
                    
                    Boolean done = false;
                    while (!done) {
                        System.out.println("The accepted functions are:\nEuclidean\nManhattan\nAverage\nAverageSquare");
                        System.out.print("Write the distance function you want to use: ");
                        try {
                            txcm.setDistanceFunction(getNextString());
                            done = true;
                            System.out.println();
                        }
                        catch(RuntimeException e) {
                            System.out.println("Error: " + e.getMessage());
                        }
                    }

                    done = false;
                    while (!done) {
                        System.out.println("The accepted algorithms are:\nKNN\nKMS1\nHybrid");
                        System.out.print("Write the recommendation algorithm you want to use: ");
                        try {
                            txcm.setRecommendationAlgorithm(getNextString());
                            done = true;
                        }
                        catch(RuntimeException e) {
                            System.out.println("Error: " + e.getMessage());
                        }
                    }
                    System.out.println("Model configured correctly.");
                    break;
                }
                
                case "NEW_ATTR": {
                    if (line.length != 3) {
                        System.out.println("Invalid format. Expected: \"NEW_ATTR oldAttrIndex newName\"");
                        break;
                    }
                    int oldIndex;
                    try {
                        oldIndex = Integer.parseInt(line[1]);
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid oldAttrIndex. Make sure it's an integer");
                        break;
                    }
                    String newName = line[2];
                    System.out.print("We are going to create a new numeric attribute called " + newName);
                    System.out.println(" by deriving the old attribute at index " + oldIndex + ".");
                    System.out.println("Enter the list of rules (one on each line).");
                    System.out.println("End the list with a blank line");
                    System.out.println();
                    System.out.println("Format: OPERATION argument valueIfTrue");
                    System.out.println("- If the old value is numeric: OPERATION = [LT, LE, GT, GE, EQ], ARGUMENT is a Double");
                    System.out.println("- If the old value is categorical: OPERATION = [HAS, HAS_cs], ARGUMENT is a String");
                    System.out.println("valueIfTrue is the value of the new attribute if the operator returns true.");
                    System.out.println("It can be a Double, or \"INVALID\" (don't use in algorithm) or \"SAME\" (keep old value)");
                    System.out.println();
                    System.out.println("Example (if oldAttr<50, newAttr=1): LT 50 1");
                    
                    ArrayList<TxCrearAtributDerivat.Operation> operations = new ArrayList<>();
                    while (true) {
                        System.out.print(operations.size() + ". ");
                        String[] c_line = getLine();
                        if (c_line.length == 0) {
                            break;
                        }
                        if (c_line.length != 3) {
                            System.out.println("Incorrect format. Expected: OPERATION argument valueIfTrue");
                            continue;
                        }
                        String operation = c_line[0];
                        String argument = c_line[1];
                        String valueIfTrue_str = c_line[2].toUpperCase();
                        
                        double valueIfTrue;
                        if (valueIfTrue_str.equals("INVALID")) {
                            valueIfTrue = TxCrearAtributDerivat.INVALID;
                        } else if (valueIfTrue_str.equals("SAME")) {
                            valueIfTrue = TxCrearAtributDerivat.SAME;
                        } else {
                            try {
                                valueIfTrue = Double.parseDouble(c_line[2]);
                            } catch (RuntimeException e) {
                                System.out.println("Error: valueIfTrue must be a number or INVALID or SAME");
                                continue;
                            }
                        }
                        operations.add(new TxCrearAtributDerivat.Operation(operation, argument, valueIfTrue));
                    };
                    
                    Double defaultValue = null;
                    while (defaultValue == null) {
                        System.out.print("Enter the default value (if none of the rules return true): ");
                        String defaultValue_str = getNextString().toUpperCase();
                        if (defaultValue_str.equals("INVALID")) {
                            defaultValue = TxCrearAtributDerivat.INVALID;
                        } else if (defaultValue_str.equals("SAME")) {
                            defaultValue = TxCrearAtributDerivat.SAME;
                        } else {
                            try {
                                defaultValue = Double.parseDouble(defaultValue_str);
                            } catch (RuntimeException e) {
                                System.out.println("Error: default value must be a number or INVALID or SAME");
                            }
                        }
                    }
                    
                    TxCrearAtributDerivat txcad = new TxCrearAtributDerivat(operations, defaultValue, oldIndex, newName);
                    try {
                        txcad.execute();
                        System.out.println("Attribute created correctly.");
                    } catch (RuntimeException e) {
                        System.out.println("There has been an error:");
                        System.out.println(e.getMessage());
                    }
                    break;
                }
                
                case "SET_DISTANCE": {
                    TxConfigurarModel txcm = new TxConfigurarModel();
                    if (line.length < 2) {
                        System.out.println("Please enter the distance function");
                        System.out.println("The accepted functions are:\nEuclidean\nManhattan\nAverage\nAverageSquare");
                        break;
                    }
                    try {
                        txcm.setDistanceFunction(line[1]);
                        System.out.println("Distance function set to " + line[1]);
                    }
                    catch(RuntimeException e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                    break;
                }
                
                case "SET_ALGORITHM": {
                    TxConfigurarModel txcm = new TxConfigurarModel();
                    if (line.length < 2) {
                        System.out.println("Please enter the recommendation algoritm");
                        System.out.println("The accepted algorithms are:\nKNN \nKMS1 \nHybrid");
                        break;
                    }
                    try {
                        txcm.setRecommendationAlgorithm(line[1]);
                        System.out.println("Recommendation algorithm set to " + line[1]);
                    }
                    catch(RuntimeException e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                    break;
                }
                    
                case "SET_WEIGHT": {
                    TxConfigurarModel txcm2 = new TxConfigurarModel();
                    ArrayList<Double> weights = new ArrayList<Double>();
                    ArrayList<Pair<String, Double>> attr = txcm2.getAttributes();
                    for (Pair<String, Double> p : attr)
                        weights.add(p.second);
                    int indexAttr;
                    double newWeight;
                    try {
                        indexAttr = Integer.parseInt(line[1]);
                        newWeight = Double.parseDouble(line[2]);
                    } catch (RuntimeException e) {
                        System.out.println("Please enter the INDEX and new WEIGHT of the attribute");
                        break;
                    }
                    weights.set(indexAttr, newWeight);
                    txcm2.setWeights(weights);
                    System.out.println("New list of attributes:");
                    print_attrs();
                    break;
                }
                    
                case "SEE_ATTR":
                    print_attrs();
                    break;

                case "EVALUATE": {
                    TxAvaluarModel txam = new TxAvaluarModel(false);
                    System.out.println("Evaluating model...");
                    try {
                        long startTime = System.nanoTime();
                        txam.execute();
                        long stopTime = System.nanoTime();
                        System.out.println("Resulting NDCG: " + txam.getResult());
                        System.out.println("Elapsed time: " + (stopTime - startTime) / 1000000000.0 + " seconds");
                    } catch (RuntimeException e) {
                        System.out.println("There has been an error:");
                        System.out.println(e.getMessage());
                    } catch (FileNotFoundException e) {
                        System.out.print("Could not open the file ");
                        System.out.println(e.getMessage());
                    }
                    break;
                }

                case "NEW_ITEM": {
                    ArrayList<String> values;
                    try {
                        int n = line.length - 1;
                        values = new ArrayList<String>(n);
                        for (int i = 0; i < n; ++i) {
                            values.add(line[i+1]);
                        }
                    } catch (RuntimeException e) {
                        System.out.println("Please enter N, followed by N values");
                        break;
                    }
                    TxCrearItem txci = new TxCrearItem(values);
                    try {
                        txci.execute();
                        System.out.println("Item created with the new ID " + txci.getResult());
                    } catch (RuntimeException e) {
                        System.out.println("There has been an error:");
                        System.out.println(e.getMessage());
                    }
                    break;
                }

                case "DELETE_ITEM": {
                    int id;
                    try {
                        id = Integer.parseInt(line[1]);
                    } catch (RuntimeException e) {
                        System.out.println("Please enter the ID of the item");
                        break;
                    }
                    TxEliminarItem txei = new TxEliminarItem(id);
                    try {
                        txei.execute();
                        System.out.println("Item with ID " + id + " has been deleted.");
                    } catch (RuntimeException e) {
                        System.out.println("There has been an error:");
                        System.out.println(e.getMessage());
                    }
                    break;
                }

                case "NEW_USER": {
                    TxCrearUsuari txcu = new TxCrearUsuari();
                    try {
                        txcu.execute();
                        System.out.println("New user created with the ID " + txcu.getResult());
                    }
                    catch (Exception e) {
                        System.out.println("There has been an error:");
                        System.out.println(e.getMessage());
                    }
                    break;
                }

                case "DELETE_USER": {
                    int id;
                    try {
                        id = Integer.parseInt(line[1]);
                    } catch (RuntimeException e) {
                        System.out.println("Please enter the ID of the user");
                        break;
                    }
                    TxEliminarUsuari txeu = new TxEliminarUsuari(id);    
                    try {
                        txeu.execute();
                        System.out.println("User with ID " + id + " has been deleted.");
                    } catch (RuntimeException e) {
                        System.out.println("There has been an error:");
                        System.out.println(e.getMessage());
                    }
                    break;
                }

                case "RATE_ITEM": {
                    int idUser;
                    int idItem;
                    double rating;
                    try {
                        idUser = Integer.parseInt(line[1]);
                        idItem = Integer.parseInt(line[2]);
                        rating = Double.parseDouble(line[3]);
                    } catch (RuntimeException e) {
                        System.out.println("Please enter the ID of the user, the ID of the item and the rating");
                        break;
                    }
                    TxValorarItem txvi = new TxValorarItem(idUser, idItem, rating);
                    try {
                        txvi.execute();
                        System.out.println("Rating " + rating + " has been assigned to user " + idUser + " and item " + idItem);
                    } catch (RuntimeException e) {
                        System.out.println("There has been an error:");
                        System.out.println(e.getMessage());
                    }
                    break;
                }

                case "RECOMMEND": {
                    int n = line.length - 3; // Subtract command, idUsuari and Q
                    if (n < 1) {
                        System.out.println("Please the userId, 1 or more itemIds, and Q");
                        break;
                    }
                    int idUsuari, Q;
                    ArrayList<Integer> idItemsQuery = new ArrayList<Integer>(n);
                    try {
                        idUsuari = Integer.parseInt(line[1]);
                    } catch (RuntimeException e) {
                        System.out.println("Please enter a valid userId");
                        break;
                    }
                    for (int i = 0; i < n; ++i) {
                        try {
                            idItemsQuery.add(Integer.parseInt(line[i+2]));
                        } catch (RuntimeException e) {
                            System.out.println("The itemId " + line[i+2] + " is not valid");
                            break;
                        }
                    }
                    try {
                        Q = Integer.parseInt(line[line.length-1]);
                    } catch (RuntimeException e) {
                        System.out.println("Please enter a valid Q");
                        break;
                    }
                    TxObtenirRecomanacions txor = new TxObtenirRecomanacions(idUsuari, idItemsQuery, Q); 
                    try {
                        txor.execute();
                        System.out.println("The " + Q + " best recommendations for user " + idUsuari + " (Item IDs):");
                        System.out.println(txor.getResult());
                    } catch (RuntimeException e) {
                        System.out.println("There has been an error:");
                        System.out.println(e.getMessage());
                    }
                    break;
                }
                
                case "SAVE_DATA": {
                    if (line.length < 2) {
                        System.out.println("Please enter the name of the saved dataset");
                        break;
                    }
                    String datasetName = line[1];
                    try {
                        TxGuardarDades txgd = new TxGuardarDades(datasetName, false);
                        txgd.execute();
                        System.out.println("Data saved in DATA" + File.separator + datasetName);
                    } catch (RuntimeException e) {
                        System.out.println("There has been an error:");
                        System.out.println(e.getMessage());
                    }
                    break;
                }
                    
                case "ECHO":
                    for (int i = 1; i < line.length; ++i) {
                        System.out.print(line[i] + " ");
                    }
                    System.out.println();
                    break;
                    
                case "HELP":
                    print_commands();
                    break;
                    
                case "EXIT":
                    capt.close();
                    System.exit(0);
                    break;
                    
                default:
                    System.out.println("Unknown command \"" + line[0] + "\"");
                    System.out.println("Use HELP to see available commands");
                    break;
                
            }
            
            System.out.println();
            do {
                System.out.print(">");
                line = getLine();
            }
            while (line.length == 0);
        }
    }
}

package Domain.TransactionControllers;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import Domain.*;
import Domain.Value.*;
import Exceptions.*;
import Utilities.CSVReader;
import Utilities.ConfigFileReader;

public class TxLlegirCSV {
    private String pathConfigFile;
    
    public TxLlegirCSV(String pathConfigFile, boolean absolutePath) {
        this.pathConfigFile = absolutePath ? pathConfigFile : "DATA" + File.separator + pathConfigFile;;
    }
    public TxLlegirCSV(String pathConfigFile) {
        this(pathConfigFile, false);
    }
    
    
    public void execute() throws FileNotFoundException {        
        File configFile = new File(pathConfigFile);
        String baseDirectory = configFile.getParent() + File.separator;
        
        // Get configuration from config file
        ConfigFileReader configFileReader = new ConfigFileReader(pathConfigFile);
        
        String pathCSVItems = configFileReader.get("ITEMS");
        if (pathCSVItems == null) {
            throw new IllegalArgumentException("The config file does not contain ITEMS (path to the CSV file with the items)");
        }
        
        String pathCSVRatings = configFileReader.get("RATINGS");
        if (pathCSVRatings == null) {
            throw new IllegalArgumentException("The config file does not contain RATINGS (path to the CSV file with the ratings)");
        }
        
        String idName = configFileReader.get("ID_NAME");
        if (idName == null) {
            throw new IllegalArgumentException("The config file does not contain ID_NAME (the column of ITEMS that should be used as ID)");
        }
        
        String maxRatingString = configFileReader.get("MAX_RATING");
        if (maxRatingString == null) {
            throw new IllegalArgumentException("The config file does not contain MAX_RATING (the maximum rating)");
        }
        
        double maxRating;
        try {
            maxRating = Double.parseDouble(maxRatingString);
        }
        catch (NumberFormatException e) {
            throw new IllegalArgumentException("The value of MAX_RATING is not a number");
        }
        
        String userIdName = configFileReader.get("USERID");
        if (userIdName == null) {
            throw new IllegalArgumentException("The config file does not contain USERID (the column of RATINGS that should be used as user ID)");
        }
        
        String itemIdName = configFileReader.get("ITEMID");
        if (itemIdName == null) {
            throw new IllegalArgumentException("The config file does not contain ITEMID (the column of RATINGS that should be used as item ID)");
        }
        
        String ratingName = configFileReader.get("RATING");
        if (ratingName == null) {
            throw new IllegalArgumentException("The config file does not contain RATING (the column of RATINGS that should be used as RATING)");
        }

        String titleName = configFileReader.get("TITLE");
        if (titleName == null) {
            throw new IllegalArgumentException("The config file does not contain TITLE (the column of ITEM that should be used as user TITLE)");
        }
        
        String overviewName = configFileReader.get("OVERVIEW");
        if (overviewName == null) {
            throw new IllegalArgumentException("The config file does not contain OVERVIEW (the column of ITEM that should be used as item OVERVIEW)");
        }
        
        String avgRatingName = configFileReader.get("AVGRATING");
        if (avgRatingName == null) {
            throw new IllegalArgumentException("The config file does not contain AVGRATING (the column of ITEM that should be used as AVGRATING)");
        }
        
        String imageName = configFileReader.get("IMG_URL");
        if (imageName == null) {
            throw new IllegalArgumentException("The config file does not contain IMG_URL (the column of ITEM that should be used as IMG_URL)");
        }
        
        
        // Read the CSV files
        CSVReader reader = new CSVReader();
        
        reader.open(baseDirectory + pathCSVItems);
        ArrayList<Attribute> attrs = Attribute.parseAttributes(reader);
        
        reader.open(baseDirectory + pathCSVItems);
        parseItems(reader, attrs, idName);
        
        
        Rating.setMaxRating(maxRating);
        UserRatings.setColumnNames(userIdName, itemIdName, ratingName);
        reader.open(baseDirectory + pathCSVRatings);
        parseRatings(reader);
        
        reader.close();
        
        
        // If the config file contains some other configurations, add them to the model
        String known = configFileReader.get("KNOWN");
        if (known != null) {
            TxAvaluarModel.pathCSVKnown = baseDirectory + known;
        }
        String unknown = configFileReader.get("UNKNOWN");
        if (unknown != null) {
            TxAvaluarModel.pathCSVUnknown = baseDirectory + unknown;
        }
        
        TxConfigurarModel txcm = new TxConfigurarModel();
        String algorithm = configFileReader.get("ALGORITHM");
        if (algorithm != null) {
            txcm.setRecommendationAlgorithm(algorithm);
        }
        String distance = configFileReader.get("DISTANCE");
        if (distance != null) {
            txcm.setDistanceFunction(distance);
        }
        
        String weightsInvalid = configFileReader.get("WEIGHTS+INVALID");
        if (weightsInvalid != null) {
            TxLlegirWeightsInvalid txlwi = new TxLlegirWeightsInvalid(baseDirectory + weightsInvalid);
            txlwi.execute();
        }
        
        // Set the column names at the end so we can check the attributes
        Item.setColumnNames(titleName, overviewName, avgRatingName, imageName);
        
        System.gc();
    }
    
    
    void parseItems(CSVReader reader, ArrayList<Attribute> attrs, String idName) {
        ModelCtrl mc = ModelCtrl.getInstance();
        
        // Find index of idName
        String[] header = reader.readNext(); // Read first line
        int idIndex = -1;
        for (int i = 0; i < header.length; i++) {
            if (header[i].equals(idName)) {
                idIndex = i;
                break;
            }
        }
        if (idIndex == -1) {
            throw new IllegalArgumentException("The header of the CSV file does not contain the attribute \"" + idName + "\"");
        }
        if (attrs.size() != header.length) {
            throw new ImplementationError("Attributes parsed incorrectly. The number of attributes is different from the header size");
        }
        if (attrs.get(idIndex).getClass() != AttributeNumeric.class) {
            throw new IllegalArgumentException("The attribute \"" + idName + "\" must be numeric!");
        }
        
        
        
        // For each line of the file, create a new item
        while (!reader.endReached()) {
            String[] strings = reader.readNext();
            // Don't allocate space for the id
            ArrayList<Value> values = new ArrayList<>(strings.length - 1);
            int id = -1;
            
            // Iterate on all values/attributes
            for (int i = 0; i < strings.length; i++) {
                String s = strings[i];
                if (i == idIndex) {
                    // The user has indicated that this value should be used as ID
                    id = Integer.parseInt(s);
                }
                else {
                    // Regular value
                    Attribute attribute = attrs.get(i);
                    values.add(attribute.parseValue(s));
                }
            }
            
            try {
                mc.createItem(id, values);
            }
            catch (ObjectAlreadyExistsException e) {
                throw new RuntimeException("The Item with ID " + id + " aleady exists (is the idIndex correct?)");
            }
        }
    }
    
    
    void parseRatings(CSVReader reader) {
        // Get the list of (userId + list of (Item, rating))
        for (UserRatings userRatings : UserRatings.parseRatings(reader)) {
            
            int id = userRatings.getUserId();
            // Now create the user with the list of items
            try {
                // The new user is not an active user
                new User(id, false, userRatings.getRatings());
            }
            catch (ObjectAlreadyExistsException e) {
                throw new ImplementationError("UserRatings.parseRatings() returned the user with id " + id + " twice");
            }
        }
    }
}

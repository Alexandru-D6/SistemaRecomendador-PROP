package Domain.TransactionControllers;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;

import Domain.Item;
import Domain.Rating;
import Domain.UserRatings;
import Domain.DataInterface.DBFactory;
import Utilities.ConfigFileWriter;

public class TxGuardarDades {
    private final String RATINGS_FILE = "ratings.db.csv";
    private final String ITEMS_FILE = "items.csv";
    private final String WEIGHTS_INVALID_FILE = "weights.invalid.csv";
    private final String CONFIG_FILE = "dataset.info";
    private final String KNOWN_FILE = "ratings.test.known.csv";
    private final String UNKNOWN_FILE = "ratings.test.unknown.csv";
    
    private String datasetName;

    public TxGuardarDades(String datasetName, boolean absolutePath) {
        this.datasetName = absolutePath ? datasetName : "DATA" + File.separator + datasetName;
    }
    
    public void execute() {
        String ratingsFilename = datasetName + File.separator + RATINGS_FILE;
        String itemsFilename = datasetName + File.separator + ITEMS_FILE;
        String weightsInvalidFilename = datasetName + File.separator + WEIGHTS_INVALID_FILE;
        String configFilename = datasetName + File.separator + CONFIG_FILE;
        
        DBFactory dbFactory = DBFactory.getInstance();
        
        // Create directory if needed
        try {
            Files.createDirectories(Paths.get(datasetName));
        }
        catch (IOException e) {
            throw new RuntimeException("Cannot create dataset directory: " + e.getMessage());
        }
        
        // Save items
        try {
            dbFactory.getItemDB().save(itemsFilename);
        }
        catch (IOException e) {
            throw new RuntimeException("Cannot write items file: " + e.getMessage());
        }
        
        // Save ratings
        try {
            dbFactory.getRatingDB().save(ratingsFilename);
        }
        catch (IOException e) {
            throw new RuntimeException("Cannot write ratings file: " + e.getMessage());
        }
        
        // Save weights + invalid
        try {
            dbFactory.getItemDB().saveWeightsInvalid(weightsInvalidFilename);
        }
        catch (IOException e) {
            throw new RuntimeException("Cannot write weights+invalid file: " + e.getMessage());
        }
        
        writeConfigFile(configFilename, datasetName);
        
        System.gc();
    }
    
    
    private void writeConfigFile(String configFilename, String directoryName) {
        ConfigFileWriter configFileWriter = new ConfigFileWriter(configFilename);
        configFileWriter.add("ITEMS", ITEMS_FILE);
        configFileWriter.add("WEIGHTS+INVALID", WEIGHTS_INVALID_FILE);
        configFileWriter.add("RATINGS", RATINGS_FILE);
        configFileWriter.addBlank();
        
        configFileWriter.add("ID_NAME", Item.DEFAULT_ID_NAME);
        configFileWriter.add("MAX_RATING", Double.toString(Rating.getMaxRating()));
        String[] ratingNames = UserRatings.getColumnNames();
        configFileWriter.add("USERID", ratingNames[0]);
        configFileWriter.add("ITEMID", ratingNames[1]);
        configFileWriter.add("RATING", ratingNames[2]);
        configFileWriter.addBlank();
        
        if (TxAvaluarModel.pathCSVKnown != null) {
            Path source = Paths.get(TxAvaluarModel.pathCSVKnown);
            Path target = Paths.get(directoryName + File.separator + KNOWN_FILE);
            try {
                Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
            }
            catch (IOException e) {
                throw new RuntimeException("Cannot copy KNOWN file: " + e.getMessage());
            }
            configFileWriter.add("KNOWN", KNOWN_FILE);
        }
        
        if (TxAvaluarModel.pathCSVUnknown != null) {
            Path source = Paths.get(TxAvaluarModel.pathCSVUnknown);
            Path target = Paths.get(directoryName + File.separator + UNKNOWN_FILE);
            try {
                Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
            }
            catch (IOException e) {
                throw new RuntimeException("Cannot copy UNKNOWN file: " + e.getMessage());
            }
            configFileWriter.add("UNKNOWN", UNKNOWN_FILE);
            configFileWriter.addBlank();
        }
        
        TxConfigurarModel txcm = new TxConfigurarModel();
        String algName = txcm.getRecommendationAlgorithm();
        configFileWriter.add("ALGORITHM", algName);
        
        String disName = txcm.getDistanceFunction();
        configFileWriter.add("DISTANCE", disName);
        configFileWriter.addBlank();

        String[] informationNames = Item.getcolumnNames();
        configFileWriter.add("TITLE", informationNames[0]);
        configFileWriter.add("OVERVIEW", informationNames[1]);
        configFileWriter.add("AVGRATING", informationNames[2]);
        configFileWriter.add("IMG_URL", informationNames[3]);
        
        
        try {
            configFileWriter.write();
        }
        catch (IOException e) {
            throw new RuntimeException("Cannot write config file: " + e.getMessage());
        }
    }
}

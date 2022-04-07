package Domain.TransactionControllers;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import Domain.DataInterface.DBFactory;
import Domain.DataInterface.ItemDB;
import Exceptions.ObjectDoesNotExistException;
import Utilities.CSVReader;

class TxLlegirWeightsInvalid {
    String pathDataFile;
    
    TxLlegirWeightsInvalid(String pathDataFile) {
        this.pathDataFile = pathDataFile;
    }
    
    public void execute() throws FileNotFoundException {
        CSVReader csvReader = new CSVReader();
        csvReader.open(pathDataFile);
        
        // First line contains the weights of each attribute
        String[] weightsStr = csvReader.readNext();
        if (weightsStr.length == 0) {
            throw new RuntimeException("Wrong format in weights/invalid file. The first line must contain all weights");
        }
        ArrayList<Double> weights = new ArrayList<>(weightsStr.length);
        for (String weightStr : weightsStr) {
            try {
                weights.add(Double.parseDouble(weightStr));
            } catch (NumberFormatException e) {
                throw new RuntimeException("Wrong format in weights/invalid file. The weights must be numbers");
            }
        }
        TxConfigurarModel txConfigurarModel = new TxConfigurarModel();
        txConfigurarModel.setWeights(weights);
        
        
        // The rest of the file contains one line per item, with the id and whether each attribute is invalid (1) or not (0)
        ItemDB itemDB = DBFactory.getInstance().getItemDB();
        
        while (!csvReader.endReached()) {
            String[] tokens = csvReader.readNext();
            ArrayList<Boolean> invalid = new ArrayList<>(tokens.length-1);
            
            int id = -1;
            try {
                id = Integer.parseInt(tokens[0]);
            }
            catch (NumberFormatException e) {
                throw new RuntimeException("Wrong format in weights/invalid file. The id must be a number");
            }
            
            for (int i = 1; i < tokens.length; i++) {
                if (tokens[i].equals("1")) {
                    invalid.add(true);
                } else if (tokens[i].equals("0")) {
                    invalid.add(false);
                } else {
                    throw new RuntimeException("Wrong format in weights/invalid file. The values must be 0 or 1");
                }
            }
            
            try {
                itemDB.get(id).setInvalidAttributes(invalid);
            }
            catch (ObjectDoesNotExistException e) {
                throw new RuntimeException("The weights/invalid file references an item that does not exist");
            }
        }
            
    }
}

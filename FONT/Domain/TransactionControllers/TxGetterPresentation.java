package Domain.TransactionControllers;

import java.util.ArrayList;

import Domain.ModelCtrl;
import Domain.Rating;
import Domain.User;
import Domain.DataInterface.DBFactory;
import Domain.DataInterface.ItemDB;
import Domain.DataInterface.UserDB;
import Domain.Recommendation.KMeansSlope1.ClusterMember;
import Domain.Value.Attribute;
import Domain.Value.AttributeBoolean;
import Domain.Value.AttributeCategorical;
import Exceptions.ImplementationError;
import Exceptions.ObjectDoesNotExistException;
import Utilities.Pair;
import java.util.Arrays;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import Domain.Item;

public class TxGetterPresentation {

    public ArrayList<Integer> getItems() {
        ArrayList<Integer> items = new ArrayList<>();

        Collection<Item> CItems =  ModelCtrl.getInstance().getItems();

        for (var str : CItems) {
            items.add(str.getId());
        }
        Collections.sort(items);

        return items;
    }

    public ArrayList<String>  getUsers() {
        ArrayList<String> items = new ArrayList<String>();

        Collection<ClusterMember> CUsers =  User.getAllMembers();

        for (var str : CUsers) {
            items.add(String.valueOf(((User)str).getId()));
        }

        return items;
    }

    public ArrayList<Pair<Integer, Double>> getValorations(int userId) {
        ArrayList<Pair<Integer, Double>> items = new ArrayList<>();

        UserDB userDB = DBFactory.getInstance().getUserDB();
        User user;
        
        try {
            user = userDB.get(userId);
        }
        catch (ObjectDoesNotExistException e) {
            throw new RuntimeException("There is no User with the provided ID!");
        }

        Collection<Rating> CRatings = user.getRatings();

        for (var str : CRatings) {
            int itemId = str.getItem().getId();
            double rating = str.getNormalizedRating() * Rating.getMaxRating();
            items.add(new Pair<>(itemId, rating));
        }
        
        // Sort the list by item ID
        Collections.sort(items, (a, b) -> a.first.compareTo(b.first));

        return items;
    }

    public ArrayList<String> getRecommendations(int userId) {
        ArrayList<String> items = new ArrayList<String>();
        ArrayList<Integer> CRecommendations = getItemsNotValorated(userId);

        TxObtenirRecomanacions txor = new TxObtenirRecomanacions(userId, CRecommendations, 20);
        txor.execute();
        ArrayList<Integer> result = txor.getResult();

        for (Integer str : result) items.add(String.valueOf(str));

        return items;
    }

    public ArrayList<String> getInfo(int _item) {
        ArrayList<String> info = new ArrayList<String>();

        ItemDB itemDB = DBFactory.getInstance().getItemDB();
        Item item = null;
        try {
            item = itemDB.get(_item);
        } catch (ObjectDoesNotExistException e) {
            throw new RuntimeException("There is no Item with the provided ID!");
        }

        String[] names = Item.getcolumnNames();
        ArrayList<Attribute> attrs = item.getAttributes();
        String[] values = item.getValues();

        for (var str : names) {
            boolean found = false;
            for (int i = 0; (i < attrs.size()) && !found; ++i) {
                if (attrs.get(i).getName().equals(str)) {
                    found = true;
                    //I get the position +1 of values to ignore (add 1 to skip ID)
                    info.add(values[i+1]);
                }
            }
            if (!found) {
                throw new ImplementationError("There is no attribute with the name \"" + str + "\"");
            }
        }

        return info;
    }

    public Set<String> getAttributeColName() {
        String[] names = Item.getcolumnNames();
        Set<String> res = new HashSet<>();

        for (var str : names) res.add(str);

        return res;
    }

    public ArrayList<String> getAllInfo(int _item) {
        ArrayList<String> info = new ArrayList<String>();

        ItemDB itemDB = DBFactory.getInstance().getItemDB();
        Item item = null;
        try {
            item = itemDB.get(_item);
        } catch (ObjectDoesNotExistException e) {
            throw new RuntimeException("There is no Item with the provided ID!");
        }

        String[] values = item.getValues();

        for (int i = 1; i < values.length; ++i) info.add(values[i]);

        return info;
    }

    public ArrayList<Integer> getItemsNotValorated(int _user) {
        ArrayList<Integer> items = new ArrayList<>();
        UserDB userDB = DBFactory.getInstance().getUserDB();
        User user;
        
        try {
            user = userDB.get(_user);
        }
        catch (ObjectDoesNotExistException e) {
            throw new RuntimeException("There is no User with the provided ID!");
        }

        Collection<Item> CItems = ModelCtrl.getInstance().getItems();

        Collection<Rating> CValorated = user.getRatings();
        Set<Integer> itemsValorated = new TreeSet<>();
        for (Rating r : CValorated) itemsValorated.add(r.getItem().getId());

        for (Item i : CItems) {
            if (!itemsValorated.contains(i.getId())){
                items.add(i.getId());
            }
        }
        return items;
    }

    public ArrayList<String> getAllAttrs() {
        ArrayList<String> attrs = new ArrayList<String>();
        
        ArrayList<Attribute> temp = ModelCtrl.getInstance().getAttributes();

        for (var str : temp) {
            attrs.add(str.getName());
        }

        return attrs;
    }

    public ArrayList<String> getAllWeight() {
        Item item = ModelCtrl.getInstance().getItems().iterator().next();
        ArrayList<String> pesos = new ArrayList<String>();
        String[] temp = item.getValuesWeights();

        for (var str: temp) pesos.add(str);

        return pesos;
    }

    public ArrayList<String> getAllTypes() {
        Item item = ModelCtrl.getInstance().getItems().iterator().next();
        ArrayList<String> types = new ArrayList<String>();
        ArrayList<Attribute> temp = item.getAttributes();

        for (var str: temp) {
            if (str.getClass() == AttributeBoolean.class) types.add("Boolean");
            else if (str.getClass() == AttributeCategorical.class) types.add("Categorical");
            else types.add("Numeric");
        }

        return types;
    }

    public ArrayList<String> getOperators(int attrIndex) {
        String type = getAllTypes().get(attrIndex);
        
        if (type.equals("Categorical")) return new ArrayList<>(Arrays.asList("CONTAINS"));
        return new ArrayList<>(Arrays.asList("=", "<", ">", "<=", ">="));
    }

    public boolean checkUser(int id) {
        UserDB userDB = DBFactory.getInstance().getUserDB();
        return userDB.exist(id);
    }

    public String[] getAlgorithms() {
        return new String[]{"KMeansSlope1", "KNearestNeighbours", "Hybrid"};
    }

    public String[] getDistances() {
        return new String[]{"Euclidean", "Manhattan", "Average", "AverageSquared"};
    }

    public String getCurrentAlgorithm() {
        TxConfigurarModel txcm = new TxConfigurarModel();
        return txcm.getRecommendationAlgorithm();
    }

    public String getCurrentDistance() {
        TxConfigurarModel txcm = new TxConfigurarModel();
        return txcm.getDistanceFunction();
    } 

}

package Presentation.CustomComponents;

import java.util.ArrayList;

import javax.swing.*;

import Domain.TransactionControllers.TxGetterPresentation;
import Presentation.CustomComponents.ItemStruct.type;

public class RecommendationsPanel extends JPanel {
    private ItemsList<ItemStruct> recommendationList = new ItemsList<>();
    
    public RecommendationsPanel(int userId) {
        super();
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        add(recommendationList);
        
        init(userId);
    }
    
    private void init(int userId) {
        recommendationList.setHeaders(new String[]{"Item ID", "Information"});
        recommendationList.clear();
        TxGetterPresentation txgp = new TxGetterPresentation();
        ArrayList<String> itemStrings = txgp.getRecommendations(userId);

        itemStrings.forEach(item -> {
            recommendationList.addItem(new ItemStruct(type.itemsRecommendation, item));
        });
    }
}

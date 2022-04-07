package Presentation.CustomComponents;

import java.awt.event.*;
import java.util.ArrayList;

import javax.swing.*;

import Domain.TransactionControllers.TxEliminarValoracio;
import Domain.TransactionControllers.TxGetterPresentation;
import Presentation.PresentationCtrl;
import Presentation.PresentationUtilities;
import Presentation.CustomComponents.ItemStruct.type;
import Utilities.Pair;

public class RatingsPanel extends JPanel {
    private JFrame parent;
    private int userId;
    private JButton createRatingButton = new JButton("Create new rating");
    private JButton deleteRatingButton = new JButton("Delete selected ratings");
    private ItemsList<ItemStruct> ratingsList = new ItemsList<>();
    
    public RatingsPanel(int userId, JFrame parent) {
        super();
        this.userId = userId;
        this.parent = parent;
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        add(ratingsList);
        add(Box.createVerticalStrut(10));
        add(PresentationUtilities.createButtonBox(createRatingButton, deleteRatingButton));
        add(Box.createVerticalStrut(10));
        
        init();
        addListeners();
    }
    
    private void init() {
        ratingsList.setHeaders(new String[]{"Item ID", "Information", "Rating", "Select for delete"});
        ratingsList.clear();
        TxGetterPresentation txgp = new TxGetterPresentation();
        ArrayList<Pair<Integer, Double>> itemStrings = txgp.getValorations(userId);

        itemStrings.forEach(item -> {
            ratingsList.addItem(new ItemStruct(type.ratings, item.first.toString(), item.second));
        });
        parent.pack();
    }
    
    
    private void addListeners() {
        RatingsPanel self = this;
        createRatingButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    PresentationCtrl.getInstance().getAfegirValoracio(self).makeVisible(true, userId);
                } catch (Exception exp) {
                    JOptionPane.showMessageDialog(deleteRatingButton, exp.getMessage(), "Error!", JOptionPane.WARNING_MESSAGE);
                }
            }
        });
        
        deleteRatingButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                int res = JOptionPane.showConfirmDialog(
                    deleteRatingButton,
                    "Are you sure that you want\n"
                    + "to delete the selected Items?",
                    "Confirmation",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
                );
                if (res == JOptionPane.NO_OPTION || res == JOptionPane.CLOSED_OPTION) {
                    ratingsList.uncheckSelected();
                    return;
                }

                for (Struct str : ratingsList.getSelected()) {
                    ItemStruct rating = (ItemStruct) str;
                    try {
                        TxEliminarValoracio txev = new TxEliminarValoracio(userId, rating.getId());
                        txev.execute();
                        ratingsList.delete(rating);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(deleteRatingButton, ex.getMessage(), "Error!", JOptionPane.WARNING_MESSAGE);
                    }
                }
                ratingsList.redraw();
            }
        });
    }
    
    public void updateRatings() {
        init();
        parent.pack();
        ratingsList.redraw();
    }
}

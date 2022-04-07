package Presentation.CustomComponents;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

import javax.swing.*;

import Presentation.PresentationCtrl;
import Presentation.PresentationUtilities;

//https://web.mit.edu/6.005/www/sp14/psets/ps4/java-6-tutorial/components.html

public class ItemStruct extends Struct {

    public enum type {
        allItems,
        ratings,
        itemsRecommendation,
        allUsers
    }
    
    private JTextField name = new JTextField(15);
    private ArrayList<JButton> buttons = new ArrayList<JButton>();
    private ArrayList<JTextField> textField = new ArrayList<JTextField>();
    private JCheckBox toDelete = new JCheckBox();

    private type structType = null;
    private static final Dimension maxSize = PresentationUtilities.getMaximumDimension();
    private static final Dimension minSize = PresentationUtilities.getMinimumDimension();

    public ItemStruct(type _structType, String _name) {
        structType = _structType;
        name.setText(_name);
        name.setMaximumSize(maxSize);
        name.setMinimumSize(minSize);
        
        initCommon();
    }

    public ItemStruct(type _structType, String _name, Double val) {
        structType = _structType;
        name.setText(_name);
        name.setMaximumSize(maxSize);
        name.setMinimumSize(minSize);

        JTextField aux = new JTextField(val.toString(), 10);
        aux.setMaximumSize(maxSize);
        aux.setMinimumSize(minSize);
        aux.setEditable(false);
        textField.add(aux);

        initCommon();
    }

    private void initCommon() {
        if (structType == type.allUsers) {
            JButton aux = new JButton("Ratings");
            aux.setMaximumSize(maxSize);
            aux.setMinimumSize(minSize);
            buttons.add(aux);
            
            aux = new JButton("Recommend");
            aux.setMaximumSize(maxSize);
            aux.setMinimumSize(minSize);
            buttons.add(aux);
        } else {
            JButton aux = new JButton("Information");
            aux.setMaximumSize(maxSize);
            aux.setMinimumSize(minSize);
            buttons.add(aux);
        }

        toDelete.setMaximumSize(maxSize);
        toDelete.setMinimumSize(minSize);
        toDelete.setHorizontalAlignment(SwingConstants.CENTER);

        name.setEditable(false);

        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));

        add(name);
        for (var str : buttons) add(str);
        for (var str : textField) add(str);
        if (structType != type.itemsRecommendation) add(toDelete);

        assignarListener();
    }

    private void assignarListener() {
        if (structType == type.allUsers) {
            buttons.get(0).addActionListener(new ActionListener() { //Rating
                public void actionPerformed(ActionEvent e) {
                    PresentationCtrl.getInstance().getMainMenu().setUserSelected(Integer.parseInt(name.getText()));
                    PresentationCtrl.getInstance().getRatings().makeVisible(true);
                }
            });

            buttons.get(1).addActionListener(new ActionListener() { //Recommendations
                public void actionPerformed(ActionEvent e) {
                    buttons.get(1).setEnabled(false);
                    SwingUtilities.invokeLater(() -> {
                        PresentationCtrl.getInstance().getMainMenu().update();
                        PresentationCtrl.getInstance().getMainMenu().setUserSelected(Integer.parseInt(name.getText()));
                        PresentationCtrl.getInstance().getRecommendations().makeVisible(true);
                    });

                    SwingUtilities.invokeLater(() -> {
                        buttons.get(1).setEnabled(true);
                    });
                }
            });
        } else {
            buttons.get(0).addActionListener(new ActionListener() { //Information
                public void actionPerformed(ActionEvent e) {
                    int itemId = Integer.parseInt(name.getText());
                    PresentationCtrl.getInstance().getInformacio().makeVisible(true, itemId);
                }
            });
        }
    }

    public boolean isForDelete() { return toDelete.isSelected(); }
    public void uncheck() { toDelete.setSelected(false); }
    
    public int getId() { return Integer.parseInt(name.getText()); }
}

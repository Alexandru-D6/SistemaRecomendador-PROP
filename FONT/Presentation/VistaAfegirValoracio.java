package Presentation;

import java.awt.*;
import javax.swing.*;

import Domain.TransactionControllers.TxGetterPresentation;
import Domain.TransactionControllers.TxValorarItem;
import Presentation.CustomComponents.RatingsPanel;

import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collections;

public class VistaAfegirValoracio {
    PresentationCtrl presentationCtrl;

    private JFrame frameVista = new JFrame("Create new rating");
    private RatingsPanel parent = null;

    private JPanel mainPane = new JPanel(new GridBagLayout());

    private JComboBox<String> selector = new JComboBox<>();
    private JTextField valoracio = new JTextField("", 5);
    private JButton AfegirButton = new JButton("Create rating");
    private JButton InformationButton = new JButton("Info");
    private int actUser = -1;

    private JButton exit = new JButton("Exit");


    public VistaAfegirValoracio(PresentationCtrl _CtrlPresentation) {
        presentationCtrl = _CtrlPresentation;
        initComponents();
        makeVisible(false);
        PresentationUtilities.setIcon(frameVista);
    }

    public void makeVisible(boolean state) {
        if (state) {
            frameVista.pack();
        }
        valoracio.setText("");
        frameVista.setVisible(state);
        frameVista.requestFocus();
    }

    public boolean isVisible() {
        return frameVista.isVisible();
    }

    public void makeVisible(boolean state, int _user) {
        frameVista.setTitle("Create new rating for user " + _user);
        actUser = _user;
        chargeInfo();
        frameVista.pack();
        frameVista.setVisible(state);
        frameVista.requestFocus();
    }

    private void asignarListenersComponents() {

        exit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                presentationCtrl.getMainMenu().updatePPane();
                makeVisible(false);
            }
        });

        AfegirButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                try {
                    TxValorarItem txvi = new TxValorarItem(presentationCtrl.getMainMenu().getUserSelected(), 
                                                            Integer.parseInt(selector.getSelectedItem().toString().split(" ")[0]), 
                                                            Double.parseDouble(valoracio.getText()));
                    txvi.execute();
                    parent.updateRatings();
                    makeVisible(false);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(AfegirButton, e.getMessage(), "Error!", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        InformationButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                int itemId = Integer.parseInt(selector.getSelectedItem().toString().split(" ")[0]);
                PresentationCtrl.getInstance().getInformacio().makeVisible(true, itemId);
            }
        });

        /*
        int itemId = Integer.parseInt(name.getText());
        PresentationCtrl.getInstance().getInformacio().makeVisible(true, itemId);
        */
    }

    private void initComponents() {
        initMainPane();
        asignarListenersComponents();

        initFrameVista();
    }

    private void initFrameVista() {
        frameVista.setMinimumSize(new Dimension(475,200));
        frameVista.setMaximumSize(new Dimension(475,200));
        //frameVista.setLocation(0, 0);
        frameVista.setPreferredSize(frameVista.getMinimumSize());
        frameVista.setResizable(false);

        // Posicion y operaciones por defecto
		frameVista.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		// Se agrega panelContenidos al contentPane (el panelContenidos se
		// podria ahorrar y trabajar directamente sobre el contentPane)

        //frameVista.getContentPane().setLayout(new BoxLayout(frameVista.getContentPane(), BoxLayout.PAGE_AXIS));

        frameVista.add(mainPane);
    }

    private void initMainPane() {
        // mainPane.setBackground(PaletteColors.getColor(3));
        
        mainPane.setLayout(new BoxLayout(mainPane, BoxLayout.PAGE_AXIS));
        
        mainPane.add(Box.createVerticalGlue());
        
        selector.setMaximumSize(new Dimension(200, 25));
        selector.setMinimumSize(new Dimension(199, 25));
        selector.setPreferredSize(selector.getMaximumSize());
        mainPane.add(PresentationUtilities.createButtonBox(Box.createHorizontalStrut(10), new JLabel("Item: "), selector, InformationButton));
        
        mainPane.add(Box.createVerticalStrut(10));

        valoracio.setMaximumSize(new Dimension(200, 25));
        mainPane.add(PresentationUtilities.createButtonBox(new JLabel("New rating: "), valoracio));
        
        mainPane.add(Box.createVerticalGlue());
        
        mainPane.add(PresentationUtilities.createButtonBox(AfegirButton, exit));
        
        mainPane.add(Box.createVerticalStrut(10));
    }

    private void chargeInfo() {
        TxGetterPresentation txgp = new TxGetterPresentation();
        ArrayList<Integer> items = txgp.getItemsNotValorated(actUser);
        Collections.sort(items);

        ArrayList<String> itemsName = new ArrayList<>();
        for (var str : items) itemsName.add(txgp.getInfo(str).get(0)); //get(0) because we only want the name of the item
        
        selector.removeAllItems();
        for (int i = 0; i < items.size(); ++i) {
            selector.addItem(items.get(i) + " - " + itemsName.get(i));
        }
    }
    
    public void setParent(RatingsPanel _parent) {
        parent = _parent;
    }
}

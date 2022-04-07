package Presentation;

import java.awt.*;

import javax.swing.*;
import java.util.ArrayList;

import Domain.TransactionControllers.TxCrearItem;
import Domain.TransactionControllers.TxGetterPresentation;
import Presentation.CustomComponents.TextFieldWithIdentifier;

import java.awt.event.*;

public class VistaAfegirItem {
    PresentationCtrl presentationCtrl;

    private JFrame frameVista = new JFrame("Create new item");

    private JPanel buttonsPane = new JPanel();

    private ArrayList<TextFieldWithIdentifier> attrs = new ArrayList<TextFieldWithIdentifier>();

    private JButton createItemButton = new JButton("Create Item");
    private JButton exit = new JButton("Exit");

    JPanel itemsList = new JPanel(new GridLayout(0,1));
    JScrollPane items = new JScrollPane(itemsList, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

    TxGetterPresentation txgp = new TxGetterPresentation();

    public VistaAfegirItem(PresentationCtrl _CtrlPresentation) {
        presentationCtrl = _CtrlPresentation;
        initComponents();
        PresentationUtilities.setIcon(frameVista);
    }

    public void makeVisible(boolean state) {
        if (state) {
            chargeInfo();
            frameVista.pack();
        }
        frameVista.setVisible(state);
        frameVista.requestFocus();
    }

    public boolean isVisible() {
        return frameVista.isVisible();
    }

    private void asignarListenersComponents() {

        exit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                makeVisible(false);
            }
        });

        createItemButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                ArrayList<String> values = new ArrayList<String>();

                for (var str : attrs) values.add(str.getText());

                TxCrearItem txci = new TxCrearItem(values);
                try {
                    txci.execute();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(createItemButton, e.getMessage(), "Error!", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                JOptionPane.showMessageDialog(frameVista, "New item id is: " + String.valueOf(txci.getResult()));

                makeVisible(false);
                presentationCtrl.getMainMenu().addItem(txci.getResult());
            }
        });
    }

    private void initComponents() {
        initMainPane();
        initButtons();
        asignarListenersComponents();

        initFrameVista();
    }

    private void initFrameVista() {
        frameVista.setMinimumSize(new Dimension(800,700));
        frameVista.setMaximumSize(new Dimension(800,700));
        //frameVista.setLocation(0, 0);
        frameVista.setPreferredSize(frameVista.getMinimumSize());
        frameVista.setResizable(true);

        // Posicion y operaciones por defecto
		frameVista.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		// Se agrega panelContenidos al contentPane (el panelContenidos se
		// podria ahorrar y trabajar directamente sobre el contentPane)

        frameVista.getContentPane().setLayout(new BoxLayout(frameVista.getContentPane(), BoxLayout.Y_AXIS));
        
        items.getVerticalScrollBar().setUnitIncrement(5);
        frameVista.getContentPane().add(items);
        frameVista.getContentPane().add(Box.createVerticalGlue());
        frameVista.getContentPane().add(Box.createVerticalStrut(10));
        frameVista.getContentPane().add(buttonsPane);
        frameVista.getContentPane().add(Box.createVerticalStrut(10));
    }

    private void chargeInfo() {
        TxGetterPresentation txgp = new TxGetterPresentation();
        ArrayList<String> attribs = txgp.getAllAttrs();
        ArrayList<String> types = txgp.getAllTypes();

        attrs.clear();
        for (int i = 0; i < attribs.size(); ++i) attrs.add(new TextFieldWithIdentifier(attribs.get(i) + " (" + types.get(i) + ")"));
        initMainPane();
        frameVista.pack();
    }

    private void initMainPane() {
        itemsList.removeAll();

        for (var str : attrs) {
            itemsList.add(str);
        }
    }

    private void initButtons() {
        //buttonsPane.setBackground(PaletteColors.getColor(3));
        buttonsPane.setLayout(new BoxLayout(buttonsPane, BoxLayout.LINE_AXIS));

        buttonsPane.add(Box.createHorizontalGlue());
        buttonsPane.add(createItemButton);
        buttonsPane.add(Box.createHorizontalStrut(10));
        buttonsPane.add(exit);
        buttonsPane.add(Box.createHorizontalGlue());
    }
}

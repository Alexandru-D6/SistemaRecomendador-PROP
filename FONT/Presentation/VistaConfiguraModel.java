package Presentation;

import java.awt.*;
import javax.swing.*;

import java.awt.event.*;
import java.io.FileNotFoundException;

import Domain.TransactionControllers.TxLlegirCSV;
import Presentation.CustomComponents.*;

public class VistaConfiguraModel {
    PresentationCtrl presentationCtrl;

    JFrame frameVista = new JFrame("Configure Model");
    JPanel selectFilesPane = new JPanel();
    JButton startModelButton = new JButton("Start Model");
    JLabel initializingLabel = new JLabel("Initializing...");
    
    boolean modelInit_ = false;

    TextFileChooser dataSetSelector = new TextFileChooser(20, 50);

    public VistaConfiguraModel(PresentationCtrl _CtrlPresentation) {
        presentationCtrl = _CtrlPresentation;
        initComponents();
        PresentationUtilities.setIcon(frameVista);
    }

    public void makeVisible(boolean state) {
        if (state) {
            frameVista.pack();
        }
        frameVista.setVisible(state);
        frameVista.requestFocus();
    }

    public boolean isVisible() {
        return frameVista.isVisible();
    }

    private void initComponents() {
        initSelectFilesPane();
        asignarListenersComponents();

        initFrameVista();
    }

    private void asignarListenersComponents() {

        startModelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                initializingLabel.setVisible(true);
                startModelButton.setEnabled(false);
                frameVista.pack();
                
                SwingUtilities.invokeLater(() -> {
                    loadModel();
                });
            }
        });
    }
    
    private void loadModel() {
        String path = dataSetSelector.getPath();
        if (!PresentationUtilities.fileExists(path)) {
            JOptionPane.showMessageDialog(frameVista, "Please select a valid data set", "Error!", JOptionPane.WARNING_MESSAGE);
            initializingLabel.setVisible(false);
            startModelButton.setEnabled(true);
            frameVista.pack();
            return;
        }
        
        TxLlegirCSV txlc = new TxLlegirCSV(path, true);
        try {
            if (!modelInit_){
                txlc.execute();
                modelInit_ = true;
            }
            presentationCtrl.initMainMenu();
        } catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(frameVista, "Could not open " + ex.getMessage(), "Fatal Error!", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frameVista, ex.getMessage(), "Fatal Error!", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    private void initFrameVista() {
        Dimension screenSize = new Dimension(400,400);
        frameVista.setMinimumSize(screenSize);
        frameVista.setMaximumSize(screenSize);
        //frameVista.setLocation(0, 0);
        frameVista.setResizable(false);

        // Posicion y operaciones por defecto
		frameVista.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// Se agrega panelContenidos al contentPane (el panelContenidos se
		// podria ahorrar y trabajar directamente sobre el contentPane)

        frameVista.setLayout(new BoxLayout(frameVista.getContentPane(), BoxLayout.Y_AXIS));

        frameVista.setBackground(PaletteColors.getColor(0));
        frameVista.getContentPane().add(selectFilesPane);        
    }

    private void initSelectFilesPane() {
        selectFilesPane.setLayout(new BoxLayout(selectFilesPane, BoxLayout.Y_AXIS));
        
        selectFilesPane.add(Box.createVerticalGlue());
        
        JLabel dataSetLabel = new JLabel("Select the dataset to be loaded:");
        dataSetLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        selectFilesPane.add(dataSetLabel);
        
        selectFilesPane.add(Box.createVerticalStrut(10));
        
        selectFilesPane.add(dataSetSelector);
        
        selectFilesPane.add(Box.createVerticalStrut(10));
        
        startModelButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        selectFilesPane.add(startModelButton);
        
        selectFilesPane.add(Box.createVerticalStrut(10));
        
        initializingLabel.setFont(new Font("Arial", Font.BOLD, 12));
        initializingLabel.setVisible(false);
        initializingLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        selectFilesPane.add(initializingLabel);
        
        selectFilesPane.add(Box.createVerticalGlue());
    }

    public boolean configured() { return modelInit_; }
}
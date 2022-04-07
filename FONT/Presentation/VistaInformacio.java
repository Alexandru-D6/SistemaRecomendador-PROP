package Presentation;

import java.awt.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.swing.*;
import javax.swing.text.*;

import java.util.ArrayList;
import java.util.Set;

import Domain.TransactionControllers.TxGetterPresentation;
import Exceptions.ImplementationError;
import Utilities.ImageFinder;

import java.awt.event.*;

public class VistaInformacio {
    PresentationCtrl presentationCtrl;
    private int itemId;

    private JFrame frameVista = new JFrame("Information");
    private JPanel mainPane = new JPanel();

    private JTextField name = new JTextField("", 20);
    private JTextArea description = new JTextArea(11, 35);
    private JTextField avgRating = new JTextField("", 20);

    private JScrollPane descriptionSP = new JScrollPane(description);
    
    private Box imageBox = Box.createHorizontalBox();
    private Thread loadImageThread;

    private JButton exitButton = new JButton("Exit");
    
    private JScrollPane additionalInfoPane;
    private JTextPane detailedInfo = new JTextPane();


    public VistaInformacio(PresentationCtrl _CtrlPresentation) {
        presentationCtrl = _CtrlPresentation;
        initComponents();
        PresentationUtilities.setIcon(frameVista);
    }

    public void makeVisible(boolean state) {
        loadImageThread.interrupt();
        if (state) frameVista.pack();
        frameVista.setVisible(state);
        frameVista.requestFocus();
    }

    public boolean isVisible() {
        return frameVista.isVisible();
    }

    public void makeVisible(boolean state, int _item) {
        try {
            itemId = _item;
            frameVista.setTitle("Information for item " + itemId);
            chargeInfo();
            chargeAdditionalInfo();
            frameVista.pack();
        }
        catch (Exception e) {
            JOptionPane.showMessageDialog(mainPane, e.getMessage(), "Error!", JOptionPane.WARNING_MESSAGE);
            frameVista.setVisible(false);
            return;
        }
        frameVista.setVisible(state);
        frameVista.requestFocus();
    }

    private void asignarListenersComponents() {

        exitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                makeVisible(false);
            }
        });
    }

    private void initComponents() {
        initMainPane();
        initAdditionalInfoPane();
        asignarListenersComponents();

        initFrameVista();
    }

    private void initFrameVista() {
        frameVista.setMinimumSize(new Dimension(900,600));
        frameVista.setMaximumSize(new Dimension(900,600));
        frameVista.setLocation(0, 0);
        frameVista.setPreferredSize(frameVista.getMinimumSize());
        frameVista.setResizable(false);

        // Posicion y operaciones por defecto
		frameVista.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		// Se agrega panelContenidos al contentPane (el panelContenidos se
		// podria ahorrar y trabajar directamente sobre el contentPane)
        frameVista.getContentPane().setLayout(new BoxLayout(frameVista.getContentPane(), BoxLayout.LINE_AXIS));
        frameVista.getContentPane().add(mainPane);
        frameVista.getContentPane().add(additionalInfoPane);
    }

    private void initMainPane() {
        mainPane.setLayout(new BoxLayout(mainPane, BoxLayout.PAGE_AXIS));
        mainPane.setMinimumSize(new Dimension(449, 599));
        mainPane.setMaximumSize(new Dimension(450, 600));
        mainPane.setPreferredSize(mainPane.getMaximumSize());
        
        addSection("Title:", name);
        descriptionSP.setPreferredSize(PresentationUtilities.getLargeDimension());
        addSection("Description:", descriptionSP);
        addSection("Average Rating:", avgRating);
        mainPane.add(imageBox);
        mainPane.add(Box.createVerticalStrut(5));
        
        mainPane.add(PresentationUtilities.createButtonBox(exitButton));
        mainPane.add(Box.createVerticalStrut(5));

        name.setHorizontalAlignment(JTextField.CENTER);
        name.setFont(new Font(Font.MONOSPACED, Font.BOLD, 20));
        name.setBorder(BorderFactory.createLineBorder(Color.white, 0));
        name.setEditable(false);
        
        description.setLineWrap(true);
        description.setWrapStyleWord(true);
        description.setEditable(false);

        avgRating.setBorder(BorderFactory.createLineBorder(Color.white, 0));
        avgRating.setEditable(false);
    }

    private void initAdditionalInfoPane() {
        detailedInfo.setLayout(new BoxLayout(detailedInfo, BoxLayout.PAGE_AXIS));
        detailedInfo.setEditable(false);
        
        additionalInfoPane = new JScrollPane(detailedInfo);
        additionalInfoPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        additionalInfoPane.getVerticalScrollBar().setUnitIncrement(5);
    }

    private void chargeAdditionalInfo() {
        // Clear the pane
        detailedInfo.setText("");
        
        TxGetterPresentation txgp = new TxGetterPresentation();
        ArrayList<String> attribs = txgp.getAllAttrs();

        Set<String> descartedAttrs = txgp.getAttributeColName(); //items from the main pane of this view;

        ArrayList<String> info = txgp.getAllInfo(itemId);
        
        // Style for the title text
        SimpleAttributeSet boldStyle = new SimpleAttributeSet();
        StyleConstants.setBold(boldStyle, true);
        
        StyledDocument doc = detailedInfo.getStyledDocument();
        for (int i = 0; i < attribs.size(); ++i) {
            String attrib = attribs.get(i);
            boolean s = descartedAttrs.contains(attrib);
            if (!s) {
                String infoAttrib = info.get(i);
                try {
                    // Insert attribute name (bold)
                    doc.insertString(doc.getLength(), attrib + ": \n", boldStyle);
                    // Insert attribute value
                    if (infoAttrib.isEmpty()) infoAttrib = "[empty]";
                    doc.insertString(doc.getLength(), infoAttrib + "\n\n", null);
                }
                catch (BadLocationException e) {
                    throw new ImplementationError("Couldn't insert text into text pane: " + e.getMessage());
                }
            }
        }
        // Return to top
        detailedInfo.setCaretPosition(0);
    }
    
    private void addSection(String name, Component content) {
        addSectionContents(new JLabel(name));
        addSectionContents(content);
    }
    
    private void addSectionContents(Component component) {
        Box box = Box.createHorizontalBox();
        box.add(Box.createHorizontalStrut(5));
        box.add(component);
        box.add(Box.createHorizontalGlue());
        box.add(Box.createHorizontalStrut(5));
        mainPane.add(box);
        mainPane.add(Box.createVerticalStrut(10));
    }

    private String reduceString(String a, int size){
        String temp[] = a.split(" ");
        String result = "";

        int i = 0;
        while (size > temp[i].length()) {
            result += temp[i] + " ";
            size -= temp[i].length()+1;
            ++i;
        }
        return result;
    }

    private void chargeInfo() {
        TxGetterPresentation txgp = new TxGetterPresentation();
        ArrayList<String> info = txgp.getInfo(itemId);
        String full_Title = info.get(0);

        if (info.get(0).length() > 40) info.set(0, info.get(0).split(":")[0]);
        if (info.get(0).length() > 40) info.set(0, reduceString(info.get(0), 40)); //For longs titles that does not have ":"

        name.setText(info.get(0));
        name.setToolTipText(full_Title);
        description.setText(info.get(1));
        avgRating.setText(info.get(2));
        String path = info.get(3);
        
        // Return to top
        description.setCaretPosition(0);
        
        // delete previous image
        imageBox.removeAll();
        
        int currentItemId = itemId;
        
        frameVista.pack();
        // schedule adding the new image (in a non-UI thread): chargeImage(path);
        loadImageThread = new Thread(() -> {
            try {
                chargeImage(path, currentItemId);
            } catch (InterruptedException e) {
                // The thread was interrupted, don't do anything
            }
        });
        loadImageThread.start();
    }
    
    private void chargeImage(String path, int conditionalId) throws InterruptedException {
        // url contains [0] url of image and [1] url of thumbnail (as backup)
        String[] url = ImageFinder.getImage(path);
        BufferedImage myPicture = loadImageWithBackup(url[0], url[1]);
        
        // Check for interrupts
        if (Thread.interrupted()) throw new InterruptedException();
        // If the frame has been closed, don't do anything
        if (!frameVista.isVisible()) return;
        // If the frame has been closed and then opened again, don't do anything
        if (itemId != conditionalId) return;
        
        SwingUtilities.invokeLater(() -> {
            ImageIcon img = new ImageIcon(myPicture);
            if (img.getImage().getHeight(null) > 200) {
                img = PresentationUtilities.scaledIcon(img, -1, 200);
            }
            JLabel picLabel = new JLabel(img);
            picLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            // add new image
            imageBox.add(picLabel);
            
            frameVista.pack();
        });
    }
    
    private BufferedImage loadImageWithBackup(String url, String backupUrl) throws InterruptedException {
        
        BufferedImage myPicture = null;
        try {
            myPicture = ImageIO.read(new URL(url));
            // Invalid image, try with backup
            if (myPicture == null) throw new IOException();
        }
        catch (IOException e) {
            // Check for interrupts
            if (Thread.interrupted()) throw new InterruptedException();
            
            // Try backup link
            try {
                myPicture = ImageIO.read(new URL(backupUrl));
            } catch (IOException e1) {
                throw new ImplementationError("Canot load image (URL: " + url + "): " + e.getMessage());
            }
        }
        return myPicture;
    }
}

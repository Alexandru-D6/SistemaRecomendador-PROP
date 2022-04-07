package Presentation;

import java.awt.*;
import javax.swing.*;

import Domain.TransactionControllers.TxGetterPresentation;
import Presentation.PresentationCtrl.userType;

import java.awt.event.*;

public class VistaLogin {

    PresentationCtrl presentationCtrl;

    private JFrame frameVista = new JFrame("Login");

    private JPanel loginPane = new JPanel();

    private JTextField userName = new JTextField("Insert your Username");
    private JButton loginButton = new JButton("Login");

    TxGetterPresentation txgp = new TxGetterPresentation();

    public VistaLogin(PresentationCtrl _CtrlPresentation) {
        presentationCtrl = _CtrlPresentation;
        initComponents();
        PresentationUtilities.setIcon(frameVista);
    }

    public void makeVisible(boolean state) {
        if (state) frameVista.pack();
        frameVista.setVisible(state);
        frameVista.requestFocus();
    }

    public boolean isVisible() {
        return frameVista.isVisible();
    }

    private void asignarListenersComponents() {

        userName.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                loginButton.doClick();
            }
        });

        //https://docs.oracle.com/javase/tutorial/uiswing/events/eventsandcomponents.html
        userName.addFocusListener(new FocusListener() {
            public void focusGained(FocusEvent e) {
                if (userName.getForeground() == Color.gray) {
                    userName.setText("");
                    userName.setForeground(Color.black);
                }
            }
            public void focusLost(FocusEvent e) {
                if (userName.getText().equals("")) {
                    userName.setForeground(Color.gray);
                    userName.setText("Insert your UserID");
                }
            }
        });

        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                if (userName.getForeground() == Color.gray) {
                    JOptionPane.showMessageDialog(frameVista, "Please insert your UserID or \"admin\"", "Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                boolean isAdmin = userName.getText().toLowerCase().equals("admin");
                
                if (isAdmin) presentationCtrl.setCurrentType(userType.ADMIN);
                else presentationCtrl.setCurrentType(userType.USER);

                if (!presentationCtrl.getConfiguraModel().configured()) {
                    // Model is not initialized
                    if (isAdmin) presentationCtrl.initConfiguraModel();
                    else JOptionPane.showMessageDialog(frameVista, "The model is not initialized, enter as \"admin\" to configure it.", "Error", JOptionPane.WARNING_MESSAGE);
                }
                else {
                    // Model is initialized
                    if (isAdmin) {
                        presentationCtrl.getMainMenu().setUserSelected(-1);
                        presentationCtrl.initMainMenu();
                    }
                    else {
                        int userId;
                        try {
                            userId = Integer.parseInt(userName.getText());
                        }
                        catch (NumberFormatException e) {
                            JOptionPane.showMessageDialog(frameVista, "The user doesn't exist in our database. Make sure you have entered a numeric ID.", "Error", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        boolean exists = txgp.checkUser(userId);
                        if (exists) {
                            presentationCtrl.getMainMenu().setUserSelected(userId);
                            presentationCtrl.initMainMenu();
                        }
                        else {
                            JOptionPane.showMessageDialog(frameVista, "The user doesn't exist in our database.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            }
        });
    }

    private void initComponents() {
        initButtons();
        initTextFields();

        initLoginPane();
        asignarListenersComponents();

        initFrameVista();
    }

    private void initFrameVista() {
        frameVista.setMinimumSize(new Dimension(450,400));
        frameVista.setMaximumSize(new Dimension(450,400));
        frameVista.setPreferredSize(frameVista.getMinimumSize());
        frameVista.setResizable(false);
		frameVista.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frameVista.setLayout(new GridLayout(1,1));

        frameVista.add(loginPane);
    }

    private void initLoginPane() {
        loginPane.setLayout(new BoxLayout(loginPane, BoxLayout.Y_AXIS));
        
		loginPane.add(Box.createVerticalGlue());
        
        JLabel label = new JLabel("Enter your user ID (or \"admin\" for admin access)");
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginPane.add(label);
        
        loginPane.add(Box.createVerticalStrut(10));
        
        Box horizontalBox = Box.createHorizontalBox();
        horizontalBox.setMaximumSize(new Dimension(5000, 30));
        horizontalBox.add(Box.createHorizontalGlue());
        horizontalBox.add(userName);
        horizontalBox.add(Box.createHorizontalStrut(10));
        horizontalBox.add(loginButton);
        horizontalBox.add(Box.createHorizontalGlue());
        
        loginPane.add(Box.createVerticalStrut(10));
        
		loginPane.add(horizontalBox);
        
        loginPane.add(Box.createVerticalGlue());
    }

    private void initTextFields() {
        userName.setForeground(Color.gray);

        userName.setFont(new Font(Font.MONOSPACED, Font.PLAIN,  13));

        userName.setColumns(20);
        
        userName.setMaximumSize(new Dimension(500, 30));
    }

    private void initButtons() {
    }
}

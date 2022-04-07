package Presentation;

import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;

import java.awt.event.*;
import java.text.DecimalFormat;

import Presentation.CustomComponents.*;
import Presentation.CustomComponents.ItemStruct.type;
import Presentation.PresentationCtrl.userType;
import Domain.TransactionControllers.*;


public class VistaMainMenu {
    PresentationCtrl presentationCtrl;

    final Dimension FRAME_SIZE = new Dimension(900,700);
    JFrame frameVista = new JFrame("Main Menu");

    JTabbedPane tabbedPane = new JTabbedPane();

    JTextField userSelected = new JTextField("50", 20);

    ItemsList<ItemStruct> itemsList = new ItemsList<>();
    ItemsList<ItemStruct> usersList = new ItemsList<>();
    ItemsList<AttributeStruct> attributeList = new ItemsList<>();

    JPanel itemsPane = new JPanel();
    JPanel usersPane = new JPanel();
    JPanel attributePane = new JPanel();

    JPanel optionPane = new JPanel();
    JButton logoutButton = new JButton("Logout");

    JButton createItemButton = new JButton("Create new item");
    JButton createUserButton = new JButton("Create new user");
    JButton deleteItemButton = new JButton("Delete selected items");
    JButton deleteUserButton = new JButton("Delete selected users");
    JButton deleteAttributeButton = new JButton("Delete selected attributes");
    JButton applyWeightsButton = new JButton("Apply Weights");

    JPanel modelConfigPane = new JPanel();
    ComboBoxWithIdentifier algorithmBox;
    ComboBoxWithIdentifier distanceBox;
    JButton applyButton = new JButton("Apply");
    JButton saveButton = new JButton("Save to file");

    JButton evaluateButton = new JButton("Evaluate");
    JButton testElbowButton = new JButton("Test Elbow Method");
    JButton testSihlouetteButton = new JButton("Test Silhouette Method");
    JButton testPerformanceButton = new JButton("Test Performance");

    JTextArea results = new JTextArea();

    TxGetterPresentation txgp = new TxGetterPresentation();

    public enum paneMode {
        AllItems,
        AllUsers,
        ModelConfig,
        RatedItems,
        RecommendatedItems,
        Option,
        ManageAttributes
    }

    paneMode mode = paneMode.AllItems;
    

    public VistaMainMenu(PresentationCtrl _CtrlPresentation) {
        presentationCtrl = _CtrlPresentation;
        initComponents();
        PresentationUtilities.setIcon(frameVista);
    }

    public void makeVisible(boolean state) {
        if (state) {
            if (presentationCtrl.currentType == userType.ADMIN) mode = paneMode.AllItems;
            else mode = paneMode.RatedItems;
            initTabs(); 
            updatePPane(); 
        }
        frameVista.setVisible(state);
        frameVista.requestFocus();
    }

    public boolean isVisible() {
        return frameVista.isVisible();
    }

    public void update() {
        frameVista.pack();
    }

    private void initComponents() {
        initPanes();
        initTabs();
        initModelConfigPane();
        initAllItems();
        initAllUsers();
        initAllAttributes();

        asignarListenersComponents();

        initFrameVista();
    }

    private void asignarListenersComponents() {

        tabbedPane.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e) {
                if (presentationCtrl.getCurrentType() == userType.ADMIN) {
                    switch(tabbedPane.getSelectedIndex()) {
                        case 0: //Items
                            setPaneMode(paneMode.AllItems);
                            break;
                        case 1: //Users
                            setPaneMode(paneMode.AllUsers);
                            break;
                        case 2: //Manage Attributes
                            setPaneMode(paneMode.ManageAttributes);
                            break;
                        case 3: //Config Model
                            chargeConfig();
                            setPaneMode(paneMode.ModelConfig);
                            break;
                        case 4: //Options
                            setPaneMode(paneMode.Option);
                            break;
                    }
                }else {
                    switch(tabbedPane.getSelectedIndex()) {
                        case 0: //Ratings
                            setPaneMode(paneMode.RatedItems);
                            break;
                        case 1: //Recommendations
                            setPaneMode(paneMode.RecommendatedItems);
                            break;
                        case 2: //Option
                            setPaneMode(paneMode.Option);
                            break;
                    }
                }
            }

            public void mousePressed(MouseEvent e) {}
            public void mouseReleased(MouseEvent e) {}
            public void mouseEntered(MouseEvent e) {}
            public void mouseExited(MouseEvent e) {}
            
        });
        
        logoutButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                presentationCtrl.initLogin();
            }
        });

        createItemButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                presentationCtrl.getAfegirItem().makeVisible(true);
            }
        });
        createUserButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                TxCrearUsuari txcu = new TxCrearUsuari();
                try {
                    txcu.execute();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(createUserButton, ex.getMessage(), "Error!", JOptionPane.WARNING_MESSAGE);
                }
                JOptionPane.showMessageDialog(frameVista, "New user id is: " + String.valueOf(txcu.getResult()));
                usersList.addItem(new ItemStruct(type.allUsers, String.valueOf(txcu.getResult())));
                update();
            }
        });

        deleteItemButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                int res = JOptionPane.showConfirmDialog(
                    deleteItemButton,
                    "Are you sure that you want\n"
                    + "to delete the selected Items?",
                    "Confirmation",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
                );
                if (res == JOptionPane.NO_OPTION || res == JOptionPane.CLOSED_OPTION) {
                    itemsList.uncheckSelected();
                    return;
                }

                for (Struct str : itemsList.getSelected()) {
                    ItemStruct item = (ItemStruct) str;
                    try {
                        TxEliminarItem txei = new TxEliminarItem(item.getId());
                        txei.execute();
                        itemsList.delete(item);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(deleteItemButton, ex.getMessage(), "Error!", JOptionPane.WARNING_MESSAGE);
                    }
                }
                itemsList.redraw();
            }
        });
        deleteUserButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                int res = JOptionPane.showConfirmDialog(
                    deleteUserButton,
                    "Are you sure that you want\n"
                    + "to delete the selected Users?",
                    "Confirmation",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
                );
                if (res == JOptionPane.NO_OPTION || res == JOptionPane.CLOSED_OPTION) {
                    usersList.uncheckSelected();
                    return;
                }

                for (Struct str : usersList.getSelected()) {
                    ItemStruct user = (ItemStruct) str;
                    try {
                        TxEliminarUsuari txeu = new TxEliminarUsuari(user.getId());
                        txeu.execute();
                        usersList.delete(user);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(deleteUserButton, ex.getMessage(), "Error!", JOptionPane.WARNING_MESSAGE);
                    }
                }
                usersList.redraw();
            }
        });
        deleteAttributeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                int res = JOptionPane.showConfirmDialog(
                    deleteAttributeButton,
                    "Are you sure that you want\n"
                    + "to delete the selected Attributes?",
                    "Confirmation",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
                );
                if (res == JOptionPane.NO_OPTION || res == JOptionPane.CLOSED_OPTION) {
                    attributeList.uncheckSelected();
                    return;
                }

                int compensateIndex = 0;

                for (Struct str : attributeList.getSelected()) {
                    AttributeStruct attr = (AttributeStruct) str;
                    try {
                        TxEliminarAtribut txea = new TxEliminarAtribut(attr.getIndex() - compensateIndex);
                        txea.execute();
                        attributeList.delete(str);
                        // modify the index variable of upcoming attributes
                        compensateIndex++;
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(deleteAttributeButton, ex.getMessage(), "Error!", JOptionPane.WARNING_MESSAGE);
                    }
                }
                
                // modify the index variable inside AttributeStruct
                for (int i = 0; i < attributeList.length(); i++) {
                    attributeList.get(i).setIndex(i);
                }
                
                attributeList.redraw();
            }
        });

        applyWeightsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ArrayList<Double> weights = new ArrayList<Double>();
                ArrayList<String> wrongWeights = new ArrayList<String>();
                for (int i = 0; i < attributeList.length(); ++i) {
                    double temp = Double.NaN;
                    try {
                        temp = Double.parseDouble(attributeList.get(i).getWeight());
                    }catch (NumberFormatException exp){
                        JOptionPane.showMessageDialog(frameVista, "The weight of the attribute\"" + attributeList.get(i).getAttributeName() + "\" must be a valid number.");
                        return;
                    }
                    if (temp >= 0.0) weights.add(temp);
                    else wrongWeights.add(attributeList.get(i).getAttributeName());
                }

                if (wrongWeights.size() != 0) {
                    String message = "The weights of the attributes:\n";
                    for (var str : wrongWeights) message += ("\"" + str + "\"\n");
                    message += "must be positive";
                    JOptionPane.showMessageDialog(applyWeightsButton, message);
                    return;
                }

                TxConfigurarModel txcm = new TxConfigurarModel();
                txcm.setWeights(weights);
                applyWeightsButton.setEnabled(false);
            }
        });
        
        algorithmBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                applyButton.setEnabled(true);
            }
        });
        distanceBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                applyButton.setEnabled(true);
            }
        });
        applyButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                applyButton.setEnabled(false);
                TxConfigurarModel txcm = new TxConfigurarModel();
                try {
                    txcm.setDistanceFunction(distanceBox.getSelectedObject());
                    txcm.setRecommendationAlgorithm(algorithmBox.getSelectedObject());
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(applyButton, ex.getMessage(), "Error!", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        saveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saveButton.setEnabled(false);
                frameVista.pack();

                SwingUtilities.invokeLater(() -> {
                    SaveFileChooser sfc = new SaveFileChooser();
                    sfc.showDialog(saveButton);
                    saveButton.setEnabled(true);
                });
            }
        });

        evaluateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                evaluateButton.setEnabled(false);
                frameVista.pack();

                SwingUtilities.invokeLater(() -> {
                    
                    TxAvaluarModel txam = new TxAvaluarModel();
                    DecimalFormat df = new DecimalFormat("0.00");
                    long startTime = System.currentTimeMillis();
                    try {
                        txam.execute();
                        long endTime = System.currentTimeMillis();
                        String elapsedTime = df.format((endTime - startTime) / 1000.0);
                        String ndcg = df.format(txam.getResult()*100);
                        String result = "Model evaluated successfully in " + elapsedTime + " seconds\nResulting NDCG: " + ndcg + "%";
                        JOptionPane.showMessageDialog(evaluateButton, result, "Done!", JOptionPane.INFORMATION_MESSAGE);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(evaluateButton, ex.getMessage(), "Error!", JOptionPane.WARNING_MESSAGE);
                    }
                    evaluateButton.setEnabled(true);
                });
            }
        });

        testElbowButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                testElbowButton.setEnabled(false);
                frameVista.pack();

                SwingUtilities.invokeLater(() -> {
                    TxAvaluarKIdeal txaki = new TxAvaluarKIdeal();
                    
                    try {
                        txaki.executeElbow();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(testElbowButton, ex.getMessage(), "Error!", JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    ArrayList<String> result = txaki.getResultString();
                    String output = "Elbow method executed successfully!\n" +
                                    "This algorithm computes the squared\n" +
                                    "error (distance) from each user to their\n" + 
                                    "cluster, when a given number of clusers\n" + 
                                    "are used. Use the results below in order\n" + 
                                    "to determine the ideal value of k (the\n" + 
                                    "iteration where the result starts to\n" + 
                                    "not vary that much).\n\n";

                    for (int i = 0; i < result.size() ; ++i) {
                        output += "  " + (i+1) + ": " + result.get(i) + "\n";
                    }

                    JOptionPane.showMessageDialog(testElbowButton, output, "Done!", JOptionPane.INFORMATION_MESSAGE);
                    testElbowButton.setEnabled(true);
                });
            }
        });

        testSihlouetteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                testSihlouetteButton.setEnabled(false);
                frameVista.pack();

                SwingUtilities.invokeLater(() -> {
                    int res = JOptionPane.showConfirmDialog(
                        testSihlouetteButton,
                        "This test takes a lot of time to\n"
                        + "execute, are you sure to do it?",
                        "Confirmation",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                    );
                    if (res == JOptionPane.NO_OPTION || res == JOptionPane.CLOSED_OPTION) {
                        testSihlouetteButton.setEnabled(true);
                        return;
                    }

                    TxAvaluarKIdeal txaki = new TxAvaluarKIdeal();
                    try {
                        txaki.executeSilhoutte();

                        ArrayList<String> result = txaki.getResultString();
                        String output = "Silhouette method executed successfully!\n" +
                                        "Each execution represents the number of\n" + 
                                        "clusters used.\n" + 
                                        "This test computes the same metrics as the Elbow\n" + 
                                        "method, but it does so more accurately. However, it\n" + 
                                        "takes more time to compute. To identify the\n" + 
                                        "ideal k, you need to find the maximum value of\n" + 
                                        "the results.\n\n";

                        for (int i = 0; i < result.size() ; ++i) {
                            output += "  " + (i+1) + ": " + result.get(i) + "\n";
                        }

                        JOptionPane.showMessageDialog(testElbowButton, output, "Done!", JOptionPane.INFORMATION_MESSAGE);
                    }catch (Exception exp) {
                        JOptionPane.showMessageDialog(testPerformanceButton, exp.getMessage(), "Error!", JOptionPane.WARNING_MESSAGE);
                    }
                    testSihlouetteButton.setEnabled(true);
                });
            }
        });

        testPerformanceButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                testPerformanceButton.setEnabled(false);
                frameVista.pack();

                SwingUtilities.invokeLater(() -> {
                    TxTestPerformance txtp = new TxTestPerformance();

                    try{
                        txtp.executeClusters();
                        ArrayList<String> result = txtp.getResultString();
                        String output = "Performance test executed successfully!\n" +
                                        "Each execution represents the time that\n" + 
                                        "has taken to execute the cluster algorithm\n" +
                                        "with that amount of clusters.\n" +
                                        "Use the results below to compare the performance\n" +
                                        "of this algorithm with different values of k.\n\n";

                        for (int i = 0; i < result.size() ; ++i) {
                            output += "  " + (i+1) + ": " + result.get(i) + " ms.\n";
                        }

                        JOptionPane.showMessageDialog(testPerformanceButton, output, "Done!", JOptionPane.INFORMATION_MESSAGE);
                    } catch (Exception exp) {
                        JOptionPane.showMessageDialog(testPerformanceButton, exp.getMessage(), "Error!", JOptionPane.WARNING_MESSAGE);
                    }
                    testPerformanceButton.setEnabled(true);
                });
            }
        });
    }

    private void initFrameVista() {
        frameVista.setMinimumSize(FRAME_SIZE);
        frameVista.setMaximumSize(FRAME_SIZE);
        frameVista.setPreferredSize(FRAME_SIZE);
        frameVista.setResizable(false);

        // Posicion y operaciones por defecto
		frameVista.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frameVista.getContentPane().setLayout(new BoxLayout(frameVista.getContentPane(), BoxLayout.LINE_AXIS));

        frameVista.getContentPane().add(tabbedPane);
    }

    private void initTabs() {
        tabbedPane.removeAll();
        if (presentationCtrl.getCurrentType() == userType.ADMIN) {
            tabbedPane.addTab("Items", itemsPane);
            tabbedPane.addTab("Users", usersPane);
            tabbedPane.addTab("Attributes", attributePane);
            tabbedPane.addTab("Configure Model", modelConfigPane);
            tabbedPane.addTab("Options", optionPane);
        } else {
            tabbedPane.addTab("Ratings", new RatingsPanel(getUserSelected(), frameVista));
            tabbedPane.addTab("Recommendations", new RecommendationsPanel(getUserSelected()));
            tabbedPane.addTab("Options", optionPane);
        }
    }

    private void initPanes() {
        // ITEMS
        itemsPane.setLayout(new BoxLayout(itemsPane, BoxLayout.PAGE_AXIS));
        itemsPane.add(itemsList);
        itemsPane.add(Box.createVerticalStrut(10));
        itemsPane.add(PresentationUtilities.createButtonBox(createItemButton, deleteItemButton));
        itemsPane.add(Box.createVerticalStrut(10));
        
        // USERS
        usersPane.setLayout(new BoxLayout(usersPane, BoxLayout.PAGE_AXIS));
        usersPane.add(usersList);
        usersPane.add(Box.createVerticalStrut(10));
        usersPane.add(PresentationUtilities.createButtonBox(createUserButton, deleteUserButton));
        usersPane.add(Box.createVerticalStrut(10));
        
        // ATTRIBUTES
        attributePane.setLayout(new BoxLayout(attributePane, BoxLayout.PAGE_AXIS));
        attributePane.add(attributeList);
        attributePane.add(Box.createVerticalStrut(10));
        attributePane.add(PresentationUtilities.createButtonBox(deleteAttributeButton, applyWeightsButton));
        applyWeightsButton.setEnabled(false);
        attributePane.add(Box.createVerticalStrut(10));
        
        // OPTIONS
        optionPane.add(Box.createVerticalStrut(10));
        optionPane.add(PresentationUtilities.createButtonBox(logoutButton));
        optionPane.add(Box.createVerticalStrut(10));
    }

    public void updatePPane() {
        if (!presentationCtrl.getConfiguraModel().configured())
            return;
        
        switch (mode) {
            case AllItems:
                tabbedPane.setSelectedIndex(0);
                break;
            case AllUsers:
                //If you want to check this go forward, the problem is that, when i try to recharge
                //this panel after being in rating does nothing. But this solve it. ¯\_(ツ)_/¯
                //tabbedPane.setSelectedIndex(4);
                tabbedPane.setSelectedIndex(1);
                break;
            case ModelConfig:
                tabbedPane.setSelectedIndex(3);
                break;
            case RecommendatedItems:
                if (presentationCtrl.getCurrentType() == userType.ADMIN) {
                    throw new IllegalArgumentException("This tab mode is only for users");
                } else {
                    tabbedPane.setSelectedIndex(1);
                } 
                break;
            case RatedItems:
                if (presentationCtrl.getCurrentType() == userType.ADMIN) {
                    throw new IllegalArgumentException("This tab mode is only for users");
                } else {
                    tabbedPane.setSelectedIndex(0);
                } 
                break;
            case Option:
                break;
            case ManageAttributes:
                updateWeigths();
                tabbedPane.setSelectedIndex(2);  
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + mode);
        }
        update();
    }

    private void chargeConfig() {
        String[] alg = txgp.getAlgorithms();
        String[] dist = txgp.getDistances();

        algorithmBox.emptyComboBox();
        distanceBox.emptyComboBox();
        algorithmBox.addItems(alg);
        distanceBox.addItems(dist);

        algorithmBox.setSelectedObject(txgp.getCurrentAlgorithm());
        distanceBox.setSelectedObject(txgp.getCurrentDistance());

        applyButton.setEnabled(false);
    }

    private void initAllItems() {
        itemsList.setHeaders(new String[]{"Item ID", "Information", "Select for delete"});
        itemsList.clear();
        ArrayList<Integer> itemStrings = txgp.getItems();

        itemStrings.forEach(item -> {
            itemsList.addItem(new ItemStruct(type.allItems, item.toString()));
        });
    }

    private void initAllUsers() {
        usersList.setHeaders(new String[]{"User ID", "Ratings", "Recommendations", "Select for delete"});
        usersList.clear();
        ArrayList<String> itemStrings = txgp.getUsers();

        itemStrings.forEach(item -> {
            usersList.addItem(new ItemStruct(type.allUsers, item));
        });
    }

    public void initAllAttributes() {
        attributeList.setHeaders(new String[]{"Attribute", "Weight", "Type", "Create Derived", "Select for delete"});
        attributeList.clear();
        ArrayList<String> attrs = txgp.getAllAttrs();
        ArrayList<String> weights = txgp.getAllWeight();
        ArrayList<String> types = txgp.getAllTypes();

        for (int i = 0; i < attrs.size(); ++i) {
            attributeList.addItem(new AttributeStruct(i, attrs.get(i), weights.get(i), types.get(i), applyWeightsButton));
        }
        attributeList.redraw();
    }

    private void updateWeigths() {
        ArrayList<String> weights = txgp.getAllWeight();
        for (int i = 0; i < attributeList.length(); ++i) {
            attributeList.get(i).setWeight(weights.get(i));
        }
        applyWeightsButton.setEnabled(false);
    }

    private void initModelConfigPane() {
        modelConfigPane.setLayout(new GridBagLayout());

        // CONFIG
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(10,10,10,10);

        c.anchor = GridBagConstraints.LINE_START;
        c.gridx = 0;
        c.gridy = 0;
        JTextField text = new JTextField("Configure model", 15);
        text.setFont(new Font(Font.MONOSPACED, Font.ITALIC, 20));
        text.setEditable(false);
        text.setHorizontalAlignment(JTextField.LEFT);
        text.setBorder(BorderFactory.createEmptyBorder());
        modelConfigPane.add(text, c);

        c.gridx = 1;
        c.gridy = 1;
        algorithmBox = new ComboBoxWithIdentifier("Algorithms", new String[0]);
        modelConfigPane.add(algorithmBox, c);

        c.gridx = 1;
        c.gridy = 2;
        distanceBox = new ComboBoxWithIdentifier("Distance", new String[0]);
        modelConfigPane.add(distanceBox, c);

        c.anchor = GridBagConstraints.LINE_END;
        c.gridx = 0;
        c.gridy = 3;
        applyButton.setEnabled(false);
        modelConfigPane.add(applyButton, c);
        c.anchor = GridBagConstraints.LINE_START;
        c.gridx = 1;
        c.gridy = 3;
        modelConfigPane.add(saveButton, c);

        // TESTING
        c.anchor = GridBagConstraints.LINE_START;
        c.insets = new Insets(50,10,10,10);
        c.gridx = 0;
        c.gridy = 4;
        JTextField text2 = new JTextField("Test model", 15);
        text2.setFont(new Font(Font.MONOSPACED, Font.ITALIC, 20));
        text2.setEditable(false);
        text2.setHorizontalAlignment(JTextField.LEFT);
        text2.setBorder(BorderFactory.createEmptyBorder());
        modelConfigPane.add(text2, c);

        c.anchor = GridBagConstraints.LINE_START;
        c.insets = new Insets(10,10,10,10);
        c.gridx = 1;
        c.gridy = 5;
        modelConfigPane.add(evaluateButton, c);
        c.gridx = 1;
        c.gridy = 6;
        modelConfigPane.add(testElbowButton, c);
        c.gridx = 1;
        c.gridy = 7;
        modelConfigPane.add(testSihlouetteButton, c);
        c.gridx = 1;
        c.gridy = 8;
        modelConfigPane.add(testPerformanceButton, c);

        chargeConfig();
    }

    public void setPaneMode(paneMode _mode) {
        mode = _mode;
        updatePPane();
    }

    public Integer getUserSelected() { return Integer.parseInt(userSelected.getText()); }
    public void setUserSelected(int userId) { userSelected.setText(String.valueOf(userId)); }

    public void addItem(int id) {
        itemsList.addItem(new ItemStruct(type.allItems, String.valueOf(id)));
        update();
    };
}
package Presentation;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import Domain.TransactionControllers.TxCrearAtributDerivat;
import Domain.TransactionControllers.TxGetterPresentation;
import Domain.TransactionControllers.TxCrearAtributDerivat.Operation;
import Presentation.CustomComponents.DerivedStruct;
import Presentation.CustomComponents.ItemsList;
import Presentation.CustomComponents.TextFieldWithIdentifier;

import java.util.ArrayList;

public class VistaCreateDerived {
    PresentationCtrl presentationCtrl;

    private JFrame frameVista = new JFrame("Create derived attribute");

    ItemsList<DerivedStruct> derivedList = new ItemsList<>();

    JButton add = new JButton("Add rule");
    JButton apply = new JButton("Create attribute");
    JButton delete = new JButton("Delete selected rules");
    TextFieldWithIdentifier name = new TextFieldWithIdentifier("New name");
    TextFieldWithIdentifier defaultValue = new TextFieldWithIdentifier("Default Value");

    ArrayList<String> operators = new ArrayList<String>();

    TxGetterPresentation txgp = new TxGetterPresentation();
    int index;
    boolean isCategorical;

    public VistaCreateDerived(PresentationCtrl _CtrlPresentation) {
        presentationCtrl = _CtrlPresentation;
        initComponents();
        PresentationUtilities.setIcon(frameVista);
    }

    public void makeVisible(boolean state, int _index) {
        index = _index;
        if (state) {
            initAttributes();
            frameVista.pack();
        }
        frameVista.setVisible(state);
        frameVista.requestFocus();
        isCategorical = txgp.getAllTypes().get(index).equals("Categorical");
    }

    public boolean isVisible() {
        return frameVista.isVisible();
    }

    private void asignarListenersComponents() {
        delete.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                int res = JOptionPane.showConfirmDialog(
                    delete,
                    "Are you sure that you want\n"
                    + "to delete the selected Rules?",
                    "Confirmation",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
                );
                if (res == JOptionPane.NO_OPTION || res == JOptionPane.CLOSED_OPTION) {
                    derivedList.uncheckSelected();
                    return;
                }
                derivedList.removeSelected();
                derivedList.redraw();
            }
        });

        add.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                derivedList.addItem(new DerivedStruct(operators));
                derivedList.redraw();
            }
        });

        apply.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (name.isEmpty()) {
                    JOptionPane.showMessageDialog(apply, "You must insert a name for this attribute");
                    return;
                }

                if (defaultValue.isEmpty()) {
                    JOptionPane.showMessageDialog(apply, "You must define default value");
                    return;
                }

                ArrayList<Operation> operations = new ArrayList<Operation>();
                double dValue = Double.NaN;

                try {
                    dValue = parseValue(defaultValue.getText()); 
                } catch (NumberFormatException exp) {
                    // valueIfTrue = TxCrearAtributDerivat.INVALID;
                    String acceptedVals = isCategorical ? "numeric or \"invalid\"" : "numeric or \"same\" or \"invalid\"";
                    JOptionPane.showMessageDialog(apply, "Default value must be " + acceptedVals);
                    return;
                }

                for (int i = 0; i < derivedList.length(); ++i) {
                    DerivedStruct temp = derivedList.get(i);
                    if (temp.getArgument().isEmpty()) {
                        JOptionPane.showMessageDialog(apply, "You must define an argument for each rule");
                        return;
                    }
                    try {
                        operations.add(new Operation(temp.getOperator(), temp.getArgument(), parseValue(temp.getValue())));
                    } catch (NumberFormatException exp) {
                        String acceptedVals = isCategorical ? "numeric or \"invalid\"" : "numeric or \"same\" or \"invalid\"";
                        JOptionPane.showMessageDialog(frameVista, "Value if true must be " + acceptedVals);
                        return;
                    }
                }

                TxCrearAtributDerivat txcad = new TxCrearAtributDerivat(operations, dValue, index, name.getText());
                try {
                    txcad.execute();
                    PresentationCtrl.getInstance().getMainMenu().initAllAttributes();
                    makeVisible(false, 0);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(apply, ex.getMessage(), "Error!", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }
        });
    }

    private void initComponents() {

        initPanes();
        initAttributes();
        asignarListenersComponents();

        initFrameVista();
    }

    private void initFrameVista() {
        frameVista.setMinimumSize(new Dimension(650,400));
        frameVista.setMaximumSize(new Dimension(650,400));
        //frameVista.setLocation(0, 0);
        frameVista.setPreferredSize(frameVista.getMinimumSize());
        frameVista.setResizable(false);
		frameVista.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    private void initAttributes() {
        name.setTextInfo("Insert name of new attribute");
        defaultValue.setTextInfo("Insert a default numeric value");
        derivedList.clear();
        derivedList.setHeaders(new String[]{"Operators", "Arguments", "Value if true", "Select for delete"});
        operators = txgp.getOperators(index);
        derivedList.addItem(new DerivedStruct(operators));
    }

    private void initPanes() {
        Container contentPane = frameVista.getContentPane();
        contentPane.setLayout(new BoxLayout(frameVista.getContentPane(), BoxLayout.Y_AXIS));

        contentPane.add(PresentationUtilities.createButtonBox(name));
        contentPane.add(Box.createVerticalStrut(5));
        
        derivedList.setPreferredSize(PresentationUtilities.getLargeDimension());
        contentPane.add(derivedList);
        
        contentPane.add(Box.createVerticalStrut(5));
        
        contentPane.add(PresentationUtilities.createButtonBox(defaultValue));
        contentPane.add(Box.createVerticalStrut(5));
        
        contentPane.add(PresentationUtilities.createButtonBox(add, delete, apply));
        contentPane.add(Box.createVerticalStrut(5));
    }
    
    double parseValue(String value) throws NumberFormatException {
        String text = value.toLowerCase();
        
        if (text.equals("invalid")) return TxCrearAtributDerivat.INVALID;
        if (!isCategorical && text.equals("same")) return TxCrearAtributDerivat.SAME;
        return Double.parseDouble(value); 
    }
}

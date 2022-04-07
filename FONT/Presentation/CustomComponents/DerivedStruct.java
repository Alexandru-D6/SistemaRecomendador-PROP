package Presentation.CustomComponents;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import Presentation.PresentationUtilities;

import java.util.ArrayList;

public class DerivedStruct extends Struct {

    private JComboBox<String> operators = new JComboBox<String>();
    private JTextField argument = new JTextField("Insert the argument", 10);
    private JTextField valueIfTrue = new JTextField("Insert numeric value", 10);
    private JCheckBox toDelete = new JCheckBox();

    private static final Dimension maxSize = PresentationUtilities.getMaximumDimension();
    private static final Dimension minSize = PresentationUtilities.getMinimumDimension();

    public DerivedStruct(ArrayList<String> _operators) {
        argument.setForeground(Color.gray);
        valueIfTrue.setForeground(Color.gray);

        Box horizontalBox = Box.createHorizontalBox();
        horizontalBox.setMaximumSize(PresentationUtilities.getMaximumDimension());
        horizontalBox.setMinimumSize(PresentationUtilities.getMinimumDimension());
        horizontalBox.add(Box.createHorizontalGlue());
        horizontalBox.add(operators);
        horizontalBox.add(Box.createHorizontalGlue());
        operators.setMaximumSize(maxSize);
        operators.setMinimumSize(minSize);
        operators.setFont(new Font(Font.MONOSPACED, Font.BOLD, 12));

        argument.setMaximumSize(maxSize);
        argument.setMinimumSize(minSize);

        valueIfTrue.setMaximumSize(maxSize);
        valueIfTrue.setMinimumSize(minSize);

        toDelete.setMaximumSize(maxSize);
        toDelete.setMinimumSize(minSize);
        toDelete.setHorizontalAlignment(SwingConstants.CENTER);

        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));

        add(horizontalBox);
        add(argument);
        add(valueIfTrue);
        add(toDelete);

        chargeComboBox(_operators);
        assignarListener();
    }

    private void chargeComboBox(ArrayList<String> _operators) {
        operators.removeAll();

        for (var str : _operators) operators.addItem(str);
    }

    public String getOperator() { return operators.getSelectedItem().toString(); }
    public String getArgument() {
        if (argument.getText().equals("Insert the argument")) return "";
        return argument.getText();
    }
    public String getValue() {
        return valueIfTrue.getText();
    }

    private void assignarListener() {
        argument.addFocusListener(new FocusListener() {
            public void focusGained(FocusEvent e) {
                if (argument.getForeground() == Color.gray) {
                    argument.setText("");
                    argument.setForeground(Color.black);
                }
            }
            public void focusLost(FocusEvent e) {
                if (argument.getText().equals("")) {
                    argument.setForeground(Color.gray);
                    argument.setText("Insert the argument");

                }
            }
        });

        valueIfTrue.addFocusListener(new FocusListener() {
            public void focusGained(FocusEvent e) {
                if (valueIfTrue.getForeground() == Color.gray) {
                    valueIfTrue.setText("");
                    valueIfTrue.setForeground(Color.black);
                }
            }
            public void focusLost(FocusEvent e) {
                if (valueIfTrue.getText().equals("")) {
                    valueIfTrue.setForeground(Color.gray);
                    valueIfTrue.setText("Insert numeric value");
                }
            }
        });
    }

    public boolean isForDelete() { return toDelete.isSelected(); }
    public void uncheck() { toDelete.setSelected(false); }
    
}

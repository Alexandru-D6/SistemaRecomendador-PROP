package Presentation.CustomComponents;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import Presentation.PresentationCtrl;
import Presentation.PresentationUtilities;

//https://web.mit.edu/6.005/www/sp14/psets/ps4/java-6-tutorial/components.html

public class AttributeStruct extends Struct {
    
    private JTextField name = new JTextField(15);
    private int index = 0;
    private JTextField weight = new JTextField(15);
    private JTextField type = new JTextField(15);

    private JButton createDerivate = new JButton("Create Derived");
    private JCheckBox toDelete = new JCheckBox();
    
    private JButton applyWeightsButton;

    private static final Dimension maxSize = PresentationUtilities.getMaximumDimension();
    private static final Dimension minSize = PresentationUtilities.getMinimumDimension();
    
    public AttributeStruct(int _index, String _name, String _weight, String _type, JButton _applyWeightsButton) {
        index = _index;
        name.setText(_name);
        name.setEditable(false);
        weight.setText(_weight);
        type.setText(_type);
        type.setEditable(false);
        applyWeightsButton = _applyWeightsButton;

        setSizes();

        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));

        toDelete.setHorizontalAlignment(SwingConstants.CENTER);

        add(name);
        add(weight);
        add(type);
        add(createDerivate);
        add(toDelete);

        assignarListener();
    }

    private void setSizes() {
        name.setMaximumSize(maxSize);
        name.setMinimumSize(minSize);

        weight.setMaximumSize(maxSize);
        weight.setMinimumSize(minSize);

        type.setMaximumSize(maxSize);
        type.setMinimumSize(minSize);

        createDerivate.setMaximumSize(maxSize);
        createDerivate.setMinimumSize(minSize);

        toDelete.setMaximumSize(maxSize);
        toDelete.setMinimumSize(minSize);
    }

    private void assignarListener() {
        createDerivate.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (type.getText().toLowerCase().equals("boolean")) JOptionPane.showMessageDialog(type, "Can not derivate a Boolean Attribute.");
                else PresentationCtrl.getInstance().getCreateDerivated().makeVisible(true, index);
            }
        });
        
        weight.addKeyListener(new KeyListener() {
            public void keyTyped(KeyEvent e) {
                applyWeightsButton.setEnabled(true);
                if (e.getKeyChar() == KeyEvent.VK_ENTER) {
                    applyWeightsButton.doClick();
                }
            }
            @Override
            public void keyPressed(KeyEvent e) {}
            @Override
            public void keyReleased(KeyEvent e) {}
        });
    }

    public boolean isForDelete() { return toDelete.isSelected(); }
    public void uncheck() { toDelete.setSelected(false); }
    
    public int getIndex() { return index; }
    public void setIndex(int _index) { index = _index; }

    public String getAttributeName() { return name.getText(); }

    public String getWeight() { return weight.getText(); }
    public void setWeight(String _weight) { weight.setText(_weight);}
}

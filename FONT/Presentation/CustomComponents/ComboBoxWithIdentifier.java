package Presentation.CustomComponents;

import javax.swing.JComboBox;
import javax.swing.JPanel;

import java.awt.*;
import javax.swing.*;
import java.awt.event.ActionListener;


public class ComboBoxWithIdentifier extends JPanel {

    private JTextField id;
    private JComboBox<String> comboBox;
    
    public ComboBoxWithIdentifier(String _id, String[] _items) {
        setLayout(new GridBagLayout());
        // setBackground(new Color(0.0f, 0.0f, 0.0f, 0.0f));

        id = new JTextField(_id + ": ", 15);
        id.setEditable(false);
        id.setFont(new Font(Font.MONOSPACED, Font.PLAIN,  13));
        id.setHorizontalAlignment(JTextField.RIGHT);
        id.setBorder(BorderFactory.createEmptyBorder());
        // id.setBackground(new Color(0.0f, 0.0f, 0.0f, 0.0f));
        
        comboBox = new JComboBox<String>();
        addItems(_items);


        GridBagConstraints GBC = new GridBagConstraints();
        GBC.anchor = GridBagConstraints.CENTER;
        GBC.insets = new Insets(10, 10, 10, 10);

        GBC.gridx = 0;
        GBC.gridy = 0;
        GBC.anchor = GridBagConstraints.EAST;
        add(id, GBC);

        GBC.gridx = 1;
        GBC.gridy = 0;
        GBC.anchor = GridBagConstraints.WEST;
        add(comboBox, GBC);
    }

    public String getSelectedObject() {
        return comboBox.getSelectedItem().toString();
    }

    public void emptyComboBox() {
        comboBox.removeAllItems();
    }

    public void addItem(String _item) {
        comboBox.addItem(_item);
    }

    public void addItems(String[] _item) {
        for (int i = 0; i < _item.length; ++i) {
            addItem(_item[i]);
        }
    }

    public void addActionListener(ActionListener listener) {
        comboBox.addActionListener(listener);
    }

    public void setSelectedObject(String _object) {
        comboBox.setSelectedItem(_object);
    }
}

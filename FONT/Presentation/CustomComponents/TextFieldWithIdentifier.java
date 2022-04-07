package Presentation.CustomComponents;

import javax.swing.JPanel;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

public class TextFieldWithIdentifier extends JPanel {

    private JTextField id;
    private JTextField textField;
    private String textInfo = "";

    public TextFieldWithIdentifier(String _id) {
        setLayout(new GridBagLayout());

        id = new JTextField(_id + ": ", Integer.max(40, _id.length()+2));
        id.setEditable(false);
        id.setFont(new Font(Font.MONOSPACED, Font.PLAIN,  13));
        id.setHorizontalAlignment(JTextField.RIGHT);
        id.setBorder(BorderFactory.createEmptyBorder());
        
        textField = new JTextField("", 20);
        assignarListener();

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
        add(textField, GBC);
    }

    public TextFieldWithIdentifier(String _id, String _info) {
        this(_id);
        setTextInfo(_info);
    }

    private void assignarListener() {
        textField.addFocusListener(new FocusListener() {
            public void focusGained(FocusEvent e) {
                if (textField.getForeground() == Color.gray) {
                    textField.setText("");
                    textField.setForeground(Color.black);
                }
            }
            public void focusLost(FocusEvent e) {
                if (textField.getText().equals("")) {
                    textField.setForeground(Color.gray);
                    textField.setText(textInfo);
                }
            }
        });
    }

    public String getText() { return textField.getText(); }
    public void setText(String _name) { textField.setText(_name); }

    public void setEditable(boolean editable) {
        textField.setEditable(editable);
        if (!editable) textField.setToolTipText(textField.getText());
        else textField.setToolTipText("");
    }
    public void setDimensions(int _width, int _height) {
        textField.setMaximumSize(new Dimension(_width, _height));
        textField.setMinimumSize(new Dimension(_width-1, _height-1));
        id.setMaximumSize(new Dimension(_width, _height));
        id.setMinimumSize(new Dimension(_width-1, _height-1));

        textField.setPreferredSize(textField.getMinimumSize());
        id.setPreferredSize(id.getMinimumSize());

    }

    public String getTextInfo() { return textInfo; }
    public void setTextInfo(String _info) { 
        textInfo = _info; 
        setText(textInfo);
        textField.setForeground(Color.gray);
    }

    public boolean isEmpty() {
        return textField.getForeground() == Color.gray;
    }

    public void setName(String _name) { id.setText(_name + ": ");}
    
}

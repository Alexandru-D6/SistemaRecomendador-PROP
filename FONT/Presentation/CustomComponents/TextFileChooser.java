package Presentation.CustomComponents;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import Presentation.PresentationUtilities;

public class TextFileChooser extends JPanel {

    private JTextField textField;
    private JButton searchButton;
    private JFileChooser fileChooser;
    
    public TextFileChooser(int cols, int fontSize) {
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        Box hBox = Box.createHorizontalBox();
        hBox.setMaximumSize(new Dimension(5000, 30));
        hBox.add(Box.createHorizontalGlue());

        textField = new JTextField("", cols);
        textField.setBorder(null);
        textField.setMaximumSize(new Dimension(300, 20));
        hBox.add(textField);

        if (PresentationUtilities.fileExists("assets/three_dot.png")) {
            Icon searchIcon = PresentationUtilities.scaledIcon("assets/three_dot.png", 20, 20);
            searchButton = new JButton(searchIcon);
        }
        else
            searchButton = new JButton("...");
        searchButton.setHideActionText(false);
        searchButton.setMargin(new Insets(0,0,0,0));
        searchButton.setPreferredSize(new Dimension(20, 20));
        hBox.add(searchButton);

        fileChooser = new JFileChooser("./DATA/");
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Config files (.info)", "info");
        fileChooser.setFileFilter(filter);
        hBox.add(Box.createHorizontalGlue());

        add(hBox);
        addListener();
    }

    public String getPath() { return textField.getText(); }
    public JTextField getTextField() { return textField; }
    public JButton getButton() { return searchButton; }

    private void addListener() {
        searchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (fileChooser.showOpenDialog(searchButton) == JFileChooser.APPROVE_OPTION) {
                    textField.setText(fileChooser.getSelectedFile().getPath());
                    fireActionListeners();
                }
            }
        });
    }

    public void addActionListener(ActionListener listener) {
        listenerList.add(ActionListener.class, listener);
    }
    
    public void removeActionListener(ActionListener listener) {
        listenerList.remove(ActionListener.class, listener);
    }
    
    public ActionListener[] getActionListeners() {
        return listenerList.getListeners(ActionListener.class);
    }
    
    protected void fireActionListeners() {
        ActionEvent event = new ActionEvent(this, 0, "Selected");
        for (ActionListener listener : getActionListeners()) {
            listener.actionPerformed(event);
        }
    }

}

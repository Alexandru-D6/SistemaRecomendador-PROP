package Presentation.CustomComponents;

import javax.swing.*;

import Domain.TransactionControllers.TxGuardarDades;

import java.awt.*;

public class SaveFileChooser extends JFileChooser {

    public SaveFileChooser() {
        super("./DATA/");
        this.setDialogTitle("Save File");
        this.setApproveButtonText("Save");
        this.setApproveButtonToolTipText("Save the file");
        this.setDialogType(JFileChooser.SAVE_DIALOG);
        this.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        this.setMultiSelectionEnabled(false);
    }
    
    public void showDialog(Component parent) {
        if (showOpenDialog(parent) == JFileChooser.APPROVE_OPTION) {
            String path = getSelectedFile().getAbsolutePath();
            TxGuardarDades txdg = new TxGuardarDades(path, true);
            try {
                txdg.execute();
                JOptionPane.showMessageDialog(parent, "Dataset sucessfully saved to " + path, "Success!", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(parent, ex.getMessage(), "Error!", JOptionPane.WARNING_MESSAGE);
            }
        }
    }     
}

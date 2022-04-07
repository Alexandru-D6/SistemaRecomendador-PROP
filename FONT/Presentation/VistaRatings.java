package Presentation;

import java.awt.Dimension;

import javax.swing.*;

import Presentation.CustomComponents.RatingsPanel;

public class VistaRatings {
    // private PresentationCtrl presentationCtrl;
    private RatingsPanel ratingsPanel;
    private JFrame frameVista = new JFrame("User ratings");
    
    public VistaRatings(PresentationCtrl presentationCtrl) {
        // this.presentationCtrl = presentationCtrl;
        frameVista.setMinimumSize(new Dimension(700, 700));
        frameVista.setMaximumSize(new Dimension(700, 700));
        frameVista.setPreferredSize(frameVista.getMaximumSize());
        PresentationUtilities.setIcon(frameVista);
    }
    
    public void makeVisible(boolean state) {
        if (state) {
            chargeInfo();
            frameVista.pack();
        }

        frameVista.setVisible(state);
        frameVista.requestFocus();
    }

    private void chargeInfo() {
        int userId = PresentationCtrl.getInstance().getMainMenu().getUserSelected();
        frameVista.setTitle("Ratings made by user " + userId);
        ratingsPanel = new RatingsPanel(userId, frameVista);
        
        frameVista.setContentPane(ratingsPanel);
    }
}

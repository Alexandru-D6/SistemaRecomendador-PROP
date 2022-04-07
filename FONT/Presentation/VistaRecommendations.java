package Presentation;

import java.awt.Dimension;

import javax.swing.*;

import Presentation.CustomComponents.RecommendationsPanel;

public class VistaRecommendations {
    // private PresentationCtrl presentationCtrl;
    private RecommendationsPanel recommendationsPanel;
    private JFrame frameVista = new JFrame("User recommendations");
    
    public VistaRecommendations(PresentationCtrl presentationCtrl) {
        // this.presentationCtrl = presentationCtrl;
        PresentationUtilities.setIcon(frameVista);
    }
    
    public void makeVisible(boolean state) {
        if (state) {
            chargeInfo();
            frameVista.pack();
        }
        frameVista.setMinimumSize(new Dimension(400, 200));
        frameVista.setVisible(state);
        frameVista.requestFocus();
    }

    private void chargeInfo() {
        int userId = PresentationCtrl.getInstance().getMainMenu().getUserSelected();
        frameVista.setTitle("User recommendations for user " + userId);
        recommendationsPanel = new RecommendationsPanel(userId);
        
        frameVista.setContentPane(recommendationsPanel);
    }
}

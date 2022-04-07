import Presentation.PresentationCtrl;


public class GUI {

    public static void main(String[] args) {

        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                PresentationCtrl mainWindow = PresentationCtrl.getInstance();
                mainWindow.initLogin();
            }
        });
    }
}

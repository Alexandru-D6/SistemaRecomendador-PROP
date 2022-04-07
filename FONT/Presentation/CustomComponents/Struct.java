package Presentation.CustomComponents;

import javax.swing.JPanel;

public abstract class Struct extends JPanel {
        
    public abstract boolean isForDelete();
    public abstract void uncheck();
    
    @Override
    public void addNotify() {
        // Do nothing (loads the list quickly, but the user cannot interact with it)
    }
    
    public void lazyUpdate() {
        super.addNotify();
    }
}

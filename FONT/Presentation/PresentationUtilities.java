package Presentation;

import javax.swing.*;
import java.awt.*;

public class PresentationUtilities {

    private static final String ICON_NAME = "assets/icon.png";
    private static ImageIcon app_icon = null;
    
    private static final int maxWidth = 150;
    private static final int maxHeight = 30;

    private static final int minWidth = 149;
    private static final int minHeight = 29;
    
    private static final Dimension paneMaxDimension = new Dimension(5000, maxHeight+5);
    private static final Dimension maxDimension = new Dimension(maxWidth, maxHeight);
    private static final Dimension minDimension = new Dimension(minWidth, minHeight);
    private static final Dimension largeDimension = new Dimension(5000, 5000);
    

    public static boolean fileExists(String path) {
        return new java.io.File(path).exists();
    }
    
    public static ImageIcon scaledIcon(String image_path, int width, int heigth) {
        ImageIcon icon = new ImageIcon(image_path);
        return scaledIcon(icon, width, heigth);
    }
    public static ImageIcon scaledIcon(ImageIcon icon, int width, int heigth) {
        Image image = icon.getImage().getScaledInstance(width, heigth, java.awt.Image.SCALE_SMOOTH);
        return new ImageIcon(image);
    }

    public static int getMaxWidthSize() {
        return maxWidth;
    }

    public static Dimension getMaximumDimension() {
        return maxDimension;
    }

    public static Dimension getPaneMaximumDimension() {
        return paneMaxDimension;
    }

    public static Dimension getMinimumDimension() {
        return minDimension;
    }
    
    public static Dimension getLargeDimension() {
        return largeDimension;
    }
    
    public static Box createButtonBox(Component ... comps) {
        Box box = Box.createHorizontalBox();
        box.add(Box.createHorizontalGlue());
        boolean first = true;
        
        for (Component comp : comps) {
            if (first) first = false;
            else box.add(Box.createHorizontalStrut(30));
            box.add(comp);
        }
        
        box.add(Box.createHorizontalGlue());
        box.setMaximumSize(getPaneMaximumDimension());
        
        return box;
    }
    
    public static void setIcon(JFrame frame) {
        app_icon = new ImageIcon(ICON_NAME);
        frame.setIconImage(app_icon.getImage());
    }
}

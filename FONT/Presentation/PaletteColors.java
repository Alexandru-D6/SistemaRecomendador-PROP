package Presentation;

import java.awt.*;

public class PaletteColors {
    //https://coolors.co/7f7caf-cbeaa6-9fb4c7-28587b-b02e0c
    private static String colors[] = {"#7F7CAF","#CBEAA6","#9FB4C7","#28587B","#B02E0C"};

    public static Color getColor(int i) {
        return Color.decode(colors[i]);
    }
}

package Presentation.CustomComponents;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.*;

import Presentation.PresentationUtilities;

public class ItemsList<T extends Struct> extends JScrollPane {
    private JPanel list = new JPanel();
    private JPanel headers = new JPanel();
    boolean initialized = false;

    public ItemsList() {
        list.setLayout(new BoxLayout(list, BoxLayout.PAGE_AXIS));
        headers.setLayout(new BoxLayout(headers, BoxLayout.LINE_AXIS));

        setViewportView(list);
        setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        getVerticalScrollBar().setUnitIncrement(5);
        //list.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        list.add(headers);
        
        addListeners();
    }
    
    public ItemsList(String[] _headers) {
        this();
        setHeaders(_headers);
    }

    public void setHeaders(String[] _headers) {
        headers.removeAll();
        for (var str : _headers) {
            JTextField aux = new JTextField(str, Math.max(5, str.length()));

            aux.setEditable(false);
            aux.setFont(new Font(Font.MONOSPACED, Font.PLAIN,  13));
            aux.setHorizontalAlignment(JTextField.LEFT);
            aux.setMaximumSize(PresentationUtilities.getMaximumDimension());

            headers.add(aux);
        }
    }

    public void addItem(Struct _item) {
        list.add(_item);
        if (initialized) _item.lazyUpdate();
    }

    public void clear() {
        list.removeAll();
        list.add(headers);
    }
    
    public int length() {
        // Remove header
        return list.getComponentCount() - 1;
    }
    
    public T get(int index) {
        @SuppressWarnings("unchecked")
        T item = (T) list.getComponent(index + 1);
        return item;
    }
    
    public void uncheckSelected() {
        for (int i = 1; i < list.getComponentCount(); ++i) {
            Struct itemStruct = (Struct) list.getComponent(i);
            itemStruct.uncheck();
        }
    }
    
    public Collection<Struct> getSelected() {
        ArrayList<Struct> toDelete = new ArrayList<>();

        //this starts with one to avoid the headers panel
        for (int i = 1; i < list.getComponentCount(); ++i) {
            Struct itemStruct = (Struct) list.getComponent(i);

            if (itemStruct.isForDelete()) {
                toDelete.add(itemStruct);
            }
        }
        
        return toDelete;
    }

    public Collection<Struct> removeSelected() {
        Collection<Struct> toDelete = getSelected();
        for (var str : toDelete) delete(str);
        
        return toDelete;
    }
    
    public void delete(Struct _item) {
        list.remove(_item);
    }
    
    private void addListeners() {
        list.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                Component comp = list.getComponentAt(evt.getPoint());
                if (comp instanceof Struct) {
                    ((Struct) comp).lazyUpdate();
                }
            }
        });
    }
    
    public void redraw() {
        revalidate();
        repaint();
    }
}

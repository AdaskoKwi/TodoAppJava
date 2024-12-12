package utils;

import java.awt.*;
import java.util.ArrayList;

public class GetComponents {
    public ArrayList<Component> getAllComponents(Container c) {
        Component[] components = c.getComponents();
        ArrayList<Component> componentArrayList = new ArrayList<>();

        for (Component comp : components) {
            componentArrayList.add(comp);
            if (comp instanceof Container) {
                getAllComponents((Container) comp);
            }
        }

        return componentArrayList;
    }
}

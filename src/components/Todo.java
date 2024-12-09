package components;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

public class Todo {
    public void addTodo(JPanel todoPanel, JPanel todo, JTextField name, JTextField category, JButton removeTodo,  JCheckBox finished) {
        finished.setBackground(Color.decode("#3c6bc3"));
        finished.setBorder(null);

        name.setPreferredSize(new Dimension(135, 25));
        category.setPreferredSize(new Dimension(135, 25));

        finished.setPreferredSize(new Dimension(20,20));

        removeTodo.setBorder(BorderFactory.createLineBorder(Color.RED,2));
        removeTodo.setFocusable(false);
        removeTodo.setBackground(Color.red);
        removeTodo.setPreferredSize(new Dimension(20,20));

        name.setFocusable(false);
        name.setEditable(false);
        category.setFocusable(false);
        category.setEditable(false);

        todo.setPreferredSize(new Dimension(150,100));
        todo.setBackground(Color.decode("#3c6bc3"));
        todo.setBorder(new LineBorder(Color.white, 3, true));
        todo.add(name);
        todo.add(category);
        todo.add(removeTodo);
        todo.add(finished);

        todoPanel.add(todo);
        todoPanel.revalidate();
        todoPanel.repaint();
    }
    public void removeTodo(JPanel todoPanel, JPanel todo) {
        todoPanel.remove(todo);
        todoPanel.revalidate();
        todoPanel.repaint();
    }
}

package UI;

import utils.CategoryTextField;
import utils.GetComponents;
import utils.Todo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.HashSet;

public class TodoAppWindow implements ActionListener {
    JFrame frame = new JFrame("Todo App");
    JPanel userPanel = new JPanel();
    JPanel todoPanel = new JPanel();
    JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
    JButton addTodoButton = new JButton("Add Todo");
    JScrollPane scrollPane = new JScrollPane(todoPanel);
    JTextField nameField = new JTextField();
    CategoryTextField categoryField = new CategoryTextField();
    JLabel appLabel = new JLabel();
    JLabel nameInputLabel = new JLabel();
    JLabel categoryInputLabel = new JLabel();
    JComboBox<String> categoryDropdown = new JComboBox<>();
    HashSet<String> existingCategories = new HashSet<>();

    TodoAppWindow() {
        addTodoButton.setPreferredSize(new Dimension(100,25));
        addTodoButton.setMaximumSize(new Dimension(100, 25));
        addTodoButton.addActionListener(this);
        addTodoButton.setFocusable(false);
        addTodoButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setViewportView(todoPanel);

        nameInputLabel.setText("Enter Todo name:");
        nameInputLabel.setFont(new Font("Arial", Font.BOLD, 15));
        nameInputLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        nameInputLabel.setForeground(Color.WHITE);

        nameField.setBorder(BorderFactory.createLineBorder(Color.decode("#54c1f2"), 3));
        nameField.setEditable(true);
        nameField.setPreferredSize(new Dimension(250,25));
        nameField.setMaximumSize(new Dimension(250, 25));
        nameField.setAlignmentX(Component.CENTER_ALIGNMENT);

        categoryInputLabel.setText("Enter category name:");
        categoryInputLabel.setFont(new Font("Arial", Font.BOLD, 15));
        categoryInputLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        categoryInputLabel.setForeground(Color.WHITE);

        categoryField.setBorder(BorderFactory.createLineBorder(Color.decode("#54c1f2"), 3));
        categoryField.setEditable(true);
        categoryField.setPreferredSize(new Dimension(250,25));
        categoryField.setMaximumSize(new Dimension(250, 25));
        categoryField.setAlignmentX(Component.CENTER_ALIGNMENT);

        appLabel.setText("Java Todo App");
        appLabel.setForeground(Color.WHITE);
        appLabel.setFont(new Font("Arial", Font.BOLD, 35));
        appLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        categoryDropdown.setFocusable(false);
        categoryDropdown.setMaximumSize(new Dimension(200, 35));
        categoryDropdown.addActionListener(this);
        categoryDropdown.addItem("Show all");

        userPanel.setLayout(new BoxLayout(userPanel, BoxLayout.Y_AXIS));
        userPanel.setBackground(Color.decode("#4007d0"));
        userPanel.add(appLabel);
        userPanel.add(Box.createVerticalStrut(10));
        userPanel.add(nameInputLabel);
        userPanel.add(nameField);
        userPanel.add(Box.createVerticalStrut(10));
        userPanel.add(categoryInputLabel);
        userPanel.add(categoryField);
        userPanel.add(Box.createVerticalStrut(10));
        userPanel.add(addTodoButton);
        userPanel.add(Box.createVerticalStrut(10));
        userPanel.add(categoryDropdown);

        todoPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        todoPanel.setBackground(Color.decode("#3c6bc3"));

        splitPane.setEnabled(false);
        splitPane.setDividerSize(0);
        splitPane.add(userPanel);
        splitPane.add(todoPanel);
        splitPane.setResizeWeight(0.1);

        frame.add(splitPane);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800,600);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setIconImage(new ImageIcon("src/icon/Animals-Cat-icon.png").getImage());
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        TodoAppWindow appWindow = new TodoAppWindow();
        appWindow.loadTodos();
    }

    public void loadTodos() {
        String url = "jdbc:mysql://localhost:3306/tododb";
        String username = "root";
        String password = "";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            Connection connection = DriverManager.getConnection(url, username, password);

            Statement statement = connection.createStatement();

            ResultSet resultSet = statement.executeQuery("SELECT * FROM todos");

            while (resultSet.next()) {
                Todo todo = new Todo();

                JPanel singleTodo = new JPanel();
                singleTodo.setLayout(new FlowLayout(FlowLayout.LEFT));
                JTextField name = new JTextField(nameField.getText());
                CategoryTextField category = new CategoryTextField(categoryField.getText());
                JButton removeTodoButton = new JButton("X");

                removeTodoButton.addActionListener(e1 -> {
                    if (e1.getSource() == removeTodoButton) {
                        todo.removeTodo(todoPanel, singleTodo);
                        deleteRowFromDatabase(name);
                        categoryDropdown.remove(category);
                    }
                });

                JCheckBox finished = new JCheckBox();

                todo.addTodo(todoPanel, singleTodo, name, category, removeTodoButton, finished);

                finished.addActionListener(e2 -> {
                    if (e2.getSource() == finished) {
                        updateCheckbox(finished, name.getText());
                    }
                });

                name.setText(resultSet.getString(2));
                category.setText(resultSet.getString(3));
                finished.setSelected(resultSet.getBoolean(4));

                nameField.setText("");
                categoryField.setText("");

                removeTodoButton.addActionListener(this);

                if (!existingCategories.contains(category.getText())) {
                    existingCategories.add(category.getText());
                    categoryDropdown.addItem(category.getText());
                }
            }

            connection.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static void deleteRowFromDatabase(JTextField name) {
        String url = "jdbc:mysql://localhost:3306/tododb";
        String username = "root";
        String password = "";
        String deleteQuery = "DELETE FROM todos WHERE NAME = ?";


        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery)) {

            preparedStatement.setString(1, name.getText());
            int rowsAffected = preparedStatement.executeUpdate();
            System.out.println("Usunięto " + rowsAffected + " wiersz(y).");

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void addRowToDatabase(String name, String category, boolean finished) {
        String url = "jdbc:mysql://localhost:3306/tododb";
        String username = "root";
        String password = "";
        String deleteQuery = "INSERT INTO todos (NAME, CATEGORY, FINISHED) VALUES ( ?, ?, ?)";


        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery)) {

            preparedStatement.setString(1, name);
            preparedStatement.setString(2, category);
            preparedStatement.setBoolean(3, finished);

            int rowsAffected = preparedStatement.executeUpdate();
            System.out.println("Dodano " + rowsAffected + " wiersz(y).");

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void updateCheckbox(JCheckBox checkBox, String name) {
        String url = "jdbc:mysql://localhost:3306/tododb";
        String username = "root";
        String password = "";
        String checkQuery = "UPDATE todos SET FINISHED = 1 WHERE NAME LIKE ?";
        String uncheckQuery = "UPDATE todos SET FINISHED = 0 WHERE NAME LIKE ?";

        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement preparedStatementCheck = connection.prepareStatement(uncheckQuery);
            PreparedStatement preparedStatementUncheck = connection.prepareStatement(checkQuery)) {

            if (checkBox.isSelected()) {
                preparedStatementUncheck.setString(1, name);
                int rowsAffected = preparedStatementUncheck.executeUpdate();
                System.out.println("Zaktualizowano " + rowsAffected + " wiersz(y).");
            } else {
                preparedStatementCheck.setString(1, name);
                int rowsAffected = preparedStatementCheck.executeUpdate();
                System.out.println("Zaktualizowano " + rowsAffected + " wiersz(y).");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void filterTodos(JPanel todoPanel, JComboBox<String> categoryDropdown) {
        GetComponents getComponents = new GetComponents();
        for (Component todo : todoPanel.getComponents()) {
            todo.setVisible(true);
            for (Component c : getComponents.getAllComponents((Container) todo)) {
                if (c instanceof CategoryTextField) {
                    if (!((CategoryTextField) c).getText().equals(categoryDropdown.getSelectedItem())) {
                        todo.setVisible(false);
                    }
                    if (categoryDropdown.getSelectedItem().equals("Show all")) {
                        todo.setVisible(true);
                    }
                }
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addTodoButton) {
            Todo todo = new Todo();

            JPanel singleTodo = new JPanel();
            singleTodo.setLayout(new FlowLayout(FlowLayout.LEFT));
            JTextField name = new JTextField(nameField.getText());
            CategoryTextField category = new CategoryTextField(categoryField.getText());
            JButton removeTodoButton = new JButton("X");

            removeTodoButton.addActionListener(e1 -> {
                if (e1.getSource() == removeTodoButton) {
                    todo.removeTodo(todoPanel, singleTodo);
                    deleteRowFromDatabase(name);
                    categoryDropdown.remove(category);
                }
            });

            JCheckBox finished = new JCheckBox();

            todo.addTodo(todoPanel, singleTodo, name, category, removeTodoButton, finished);

            finished.addActionListener(e2 -> {
                if (e2.getSource() == finished) {
                    updateCheckbox(finished, name.getText());
                }
            });

            addRowToDatabase(name.getText(), category.getText(), finished.isSelected());

            nameField.setText("");
            categoryField.setText("");

            removeTodoButton.addActionListener(this);

            if (!existingCategories.contains(category.getText())) {
                existingCategories.add(category.getText());
                categoryDropdown.addItem(category.getText());
            }

        }
        if (e.getSource() == categoryDropdown) {
            filterTodos(todoPanel, categoryDropdown);
        }
    }
}

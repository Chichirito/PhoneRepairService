import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Arrays;

public class LoginForm extends JFrame {

    // Поля "логина" и "пароля"
    JTextField loginField;
    JPasswordField passField;

    public LoginForm(){
        super("Вход");
        super.setBounds(650, 400, 150, 150);
        super.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Container containerUserData = super.getContentPane();

        // Инициализация полей и создание надписей к ним
        JLabel loginLabel = new JLabel("Логин:");
        loginField = new JTextField("", 10);
        loginField.setPreferredSize(new Dimension(15, 5));
        JLabel passLabel = new JLabel("Пароль:");
        passField = new JPasswordField("", 10);
        passField.setPreferredSize(new Dimension(15, 5));

        // Создание слушателя клавиатуры для поле "логина" и "пароля"
        loginField.addKeyListener(new ButtonSendLoginData());
        passField.addKeyListener(new ButtonSendLoginData());

        // Создание всех панелей, на которых будут располагаться элементы формы
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        JPanel panelButton = new JPanel();
        panelButton.setLayout(new FlowLayout(FlowLayout.CENTER));
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        // Создание кнопки, отправляющей данные для авторизации
        JButton sendLogin = new JButton("Войти");

        // Создание слушателя клавиатуры и нажатия на кнопку
        sendLogin.addActionListener(new ButtonSendLoginData());
        sendLogin.addKeyListener(new ButtonSendLoginData());

        // Размещение элементов по панелям
        panel.add(loginLabel);
        panel.add(loginField);
        panel.add(passLabel);
        panel.add(passField);

        panelButton.add(sendLogin);

        mainPanel.add(panel);
        mainPanel.add(panelButton);

        containerUserData.add(mainPanel);

        // Отображение формы на экране
        super.setVisible(true);

    }

    // Класс, реализующий интерфейс слушателя по отправке данных для авторизации
    class ButtonSendLoginData implements ActionListener, KeyListener {

        @Override
        public void actionPerformed(ActionEvent e) {

            loginSystem();

        }

        public void loginSystem(){
            String login = loginField.getText();
            char[] password = passField.getPassword();
            String firstName = "";
            String lastName = "";

            String rootName = "root";
            String rootPassword = "4554045540";
            String connectURL = "jdbc:mysql://localhost:3306/PhoneService";
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
            } catch (ClassNotFoundException ex) {
                throw new RuntimeException(ex);
            }

            try(Connection connection = DriverManager.getConnection(connectURL, rootName, rootPassword);
                Statement statement = connection.createStatement()) {
                System.out.println("Connection is well");

                ResultSet resultSet = statement.executeQuery("SELECT * FROM users");
                Boolean passFound = false;
                while((resultSet.next()) & (passFound == false)){
                    String log = resultSet.getString("login");
                    char[] pass = resultSet.getString("password").toCharArray();
                    firstName = resultSet.getString("firstname");
                    lastName = resultSet.getString("lastname");
                    if (login.equals(log) & Arrays.equals(password, pass)) {
                        passFound = true;
                        password = null;
                    }
                }
                if (passFound){
                    LoginForm.super.dispose();
                    new ListActsForm();
                    JOptionPane.showMessageDialog(null, "Добро пожаловать, " + firstName + " " + lastName, "Access allowed", JOptionPane.PLAIN_MESSAGE);
                }else{
                    JOptionPane.showMessageDialog(null, "Вы ввели неправильный Логин или Пароль", "Access denied", JOptionPane.PLAIN_MESSAGE);
                }
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }

        @Override
        public void keyTyped(KeyEvent e) {

        }

        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER){
                loginSystem();
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {

        }
    }

}

import org.jdatepicker.JDatePanel;
import org.jdatepicker.JDatePicker;
import org.jdatepicker.SqlDateModel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Properties;

public class ActPage extends JFrame {

    // Данные для подключения к БД
    String rootName = "root";
    String rootPassword = "4554045540";
    String connectURL = "jdbc:mysql://localhost:3306/PhoneService";

    // Создание всех элементов формы
    JTextField fullNameField, phnNmbrTextField, phnMdlTextField, qpmntTextField;
    JTextArea aprncTextArea, fltDscrptonTextArea;
    JDatePicker datePicker;

    // Создание модели данных для даты
    SqlDateModel dateModel;

    // ID открытой записи
    int idAct;

    // Признак создания нового акта
    boolean newAct;

    public ActPage(String id, Boolean newActBool,int xPosition, int yPostion){

        // Начальная инициализация формы
        super("Акт №" + id);
        super.setBounds(xPosition, yPostion, 265, 500);
        super.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Создание слушателя состояния фармы
        super.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                new ListActsForm();
            }
        });

        super.setResizable(false);

        // Сохранения данных в глобальный "пул данных"
        idAct = Integer.parseInt(id);
        newAct = newActBool;

        // Получение контейнера формы
        Container containerActData = super.getContentPane();

        // Создание общей панели, на которой будет размещены все элементы формы
        JPanel dataPanel = new JPanel();
        dataPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        dataPanel.setPreferredSize(new Dimension(265, 400));

        // Создание надписей для поле ввода
        JLabel fllNmLabel = new JLabel("ФИО:");
        JLabel phnNmbrLabel = new JLabel("Телефон:");
        JLabel dtActLabel = new JLabel("Дата:");
        JLabel apprnceLabel = new JLabel("Внешний вид устройства:");
        JLabel qpmntLabel = new JLabel("Комплектация устройства:");
        JLabel fltDscrptnLabel = new JLabel("Описание неисправности:");
        JLabel phnMdlLabel = new JLabel("Модель телефона:");

        // Создание элемента по выбору даты и всех необходимых к нему элементов
        dateModel = new SqlDateModel();
        Properties dateProperties = new Properties();
        dateProperties.put("text.day", "Day");
        dateProperties.put("text.month", "Month");
        dateProperties.put("text.year", "Year");
        JDatePanel datePanel = new JDatePanel(dateModel);
        datePicker = new JDatePicker(dateModel);
        datePicker.setPreferredSize(new Dimension(250, 30));

        // Создание текстовых полей ввода и их настройка
        fullNameField = new JTextField("", 20);
        phnNmbrTextField = new JTextField("", 20);
        qpmntTextField = new JTextField("", 20);
        phnMdlTextField = new JTextField("", 20);
        fltDscrptonTextArea = new JTextArea("",3, 20);
        fltDscrptonTextArea.setLineWrap(true);
        JScrollPane fltDescrptnScrPn = new JScrollPane(fltDscrptonTextArea);
        aprncTextArea = new JTextArea("", 3, 20);
        aprncTextArea.setLineWrap(true);
        JScrollPane aprncScrPn = new JScrollPane(aprncTextArea);

        // Создание кнопок
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new cancelActionButton());
        JButton saveDataButton = new JButton("Save");
        saveDataButton.addActionListener(new saveActionButton());
        JButton showRepairAct = new JButton("Акт на ремонт");
        showRepairAct.addActionListener(new ShowRepairActActionButton());

        // Создание пустой надписи для создания пространства между элементами
        JLabel blankLabel = new JLabel("      ");

        // Добавление всех элементов в общую панель формы
        dataPanel.add(dtActLabel);
        dataPanel.add(datePicker);
        dataPanel.add(fllNmLabel);
        dataPanel.add(fullNameField);
        dataPanel.add(phnNmbrLabel);
        dataPanel.add(phnNmbrTextField);
        dataPanel.add(phnMdlLabel);
        dataPanel.add(phnMdlTextField);
        dataPanel.add(fltDscrptnLabel);
        dataPanel.add(fltDescrptnScrPn);
        dataPanel.add(qpmntLabel);
        dataPanel.add(qpmntTextField);
        dataPanel.add(apprnceLabel);
        dataPanel.add(aprncScrPn);
        //dataPanel.add(cancelButton);
        dataPanel.add(showRepairAct);
        dataPanel.add(blankLabel);
        dataPanel.add(saveDataButton);

        // Создание "скролла" для общей панели формы
        JScrollPane dtPnlScrPn = new JScrollPane(dataPanel);
        dtPnlScrPn.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        // Размещение всех элементов на форме
        containerActData.add(dtPnlScrPn);

        // Метод получения данных из БД
        GetActFromDB(id);

        // Отображение формы на экране
        super.setVisible(true);
    }

    // Действие по нажатию на кнопку "Сancel"
    class cancelActionButton implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            dispose();
            ListActsForm listActsForm = new ListActsForm();
        }
    }

    // Действие по нажатию на кнопку "Save"
    class saveActionButton implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {

            String sqlRequest = "";
            int day = dateModel.getDay();
            int month = dateModel.getMonth();
            int year = dateModel.getYear();
            String dateAct = year + "-" + (month + 1) + "-" + day;

            if(newAct){
                sqlRequest = "INSERT INTO acts " +
                                "(idacts, date, fullname, phonenumber, phonemodel, equipment, appearance, faultdescription)" +
                                " VALUES" +
                                " (" + idAct + ", '" + dateAct + "', '" + fullNameField.getText() + "', '" +
                                phnNmbrTextField.getText() + "', '" + phnMdlTextField.getText() + "', '" + qpmntTextField.getText() + "', '" +
                                aprncTextArea.getText() + "', '" + fltDscrptonTextArea.getText() + "')";
            }else{
                sqlRequest = "UPDATE acts SET" +
                                " date = " + "'" + dateAct + "'" + "," +
                                " fullname = " + "'" + fullNameField.getText() + "'" + "," +
                                " phonenumber = " + "'" + phnNmbrTextField.getText() + "'" + "," +
                                " phonemodel = " + "'" + phnMdlTextField.getText() + "'" + "," +
                                " equipment = " + "'" + qpmntTextField.getText() + "'" + "," +
                                " appearance = " + "'" + aprncTextArea.getText() + "'" + "," +
                                " faultdescription = " + "'" + fltDscrptonTextArea.getText() + "'" +
                                " WHERE idacts = " + "'" + idAct + "'";
            }


            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
            } catch (ClassNotFoundException ex) {
                throw new RuntimeException(ex);
            }

            try(Connection connection = DriverManager.getConnection(connectURL, rootName, rootPassword);
                Statement statement = connection.createStatement()) {
                System.out.println("Connection is well");

                int i = statement.executeUpdate(sqlRequest);
                JOptionPane.showMessageDialog(ActPage.this,"Данные записаны");
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }

        }
    }

    // Действие по отображению "Акта на ремонт"
    class ShowRepairActActionButton implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            new RepairAct(idAct, 765, 0);
        }
    }

    // Получение данных из БД
    public void GetActFromDB(String id){

        int actID = Integer.parseInt(id);

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException ex) {
            throw new RuntimeException(ex);
        }

        try(Connection connection = DriverManager.getConnection(connectURL, rootName, rootPassword);
            Statement statement = connection.createStatement()) {
            System.out.println("Connection is well");

            ResultSet resultSet = statement.executeQuery("SELECT * FROM acts WHERE idacts = " + actID);
            while(resultSet.next()){
                String phoneNumber = resultSet.getString("phonenumber");
                String fullName = resultSet.getString("fullname");
                String phoneModel = resultSet.getString("phonemodel");
                String faultDescription = resultSet.getString("faultdescription");
                String appearance = resultSet.getString("appearance");
                String dateAct = resultSet.getString("date");
                Date date = resultSet.getDate("date");
                String equipment = resultSet.getString("equipment");

                dateModel.setValue(date);
                fullNameField.setText(fullName);
                phnNmbrTextField.setText(phoneNumber);
                phnMdlTextField.setText(phoneModel);
                fltDscrptonTextArea.setText(faultDescription);
                qpmntTextField.setText(equipment);
                aprncTextArea.setText(appearance);

            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }

    }

}

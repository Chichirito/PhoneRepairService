import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import java.text.SimpleDateFormat;

public class ListActsForm extends JFrame {

    // Данные для подключения к БД
    String rootName = "root";
    String rootPassword = "4554045540";
    String connectURL = "jdbc:mysql://localhost:3306/PhoneService";

    String sqlDeleteAct = "DELETE FROM acts WHERE idacts = ";
    String sqlDeleteRepairAct = "DELETE FROM repair_act WHERE idrepair_act = ";
    String sqlDeleteStlForm = "DELETE FROM settlement_form WHERE id_repair_act = ";

    // Модель основной таблицы данных
    DefaultTableModel tableModel, tableRprActModel, tableStlFormModel;

    // Основная таблица данных
    JTable listActs, repairActs, listStlForm;

    // Выбранный на форме ID записи
    String enteredID;

    // Признак создания нового акта
    Boolean newActBool;

    public ListActsForm(){

        // Основная инициализация формы
        super("Список актов");
        super.setBounds(500, 500, 1000, 260);
        super.setMinimumSize(new Dimension(1000,260));
        super.setPreferredSize(new Dimension(1005, 260));
        super.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //super.setPreferredSize(new Dimension(1010, 280));
        super.setResizable(false);

        // Получение контейнера формы
        Container containerUserData = super.getContentPane();

        // Создание панели вкладок для разных таблиц данных
        JTabbedPane tabbedPane = new JTabbedPane();

        // Создание таблиц данных
        tableModel = new DefaultTableModel();
        listActs = new JTable(tableModel);
        listActs.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);

        tableRprActModel = new DefaultTableModel();
        repairActs = new JTable(tableRprActModel);
        repairActs.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);

        tableStlFormModel = new DefaultTableModel();
        listStlForm = new JTable(tableStlFormModel);
        listStlForm.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);

        // Заполнение таблицы из БД
        GetTableFromDB();

        // Установка размеров колонок для таблиц
        listActs.getColumnModel().getColumn(0).setMinWidth(40);
        listActs.getColumnModel().getColumn(0).setMaxWidth(40);
        listActs.getColumnModel().getColumn(1).setMinWidth(85);
        listActs.getColumnModel().getColumn(1).setMaxWidth(85);
        listActs.getColumnModel().getColumn(2).setMinWidth(215);
        listActs.getColumnModel().getColumn(2).setMaxWidth(305);
        listActs.getColumnModel().getColumn(3).setMinWidth(120);
        listActs.getColumnModel().getColumn(3).setMaxWidth(120);
        listActs.getColumnModel().getColumn(4).setMinWidth(125);
        listActs.getColumnModel().getColumn(5).setMinWidth(200);

        repairActs.getColumnModel().getColumn(0).setMinWidth(40);
        repairActs.getColumnModel().getColumn(0).setMaxWidth(40);

        listStlForm.getColumnModel().getColumn(0).setMinWidth(40);
        listStlForm.getColumnModel().getColumn(0).setMaxWidth(40);

        // Добавление "скролла" для таблиц
        JScrollPane scrollPane1 = new JScrollPane(listActs);

        JScrollPane scrollPane2 = new JScrollPane(repairActs);

        JScrollPane scrollPane3 = new JScrollPane(listStlForm);

        //Создание панелей кнопок
        JPanel buttonPanel1 = new JPanel();
        // Создание кнопок взаимодействия с таблицей и данными
        JButton openActButton = new JButton("Open");
        openActButton.addActionListener(new OpenActActionButton("Act"));
        JButton newActButton = new JButton("New");
        newActButton.addActionListener(new NewActActionButton());
        JButton delActButton = new JButton("Delete");
        delActButton.addActionListener(new DelActActionButton(sqlDeleteAct));
        JButton updateActButton = new JButton("Update");
        updateActButton.addActionListener(new UpdateActActionButton());

        JPanel buttonPanel2 = new JPanel();
        JButton openRprActButton = new JButton("Open");
        openRprActButton.addActionListener(new OpenActActionButton("RepairAct"));
        JButton delRprActButton = new JButton("Delete");
        delRprActButton.addActionListener(new DelActActionButton(sqlDeleteRepairAct));
        JButton updateRprActButton = new JButton("Update");
        updateRprActButton.addActionListener(new UpdateActActionButton());

        JPanel buttonPanel3 = new JPanel();
        JButton openStlFormButton = new JButton("Open");
        openStlFormButton.addActionListener(new OpenActActionButton("StlForm"));
        JButton delStlFormButton = new JButton("Delete");
        delStlFormButton.addActionListener(new DelActActionButton(sqlDeleteStlForm));
        JButton updateStlFormButton = new JButton("Update");
        updateStlFormButton.addActionListener(new UpdateActActionButton());

        // Создание пустых надписей для распределения кнопок по форме
        JLabel blankLabel1 = new JLabel("                          ");
        JLabel blankLabel2 = new JLabel("                          ");
        JLabel blankLabel3 = new JLabel("                          ");

        // Размещение всех элементов на панели кнопок
        buttonPanel1.add(delActButton);
        buttonPanel1.add(updateActButton);
        buttonPanel1.add(blankLabel1);
        buttonPanel1.add(blankLabel2);
        buttonPanel1.add(blankLabel3);
        buttonPanel1.add(openActButton);
        buttonPanel1.add(newActButton);

        buttonPanel2.add(delRprActButton);
        buttonPanel2.add(updateRprActButton);
        buttonPanel2.add(openRprActButton);

        buttonPanel3.add(delStlFormButton);
        buttonPanel3.add(updateStlFormButton);
        buttonPanel3.add(openStlFormButton);

        // Создание панели для таблиц
        JPanel tablePanel1 = new JPanel();
        tablePanel1.add(scrollPane1);
        tablePanel1.setPreferredSize(new Dimension(1000, 150));
        tablePanel1.setLayout(new BoxLayout(tablePanel1, BoxLayout.Y_AXIS));

        JPanel tablePanel2 = new JPanel();
        tablePanel2.add(scrollPane2);
        tablePanel2.setPreferredSize(new Dimension(1000, 150));
        tablePanel2.setLayout(new BoxLayout(tablePanel2, BoxLayout.Y_AXIS));

        JPanel tablePanel3 = new JPanel();
        tablePanel3.add(scrollPane3);
        tablePanel3.setPreferredSize(new Dimension(1000, 150));
        tablePanel3.setLayout(new BoxLayout(tablePanel3, BoxLayout.Y_AXIS));

        // Создание общей панели, в которую мы добавим все остальные панели
        JPanel tableButtonPanel1 = new JPanel();
        tableButtonPanel1.setLayout(new BoxLayout(tableButtonPanel1, BoxLayout.Y_AXIS));
        tableButtonPanel1.add(tablePanel1);
        tableButtonPanel1.add(buttonPanel1);

        JPanel tableButtonPanel2 = new JPanel();
        tableButtonPanel2.setLayout(new BoxLayout(tableButtonPanel2, BoxLayout.Y_AXIS));
        tableButtonPanel2.add(tablePanel2);
        tableButtonPanel2.add(buttonPanel2);

        JPanel tableButtonPanel3 = new JPanel();
        tableButtonPanel3.setLayout(new BoxLayout(tableButtonPanel3, BoxLayout.Y_AXIS));
        tableButtonPanel3.add(tablePanel3);
        tableButtonPanel3.add(buttonPanel3);

        // Вставка данных в панель вкладок
        tabbedPane.addTab("Таблица актов", tableButtonPanel1);
        tabbedPane.addTab("Таблица актов на ремонт", tableButtonPanel2);
        tabbedPane.addTab("Таблица расчетных бланков", tableButtonPanel3);

        // Вставка всех данных в основную панель
        JPanel mainPanel = new JPanel();
        mainPanel.add(tabbedPane);

        // Сборка формы и вывод ее на экран
        super.getContentPane().add(mainPanel);
        super.pack();
        super.setLocationRelativeTo(null);
        super.setVisible(true);

    }

    // Действие по нажатию на кнопку "Open"
    class OpenActActionButton implements ActionListener{

        String file;
        public OpenActActionButton(String file){
            this.file = file;
        }
        @Override
        public void actionPerformed(ActionEvent e) {

            newActBool = false;

            switch (file){
                case "Act" ->{
                    int activeRow = listActs.getSelectedRow();
                    int activeColumn = 0;
                    enteredID = listActs.getModel().getValueAt(activeRow, activeColumn).toString();
                    ActPage actPage = new ActPage(enteredID, newActBool, 500, 0);
                }
                case "RepairAct" ->{
                    int activeRow = repairActs.getSelectedRow();
                    int activeColumn = 0;
                    enteredID = repairActs.getModel().getValueAt(activeRow, activeColumn).toString();
                    new ActPage(enteredID, newActBool, 500, 0);
                    new RepairAct(Integer.valueOf(enteredID), 765, 0);
                    break;
                }
                case "StlForm" ->{
                    int activeRow = listStlForm.getSelectedRow();
                    int activeColumn = 0;
                    enteredID = listStlForm.getModel().getValueAt(activeRow, activeColumn).toString();
                    new ActPage(enteredID, newActBool, 500, 0);
                    new RepairAct(Integer.valueOf(enteredID), 765, 0);
                    new SettlementForm(Integer.valueOf(enteredID));
                    break;
                }
                default -> {
                    break;
                }
            }

            ListActsForm.super.dispose();

        }
    }

    // Действие по нажатию на кнопку "New"
    class NewActActionButton implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {

            int maxId = 0;

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

                ResultSet resultSet = statement.executeQuery("SELECT MAX(idacts) FROM acts ");
                while(resultSet.next()){

                    maxId = resultSet.getInt(1);
                    maxId++;
                    System.out.println("maxid = " + maxId);

                }
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }

            newActBool = true;
            ActPage actPage = new ActPage(String.valueOf(maxId), newActBool, 500, 0);
            ListActsForm.super.dispose();

        }
    }

    // Действие по нажатию на кнопку "Delete"
    class DelActActionButton implements ActionListener{

        String sqlRequest;

        public DelActActionButton(String sqlRequest){
            this.sqlRequest = sqlRequest;
        }

        @Override
        public void actionPerformed(ActionEvent e) {

            Object[] optionsFrame = {"Yes", "No"};
            int unswer = JOptionPane.showOptionDialog(null, "Вы действительно хотите удалить запись?",
                    "Подтверждение", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
                    null, optionsFrame, optionsFrame[0]);
            if (unswer == 0) {

                int activeRow = listStlForm.getSelectedRow();
                int activeColumn = 0;
                enteredID = listStlForm.getModel().getValueAt(activeRow, activeColumn).toString();

                try {
                    Class.forName("com.mysql.cj.jdbc.Driver");
                } catch (ClassNotFoundException ex) {
                    throw new RuntimeException(ex);
                }

                try (Connection connection = DriverManager.getConnection(connectURL, rootName, rootPassword);
                     Statement statement = connection.createStatement()) {
                    System.out.println("Connection is well");

                    sqlRequest += enteredID;

                    statement.executeUpdate(sqlRequest);

                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }

                ListActsForm.super.dispose();
                new ListActsForm();
                JOptionPane.showMessageDialog(ListActsForm.this, "Данные удалены");
            }

        }
    }

    // Действие по нажатию на кнопку "Update"
    class UpdateActActionButton implements  ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {

            ListActsForm.super.dispose();
            new ListActsForm();
            JOptionPane.showMessageDialog(ListActsForm.this,"Данные обновлены");

        }
    }

    // Метод по обновлению таблицы из БД
    public void GetTableFromDB(){

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException ex) {
            throw new RuntimeException(ex);
        }

        try(Connection connection = DriverManager.getConnection(connectURL, rootName, rootPassword);
            Statement statement = connection.createStatement()) {
            System.out.println("Connection is well");

            tableModel.addColumn("№");
            tableModel.addColumn("Дата");
            tableModel.addColumn("ФИО");
            tableModel.addColumn("Номер телефона");
            tableModel.addColumn("Модель телефона");
            tableModel.addColumn("Описание неисправности");


            ResultSet resultSet = statement.executeQuery("SELECT * FROM acts");
            while(resultSet.next()){

                String idAct = resultSet.getString("idacts");
                Date date = resultSet.getDate("date");
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
                String dateAct = dateFormat.format(date);
                String fullName = resultSet.getString("fullname");
                String phoneNumber = resultSet.getString("phonenumber");
                String phoneModel = resultSet.getString("phonemodel");
                String faultDescription = resultSet.getString("faultdescription");

                tableModel.insertRow(0, new Object[] {idAct, dateAct ,fullName, phoneNumber, phoneModel, faultDescription});
            }

            tableRprActModel.addColumn("№");
            tableRprActModel.addColumn("ФИО");
            tableRprActModel.addColumn("Номер телефона");
            tableRprActModel.addColumn("Модель телефона");
            tableRprActModel.addColumn("Причина неисправности");

            resultSet = statement.executeQuery("SELECT * FROM PhoneService.repair_act INNER JOIN PhoneService.acts " +
                                                    "ON PhoneService.repair_act.id_act = PhoneService.acts.idacts");
            while(resultSet.next()){

                String idRepairAct = resultSet.getString("idrepair_act");
                String fullName = resultSet.getString("fullname");
                String phoneModel = resultSet.getString("phonemodel");
                String phoneNumber = resultSet.getString("phonenumber");
                String causeFailure = resultSet.getString("cause_of_failure");

                tableRprActModel.insertRow(0, new Object[] {idRepairAct, fullName, phoneNumber, phoneModel, causeFailure});
            }

            tableStlFormModel.addColumn("№");
            tableStlFormModel.addColumn("ФИО");
            tableStlFormModel.addColumn("Номер телефона");
            tableStlFormModel.addColumn("Модель телефона");
            tableStlFormModel.addColumn("Причина неисправности");
            tableStlFormModel.addColumn("Проведенные работы");

            resultSet = statement.executeQuery("SELECT DISTINCT stl.id_repair_act, act.fullname, act.phonenumber, act.phonemodel, rep.cause_of_failure, rep.repair_description\n" +
                    "FROM PhoneService.settlement_form as stl\n" +
                    "INNER JOIN PhoneService.repair_act as rep\n" +
                    "ON stl.id_repair_act = rep.id_act \n" +
                    "INNER JOIN PhoneService.acts as act\n" +
                    "ON rep.id_act = act.idacts");
            while(resultSet.next()){

                String idRepairAct = resultSet.getString("stl.id_repair_act");
                String fullName = resultSet.getString("act.fullname");
                String phoneModel = resultSet.getString("act.phonemodel");
                String phoneNumber = resultSet.getString("act.phonenumber");
                String causeFailure = resultSet.getString("rep.cause_of_failure");
                String repairDescription = resultSet.getString("rep.repair_description");

                tableStlFormModel.insertRow(0, new Object[] {idRepairAct, fullName, phoneNumber, phoneModel, causeFailure, repairDescription});
            }

        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }

    }

}

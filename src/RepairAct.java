import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class RepairAct extends JFrame {

    // Данные для подключения к БД
    String rootName = "root";
    String rootPassword = "4554045540";
    String connectURL = "jdbc:mysql://localhost:3306/PhoneService";

    // ID открытой записи
    int idAct;

    // Признак нового "файла"
    Boolean newAct;

    // Создания текстовых полей
    JTextArea causeFailureTxtFld, replacedPartTxtFld, repairDscrptnTxtFld;

    public RepairAct(int id, int xPosition, int yPostion){

        // Начальная инициализация формы
        super("Акт на ремонт №" + id);
        super.setBounds(xPosition, yPostion, 265, 500);
        super.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        super.setResizable(false);

        Container containerActData = super.getContentPane();

        // Присвоение глобальным переменным значения
        idAct = id;
        newAct = true;

        // Создание общей панели, на которой будет размещены все элементы формы
        JPanel dataPanel = new JPanel();
        dataPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        dataPanel.setPreferredSize(new Dimension(265, 400));

        // Создание подписей для текстовых полей
        JLabel causeFailureLabel = new JLabel("ПричинаПоломки");
        JLabel purchasedPartLabel = new JLabel("Замененные детали");
        JLabel repairDscrptnLabel = new JLabel("Описание ремонта");

        // Настройка текстовых полей и создание для них функции "скролла"
        causeFailureTxtFld = new JTextArea("", 3, 20);
        causeFailureTxtFld.setLineWrap(true);
        JScrollPane aprncScrPn1 = new JScrollPane(causeFailureTxtFld);

        replacedPartTxtFld = new JTextArea("", 3, 20);
        replacedPartTxtFld.setLineWrap(true);
        JScrollPane aprncScrPn2 = new JScrollPane(replacedPartTxtFld);

        repairDscrptnTxtFld = new JTextArea("", 3, 20);
        repairDscrptnTxtFld.setLineWrap(true);
        JScrollPane aprncScrPn3 = new JScrollPane(repairDscrptnTxtFld);

        // Создание кнопок формы и привязка к них "активных слушателей"
        JButton saveDataButton = new JButton("Save");
        saveDataButton.addActionListener(new SaveActionButton());

        JButton stlFormButton = new JButton("Расчетный бланк");
        stlFormButton.addActionListener(new addStlFormAction());

        // Размещение элементов по панелям формы
        dataPanel.add(causeFailureLabel);
        dataPanel.add(aprncScrPn1);
        dataPanel.add(purchasedPartLabel);
        dataPanel.add(aprncScrPn2);
        dataPanel.add(repairDscrptnLabel);
        dataPanel.add(aprncScrPn3);
        dataPanel.add(stlFormButton);
        dataPanel.add(saveDataButton);

        containerActData.add(dataPanel);

        // Метод получения данных из БД
        GetActFromDB(idAct);

        // Отображение формы на экране
        super.setVisible(true);

    }

    // Создание нового "Расчетного акта" для данного "Акта на ремонт"
    class addStlFormAction implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            new SettlementForm(idAct);
        }
    }

    // Сохранения данных по "Акту на ремонт" в БД
    class SaveActionButton implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {

            String sqlRequest = "";

            if(newAct){
                sqlRequest = "INSERT INTO repair_act " +
                        "(idrepair_act, id_act, cause_of_failure, replaced_parts, repair_description)" +
                        " VALUES" +
                        " (" + idAct + ", " + idAct + ", '" + causeFailureTxtFld.getText() + "', '" +
                        replacedPartTxtFld.getText() + "', '" + repairDscrptnTxtFld.getText() + "')";

            }else{

                sqlRequest = "UPDATE repair_act SET" +
                        " idrepair_act = " + "'" + idAct + "'" + "," +
                        " id_act = " + "'" + idAct + "'" + "," +
                        " cause_of_failure = " + "'" + causeFailureTxtFld.getText() + "'" + "," +
                        " replaced_parts = " + "'" + replacedPartTxtFld.getText() + "'" + "," +
                        " repair_description = " + "'" + repairDscrptnTxtFld.getText() + "'";
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
                JOptionPane.showMessageDialog(RepairAct.this,"Данные записаны");

            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }

        }
    }

    // Получение данных из БД
    public void GetActFromDB(int actID){

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException ex) {
            throw new RuntimeException(ex);
        }

        try(Connection connection = DriverManager.getConnection(connectURL, rootName, rootPassword);
            Statement statement = connection.createStatement()) {
            System.out.println("Connection is well");

            ResultSet resultSet = statement.executeQuery("SELECT * FROM PhoneService.repair_act WHERE id_act = " + actID);

                while(resultSet.next()){

                    causeFailureTxtFld.setText(resultSet.getString("cause_of_failure"));
                    replacedPartTxtFld.setText(resultSet.getString("replaced_parts"));
                    repairDscrptnTxtFld.setText(resultSet.getString("repair_description"));

                    newAct = false;

                }

        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }

    }
}

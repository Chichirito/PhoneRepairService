import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.Vector;

public class SettlementForm extends JFrame {

    // Данные для подключения к БД
    String rootName = "root";
    String rootPassword = "4554045540";
    String connectURL = "jdbc:mysql://localhost:3306/PhoneService";

    // Создание модели таблици и самой таблицы
    DefaultTableModel stlFormTabModel;
    JTable stlFormTable;

    // Создание поля с итоговой суммой
    JTextField ttlPrcTextField;

    // Признак нового файла
    Boolean newAct;

    // ID открытого акта
    int actID;

    // ID открытого "Расчетного счета"
    int idStlForm;

    public SettlementForm(int idAct) {

        // Начальная инициализация формы
        super("Расчетный бланк заказа №" + idAct);
        super.setBounds(350, 520, 850, 225);
        super.setMinimumSize(new Dimension(850,225));
        super.setPreferredSize(new Dimension(850, 225));
        super.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        super.setResizable(false);

        Container containerUserData = super.getContentPane();

        // Присвоение глобальным переменным значения
        newAct = true;
        actID = idAct;

        // Связывание модели таблици и самой таблицы
        stlFormTabModel = new DefaultTableModel();
        stlFormTable = new JTable(stlFormTabModel);
        stlFormTable.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);

        // Создание надписи и текстового поля итоговой суммы
        JLabel totalPriceLabel = new JLabel("Итог:");
        ttlPrcTextField = new JTextField(10);

        // Вызов функции по заполнению таблицы данными из БД
        GetTableFromDB(idAct);

        // Настройка колонок таблицы с данным
        stlFormTable.getColumnModel().getColumn(0).setMinWidth(40);
        stlFormTable.getColumnModel().getColumn(0).setMaxWidth(40);

        // Создание скролла для таблицы
        JScrollPane scrollPane = new JScrollPane(stlFormTable);

        // Выравнивание содержимого таблицы по правому краю
        DefaultTableCellRenderer renderer = (DefaultTableCellRenderer)stlFormTable.getDefaultRenderer(String.class);
        renderer.setHorizontalAlignment(SwingConstants.RIGHT);

        // Создание панели для таблицы и добавление в нее самой таблицы
        JPanel tablePanel = new JPanel();
        tablePanel.add(scrollPane);
        tablePanel.setPreferredSize(new Dimension(850, 150));
        tablePanel.setLayout(new BoxLayout(tablePanel, BoxLayout.Y_AXIS));

        // Создание панели для кнопок
        JPanel undrTblButtonPanel = new JPanel();

        // Создание кнопок формы
        JButton doneFormButton = new JButton("Done");
        doneFormButton.addActionListener(new DoneButtonAction());

        JButton calButton = new JButton("Calc");
        calButton.addActionListener(new calcButtonAction());

        JButton addTableRow = new JButton("add");
        addTableRow.addActionListener(new AddNewTableRow());

        JButton saveTableRow = new JButton("Save");
        saveTableRow.addActionListener(new SaveTableRow());

        JButton delTableRow = new JButton("Delete");
        delTableRow.addActionListener(new DelTableRowAction());

        // Размещение кнопок в соответствующую панель
        undrTblButtonPanel.add(calButton, BorderLayout.EAST);
        undrTblButtonPanel.add(doneFormButton, BorderLayout.EAST);
        undrTblButtonPanel.add(totalPriceLabel, BorderLayout.EAST);
        undrTblButtonPanel.add(ttlPrcTextField, BorderLayout.EAST);

        // Сздание панели для функциональных кнопок и ее заполнению
        JPanel tableButtonPanel = new JPanel();
        tableButtonPanel.add(addTableRow, BorderLayout.WEST);
        tableButtonPanel.add(saveTableRow, BorderLayout.WEST);
        tableButtonPanel.add(delTableRow, BorderLayout.WEST);

        // Создание главной панели и ее заполнение
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        mainPanel.add(tablePanel, BorderLayout.NORTH);
        mainPanel.add(undrTblButtonPanel, BorderLayout.EAST);
        mainPanel.add(tableButtonPanel, BorderLayout.WEST);

        containerUserData.add(mainPanel);

        // Сборка формы и отображение ее на экране
        super.pack();
        super.setVisible(true);
    }

    // Кнопка вызова калькулятора
    class calcButtonAction implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {

            new Calculator();

        }
    }

    // Кнопка записи данных в БД
    class SaveTableRow implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {

            String purchasedPart = "";
            String pricePart = "";
            int amountPart = 0;
            String costWork = "";
            String totalPrice = "";

            String sqlDeleteRequest = "DELETE FROM PhoneService.settlement_form\n" +
                    "WHERE PhoneService.settlement_form.id_repair_act = " + actID;

                try {
                    Class.forName("com.mysql.cj.jdbc.Driver");
                } catch (ClassNotFoundException ex) {
                    throw new RuntimeException(ex);
                }

                try (Connection connection = DriverManager.getConnection(connectURL, rootName, rootPassword);
                     Statement statement = connection.createStatement()) {
                    System.out.println("Connection is well");

                    int d = statement.executeUpdate(sqlDeleteRequest);

                    for(int r = 0; r < stlFormTable.getRowCount(); r++) {

                        purchasedPart = stlFormTable.getValueAt(r, 1).toString();
                        pricePart = stlFormTable.getValueAt(r, 2).toString();
                        amountPart = Integer.valueOf(stlFormTable.getValueAt(r, 3).toString());
                        costWork = stlFormTable.getValueAt(r, 4).toString();
                        totalPrice = stlFormTable.getValueAt(r, 5).toString();

                        String sqlRequest = "INSERT INTO settlement_form " +
                                "(id_repair_act, purchased_part, price_of_part, amount_of_parts, cost_of_work, total_price)" +
                                " VALUES" +
                                " ('" + actID + "', '" + purchasedPart + "', '" + pricePart + "', '" +
                                amountPart + "', '" + costWork + "', '" + totalPrice + "')";

                        int u = statement.executeUpdate(sqlRequest);
                        JOptionPane.showMessageDialog(SettlementForm.this,"Данные записаны");
                    }


                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }


        }
    }

    // Кнопка добавления новой строки таблицы
    class AddNewTableRow implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {

            String numRow = String.valueOf(stlFormTabModel.getRowCount() + 1);
            String costWork = "";
            String pricePart = "";
            String purchasedPart = "";
            String amountPart = "";
            String totalPrice = "";

            stlFormTabModel.insertRow(stlFormTabModel.getRowCount(), new Object[] {numRow, purchasedPart, pricePart, amountPart, costWork, totalPrice});

            newAct = true;

        }
    }

    // Кнопка по удалению выделенной строки таблицы
    class DelTableRowAction implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {

            int selectedRow = stlFormTable.getSelectedRow();

            stlFormTabModel.removeRow(selectedRow);

        }
    }

    // Кнопка подсчета итоговых сумм таблицы
    class DoneButtonAction implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {

            double totalPrice = 0;

            for(int i = 0; i < stlFormTabModel.getRowCount(); i++ ){

                String objectPrice = stlFormTabModel.getValueAt(i,2).toString();
                String objectAmount = stlFormTabModel.getValueAt(i,3).toString();
                String objectCost = stlFormTabModel.getValueAt(i,4).toString();
                Object objectTotal = ((Double.valueOf(objectPrice)) * (Double.valueOf(objectAmount)) + (Double.valueOf(objectCost)));
                stlFormTabModel.setValueAt(objectTotal, i, 5);
                totalPrice += Double.valueOf(objectTotal.toString());

            }

            ttlPrcTextField.setText(String.valueOf(totalPrice));

        }
    }

    // Получение данных из БД
    public void GetTableFromDB(int actID){

        double ttl = 0;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException ex) {
            throw new RuntimeException(ex);
        }

        try(Connection connection = DriverManager.getConnection(connectURL, rootName, rootPassword);
            Statement statement = connection.createStatement()) {
            System.out.println("Connection is well");

            stlFormTabModel.addColumn("№");
            stlFormTabModel.addColumn("Замененная запчасть");
            stlFormTabModel.addColumn("Стоимость запчасти");
            stlFormTabModel.addColumn("Количество");
            stlFormTabModel.addColumn("Стоимость работы");
            stlFormTabModel.addColumn("Общая стоимость");

            ResultSet resultSet = statement.executeQuery("SELECT * FROM PhoneService.settlement_form WHERE id_repair_act = " + actID);

            int numRow = 0;

            while(resultSet.next()){

                numRow++;

                idStlForm = Integer.valueOf(resultSet.getString("idsettlement_form"));
                String costWork = (resultSet.getString("cost_of_work"));
                String pricePart = (resultSet.getString("price_of_part"));
                String purchasedPart =  (resultSet.getString("purchased_part"));
                String amountPart = (resultSet.getString("amount_of_parts"));

                newAct = false;

                ttl = ttl + ((Double.valueOf(pricePart)) + (Double.valueOf(costWork)));

                ttlPrcTextField.setText(String.valueOf(ttl));

                stlFormTabModel.insertRow(0, new Object[] {numRow, purchasedPart, pricePart, amountPart, costWork, ((Double.valueOf(pricePart)) + (Double.valueOf(costWork)) )});

            }

        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }

    }

}

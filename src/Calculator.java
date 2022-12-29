import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static java.lang.Math.sqrt;

public class Calculator extends JFrame {

    // Создание глабольных переменных, полей класса
    JTextField mainWin;
    JButton jButton[] = new JButton[20];
    String firstNumber = "";
    String secondNumber = "";
    String lastSign = "";
    Double res = null;

    public Calculator(){

        // Начальная настройка формы
        super("Калькулятор");
        super.setBounds(500, 500, 400, 190);
        super.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        Container containerUserData = super.getContentPane();

        // Создание главной панели
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        mainPanel.setPreferredSize(new Dimension(400, 190));

        // Создание панелей для элементов формы
        JPanel textFieldPanel = new JPanel();
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setPreferredSize(new Dimension(400, 190));

        // Создание главного текстового поля
        mainWin = new JTextField(20);

        // Размещение главного текстового поля на панель
        textFieldPanel.add(mainWin);

        // Создание цифровых и знаковых кнопок
        addCalcButton(buttonPanel);

        // Размещение всех элементов на главной панели
        mainPanel.add(textFieldPanel);
        mainPanel.add(buttonPanel);

        // Размещение главной панели на форме
        containerUserData.add(mainPanel);

        // Сборка формы и ее отображение
        super.pack();
        super.setVisible(true);

    }

    // Функция создания кнопок
    public void addCalcButton(JPanel panel){

        String[] strings = { "7", "8", "9", "/", "C", "4", "5", "6", "*", "Sqrt", "1", "2", "3", "-", "1/X", "+/-", "0",
                    ".", "+", "=" };
        for (int i = 0; i < jButton.length; i++){
            jButton[i] = new JButton(strings[i]);
            panel.add(jButton[i]);
            jButton[i].addActionListener(new CalcButtonAction(jButton[i].getText()));
        }

    }

    // Класс, отрабатывающий нажатия на кнопки
    class CalcButtonAction implements ActionListener{

        // Поля для хранения наименования нажатой кнопки
        String textButton = "";

        //Конструктор класса, отрабатывающего нажатие кнопки
        public CalcButtonAction(String text) {

            textButton = text;

        }

        // Главный метод улавливающий нажатие кнопки. В зависимости от кнопки будет вызываться
        // соответствующая функция
        @Override
        public void actionPerformed(ActionEvent e) {

            switch (textButton){
                case "1" -> {
                    numberCalcButton("1");
                    break;}
                case "2" -> {
                    numberCalcButton("2");
                    break;}
                case "3" -> {
                    numberCalcButton("3");
                    break;}
                case "4"-> {
                    numberCalcButton("4");
                    break;}
                case "5" -> {
                    numberCalcButton("5");
                    break;}
                case "6" -> {
                    numberCalcButton("6");
                    break;}
                case "7" -> {
                    numberCalcButton("7");
                    break;}
                case "8" -> {
                    numberCalcButton("8");
                    break;}
                case "9" -> {
                    numberCalcButton("9");
                    break;}
                case "0" -> {
                    numberCalcButton("0");
                    break;}
                case "+" -> {
                    signCalcButton("+");
                    break;}
                case "-" -> {
                    signCalcButton("-");
                    break;}
                case "*" -> {
                    signCalcButton("*");
                    break;}
                case "/" -> {
                    signCalcButton("/");
                    break;}
                case "=" -> {
                    equalsCalcButton();
                    break;}
                // Случай нажатия по сбросу состояния калькулятора
                case "C" -> {
                    res = null;
                    firstNumber = "";
                    secondNumber = "";
                    lastSign = "";
                    mainWin.setText("");
                    break;}
                // Случай нажатия вычисления квадратного корня
                case "Sqrt" -> {
                    String subString1 = "(";
                    String subString2 = ")";
                    firstNumber = firstNumber.replace(subString1, "");
                    firstNumber = firstNumber.replace(subString2, "");
                    secondNumber = secondNumber.replace(subString1, "");
                    secondNumber = secondNumber.replace(subString2, "");
                    String win = mainWin.getText();
                    win = win.replace(subString1, "");
                    win = win.replace(subString2, "");
                    if(!(firstNumber.equals("")) & (secondNumber.equals("")) & (Double.valueOf(firstNumber) > 0)){
                        res = sqrt(Double.valueOf(firstNumber));
                        mainWin.setText(res.toString());
                    }else if(!(firstNumber.equals("")) & !(secondNumber.equals(""))){
                        if (Double.valueOf(secondNumber) > 0){
                            res = sqrt(Double.valueOf(secondNumber));
                            mainWin.setText(firstNumber.toString() + lastSign + res.toString());
                            secondNumber = res.toString();
                        }else JOptionPane.showMessageDialog(null, "Невозможно извлечь корень из отрицательного числа", "Invalid input", JOptionPane.PLAIN_MESSAGE);
                    }else if(Double.valueOf(win) > 0){
                        res = sqrt(Double.valueOf(win));
                        mainWin.setText(res.toString());
                    }else{
                        JOptionPane.showMessageDialog(null, "Невозможно извлечь корень из отрицательного числа", "Invalid input", JOptionPane.PLAIN_MESSAGE);
                    }
                    break;}

                // Случай нажатия вычисления части целого числа
                case "1/X" -> {
                    if(!(firstNumber.equals("")) & (secondNumber.equals(""))){
                        res = 1 / (Double.valueOf(firstNumber));
                        mainWin.setText(res.toString());
                    }else if(!(firstNumber.equals("")) & !(secondNumber.equals(""))){
                        res = 1 / (Double.valueOf(secondNumber));
                        mainWin.setText(firstNumber.toString() + lastSign + res.toString());
                        secondNumber = res.toString();
                    }else{
                        res = 1 / (Double.valueOf(mainWin.getText()));
                        mainWin.setText(res.toString());
                    }
                    break;}

                // Случай нажатия смены знака числа
                case "+/-" -> {
                    String subString1 = "(-";
                    String subString2 = ")";
                    String subString3 = "-";
                    if(!(firstNumber.equals("")) & (secondNumber.equals(""))){
                        if(!(firstNumber.contains(subString1))) {
                            firstNumber = "(-" + firstNumber + ")";
                            mainWin.setText(firstNumber);
                        }else{
                            firstNumber = firstNumber.replace(subString1, "");
                            firstNumber = firstNumber.replace(subString2, "");
                            mainWin.setText(firstNumber);
                        }
                    }else if(!(firstNumber.equals("")) & !(secondNumber.equals(""))){
                        if(!(secondNumber.contains(subString1))){
                            secondNumber = "(-" + secondNumber + ")";
                            mainWin.setText(firstNumber + lastSign + secondNumber);
                        }else{
                            secondNumber = secondNumber.replace(subString1, "");
                            secondNumber = secondNumber.replace(subString2, "");
                            mainWin.setText(firstNumber + lastSign + secondNumber);
                        }
                    }else if(firstNumber.equals("") & res !=null){
                        firstNumber = res.toString();
                        if(!(firstNumber.contains(subString3))) {
                            firstNumber = "(-" + firstNumber + ")";
                            mainWin.setText(firstNumber);
                        }else{
                            firstNumber = firstNumber.replace(subString1, "");
                            firstNumber = firstNumber.replace(subString2, "");
                            firstNumber = firstNumber.replace(subString3, "");
                            mainWin.setText(firstNumber);
                        }
                    }
                    break;}
                default -> {
                    break;}
            }

        }

        // Функция, вызываемая при нажатии кнопки с цифровым обозначением
        public void numberCalcButton(String num) {
            String subString1 = "(";
            String subString2 = ")";
            firstNumber = firstNumber.replace(subString1, "");
            firstNumber = firstNumber.replace(subString2, "");
            secondNumber = secondNumber.replace(subString1, "");
            secondNumber = secondNumber.replace(subString2, "");
            String win = mainWin.getText();
            win = win.replace(subString1, "");
            win = win.replace(subString2, "");
            if ((res != null) & (lastSign == "")) {
                mainWin.setText(num);
                res = null;
                firstNumber = num;
            } else if(!(win.equals(""))){
                if (lastSign.equals("")) {
                    firstNumber = firstNumber + num;
                    win = win + num;
                    mainWin.setText(win);
                } else {
                    secondNumber = secondNumber + num;
                    win = win + num;
                    mainWin.setText(win);
                }
            }else {
                win = win + num;
                mainWin.setText(win);
                firstNumber = num;
            }
            if ((!(lastSign.equals(""))) & (res == null) & (!(firstNumber.equals(""))) & (!(secondNumber.equals("")))){
                if ((Double.valueOf(firstNumber)) < 0 ){
                    win = "(" + firstNumber + ")";
                }
                if ((Double.valueOf(secondNumber)) < 0 ){
                    win = win + lastSign + "(" + secondNumber + ")";
                }
            }
        }

        // Функция, вызываемая при нажатии кнопки со знаковым обозначением
        public void signCalcButton(String sign){
            String subString1 = "(";
            String subString2 = ")";
            String win = mainWin.getText();
            win = win.replace(subString1, "");
            win = win.replace(subString2, "");
            if(win.equals(String.valueOf(res))){
                if(res < 0){
                    firstNumber = "(" + res + ")";
                }else{
                    firstNumber = String.valueOf(res);
                }
                lastSign = sign;
                mainWin.setText(firstNumber + lastSign);
            }else if(!(firstNumber.equals("")) & !(secondNumber.equals("")) & !(lastSign.equals(""))){
                equalsCalcButton();
                firstNumber = res.toString();
                lastSign = sign;
                mainWin.setText(res.toString() + sign);
            }else if(!(firstNumber.equals("")) & (secondNumber.equals("")) & !(lastSign.equals(""))){
                lastSign = sign;
                mainWin.setText(firstNumber.toString() + lastSign);
            }else {
                mainWin.setText(mainWin.getText() + sign);
                lastSign = sign;
            }
        }

        // Функция, вызываемая при нажатии на кнопку "равно" (=)
        public void equalsCalcButton(){
            String subString1 = "(";
            String subString2 = ")";
            firstNumber = firstNumber.replace(subString1, "");
            firstNumber = firstNumber.replace(subString2, "");
            secondNumber = secondNumber.replace(subString1, "");
            secondNumber = secondNumber.replace(subString2, "");
            if(!(firstNumber.equals("")) & !(secondNumber.equals(""))) {
                switch (lastSign){
                    case "+" :
                        res = Double.valueOf(firstNumber) + Double.valueOf(secondNumber);
                        break;
                    case "-" :
                        res = Double.valueOf(firstNumber) - Double.valueOf(secondNumber);
                        break;
                    case "*" :
                        res = Double.valueOf(firstNumber) * Double.valueOf(secondNumber);
                        break;
                    case "/" :
                        res = Double.valueOf(firstNumber) / Double.valueOf(secondNumber);
                        break;
                    default:
                        break;
                }
                if (res < 0){
                    mainWin.setText("(" + String.valueOf(res) + ")");
                }else {
                    mainWin.setText(String.valueOf(res));
                }

                firstNumber = "";
                secondNumber = "";
                lastSign = "";
            }
        }
    }

}

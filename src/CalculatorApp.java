import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

public class CalculatorApp extends JFrame {
    private JTextArea textArea;
    private JButton[] numberButtons;
    private JButton[] operatorButtons;
    private JButton equalsButton;
    private JButton clearButton;
    private StringBuilder inputBuffer;
    private double num1, num2;
    private String operator;
    private boolean isNewCalculation;

    public CalculatorApp() {
        setTitle("Calculator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        initializeComponents();
        addComponentsToFrame();
        addKeyboardBindings();

        pack();
        setSize(250, 300);
        setLocationRelativeTo(null);
        setResizable(false);
        setVisible(true);
    }

    private void initializeComponents() {
        textArea = new JTextArea();
        textArea.setPreferredSize(new Dimension(200,32));
        textArea.setFont(new Font("SansSerif", Font.BOLD, 22));
        textArea.setRows(2);
        textArea.setLineWrap(true);
        textArea.setTabSize(10);
        textArea.setBackground(Color.WHITE);
        textArea.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        textArea.setBorder(null);
        textArea.setEditable(false);

        numberButtons = new JButton[10];
        for (int i = 0; i < 10; i++) {
            numberButtons[i] = new JButton(String.valueOf(i));
            numberButtons[i].setFont(new Font("SansSerif", Font.PLAIN, 16));
            numberButtons[i].addActionListener(new NumberButtonListener());
        }

        operatorButtons = new JButton[4];
        operatorButtons[0] = new JButton("+");
        operatorButtons[1] = new JButton("-");
        operatorButtons[2] = new JButton("*");
        operatorButtons[3] = new JButton("/");
        for (JButton button : operatorButtons) {
            button.addActionListener(new OperatorButtonListener());
        }

        equalsButton = new JButton("=");
        equalsButton.addActionListener(new EqualsButtonListener());

        clearButton = new JButton("C");
        clearButton.addActionListener(new ClearButtonListener());

        inputBuffer = new StringBuilder();
        isNewCalculation = true;
    }

    private void addComponentsToFrame() {
        var panel = new JPanel(new GridBagLayout());
        var gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        gridBagConstraints.anchor = GridBagConstraints.LINE_END;

        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 4;
        panel.add(textArea, gridBagConstraints);

        // Add number buttons
        var numberButtonsPanel = new JPanel(new GridLayout(4, 3, 7, 7));

        for (int i = 7; i <= 9; i++) {
            numberButtonsPanel.add(numberButtons[i]);
            for (int j = 4; j <= 6; j++) {
                numberButtonsPanel.add(numberButtons[j]);
            }
            for (int k = 1; k <= 3; k++) {
                numberButtonsPanel.add(numberButtons[k]);
                numberButtons[k].setSize(new Dimension(10,20));
            }
        }

        numberButtonsPanel.add(clearButton);
        numberButtonsPanel.add(numberButtons[0]);
        numberButtonsPanel.add(equalsButton);

        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        panel.add(numberButtonsPanel, gridBagConstraints);

        // Add operator buttons
        JPanel operatorsButtonsPanel = new JPanel(new GridLayout(4, 1, 7, 7));

        for (JButton button : operatorButtons) {
            operatorsButtonsPanel.add(button);
        }
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 4;
        panel.add(operatorsButtonsPanel, gridBagConstraints);

        add(panel);
    }

    private void addKeyboardBindings() {
        InputMap inputMap = textArea.getInputMap(JComponent.WHEN_FOCUSED);
        ActionMap actionMap = textArea.getActionMap();

        for (int i = 0; i < 10; i++) {
            String numberKey = "number" + i;
            inputMap.put(KeyStroke.getKeyStroke(Character.forDigit(i, 10)), numberKey);
            actionMap.put(numberKey, new NumberButtonAction(i));
        }

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_PLUS, 0), "plus");
        actionMap.put("plus", new OperatorButtonAction("+"));

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, 0), "minus");
        actionMap.put("minus", new OperatorButtonAction("-"));

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_MULTIPLY, 0), "multiply");
        actionMap.put("multiply", new OperatorButtonAction("*"));

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_SLASH, 0), "divide");
        actionMap.put("divide", new OperatorButtonAction("/"));

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "equals");
        actionMap.put("equals", new EqualsButtonAction());

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "clear");
        actionMap.put("clear", new ClearButtonAction());
    }

    private class NumberButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            handleNumberInput(((JButton) e.getSource()).getText());
        }
    }

    private class NumberButtonAction extends AbstractAction {
        private int number;

        public NumberButtonAction(int number) {
            this.number = number;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            handleNumberInput(String.valueOf(number));
        }
    }

    private class OperatorButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            handleOperatorSelection(((JButton) e.getSource()).getText());
        }
    }

    private class OperatorButtonAction extends AbstractAction {
        private String operator;

        public OperatorButtonAction(String operator) {
            this.operator = operator;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            handleOperatorSelection(operator);
        }
    }

    private class EqualsButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            performCalculation();
        }
    }

    private class EqualsButtonAction extends AbstractAction {
        @Override
        public void actionPerformed(ActionEvent e) {
            performCalculation();
        }
    }

    private class ClearButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            clearInput();
        }
    }

    private class ClearButtonAction extends AbstractAction {
        @Override
        public void actionPerformed(ActionEvent e) {
            clearInput();
        }
    }

    private void handleNumberInput(String number) {
        if (isNewCalculation) {
            textArea.setText("0");
            isNewCalculation = false;
        }
        if (number.equals(".") && inputBuffer.indexOf(".") != -1) {
            // If decimal point is already present, ignore the input
            return;
        }
        inputBuffer.append(number);
        textArea.setText(inputBuffer.toString());
    }


    private void handleOperatorSelection(String operator) {
        num1 = Double.parseDouble(inputBuffer.toString());
        this.operator = operator;
        isNewCalculation = true;
        inputBuffer.setLength(0);
    }

    private void performCalculation() {
        if (operator != null) {
            double num2 = Double.parseDouble(inputBuffer.toString());
            double result = performOperation(num1, num2, operator);
            textArea.setText(String.valueOf(result));
            isNewCalculation = true;
            inputBuffer.setLength(0);
        } else {
            JOptionPane.showMessageDialog(null, "Error: Operator not selected!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearInput() {
        textArea.setText("");
        isNewCalculation = false;
        inputBuffer.setLength(0);
    }

    private double performOperation(double num1, double num2, String operator) {
        switch (operator) {
            case "+" -> {
                return num1 + num2;
            }
            case "-" -> {
                return num1 - num2;
            }
            case "*" -> {
                return num1 * num2;
            }
            case "/" -> {
                if (num2 != 0) {
                    return num1 / num2;
                } else {
                    JOptionPane.showMessageDialog(null, "Error: Cannot divide by zero!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
            default ->
                    JOptionPane.showMessageDialog(null, "Error: Invalid operator!", "Error", JOptionPane.ERROR_MESSAGE);
        }
        return 0.0;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(CalculatorApp::new);
    }
}
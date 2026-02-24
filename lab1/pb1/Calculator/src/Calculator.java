import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Stack;
import java.util.StringTokenizer;

public class Calculator extends JFrame {
    JButton digits[] = {
            new JButton(" 0 "),
            new JButton(" 1 "),
            new JButton(" 2 "),
            new JButton(" 3 "),
            new JButton(" 4 "),
            new JButton(" 5 "),
            new JButton(" 6 "),
            new JButton(" 7 "),
            new JButton(" 8 "),
            new JButton(" 9 ")
    };
    JButton operators[] = {
            new JButton(" + "),
            new JButton(" - "),
            new JButton(" * "),
            new JButton(" / "),
            new JButton(" ( "),
            new JButton(" ) "),
            new JButton(" = "),
            new JButton(" C ")
    };

    String oper_values[] = {"+", "-", "*", "/", "(", ")", "=", ""};
    JTextArea area = new JTextArea(3, 5);

    public static void main(String[] args) {
        Calculator calculator = new Calculator();
        calculator.setSize(230, 230);
        calculator.setTitle(" Java-Calc Postfix Lab 1 ");
        calculator.setResizable(false);
        calculator.setVisible(true);
        calculator.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public Calculator() {
        setLayout(new BorderLayout());
        add(new JScrollPane(area), BorderLayout.NORTH);

        JPanel buttonpanel = new JPanel();//gridlayout pt butoane asezate ok
        buttonpanel.setLayout(new GridLayout(5, 4, 5, 5)); // Grid mai ordonat

        // Iniț cifre
        for (int i = 0; i < 10; i++) {
            digits[i] = new JButton(" " + i + " ");//initializeaza buton, se pune ca text cifra coresp lui i
            int finalI = i;//copie i
            digits[i].addActionListener(e -> area.append(Integer.toString(finalI)));
            //e(action event), area.append adauga text la finalul zonei de afisare si la final transf cifra in text pentru a fi afisata
            buttonpanel.add(digits[i]);//butoane vizibile
        }

        // Iniț operatori
        for (int i = 0; i < operators.length; i++) {//parcurge vect butoane
            int finalI = i;//copie i
            operators[i].addActionListener(new ActionListener() {//se executa ce este intre paranteze la click
                @Override
                public void actionPerformed(ActionEvent e) {//simte click
                    String cmd = oper_values[finalI];//extrage simbolul
                    if (cmd.equals("")) area.setText(""); // Butonul C
                    else if (cmd.equals("=")) {
                        try {//incearca sa calculeze
                            double rezultat = evalueazaExpresie(area.getText());//trimite textul de pe ecran catre alg de evaluare poloneza
                            area.append("\n= " + rezultat);//afiseaza rezultatul
                        } catch (Exception ex) {
                            area.setText(" Eroare Sintaxa ");
                        }
                    } else {
                        area.append(cmd);//adauga simbol
                    }
                }
            });
            buttonpanel.add(operators[i]);
        }

        add(buttonpanel, BorderLayout.CENTER);//plaseaza panoul de butoane in centru
        area.setEditable(false);//nu se poate scrie de la tastaura
        area.setFont(new Font("Monospaced", Font.BOLD, 14));
    }

    // logica de evaluare (Shunting-yard + RPN)

    private double evalueazaExpresie(String expresie) {
        return calculeazaRPN(transformaInPostfix(expresie));
    }
//primeste textul si l trimite la a fi transf in forma postfixata, calculat si returnat
    // iInfix -> postfix (Shunting-yard)
    private String transformaInPostfix(String infix) {
        StringBuilder output = new StringBuilder();//rez final
        Stack<String> stack = new Stack<>();//stiva pt operatori
        // folosim StringTokenizer pentru a separa numerele de operatori
        StringTokenizer st = new StringTokenizer(infix, "+-*/()", true);
        //sparge textul in bucati
        while (st.hasMoreTokens()) {
            String token = st.nextToken().trim();
            if (token.isEmpty()) continue;
            //parcurge fiecare bucata din expr
            if (isNumeric(token)) {//daca e nr se trece la rez final
                output.append(token).append(" ");
            } else if (token.equals("(")) {//daca e paranteza deschisa se pune pe stiva
                stack.push(token);
            } else if (token.equals(")")) {//daca e paranteza inchisa se scoate din stiva si se pune in output
                while (!stack.isEmpty() && !stack.peek().equals("("))
                    output.append(stack.pop()).append(" ");
                stack.pop();
            } else {
                while (!stack.isEmpty() && precedenta(token) <= precedenta(stack.peek()))
                    output.append(stack.pop()).append(" ");
                stack.push(token);
            }
        }
        while (!stack.isEmpty()) output.append(stack.pop()).append(" ");
        return output.toString();
    }

    // calculul propriu-zis al formei postfixate
    private double calculeazaRPN(String postfix) {
        Stack<Double> stack = new Stack<>();//stiva de nr
        String[] tokens = postfix.split("\\s+");//sparge sirul, spatii ca separator

        for (String token : tokens) { //analizam fiecare bucata
            if (isNumeric(token)) {//daca e nr se pune pe stiva
                stack.push(Double.parseDouble(token));
            } else {//daca e operator
                double b = stack.pop();//scoate ultimul nr
                double a = stack.pop();//scoate nr dinaintea lui
                switch (token) {//se foloseste operatia
                    case "+": stack.push(a + b); break;
                    case "-": stack.push(a - b); break;
                    case "*": stack.push(a * b); break;
                    case "/": stack.push(a / b); break;
                }
            }
        }
        return stack.pop();//ramane rez final
    }

    private int precedenta(String op) {//prioritate
        if (op.equals("+") || op.equals("-")) return 1;//prioritate mica
        if (op.equals("*") || op.equals("/")) return 2;//prioritate mare
        return -1;//pt paranteze, caract necunoscute
    }

    private boolean isNumeric(String str) {//verif daca e nr valid sau simbol
        return str.matches("-?\\d+(\\.\\d+)?");//poate incepe cu minus, trebuie sa contina cel putin o cifra si poate contine optional un punct urmat de alte nr
    }
}
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Equations {
    public static final double eps1 = Math.pow(10, -4); // Точность для задания 1
    public static final double eps2 = Math.pow(10, -8); // Точность для задания 2
    public static List<Operationable> equations = new LinkedList<>();
    public static String[] equationsStr = new String[]{"sin(x)-2x^2+0.5=0", "x^n=a", "sqrt(1-x^2)-exp^x+0.1=0", "x^6-5x^3-2=0",
            "ln(x)-1/(1+x^2)=0", "3^x-5x^2+1=0", "sin(x)=0", "ln(x)=1"};

    public static void main(String[] args) {
        System.out.println(equationsStr[1]);
        int n = checkNumberN();
        double a = checkNumberA();

        equations.add(x -> Math.sin(x) - 2 * Math.pow(x, 2) + 0.5);
        equations.add(x -> Math.pow(x, n - a));
        equations.add(x -> Math.sqrt(1 - Math.pow(x, 2)) - Math.exp(x) + 0.1);
        equations.add(x -> Math.pow(x, 6) - 5 * Math.pow(x, 3) - 2);
        equations.add(x -> Math.log(x) - 1 / (1 + Math.pow(x, 2)));
        equations.add(x -> Math.pow(3, x) - 5 * Math.pow(x, 2) + 1);
        equations.add(x -> Math.sin(x));
        equations.add(x -> Math.log(x) - 1);

        double segments[] = new double[]{0.5, 1, a, n, 0, 0.5, -1, -0.5, 1, 2, 0, 1};
        double points[] = new double[]{0.7, 2, 0.1, -0.7, 1.4, 0.8, 3.14, 2.71};

        findXByDichotomy(segments);
        findXByNewton(points);
    }

    // Проверка значения "n" на корректность
    public static int checkNumberN() {
        Scanner in = new Scanner(System.in);
        Pattern pat1 = Pattern.compile("^[1-9]+[0-9]*$");
        Matcher match1;
        String str;
        do {
            System.out.print("Введите число n: ");
            str = in.nextLine();
            match1 = pat1.matcher(str);
            if (!match1.matches()) {
                System.out.println("Число должно быть натуральным!");
            }
        } while (!match1.matches());
        return Integer.parseInt(str);
    }

    // Проверка значения "n" на корректность
    public static float checkNumberA() {
        Scanner in = new Scanner(System.in);
        Pattern pat2 = Pattern.compile("^([0]|[1-9]+[0-9]*)(\\.([0]|[1-9]+[0-9]*))?$");
        Matcher match2;
        String str;
        do {
            System.out.print("Введите число a: ");
            str = in.nextLine();
            match2 = pat2.matcher(str);
            if (!match2.matches()) {
                System.out.println("Число должно быть больше 0!");
            }
        } while (!match2.matches());
        return Float.parseFloat(str);
    }

    public static void findXByDichotomy(double[] segments) {
        for (int i = 0; i < segments.length / 2; ++i) {
            System.out.println();
            System.out.println(equationsStr[i]);
            System.out.println("\nРешение методом дихотомии: ");
            double c;
            int iterationNum = 1;
            do {
                c = (segments[i] + segments[i + 1]) / 2;
                if (equations.get(i).calculate(segments[i]) * equations.get(i).calculate(c) > 0) {
                    segments[i] = c;
                } else {
                    segments[i + 1] = c;
                }
                System.out.println("Итерация № " + iterationNum);
                System.out.println("d = " + String.format("%.8f", Math.abs(segments[i + 1] - segments[i])));
                System.out.println("x = " + String.format("%.8f", c));
                ++iterationNum;
            } while (Math.abs(segments[i + 1] - segments[i]) > eps1);
            System.out.println();
        }
    }

    public static void findXByNewton(double[] points) {
        for (int i = 0; i < points.length; ++i) {
            double eps;
            if (i <= 5) {
                eps = eps1;
            } else {
                eps = eps2;
            }
            System.out.println();
            System.out.println(equationsStr[i]);
            System.out.println("\nРешение методом Ньютона: ");
            double y, d;
            int iterationNum = 1;
            do {
                double h = points[i] * 1e-8;
                double derivative = (equations.get(i).calculate(points[i] + h) - equations.get(i).calculate(points[i])) / h;
                y = points[i] - equations.get(i).calculate(points[i]) / derivative;
                d = Math.abs(points[i] - y);
                if (d > eps) {
                    points[i] = y;
                }
                System.out.println("Итерация № " + iterationNum);
                System.out.println("d = " + String.format("%.8f", d));
                System.out.println("x = " + String.format("%.8f", y));
                ++iterationNum;
            } while (d > eps);
        }
    }
    interface Operationable {
        double calculate(double x);
    }
}
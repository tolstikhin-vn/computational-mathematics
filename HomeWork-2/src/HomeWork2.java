import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

public class HomeWork2 {
    public static final int A = 2;
    public static final int K = 4;
    public static final double EPS = Math.pow(10, -K);

    public static void main(String[] args) {

        LinkedList<Operationable1> equations1 = new LinkedList<>(); // Исходные уравнения задания 1
        LinkedList<Operationable21> equations21 = new LinkedList<>(); // Система уравнений задания 2.1
        LinkedList<Operationable22> equations22 = new LinkedList<>(); // Система уравнений задания 2.2
        LinkedList<Operationable1> firstDerivatives1 = new LinkedList<>(); // Первые производные уравненений задания 1
        LinkedList<Operationable21> chDerivatives21 = new LinkedList<>(); // Частные производные задания 2.1
        LinkedList<Operationable22> chDerivatives22 = new LinkedList<>(); // Частные производные задания 2.2
        String[] equationsStr1 = new String[]{"x^3+x^2-x+1/2=0", "(e^x)/A=x+1", "x^3-20x+1=0", "2^x+x^2-2=0",
                "xln(x+2)-1+x^2=0", "(x^3)/A=Acos(x)"}; // Набор уравнений задания 1.1 в формате String для вывода

        equations1.add(x -> Math.pow(x, 3) + Math.pow(x, 2) - x + 1. / 2);
        equations1.add(x -> Math.pow(Math.E, x) / A - x - 1);
        equations1.add(x -> Math.pow(x, 3) - 20 * x + 1);
        equations1.add(x -> Math.pow(2, x) + Math.pow(x, 2) - 2);
        equations1.add(x -> x * Math.log(x + 2) - 1 + Math.pow(x, 2));
        equations1.add(x -> Math.pow(x, 3) - A * Math.cos(x));

        firstDerivatives1.add(x -> 3 * Math.pow(x, 2) + 2 * x - 1);
        firstDerivatives1.add(x -> Math.pow(Math.E, x) / A - 1);
        firstDerivatives1.add(x -> 3 * Math.pow(x, 2) - 20);
        firstDerivatives1.add(x -> Math.pow(2, x) * Math.log(2) + 2 * x);
        firstDerivatives1.add(x -> Math.log(x + 2) + x / (x + 2) + 2 * x);
        firstDerivatives1.add(x -> 3 * Math.pow(x, 2) / A + A * Math.sin(x));

        LinkedList<Point> listOfPints1 = new LinkedList<>();
        LinkedList<Point> listOfPints2 = new LinkedList<>();
        LinkedList<Point> listOfPints3 = new LinkedList<>();
        LinkedList<Point> listOfPints4 = new LinkedList<>();
        LinkedList<Point> listOfPints5 = new LinkedList<>();
        LinkedList<Point> listOfPints6 = new LinkedList<>();

        listOfPints1.add(new Point(-1.7));
        listOfPints2.add(new Point(-0.8));
        listOfPints2.add(new Point(1.7));
        listOfPints3.add(new Point(-4.5));
        listOfPints3.add(new Point(0.1));
        listOfPints3.add(new Point(4.5));
        listOfPints4.add(new Point(-1.3));
        listOfPints4.add(new Point(0.7));
        listOfPints5.add(new Point(-1));
        listOfPints5.add(new Point(0.6));
        listOfPints6.add(new Point(1.2));
        
        double[] points21 = new double[]{1.1, 1, 1, 0.6, -1};
        double[] points22 = new double[]{0.4, 0.4, 0.5, 0.6, 1};

        LinkedHashMap<String, LinkedList<Point>> mapOfEquations = new LinkedHashMap<>();
        mapOfEquations.put(equationsStr1[0], listOfPints1);
        mapOfEquations.put(equationsStr1[1], listOfPints2);
        mapOfEquations.put(equationsStr1[2], listOfPints3);
        mapOfEquations.put(equationsStr1[3], listOfPints4);
        mapOfEquations.put(equationsStr1[4], listOfPints5);
        mapOfEquations.put(equationsStr1[5], listOfPints6);

        Parameter parameter1 = new Parameter(0.2, 1 / 0.6, 1. / 2);
        Parameter parameter2 = new Parameter(0.4, 1 / 0.8, 1. / 2);
        Parameter parameter3 = new Parameter(0.3, 1 / 0.2, 1. / 3);
        Parameter parameter4 = new Parameter(0, 1 / 0.6, 1. / 2);
        LinkedList<Parameter> parametersList = new LinkedList<>();
        parametersList.add(parameter1);
        parametersList.add(parameter2);
        parametersList.add(parameter3);
        parametersList.add(parameter4);

        equations21.add((x, y, a, alpha, betta) -> Math.tan(x * y + a) - Math.pow(x, 2));
        equations21.add((x, y, a, alpha, betta) -> Math.pow(x, 2) / alpha + Math.pow(y, 2) / betta - 1);
        chDerivatives21.add((x, y, a, alpha, betta) -> y * (Math.pow(Math.tan(x * y + a), 2) + 1) - 2 * x);
        chDerivatives21.add((x, y, a, alpha, betta) -> x * (Math.pow(Math.tan(x * y + a), 2)) + 1);
        chDerivatives21.add((x, y, a, alpha, betta) -> 1 / alpha * 2 * x);
        chDerivatives21.add((x, y, a, alpha, betta) -> 1 / betta * 2 * x);

        equations22.add((x, y) -> Math.pow(x, 2) + Math.pow(y, 2) - 2);
        equations22.add((x, y) -> Math.exp(x - 1) + Math.pow(y, 3) - 2);
        chDerivatives22.add((x, y) -> 2 * x);
        chDerivatives22.add((x, y) -> 2 * y);
        chDerivatives22.add((x, y) -> Math.exp(x - 1));
        chDerivatives22.add((x, y) -> 3 * Math.pow(y, 2));

        System.out.println("----ЗАДАНИЕ 1----");
        simpleIterationMethod(mapOfEquations, equations1, firstDerivatives1);

        System.out.println("\n----ЗАДАНИЕ 2.1----\n");
        System.out.println("tg(xy + A) = x^2");
        System.out.println("x^2 / a^2 + y^2 / b^2 = 1");
        newtonMethod(points21, points22, chDerivatives21, equations21, parametersList);

        System.out.println("\n----ЗАДАНИЕ 2.2----\n");
        System.out.println("x1^2 + x2^2 - 2 = 0");
        System.out.println("e^(x1 - 2) + x2^3 - 2 = 0\n");
        newtonMethodMod2(points21, points22, chDerivatives22, equations22);
    }

    // Метод простой итерации (для задания 1)
    public static void simpleIterationMethod(LinkedHashMap<String, LinkedList<Point>> mapOfEquations,
                                             LinkedList<Operationable1> equations1,
                                             LinkedList<Operationable1> firstDerivatives1) {
        int equationNumber = 0;
        for (Map.Entry<String, LinkedList<Point>> entry : mapOfEquations.entrySet()) {
            System.out.println();
            System.out.println(entry.getKey());
            for (int i = 0; i < entry.getValue().size(); ++i) {
                System.out.println("\nТочка " + (i + 1) + "\n");
                double x1, d;
                int iterationNum = 1;
                double x0 = entry.getValue().get(i).getX();
                double lambda = 1 / firstDerivatives1.get(equationNumber).calculate(x0);
                do {
                    x1 = x0;
                    x0 = x1 - lambda * equations1.get(equationNumber).calculate(x1);
                    d = Math.abs(x0 - x1);
                    System.out.println("Итерация №" + iterationNum);
                    System.out.println("x = " + String.format("%.8f", x0));
                    System.out.println("d = " + String.format("%.8f", d));
                    ++iterationNum;
                } while (d > EPS);
            }
            ++equationNumber;
        }
    }

    // Поиск обратной матрицы для метода Ньютона
    public static void obrMatrix(double[][] a) {
        double[][] firstMatrix = new double[2][2];
        firstMatrix[0][0] = a[0][0];
        firstMatrix[0][1] = a[0][1];
        firstMatrix[1][0] = a[1][0];
        firstMatrix[1][1] = a[1][1];
        double det = firstMatrix[0][0] * firstMatrix[1][1] - firstMatrix[0][1] * firstMatrix[1][0];
        a[0][0] = firstMatrix[1][1] / det;
        a[1][1] = firstMatrix[0][0] / det;
        a[0][1] = -firstMatrix[0][1] / det;
        a[1][0] = -firstMatrix[1][0] / det;
    }

    // Метод Ньютона (для задания 2.1)
    public static void newtonMethod(double[] points21, double[] points22, LinkedList<Operationable21> chDerivatives21,
                                    LinkedList<Operationable21> equations21, LinkedList<Parameter> parametersList) {
        for (int i = 0; i < 4; ++i) {
            System.out.println("\nПараметры " + i);
            double x = points21[i];
            double y = points22[i];
            int iterationNum = 1;
            double[][] a = new double[2][2];
            double dx, dy, norm1, norm2;
            double[] b = new double[2];
            do {
                a[0][0] = chDerivatives21.get(0).calculate(x, y, parametersList.get(i).getAValue(), parametersList.get(i)
                        .getAlphaSquaredValue(), parametersList.get(i).getBetaSquaredValue());
                a[0][1] = chDerivatives21.get(1).calculate(x, y, parametersList.get(i).getAValue(), parametersList.get(i)
                        .getAlphaSquaredValue(), parametersList.get(i).getBetaSquaredValue());
                a[1][0] = chDerivatives21.get(2).calculate(x, y, parametersList.get(i).getAValue(), parametersList.get(i)
                        .getAlphaSquaredValue(), parametersList.get(i).getBetaSquaredValue());
                a[1][1] = chDerivatives21.get(3).calculate(x, y, parametersList.get(i).getAValue(), parametersList.get(i)
                        .getAlphaSquaredValue(), parametersList.get(i).getBetaSquaredValue());
                obrMatrix(a);
                dx = -a[0][0] * equations21.get(0).calculate(x, y, parametersList.get(i).getAValue(), parametersList.get(i)
                        .getAlphaSquaredValue(), parametersList.get(i).getBetaSquaredValue())
                        + -a[0][1] * equations21.get(1).calculate(x, y, parametersList.get(i).getAValue(), parametersList
                        .get(i).getAlphaSquaredValue(), parametersList.get(i).getBetaSquaredValue());
                dy = -a[1][0] * equations21.get(0).calculate(x, y, parametersList.get(i).getAValue(), parametersList.get(i)
                        .getAlphaSquaredValue(), parametersList.get(i).getBetaSquaredValue())
                        + -a[1][1] * equations21.get(1).calculate(x, y, parametersList.get(i).getAValue(), parametersList
                        .get(i).getAlphaSquaredValue(), parametersList.get(i).getBetaSquaredValue());
                x = x + dx;
                y = y + dy;
                b[0] = equations21.get(0).calculate(x, y, parametersList.get(i).getAValue(), parametersList.get(i)
                        .getAlphaSquaredValue(), parametersList.get(i).getBetaSquaredValue());
                b[1] = equations21.get(1).calculate(x, y, parametersList.get(i).getAValue(), parametersList.get(i)
                        .getAlphaSquaredValue(), parametersList.get(i).getBetaSquaredValue());
                norm1 = Math.sqrt(b[0] * b[0] + b[1] * b[1]);
                norm2 = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
                System.out.println("Итерация №" + iterationNum);
                System.out.println("x = " + String.format("%.8f", x));
                System.out.println("y = " + String.format("%.8f", y));
                iterationNum++;
            }
            while (norm1 >= EPS && norm2 >= EPS);
        }
    }

    // Метод Ньютона (для задания 2.2)
    public static void newtonMethodMod2(double[] points21, double[] points22, LinkedList<Operationable22> chDerivatives22,
                                        LinkedList<Operationable22> equations22) {
        double x = points21[4];
        double y = points22[4];
        int iterationNum = 1;
        double[][] a = new double[2][2];
        double dx, dy, norm1, norm2;
        double[] b = new double[2];
        do {
            a[0][0] = chDerivatives22.get(0).calculate(x, y);
            a[0][1] = chDerivatives22.get(0).calculate(x, y);
            a[1][0] = chDerivatives22.get(2).calculate(x, y);
            a[1][1] = chDerivatives22.get(3).calculate(x, y);
            obrMatrix(a);
            dx = -a[0][0] * equations22.get(0).calculate(x, y) + -a[0][1] * equations22.get(1).calculate(x, y);
            dy = -a[1][0] * equations22.get(0).calculate(x, y) + -a[1][1] * equations22.get(1).calculate(x, y);
            x = x + dx;
            y = y + dy;
            b[0] = equations22.get(0).calculate(x, y);
            b[1] = equations22.get(1).calculate(x, y);
            norm1 = Math.sqrt(b[0] * b[0] + b[1] * b[1]);
            norm2 = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
            System.out.println("Итерация №" + iterationNum);
            System.out.println("x1 = " + String.format("%.8f", x));
            System.out.println("x2 = " + String.format("%.8f", y));
            iterationNum++;
        }
        while (norm1 >= EPS  && norm2 >= EPS);
    }

    // Интерфейс с методом calculate для задания 1
    interface Operationable1 {
        double calculate(double x);
    }

    // Интерфейс с методом calculate для задания 2.1
    interface Operationable21 {
        double calculate(double x, double y, double a, double alpha, double betta);
    }

    // Интерфейс с методом calculate для задания 2.2
    interface Operationable22 {
        double calculate(double x, double y);
    }
}
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.Axis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.RadioButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

import java.net.URL;
import java.util.Arrays;
import java.util.DoubleSummaryStatistics;
import java.util.LinkedList;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    private final float SEGMENT_START = 0;
    private final float SEGMENT_END_FOR_ONE = 1000;
    private final float SEGMENT_END_FOR_TWO = 10;

    private final int NUMBER_OF_INTERVALS_FOR_ONE = 20000;
    private final int NUMBER_OF_INTERVALS_FOR_TWO = 10000;

    private final double FIELD_SIZE = 420.0;

    private final double m1 = 100000;
    private final double m2 = 1000;
    private final double k1 = 2 * Math.pow(10, -6);
    private final double g1 = 9.8;
    private final double k2 = 0.002;
    private final double k3 = 6.22 * Math.pow(10, -19);
    private final double n1 = 2 * Math.pow(10, 3);
    private final double n2 = n1;
    private final double n3 = 3 * Math.pow(10, 3);

    private final double k4 = 3;

    private final double k5 = 0.002;

    private final double k6 = 0.0006;

    private final double k7 = 0.5;
    private final double g2 = 32.17;
    private final double L = 10;

    private double STEP_H;

    private Operation operation1;
    private Operation operation2;
    private Operation operation3;
    private Operation operation4;
    private Operation2 operation5;
    private Operation2 operation6;
    private Operation2 operation7;
    private Operation2 operation8;
    private Operation2 operation9;
    private Operation2 operation10;

    private LinkedList<Operation2> systemOperations = new LinkedList<>();

    private LinkedList<InitialCondition> initialConditionsList;

    @FXML
    private Pane graphPane1;

    @FXML
    private Pane graphPane2;

    @FXML
    private RadioButton radioButton1;

    @FXML
    private RadioButton radioButton2;

    @FXML
    private RadioButton radioButton3;

    @FXML
    private RadioButton radioButton4;

    @FXML
    private RadioButton radioButton5;

    @FXML
    private RadioButton radioButton6;

    @FXML
    public void searchForAllValues(MouseEvent event) {
        // Очищаем поля перед отрисовкой новых графиков
        graphPane1.getChildren().clear();
        graphPane2.getChildren().clear();
        if (!isSystem()) {
            float[] xValues = generateXValues(false);
            Operation currFunction = getSelectedFunction();
            double[] yEulerValues = euler(currFunction, xValues);
            double[] yRungeKuttaValues = rungeKutta(currFunction, xValues);
            buildGraphs(xValues, yEulerValues, yRungeKuttaValues, false);
            solveIntegral(currFunction, yRungeKuttaValues);
        } else {
            float[] xValues = generateXValues(true);
            double[][] yEulerForSystem = eulerForSystem(getEquationIndex(), xValues);
            double[][] yRungeKuttaValuesForSystem = rungeKuttaForSystem(getEquationIndex(), xValues);

            if (getInitialConditionsIndex() == 4) {
                buildGraphs(xValues, getFxValues(yEulerForSystem, 0), getFxValues(yEulerForSystem, 1), true);
//                buildGraphs(xValues, getFxValues(yRungeKuttaValuesForSystem, 0), getFxValues(yRungeKuttaValuesForSystem, 1), true);
            } else if (getInitialConditionsIndex() == 5) {
                double[][] yEulerValuesForSystem2 = eulerForSystem(getEquationIndex() + 2, xValues);
//                buildGraphs(xValues, getFxValues(yEulerForSystem, 0), getFxValues(yEulerValuesForSystem2, 0), true);
                double[][] yRungeKuttaValuesForSystem2 = rungeKuttaForSystem(getEquationIndex() + 2, xValues);
                buildGraphs(xValues, getFxValues(yRungeKuttaValuesForSystem, 0), getFxValues(yRungeKuttaValuesForSystem2, 0), true);
            }
        }
    }

    /**
     * Получить индекс уравнения для выбора его из списка уравнений систем systemOperations
     *
     * @return индекс уравнения
     */
    private int getEquationIndex() {
        if (getInitialConditionsIndex() == 4) {
            return 0;
        }
        return 2;
    }

    /**
     * Создать функции в виде объектов интерфейса для решения их с помощью лямбда выражений
     */
    private void createFunctions() {
        operation1 = (t, x) -> 0.1 - 3 * x / (1000 + t);
        operation2 = (t, y) -> k1 * (m1 - y) * y;
        operation3 = (t, v) -> -g1 - (k2 * v * Math.abs(v)) / m2;
        operation4 = (t, x) -> k3 * Math.pow((n1 - t / 2), 2) * Math.pow((n2 - t / 2), 2) * Math.pow((n3 - (3 * t) / 4), 3);
        operation5 = (t, x1, x2) -> k4 * x1 - k5 * x1 * x2;
        operation6 = (t, x1, x2) -> k6 * x1 * x2 - k7 * x2;
        operation7 = (t, y, y1) -> y;
        operation8 = (t, y, y1) -> -(g2 / L) * Math.sin(y);
        operation9 = (t, y, y1) -> y;
        operation10 = (t, y, y1) -> -(g2 / L) * y;
        systemOperations.add(operation5);
        systemOperations.add(operation6);
        systemOperations.add(operation7);
        systemOperations.add(operation8);
        systemOperations.add(operation9);
        systemOperations.add(operation10);
    }

    /**
     * Поместить начальные условия задач Коши в список начальных условий
     */
    private void createInitialConditions() {
        initialConditionsList = new LinkedList<>();
        initialConditionsList.add(new InitialCondition(0, 50));
        initialConditionsList.add(new InitialCondition(0, 1000));
        initialConditionsList.add(new InitialCondition(0, 8));
        initialConditionsList.add(new InitialCondition(0, 0));
        initialConditionsList.add(new InitialCondition(0, 500));
        initialConditionsList.add(new InitialCondition(0, 500));
        initialConditionsList.add(new InitialCondition(0, Math.PI / 6));
        initialConditionsList.add(new InitialCondition(0, 0));
        initialConditionsList.add(new InitialCondition(0, Math.PI / 6));
        initialConditionsList.add(new InitialCondition(0, 0));
    }

    /**
     * Найти значения текущей функции по методу Эйлера
     *
     * @param currFunction текущаяя функция
     * @param xValues      значения переменной x
     * @return значения функции
     */
    private double[] euler(Operation currFunction, float[] xValues) {
        double[] fxValues = new double[xValues.length];
        double fx = initialConditionsList.get(getInitialConditionsIndex()).getFxValue();
        fxValues[0] = fx;
        for (int i = 1; i < xValues.length; ++i) {
            fx = fx + STEP_H * currFunction.calculate(xValues[i], fx);
            fxValues[i] = fx;
        }
        return fxValues;
    }

    private double[][] eulerForSystem(int index, float[] xValues) {
        double[][] fxValues = new double[2][xValues.length];
        double fx1 = initialConditionsList.get(getInitialConditionsIndex()).getFxValue();
        double fx2 = initialConditionsList.get(getInitialConditionsIndex() + 1).getFxValue();
        fxValues[0][0] = fx1;
        fxValues[1][0] = fx2;

        Operation2 firstEquation = systemOperations.get(index);
        Operation2 secondEquation = systemOperations.get(index + 1);
        for (int i = 1; i < xValues.length; ++i) {
            fx1 = fx1 + STEP_H * firstEquation.calculate(xValues[i], fx1, fx2);
            fx2 = fx2 + STEP_H * secondEquation.calculate(xValues[i], fx1, fx2);
            fxValues[0][i] = fx1;
            fxValues[1][i] = fx2;
        }
        return fxValues;
    }

    /**
     * Найти значения текущей функции по методу Рунге-Кутта
     *
     * @param currFunction текущаяя функция
     * @param xValues      значения переменной x
     * @return значения функции
     */
    private double[] rungeKutta(Operation currFunction, float[] xValues) {
        double[] fxValues = new double[xValues.length];
        double fx = initialConditionsList.get(getInitialConditionsIndex()).getFxValue();
        fxValues[0] = fx;
        double p1, p2, p3, p4;
        for (int i = 1; i < xValues.length; ++i) {
            p1 = currFunction.calculate(xValues[i], fx);
            p2 = currFunction.calculate(xValues[i] + STEP_H / 2, fx + STEP_H * p1 / 2);
            p3 = currFunction.calculate(xValues[i] + STEP_H / 2, fx + STEP_H * p2 / 2);
            p4 = currFunction.calculate(xValues[i] + STEP_H, fx + STEP_H * p3);

            fx = fx + (STEP_H / 6) * (p1 + 2 * p2 + 2 * p3 + p4);
            fxValues[i] = fx;
        }
        return fxValues;
    }

    /**
     * Найти значения системы уравнений по методу Рунге-Кутты
     *
     * @param xValues значения переменной x
     * @return значения системы уравнений
     */
    private double[][] rungeKuttaForSystem(int index, float[] xValues) {
        double[][] fxValues = new double[2][xValues.length];
        double fx1 = initialConditionsList.get(getInitialConditionsIndex()).getFxValue();
        double fx2 = initialConditionsList.get(getInitialConditionsIndex() + 1).getFxValue();
        fxValues[0][0] = fx1;
        fxValues[1][0] = fx2;

        Operation2 firstEquation = systemOperations.get(index);
        Operation2 secondEquation = systemOperations.get(index + 1);

        double p11, p12, p21, p22, p31, p32, p41, p42;
        for (int i = 1; i < xValues.length; ++i) {
            p11 = firstEquation.calculate(xValues[i], fx1, fx2);
            p12 = secondEquation.calculate(xValues[i], fx1, fx2);
            p21 = firstEquation.calculate(xValues[i] + STEP_H / 2, fx1 + p11 / 2, fx2 + STEP_H * p12);
            p22 = secondEquation.calculate(xValues[i] + STEP_H / 2, fx1 + STEP_H * p11 / 2, fx2 + STEP_H * p12);
            p31 = firstEquation.calculate(xValues[i] + STEP_H / 2, fx1 + STEP_H * p21 / 2, fx2 + STEP_H * p22 / 2);
            p32 = secondEquation.calculate(xValues[i] + STEP_H / 2, fx1 + STEP_H * p21 / 2, fx2 + STEP_H * p22 / 2);
            p41 = firstEquation.calculate(xValues[i] + STEP_H, fx1 + STEP_H * p31, fx2 + STEP_H * p32);
            p42 = secondEquation.calculate(xValues[i] + STEP_H, fx1 + STEP_H * p31, fx2 + STEP_H * p32);

            fx1 = fx1 + (STEP_H / 6) * (p11 + 2 * p21 + 2 * p31 + p41);
            fx2 = fx2 + (STEP_H / 6) * (p12 + 2 * p22 + 2 * p32 + p42);
            fxValues[0][i] = fx1;
            fxValues[1][i] = fx2;
        }
        return fxValues;
    }


    /**
     * Сгенерировать значения переменной x исходя из шага и интервала
     *
     * @param isSystem true, если система, иначе - false
     * @return значения переменной x
     */
    private float[] generateXValues(boolean isSystem) {
        int size;
        if (!isSystem) {
            STEP_H = (SEGMENT_END_FOR_ONE - SEGMENT_START) / NUMBER_OF_INTERVALS_FOR_ONE;
            size = (int) ((SEGMENT_END_FOR_ONE - SEGMENT_START) / STEP_H);
        } else {
            STEP_H = (SEGMENT_END_FOR_TWO - SEGMENT_START) / NUMBER_OF_INTERVALS_FOR_TWO;
            size = (int) ((SEGMENT_END_FOR_TWO - SEGMENT_START) / STEP_H);
        }
        float[] xValues = new float[size + 1];

        float x = SEGMENT_START;
        for (int i = 0; i < xValues.length; ++i) {
            xValues[i] = x;
            x += STEP_H;
        }
        return xValues;
    }

    /**
     * Построить графики приближенного решения задачи Коши для уравнений и систем
     *
     * @param xValues  значения переменной x
     * @param y1Values решения первого уравнения
     * @param y2Values решения второго уравнения (если это система)
     * @param isSystem true, если система, иначе - false
     */
    private void buildGraphs(float[] xValues, double[] y1Values, double[] y2Values, boolean isSystem) {
        ObservableList<XYChart.Series<Number, Number>> sl1 = FXCollections.observableArrayList();
        ObservableList<XYChart.Data<Number, Number>> l1 = FXCollections.observableArrayList();

        for (int i = 0; i < xValues.length; ++i) {
            l1.add(new XYChart.Data<>(xValues[i], y1Values[i]));
        }

        ObservableList<XYChart.Data<Number, Number>> l2 = FXCollections.observableArrayList();

        for (int i = 0; i < xValues.length; ++i) {
            l2.add(new XYChart.Data<>(xValues[i], y2Values[i]));
        }

        if (!isSystem) {
            sl1.add(new XYChart.Series<>("Эйлер", l1));
            sl1.add(new XYChart.Series<>("Рунге-Кутта", l2));
        } else {
            sl1.add(new XYChart.Series<>("Уравнение 1", l1));
            sl1.add(new XYChart.Series<>("Уравнение 2", l2));

            NumberAxis xy1 = new NumberAxis();
            xy1.setLabel("X1");

            NumberAxis xy2 = new NumberAxis();
            xy2.setLabel("X2");

            ScatterChart<Number, Number> l3 = new ScatterChart<>(xy1, xy2);

            XYChart.Series<Number, Number> sr = new XYChart.Series<>();

            sr.setName("График 2");

            for (int i = 0; i < xValues.length; ++i) {
                sr.getData().add(new XYChart.Data<>( y1Values[i], y2Values[i]));
            }
            l3.getData().add(sr);
            l3.setMaxSize(FIELD_SIZE, FIELD_SIZE);
            graphPane2.getChildren().add(l3);
        }

        DoubleSummaryStatistics stat1 = Arrays.stream(y1Values).summaryStatistics();
        DoubleSummaryStatistics stat2 = Arrays.stream(y2Values).summaryStatistics();

        // Создаем оси линейных графиков
        Axis<Number> x1;
        if (!isSystem) {
            x1 = new NumberAxis("t", SEGMENT_START, SEGMENT_END_FOR_ONE, (SEGMENT_END_FOR_ONE - SEGMENT_START) / 10);
        } else {
            x1 = new NumberAxis("t", SEGMENT_START, SEGMENT_END_FOR_TWO, (SEGMENT_END_FOR_TWO - SEGMENT_START) / 10);
        }
        Axis<Number> y1 = new NumberAxis("Y",
                Math.min(stat1.getMin(), stat2.getMin()),
                Math.max(stat1.getMax(), stat2.getMax()),
                (Math.max(stat1.getMax(), stat2.getMax() -  Math.min(stat1.getMin(), stat2.getMin()))) / 10);
        LineChart<Number, Number> chart1 = new LineChart<>(x1, y1, sl1);
        chart1.setMaxSize(FIELD_SIZE, FIELD_SIZE);
        chart1.setCreateSymbols(false);

        graphPane1.getChildren().add(chart1);

        System.out.println();
    }

    /**
     * Получить объект Operation выбранной функции
     *
     * @return объект Operation выбранной функции
     */
    private Operation getSelectedFunction() {
        if (radioButton1.isSelected()) {
            return operation1;
        } else if (radioButton2.isSelected()) {
            return operation2;
        } else if (radioButton3.isSelected()) {
            return operation3;
        } else if (radioButton4.isSelected()) {
            return operation4;
        }
        return null;
    }

    /**
     * Получить индекс выбранной задачи для выбора соответствующего начального условия для нее из списка
     *
     * @return индекс задачи
     */
    private int getInitialConditionsIndex() {
        if (radioButton1.isSelected()) {
            return 0;
        } else if (radioButton2.isSelected()) {
            return 1;
        } else if (radioButton3.isSelected()) {
            return 2;
        } else if (radioButton4.isSelected()) {
            return 3;
        } else if (radioButton5.isSelected()) {
            return 4;
        } else if (radioButton6.isSelected()) {
            return 5;
        }
        return 0;
    }

    /**
     * Получить значения одного конкретного уравнения из системы для отрисовки отдельных графиков
     *
     * @param yRungeKuttaValuesForSystem значения диф. уравнений в системе
     * @param rowNumber                  номер строки в двумерном массиве
     * @return значения одного уравнения из системы
     */
    private double[] getFxValues(double[][] yRungeKuttaValuesForSystem, int rowNumber) {
        int size = yRungeKuttaValuesForSystem[0].length;
        double[] fxValues = new double[size];
        for (int i = 0; i < size; ++i) {
            fxValues[i] = yRungeKuttaValuesForSystem[rowNumber][i];
        }
        return fxValues;
    }

    /**
     * Проверить, является ли задача Коши системой уравнений или же состоит из одного уравнения
     *
     * @return true, если система, иначе - false
     */
    private boolean isSystem() {
        if (radioButton1.isSelected() || radioButton2.isSelected() || radioButton3.isSelected() || radioButton4.isSelected()) {
            return false;
        }
        return true;
    }

    /**
     * Выбрать и вывести решение нужного интеграла
     *
     * @param currFunction      подынтегральная функция
     * @param yRungeKuttaValues подынтегральные значения
     */
    private void solveIntegral(Operation currFunction, double[] yRungeKuttaValues) {
        if (currFunction == operation1) {
            System.out.println("Метод прямоугольников для интеграла 1");
            System.out.println("Площадь: " + rectangleMethod(yRungeKuttaValues));
        } else if (currFunction == operation2) {
            System.out.println("Метод трапеций для интеграла 2");
            System.out.println("Площадь: " + trapezoidMethod(yRungeKuttaValues));
        } else if (currFunction == operation3) {
            System.out.println("Метод прямоугольников для интеграла 3");
            System.out.println("Площадь: " + rectangleMethod(yRungeKuttaValues));
            System.out.println("Метод трапеций для интеграла 3");
            System.out.println("Площадь: " + trapezoidMethod(yRungeKuttaValues));
        }
    }

    /**
     * Найти значение опредленного интеграла методом прямоугольников (средних)
     *
     * @param yRungeKuttaValues подынтегральные значения
     * @return значение определенного интеграла
     */
    private double rectangleMethod(double[] yRungeKuttaValues) {
        double sum = 0;
        for (double y : yRungeKuttaValues) {
            sum += Math.abs(y + STEP_H / 2);
        }
        return STEP_H * sum;
    }

    /**
     * Найти значение опредленного интеграла методом трапеций
     *
     * @param yRungeKuttaValues подынтегральные значения
     * @return значение определенного интеграла
     */
    private double trapezoidMethod(double[] yRungeKuttaValues) {
        double sum = 0;
        for (int i = 0; i < yRungeKuttaValues.length; ++i) {
            if (i == 0 || i == yRungeKuttaValues.length - 1) {
                sum += Math.abs(yRungeKuttaValues[i] / 2);
            } else {
                sum += Math.abs(yRungeKuttaValues[i]);
            }
        }
        return STEP_H * sum;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        createFunctions();
        createInitialConditions();
    }

    interface Operation {
        double calculate(double x, double y);
    }

    interface Operation2 {
        double calculate(double x, double y1, double y2);
    }
}

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

import java.net.URL;
import java.util.Arrays;
import java.util.DoubleSummaryStatistics;
import java.util.LinkedList;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    @FXML
    private ComboBox<Integer> numberOfNodesBox;

    @FXML
    private ComboBox<String> intervalBox;

    @FXML
    private Pane functionGraph;

    @FXML
    private Pane lagrangePolynomialGraph;

    @FXML
    private Pane newtonPolynomialGraph;

    @FXML
    private RadioButton radioButton1;

    @FXML
    private RadioButton radioButton2;

    @FXML
    private RadioButton radioButton3;

    @FXML
    private RadioButton radioButton4;

    private final double FIELD_SIZE = 380.0;

    private LinkedList<Interval> intervalsList;
    private Operation operation1;
    private Operation operation2;
    private Operation operation3;
    private Operation operation4;

    /**
     * Заполнить ComboBox узлов различными значениями количества узлов
     */
    private void fillNodesComboBox() {
        ObservableList<Integer> nodesObservList = FXCollections.observableArrayList();
        nodesObservList.add(3);
        nodesObservList.add(4);
        nodesObservList.add(8);
        nodesObservList.add(10);
        nodesObservList.add(16);
        nodesObservList.add(32);
        nodesObservList.add(64);
        numberOfNodesBox.setItems(nodesObservList);
    }

    /**
     * Заполнить ComboBox отрезков различными интервалами значений
     */
    private void fillIntervalComboBox() {
        ObservableList<String> intervalsObservList = FXCollections.observableArrayList();
        for (Interval interval : intervalsList) {
            intervalsObservList.add("[" + interval.getStart() + "," + interval.getEnd() + "]");
        }
        intervalBox.setItems(intervalsObservList);
    }

    @FXML
    void searchForAllValues(MouseEvent event) {
        // Очищаем поля перед отрисовкой новых графиков
        functionGraph.getChildren().clear();
        lagrangePolynomialGraph.getChildren().clear();
        newtonPolynomialGraph.getChildren().clear();

        int numberOfNodes = numberOfNodesBox.getSelectionModel().getSelectedItem(); // Количество узлов
        Interval interval = getInterval(); // Текущий отрезок, на котором задана функция

        double step = (interval.getEnd() - interval.getStart()) / (numberOfNodes - 1); // Интервал между соседними точками
        double[] xValues = findValuesX(numberOfNodes, interval, step); // Переменные x исходной функции
        double[] yValues1 = findFunctionValuesY(getSelectedFunction(), xValues); // Значения исходной функции

        double[] yValues2 = new double[xValues.length];
        for (int i = 0; i < yValues2.length; ++i) {
            yValues2[i] = interpolateLagrangePolynomial(xValues[i], xValues, yValues1);
        }

        double[][] finiteDifferences = findFiniteDifferences(xValues, yValues1);
        double[] coefficients = findNewtonCoefficients(finiteDifferences);

        double[] yValues3 = new double[xValues.length];
        int degree = xValues.length - 1; // Степень интерполяционного многочлена Ньютона
        for (int i = 0; i < yValues3.length; ++i) {
            yValues3[i] = newtonPolValue(degree, xValues, coefficients, xValues[i]);
        }

        buildGraphs(xValues, yValues1, yValues2, yValues3, step, numberOfNodes); // Вызов функции отрисовки графиков
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
     * Получить объект Interval выбранного отрезка
     *
     * @return объект Interval отрезка
     */
    private Interval getInterval() {
        return intervalsList.get(intervalBox.getSelectionModel().getSelectedIndex());
    }

    private double[] findValuesX(int numberOfNodes, Interval interval, double step) {
        double[] xValues = new double[numberOfNodes];
        xValues[0] = interval.getStart();
        xValues[numberOfNodes - 1] = interval.getEnd();

        for (int i = 1; i < xValues.length - 1; ++i) {
            xValues[i] = xValues[i - 1] + step;
        }
        return xValues;
    }

    /**
     * Рассчет значений функции в точках
     *
     * @param function исходная функция
     * @param xValues  значения x
     * @return массив значений функции
     */
    private double[] findFunctionValuesY(Operation function, double[] xValues) {
        double[] yValues1 = new double[xValues.length];
        for (int i = 0; i < yValues1.length; ++i) {
            yValues1[i] = function.calculate(xValues[i]);
        }
        return yValues1;
    }

    /**
     * Построение графиков
     *
     * @param xValues  массив переменных x
     * @param yValues1 массив значений функции
     * @param yValues2 массив значений полинома Лагранжа
     * @param yValues3 массив значений полинома Ньютона
     * @param step     шаг деления оси x
     */
    private void buildGraphs(double[] xValues, double[] yValues1, double[] yValues2, double[] yValues3, double step, int numberOfNodes) {
        XYChart.Series<Number, Number> sr1 = new XYChart.Series<>();
        sr1.setName("Функция");

        for (int i = 0; i < xValues.length; ++i) {
            sr1.getData().add(new XYChart.Data<>(xValues[i], yValues1[i]));
        }

        XYChart.Series<Number, Number> sr2 = new XYChart.Series<>();
        sr2.setName("Полином Лагранжа");

        for (int i = 0; i < xValues.length; ++i) {
            sr2.getData().add(new XYChart.Data<>(xValues[i], yValues2[i]));
        }

        XYChart.Series<Number, Number> sr3 = new XYChart.Series<>();
        sr3.setName("Полином Ньютона");


        for (int i = 0; i < xValues.length; ++i) {
            sr3.getData().add(new XYChart.Data<>(xValues[i], yValues3[i]));
        }

        DoubleSummaryStatistics stat = Arrays.stream(yValues1).summaryStatistics();

        // Создаем оси линейных графиков
        LineChart<Number, Number> lc1 = new LineChart<>
                (new NumberAxis("x", xValues[0] - 1, xValues[xValues.length - 1] + 1, step),
                        new NumberAxis("y", stat.getMin() - 1, stat.getMax() + 1, 1));
        LineChart<Number, Number> lc2 = new LineChart<>
                (new NumberAxis("x", xValues[0] - 1, xValues[xValues.length - 1] + 1, step),
                        new NumberAxis("y", stat.getMin() - 1, stat.getMax() + 1, 1));
        LineChart<Number, Number> lc3 = new LineChart<>
                (new NumberAxis("x", xValues[0] - 1, xValues[xValues.length - 1] + 1, step),
                        new NumberAxis("y", stat.getMin() - 1, stat.getMax() + 1, 1));

        lc1.getData().add(sr1);
        lc2.getData().add(sr2);
        lc3.getData().add(sr3);

        lc1.setMaxSize(FIELD_SIZE, FIELD_SIZE);
        lc2.setMaxSize(FIELD_SIZE, FIELD_SIZE);
        lc3.setMaxSize(FIELD_SIZE, FIELD_SIZE);

        setCreateSymbols(numberOfNodes, lc1, lc2, lc3);

        functionGraph.getChildren().add(lc1);
        lagrangePolynomialGraph.getChildren().add(lc2);
        newtonPolynomialGraph.getChildren().add(lc3);
    }

    /**
     * Выбор: отображать или нет опорные точки графиков
     *
     * @param numberOfNodes число узлов
     * @param lc1           грфаик функции
     * @param lc2           график полинома Лагранжа
     * @param lc3           график полинома Ньютона
     */
    private void setCreateSymbols(
            int numberOfNodes,
            LineChart<Number, Number> lc1,
            LineChart<Number, Number> lc2,
            LineChart<Number, Number> lc3) {
        if (numberOfNodes == 64) {
            lc1.setCreateSymbols(false);
            lc2.setCreateSymbols(false);
            lc3.setCreateSymbols(false);
        } else {
            lc1.setCreateSymbols(true);
            lc2.setCreateSymbols(true);
            lc3.setCreateSymbols(true);
        }
    }

    /**
     * Найти конечные разности полинома Ньютона
     *
     * @param xValues  массив переменных x
     * @param yValues1 массив значений функции
     * @return массив конечных разностей
     */
    private double[][] findFiniteDifferences(double[] xValues, double[] yValues1) {
        int size = xValues.length;

        double[][] newton = new double[(2 * size) - 1][size + 1];
        for (int i = 0, j = 0; i < size; ++i, j += 2) {
            newton[j][0] = xValues[i];
            newton[j][1] = yValues1[i];
        }

        int i = 1, ii = 2, j = 2, ss = size;
        for (int k = 0; k < size - 1; ++k, ++j, ss -= 1, ++ii) {
            for (int l = 0; l < ss - 1; ++l, i += 2) {
                newton[i][j] = (newton[i + 1][j - 1] - newton[i - 1][j - 1]) / (newton[i + (ii - 1)][0] - newton[i - (ii - 1)][0]);
            }
            i = ii;
        }
        return newton;
    }

    /**
     * Найти коэффициенты результирующего полинома Ньютона
     *
     * @param finiteDifferences массив конечных разностей
     * @return коэффициенты полинома Ньютона
     */
    private double[] findNewtonCoefficients(double[][] finiteDifferences) {
        double length = numberOfNodesBox.getSelectionModel().getSelectedItem();

        double[] coefficients = new double[finiteDifferences[0].length - 1];
        for (int i = 0; i < length; ++i) {
            coefficients[i] = finiteDifferences[i][i + 1];
        }
        return coefficients;
    }

    /**
     * Вычислить значение полинома Ньютона в точке
     *
     * @param n       степень интерполяционного многочлена Ньютона
     * @param xValues массив переменных x
     * @param aValues массив коэффициентов
     * @param point   точка для вычисления
     * @return значение полинома в точке
     */
    private double newtonPolValue(int n, double[] xValues, double[] aValues, double point) {
        double s = aValues[0];
        double p = 1.0;
        for (int i = 1; i <= n; ++i) {
            p *= (point - xValues[i - 1]);
            s += aValues[i] * p;
        }
        return s;
    }

    /**
     * Вычислить значение полинома Лагранжа в точке
     *
     * @param point    точка
     * @param xValues  массив переменных x
     * @param yValues1 массив значений функции
     * @return значение полинома в точке
     */
    private double interpolateLagrangePolynomial(double point, double[] xValues, double[] yValues1) {
        double lagrangePol = 0;

        for (int i = 0; i < xValues.length; ++i) {
            double basicsPol = 1;
            for (int j = 0; j < xValues.length; ++j) {
                if (j != i) {
                    basicsPol *= (point - xValues[j]) / (xValues[i] - xValues[j]);
                }
            }
            lagrangePol += basicsPol * yValues1[i];
        }
        return lagrangePol;
    }

    /**
     * Создать исходные функции
     */
    private void createFunctions() {
        operation1 = x -> Math.pow(x, 3) - 6.5 * Math.pow(x, 2) + 11 * x - 4;
        operation2 = x -> 3 * Math.cos((Math.PI * x) / 8);
        operation3 = x -> Math.pow(Math.E, (-x / 4)) * Math.sin(x / 3);
        operation4 = x -> 8 * x * Math.pow(Math.E, (-Math.pow(x, 2) / 12));
    }

    /**
     * Заполнить список отрезков
     */
    private void createIntervals() {
        intervalsList = new LinkedList<>();
        intervalsList.add(new Interval(2, 4));
        intervalsList.add(new Interval(0.5, 3));
        intervalsList.add(new Interval(4, 10));
        intervalsList.add(new Interval(0, 12));
    }

    /**
     * Интерфейс для расчета значения функции от переменной x
     */
    interface Operation {
        double calculate(double x);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        createFunctions();
        fillNodesComboBox();
        createIntervals();
        fillIntervalComboBox();
    }
}

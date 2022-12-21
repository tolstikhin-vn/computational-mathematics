import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.Axis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.Pane;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.DoubleSummaryStatistics;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ResourceBundle;

public class InterpolationPolynomial implements Initializable {

    @FXML
    private Pane graphPane1;

    @FXML
    private Pane graphPane2;

    @FXML
    private Pane graphPane3;

    private final double PANE_SIZE = 350.0;
    public static LinkedList<double[][]> listOfTables = new LinkedList<>();

    // Интерполяция Лагранжа для табличной функции
    private static StringBuilder tableLagrangeInterpolation(LinkedList<double[]> listOfTables2) {
        StringBuilder lagrangePolynomials = new StringBuilder();
        for (int i = 0; i < listOfTables2.size(); i += 2) {
            for (int j = 0; j < listOfTables2.get(i + 1).length; ++j) {
                lagrangePolynomials.append(listOfTables2.get(i + 1)[j]);
                // Формируем числитель
                for (int k = 0; k < listOfTables2.get(i).length; ++k) {
                    if (j != k) {
                        if (listOfTables2.get(i)[k] >= 0) {
                            lagrangePolynomials.append("(x-").append(listOfTables2.get(i)[k]).append(")");
                        } else {
                            lagrangePolynomials.append("(x+").append(Math.abs(listOfTables2.get(i)[k])).append(")");
                        }
                    }
                }

                lagrangePolynomials.append(" / ");

                // Формируем знаменатель
                for (int l = 0; l < listOfTables2.get(i).length; ++l) {
                    if (i != l) {
                        if (listOfTables2.get(i)[l] >= 0) {
                            lagrangePolynomials.append("(").append(listOfTables2.get(i)[j]).append("-").append(listOfTables2.get(i)[l]).append(")");
                        } else {
                            lagrangePolynomials.append("(").append(listOfTables2.get(i)[j]).append("+").append(Math.abs(listOfTables2.get(i)[l])).append(")");
                        }
                    }
                }

                // Если не последний член, то добавляем "+"
                if (j != listOfTables2.get(i + 1).length - 1) {
                    lagrangePolynomials.append("  +  ");
                }
            }
            lagrangePolynomials.append("\n");
        }
        return lagrangePolynomials;
    }

    // Интерполяция Ньютона для табличной функции
    private static String tableNewtonInterpolation() {
        StringBuilder newtonPolynomials = new StringBuilder();
        float step;

        for (double[][] table : listOfTables) {

            step = getXValuesStep(table);

            double[][] finiteDifferences = findFiniteDifferences(getXValues(table), getYValues(table));
            double[] coefficients = findNewtonCoefficients(finiteDifferences);

            int degree = 1; // степень полинома
            newtonPolynomials.append(String.format("%.5f", coefficients[0]) + " + ");
            for (int i = 1; i < coefficients.length; ++i) {
                newtonPolynomials.append(String.format("%.5f", coefficients[i]));
                for (int j = 0; j < degree; ++j) {
                    newtonPolynomials.append("(x-" + table[0][j] + ")");
                }
                newtonPolynomials.append(" / " + degree + "!*" + step + "^" + degree);

                if (i != coefficients.length - 1) {
                    newtonPolynomials.append(" + ");
                }
                ++degree;
            }
            newtonPolynomials.append("\n");
        }
        return newtonPolynomials.toString().replace("--", "+").replace("+-", "-");
    }

    private static double[] getXValues(double[][] table) {
        double[] xValues = new double[table[0].length];
        System.arraycopy(table[0], 0, xValues, 0, xValues.length);
        return xValues;
    }

    private static double[] getYValues(double[][] table) {
        double[] yValues = new double[table[0].length];
        System.arraycopy(table[1], 0, yValues, 0, yValues.length);
        return yValues;
    }

    /**
     * Найти конечные разности полинома Ньютона
     *
     * @param xValues массив переменных x
     * @param yValues массив значений функции
     * @return массив конечных разностей
     */
    private static double[][] findFiniteDifferences(double[] xValues, double[] yValues) {
        int size = xValues.length;

        double[][] newton = new double[(2 * size) - 1][size + 1];
        for (int i = 0, j = 0; i < size; ++i, j += 2) {
            newton[j][0] = xValues[i];
            newton[j][1] = yValues[i];
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
    private static double[] findNewtonCoefficients(double[][] finiteDifferences) {

        double[] coefficients = new double[finiteDifferences[0].length - 1];
        for (int i = 0; i < 6; ++i) {
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

    private static float getXValuesStep(double[][] table) {
        return (float) Math.abs(table[0][1] - table[0][0]);
    }

    // Разделить массивы на два (для x и для y) для удобства вычислений
    private static void splitTables(LinkedList<double[]> listOfSplitTables) {
        for (double[][] table : listOfTables) {
            for (double[] doubles : table) {
                double[] table1 = new double[6];
                System.arraycopy(doubles, 0, table1, 0, doubles.length);
                listOfSplitTables.add(table1);
            }
        }
    }

    // Чтение файла, содержащего функции, заданные таблично
    public static void fileReading() {
        File file = new File("input.txt");
        if (file.length() == 0) {
            System.out.println("Файл пуст");
        } else {
            ArrayList<String[]> coefficients = new ArrayList<>();
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String str;
                while ((str = reader.readLine()) != null) {
                    if (!str.equals("")) {
                        coefficients.add(str.split(" "));
                    } else {
                        fillingListOfMatrices(coefficients);
                        coefficients.clear();
                    }
                }
                // Заполнение последнего массива
                fillingListOfMatrices(coefficients);
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    // Заполнение коллекции с массивами систем
    public static void fillingListOfMatrices(ArrayList<String[]> coefficients) {
        int columns = coefficients.get(0).length;
        double[][] table = new double[coefficients.size()][columns];
        Iterator<String[]> iter = coefficients.iterator();
        for (int i = 0; i < table.length; ++i) {
            String[] s = iter.next();
            for (int j = 0; j < columns; ++j) {
                table[i][j] = Double.parseDouble(s[j]);
            }
        }
        listOfTables.add(table);
    }

    private void buildGraphs(LinkedList<double[]> xValuesList, LinkedList<double[]> lagrangeValuesList, LinkedList<double[]> newtonValuesList) {
        ObservableList<XYChart.Series<Number, Number>> sl1 = FXCollections.observableArrayList();
        ObservableList<XYChart.Series<Number, Number>> sl2 = FXCollections.observableArrayList();
        ObservableList<XYChart.Series<Number, Number>> sl3 = FXCollections.observableArrayList();
        LinkedList<ObservableList<XYChart.Series<Number, Number>>> listOfSeries = new LinkedList<>();
        listOfSeries.add(sl1);
        listOfSeries.add(sl2);
        listOfSeries.add(sl3);

        ObservableList<XYChart.Data<Number, Number>> l1 = FXCollections.observableArrayList();
        ObservableList<XYChart.Data<Number, Number>> l2 = FXCollections.observableArrayList();
        ObservableList<XYChart.Data<Number, Number>> l3 = FXCollections.observableArrayList();
        ObservableList<XYChart.Data<Number, Number>> l4 = FXCollections.observableArrayList();
        ObservableList<XYChart.Data<Number, Number>> l5 = FXCollections.observableArrayList();
        ObservableList<XYChart.Data<Number, Number>> l6 = FXCollections.observableArrayList();
        LinkedList<ObservableList<XYChart.Data<Number, Number>>> listOfData = new LinkedList<>();
        listOfData.add(l1);
        listOfData.add(l2);
        listOfData.add(l3);
        listOfData.add(l4);
        listOfData.add(l5);
        listOfData.add(l6);

        for (int i = 0; i < listOfSeries.size(); ++i) {
            int numOfData = 0;
            for (int j = 0; j < xValuesList.get(i).length; ++j) {
                listOfData.get(i).add(new XYChart.Data<>(xValuesList.get(i)[j], lagrangeValuesList.get(i)[j]));
                listOfData.get(i + 3).add(new XYChart.Data<>(xValuesList.get(i)[j], newtonValuesList.get(i)[j]));
            }
        }

        sl1.add(new XYChart.Series<>("Лагранж", l1));
        sl1.add(new XYChart.Series<>("Ньютон", l4));

        sl2.add(new XYChart.Series<>("Лагранж", l2));
        sl2.add(new XYChart.Series<>("Ньютон", l5));

        sl3.add(new XYChart.Series<>("Лагранж", l3));
        sl3.add(new XYChart.Series<>("Ньютон", l6));

        LinkedList<Axis<Number>> xAxisList = new LinkedList<>();

        for (int i = 0; i < xValuesList.size(); ++i) {
            xAxisList.add(new NumberAxis("X", xValuesList.get(i)[0], xValuesList.get(i)[xValuesList.get(i).length - 1], Math.abs(xValuesList.get(i)[1] - xValuesList.get(i)[0])));
        }

        LinkedList<Axis<Number>> yAxisList = new LinkedList<>();

        DoubleSummaryStatistics stat1;
        DoubleSummaryStatistics stat2;


        for (int i = 0; i < listOfSeries.size(); ++i) {
            stat1 = Arrays.stream(lagrangeValuesList.get(i)).summaryStatistics();
            stat2 = Arrays.stream(newtonValuesList.get(i)).summaryStatistics();

            yAxisList.add(new NumberAxis("Y", Math.min(stat1.getMin(),
                    stat2.getMin()), Math.max(stat1.getMax(), stat2.getMax()),
                    (Math.max(stat1.getMax(), stat2.getMax() - Math.min(stat1.getMin(), stat2.getMin()))) / xValuesList.get(i).length));
        }

        LineChart<Number, Number> chart1 = new LineChart<>(xAxisList.get(0), yAxisList.get(0), sl1);
        LineChart<Number, Number> chart2 = new LineChart<>(xAxisList.get(1), yAxisList.get(1), sl2);
        LineChart<Number, Number> chart3 = new LineChart<>(xAxisList.get(2), yAxisList.get(2), sl3);

        chart1.setMaxSize(PANE_SIZE, PANE_SIZE);
        chart1.setCreateSymbols(false);
        chart2.setMaxSize(PANE_SIZE, PANE_SIZE);
        chart2.setCreateSymbols(false);
        chart3.setMaxSize(PANE_SIZE, PANE_SIZE);
        chart3.setCreateSymbols(false);

        graphPane1.getChildren().add(chart1);
        graphPane2.getChildren().add(chart2);
        graphPane3.getChildren().add(chart3);
    }

    private void searchValues(LinkedList<double[]> listOfSplitTables) {
        double[] xValues, yValues;
        LinkedList<double[]> lagrangeValuesList = new LinkedList<>();
        LinkedList<double[]> newtonValuesList = new LinkedList<>();
        LinkedList<double[]> xValuesList = new LinkedList<>();

        for (int i = 0; i < listOfSplitTables.size(); i += 2) {
            xValues = listOfSplitTables.get(i);
            xValuesList.add(xValues);

            yValues = listOfSplitTables.get(i + 1);

            double[][] finiteDifferences = findFiniteDifferences(xValues, yValues);
            double[] coefficients = findNewtonCoefficients(finiteDifferences);

            double[] yValuesLagrange = new double[xValues.length];
            double[] yValuesNewton = new double[xValues.length];
            int degree = xValues.length - 1; // Степень интерполяционного многочлена Ньютона

            for (int j = 0; j < yValues.length; ++j) {
                yValuesLagrange[j] = interpolateLagrangePolynomial(xValues[j], xValues, yValues);
                yValuesNewton[j] = newtonPolValue(degree, xValues, coefficients, xValues[j]);
            }
            lagrangeValuesList.add(yValuesLagrange);
            newtonValuesList.add(yValuesNewton);
        }

        buildGraphs(xValuesList, lagrangeValuesList, newtonValuesList);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        LinkedList<double[]> listOfSplitTables = new LinkedList<>();
        fileReading();

        splitTables(listOfSplitTables);

        System.out.println("Лагранж");
        System.out.println(tableLagrangeInterpolation(listOfSplitTables));

        System.out.println("Ньютон");
        System.out.println(tableNewtonInterpolation());

        searchValues(listOfSplitTables);
    }
}
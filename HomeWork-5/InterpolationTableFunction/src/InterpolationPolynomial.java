import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

public class InterpolationPolynomial {

    public static LinkedList<double[][]> listOfTables = new LinkedList<>();

    public static void main(String[] args) {
        LinkedList<double[]> listOfTables2 = new LinkedList<>();
        fileReading();

        splitTables(listOfTables2);

        System.out.println("Лагранж");
        System.out.println(tableLagrangeInterpolation(listOfTables2));

        System.out.println("Ньютон");
        System.out.println(tableNewtonInterpolation());
    }

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

    private static float getXValuesStep(double[][] table) {
        return (float) Math.abs(table[0][1] - table[0][0]);
    }

    // Разделить массивы на два (для x и для y) для удобства вычислений
    private static void splitTables(LinkedList<double[]> listOfTables2) {
        for (double[][] table : listOfTables) {
            for (double[] doubles : table) {
                double[] table1 = new double[6];
                System.arraycopy(doubles, 0, table1, 0, doubles.length);
                listOfTables2.add(table1);
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
}
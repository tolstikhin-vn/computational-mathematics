// Методом Гаусса найдите решение СЛАУ

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

public class GaussMethod {

    public static LinkedList<double[][]> listOfMatrices;

    public static void main(String[] args) {
        listOfMatrices = new LinkedList<>(); // Коллекция из массивов систем

        fileReading();

        // Выполнение метода Гаусса
        for (int i = 0; i < listOfMatrices.size(); ++i) {
            boolean isCompatibleSystem;
            double[][] matrix = listOfMatrices.get(i).clone();
            makeStraightMove(matrix);
            isCompatibleSystem = compatibilityCheck(matrix);

            if (isCompatibleSystem) {
                double[] result = makeReverseMove(matrix);

                // Вывод результатов
                System.out.println("\nСистема №" + (i + 1));
                for (int j = 0; j < result.length; ++j) {
                    System.out.println("x" + (j + 1) + " = " + Math.round(result[j]));
                }
            } else {
                System.out.println("Система несовместна. Решений нет.");
            }
        }
    }

    // Чтение файла, содержащего системы уравнений
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
        double[][] system = new double[coefficients.size()][columns];
        Iterator<String[]> iter = coefficients.iterator();
        for (int i = 0; i < system.length; ++i) {
            String[] s = iter.next();
            for (int j = 0; j < columns; ++j) {
                system[i][j] = Integer.parseInt(s[j]);
            }
        }
        listOfMatrices.add(system);
    }

    // Прямой ход метода Гаусса
    public static void makeStraightMove(double[][] matrix) {
        for (int i = 0; i < matrix.length - 1; ++i) {
            for (int j = i + 1; j < matrix.length; ++j) {
                double buf = -matrix[j][i] / matrix[i][i];
                for (int k = 0; k <= matrix.length; ++k) {
                    matrix[j][k] = matrix[i][k] * buf + matrix[j][k];
                }
            }
        }
    }

    // Проверка на совместность
    public static boolean compatibilityCheck(double[][] matrix) {
        int numOfZeros;
        boolean isCompatibleSystem = true;
        for (int i = 0; i < matrix.length; ++i) {
            numOfZeros = 0;
            for (int j = 0; j < matrix[i].length - 1; ++j) {
                if (matrix[i][j] == 0) {
                    ++numOfZeros;
                }
            }
            if (numOfZeros == matrix[i].length - 1) {
                isCompatibleSystem = false;
            }
        }
        return isCompatibleSystem;
    }

    // Обратный ход метода Гаусса
    public static double[] makeReverseMove(double[][] matrix) {
        double[] resultMatrix = new double[matrix.length];
        int numOfX = matrix.length;
        if (matrix[numOfX - 1][numOfX - 1] != 0) {
            resultMatrix[numOfX - 1] = matrix[numOfX - 1][4] / matrix[numOfX - 1][numOfX - 1];
        }
        for (int i = numOfX - 2; i >= 0; i--) {
            double buf = 0;
            for (int j = i + 1; j < numOfX; j++) {
                buf += matrix[i][j] * resultMatrix[j];
            }
            if (matrix[i][i] != 0) {
                resultMatrix[i] = (matrix[i][numOfX] - buf) / matrix[i][i];
            }
        }
        return resultMatrix;
    }
}
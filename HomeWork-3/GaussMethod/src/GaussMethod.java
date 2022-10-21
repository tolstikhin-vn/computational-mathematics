// Методом Гаусса найдите решение СЛАУ

import java.util.LinkedList;

public class GaussMethod {

    public static void main(String[] args) {

        // Набор массивов, состоящих из коэффициентов систем уравнений при неизвестных
        double[][] system1 = new double[][]{{2, 2, -1, 1, 4}, {4, 3, -1, 2, 6}, {8, 5, -3, 4, 12}, {3, 3, -2, 2, 6}};
        double[][] system2 = new double[][]{{1, 7, -9, -8, -7}, {-3, -18, 23, 28, 5}, {0, -3, 6, -1, 8}, {-1, -1, 1, 18, -29}};
        double[][] system3 = new double[][]{{3, -3, 7, -4, 0}, {-6, 9, -21, 9, 9}, {9, -12, 30, -22, -2}, {6, 0, 6, -31, 37}};
        double[][] system4 = new double[][]{{9, -5, -6, 3, -8}, {1, -7, 1, 0, 38}, {3, -4, 9, 0, 47}, {6, -1, 9, 8, -8}};
        double[][] system5 = new double[][]{{-6, -5, -3, -8, 101}, {5, -1, -5, -4, 51}, {-6, 0, 5, 5, -53}, {-7, -2, 8, 5, -63}};

        LinkedList<double[][]> listOfMatrices = new LinkedList<>(); // Коллекция из массивов систем
        listOfMatrices.add(system1);
        listOfMatrices.add(system2);
        listOfMatrices.add(system3);
        listOfMatrices.add(system4);
        listOfMatrices.add(system5);

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
            }
        }
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
                System.out.println("Система несовместна: решений нет.");
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
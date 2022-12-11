/*Для заданных систем линейных алгебраических уравнений реализуйте методы поиска численных решений методами
минимальных невязок и скорейшего спуска.*/

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.DoubleSummaryStatistics;
import java.util.Iterator;
import java.util.LinkedList;

public class MRAndSDMethods {
    private static final int NORMS_NUMBER = 3;
    private static final String[] LIST_OF_NORMS = new String[]{"||X||∞", "||X||₁", "||X||₂ₗ"};

    private static final int L_VALUE = 2;  // Параметр для нормы ||X||₂ₗ

    public static void main(String[] args) {

        int[] degrees = new int[]{2, 3, 4, 5, 10, 12, 15}; // Степени десятки для точности ε
        LinkedList<double[][]> listOfSourceMatrices = new LinkedList<>(); // Коллекция из массивов систем
        LinkedList<double[][]> listOfSymmetricalMatrices = new LinkedList<>();
        LinkedList<double[]> listOfFreeMembersColumns = new LinkedList<>(); // Коллекция из столбцов свободных членов
        LinkedList<double[]> listOfSolutions = new LinkedList<>(); // Начальные приближения
        listOfSolutions.add(new double[]{1.1, 1.1, -1.1, -1.1});
        listOfSolutions.add(new double[]{-0.8, 0, 0.3, 0.7});
        listOfSolutions.add(new double[]{0.2, 0.02, -0.2, -0.1});
        listOfSolutions.add(new double[]{0.5, 0.7, 1.7, 1.7, 1.8});
        listOfSolutions.add(new double[]{0.9, 1.9, 0.9, 1.9, 0.9, 1.9});
        listOfSolutions.add(new double[]{0.4, 1.4, 0.4, 1.5, 0.2, 1.5});

        fileReading(listOfSourceMatrices, listOfSymmetricalMatrices, listOfFreeMembersColumns);

        removeExtraColumns(listOfFreeMembersColumns);

        matrixSymmetrization(listOfSymmetricalMatrices, listOfSourceMatrices, listOfFreeMembersColumns);

        SpeedyDescent speedyDescent = new SpeedyDescent();
        MinimalResiduals minimalResiduals = new MinimalResiduals();
        for (int i = 0; i < listOfSymmetricalMatrices.size(); ++i) {
            System.out.println("Система № " + (i + 1));
            for (int degree : degrees) {
                for (int j = 0; j < NORMS_NUMBER; ++j) {
                    System.out.println("Норма " + LIST_OF_NORMS[j]);
                    // Вывод результатов
                    System.out.println("Метод минимальных невязок");
                    minimalResiduals.findSolution(listOfSymmetricalMatrices.get(i), listOfFreeMembersColumns.get(i), listOfSolutions.get(i), degree, (j + 1));
                    System.out.println();
                    System.out.println("Метод наискорейшего спуска");
                    speedyDescent.findSolution(listOfSymmetricalMatrices.get(i), listOfFreeMembersColumns.get(i), listOfSolutions.get(i), degree, (j + 1));
                    System.out.println();
                }
            }
            System.out.println();
        }
    }

    // Симметризация матрицы
    private static void matrixSymmetrization(LinkedList<double[][]> listOfSymmetricalMatrices, LinkedList<double[][]> listOfSourceMatrices, LinkedList<double[]> listOfFreeMembersColumns) {
        MinimalResiduals minimalResiduals = new MinimalResiduals();
        double[][] transposedMatrix;
        for (int i = 0; i < listOfSymmetricalMatrices.size(); ++i) {
            if (!isSymmetrical(listOfSymmetricalMatrices.get(i))) {
                transposedMatrix = transposition(listOfSymmetricalMatrices.get(i));
                listOfSymmetricalMatrices.set(i, multiplyMatrices(transposedMatrix, listOfSourceMatrices.get(i)));
                listOfFreeMembersColumns.set(i, minimalResiduals.multiplyMatrices(transposedMatrix, listOfFreeMembersColumns.get(i)));
            }
        }
    }

    // Транспонирование матрицы
    private static double[][] transposition(double[][] currMatrix) {
        for (int i = 0; i < currMatrix.length; ++i) {
            for (int j = i + 1; j < currMatrix[i].length; ++j) {
                double temp = currMatrix[i][j];
                currMatrix[i][j] = currMatrix[j][i];
                currMatrix[j][i] = temp;
            }
        }
        return currMatrix;
    }

    // Проверка матрицы на симметричность
    private static boolean isSymmetrical(double[][] currMatrix) {
        boolean isSymmetrical = true;
        for (int i = 0; i < currMatrix.length; ++i) {
            for (int j = 0; j < currMatrix.length; ++j) {
                if (currMatrix[i][j] != currMatrix[j][i]) {
                    isSymmetrical = false;
                    break;
                }
            }
        }
        return isSymmetrical;
    }

    // Нахождение результата перемножения двух двумерных массивов
    private static double[][] multiplyMatrices(double[][] firstMatrix, double[][] secondMatrix) {
        double[][] result = new double[firstMatrix.length][secondMatrix[0].length];

        for (int row = 0; row < result.length; ++row) {
            for (int col = 0; col < result[row].length; ++col) {
                result[row][col] = multiplyMatricesCell(firstMatrix, secondMatrix, row, col);
            }
        }
        return result;
    }

    private static double multiplyMatricesCell(double[][] firstMatrix, double[][] secondMatrix, int row, int col) {
        double cell = 0;
        for (int i = 0; i < secondMatrix.length; ++i) {
            cell += firstMatrix[row][i] * secondMatrix[i][col];
        }
        return cell;
    }

    // Чтение файла, содержащего системы уравнений
    public static void fileReading(LinkedList<double[][]> listOfSourceMatrices, LinkedList<double[][]> listOfSymmetricalMatrices, LinkedList<double[]> listOfFreeMembersColumns) {
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
                        fillingListOfMatrices(listOfSourceMatrices, coefficients, listOfFreeMembersColumns); // Вставляем исходные матрицы
                        fillingListOfMatrices(listOfSymmetricalMatrices, coefficients, listOfFreeMembersColumns); // Вставляем будущие симметричные матрицы
                        coefficients.clear();
                    }
                }
                // Заполнение последним массивов
                fillingListOfMatrices(listOfSourceMatrices, coefficients, listOfFreeMembersColumns);
                fillingListOfMatrices(listOfSymmetricalMatrices, coefficients, listOfFreeMembersColumns);
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    // Заполнение коллекции с массивами систем
    public static void fillingListOfMatrices(LinkedList<double[][]> listOfMatrices, ArrayList<String[]> coefficients, LinkedList<double[]> listOfFreeMembersColumns) {
        int columns = coefficients.get(0).length;
        double[][] system1 = new double[coefficients.size()][columns - 1];
        double[] system2 = new double[coefficients.size()];
        Iterator<String[]> iter = coefficients.iterator();
        for (int i = 0; i < system1.length; ++i) {
            String[] s = iter.next();
            for (int j = 0; j < columns; ++j) {
                if (j == columns - 1) {
                    system2[i] = Double.parseDouble(s[j]);
                    continue;
                }
                system1[i][j] = Double.parseDouble(s[j]);
            }
        }
        listOfMatrices.add(system1);
        listOfFreeMembersColumns.add(system2);
    }

    // Удаление дублирующихся столбцов свободных членов систем
    private static void removeExtraColumns(LinkedList<double[]> listOfFreeMembers) {
        for (int i = 0; i < listOfFreeMembers.size(); ++i) {
            listOfFreeMembers.remove(i + 1);
        }
    }

    // Поиск нормы исходя из текущей рассчитываемой нормы
    public static double findNorm(int numOfNorm, int size, double[] currentVariableValues, double[] previousVariableValues) {
        double norm = 0;
        switch (numOfNorm) {
            // Для нормы ||X||∞
            case (1) -> {
                double[] values = new double[size];
                for (int i = 0; i < size; ++i) {
                    values[i] = Math.abs(currentVariableValues[i] - previousVariableValues[i]);
                }
                DoubleSummaryStatistics stat = Arrays.stream(values).summaryStatistics();
                return stat.getMax();
            }
            // Для нормы ||X||₁
            case (2) -> {
                for (int i = 0; i < size; ++i) {
                    norm += Math.abs(currentVariableValues[i] - previousVariableValues[i]);
                }
                return norm;
            }
            // Для нормы ||X||₂ₗ
            case (3) -> {
                for (int i = 0; i < size; ++i) {
                    norm += Math.pow(currentVariableValues[i] - previousVariableValues[i], 2 * L_VALUE);
                }
                return Math.pow(norm, 1. / (2 * L_VALUE));
            }
        }
        return 0;
    }
}
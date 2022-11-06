/*Для заданных систем линейных алгебраических уравнений реализуйте метод поиска численного решения методами
простой итерации и методом Зайделя.*/

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.DoubleSummaryStatistics;
import java.util.Iterator;
import java.util.LinkedList;

public class MPIAndSeidel {
    private static final int NORMS_NUMBER = 3;
    private static final String[] LIST_OF_NORMS = new String[]{"||X||∞", "||X||₁", "||X||₂ₗ"};

    private static final int L_VALUE = 2;  // Параметр для нормы ||X||₂ₗ

    public static void main(String[] args) {
        int[] degrees = new int[]{2, 3, 4, 5, 10, 12, 15}; // Степени десятки для точности ε
        LinkedList<double[][]> listOfMatrices = new LinkedList<>(); // Коллекция из массивов систем

        fileReading(listOfMatrices);


        Seidel seidel = new Seidel();
        MPI mpi = new MPI();

        for (int i = 0; i < listOfMatrices.size(); ++i) {
            System.out.println("Система № " + (i + 1));
            if (convergenceConditionChecking(listOfMatrices.get(i))) {
                for (int degree : degrees) {
                    for (int j = 0; j < NORMS_NUMBER; ++j) {
                        System.out.println("Норма " + LIST_OF_NORMS[j]);
                        // Вывод результатов
                        System.out.println("МПИ");
                        mpi.findSolution(listOfMatrices.get(i), degree, (j + 1));
                        System.out.println("Зейдель");
                        seidel.findSolution(listOfMatrices.get(i), degree, (j + 1));
                        System.out.println();
                    }
                }
                System.out.println();
            } else {
                System.out.println("Не выполнено достаточное условие сходимости!");
            }
        }
    }

    // Чтение файла, содержащего системы уравнений
    public static void fileReading(LinkedList<double[][]> listOfMatrices) {
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
                        fillingListOfMatrices(coefficients, listOfMatrices);
                        coefficients.clear();
                    }
                }
                // Заполнение последним массивов
                fillingListOfMatrices(coefficients, listOfMatrices);
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    // Заполнение коллекции с массивами систем
    public static void fillingListOfMatrices(ArrayList<String[]> coefficients, LinkedList<double[][]> listOfMatrices) {
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

    // Проверка достаточного условия сходимости (диагонального преобладания)
    public static boolean convergenceConditionChecking(double[][] initialMatrix) {
        double mainDiagonalElement; // Элемент главной диагонали в строке
        double sumOtherElements; // Сумма элементов, стоящих не на главной диагонали в строке
        for (int i = 0; i < initialMatrix.length; ++i) {
            mainDiagonalElement = 0.0;
            sumOtherElements = 0.0;
            for (int j = 0; j < initialMatrix[i].length - 1; ++j) {
                // Выбираем элемент главной диагонали
                if (i == j) {
                    mainDiagonalElement = Math.abs(initialMatrix[i][j]);
                } else {
                    sumOtherElements += Math.abs(initialMatrix[i][j]);
                }
            }
            if (mainDiagonalElement <= sumOtherElements) {
                return false;
            }
        }
        return true;
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
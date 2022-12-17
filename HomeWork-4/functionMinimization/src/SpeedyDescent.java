import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.DoubleSummaryStatistics;
import java.util.List;

public class SpeedyDescent {

    private static final int NORMS_NUMBER = 3;
    private static final String[] LIST_OF_NORMS = new String[]{"||X||∞", "||X||₁", "||X||₂ₗ"};
    private static final int L_VALUE = 10;  // Параметр для нормы ||X||₂ₗ

    public static void speedyDescent(RealMatrix A, RealVector b, RealVector xo, int n, int numOfNorm) {
        int size = xo.getDimension();

        double[] previousVariableValues = new double[size];

        double norm;
        int iterationsNumber = 0;

        do {
            double[] currentVariableValues = new double[size];
            RealVector xn;

            double alpha_num;
            double alpha_den;

            RealVector g = (A.operate(xo)).subtract(b);

            alpha_num = (g.dotProduct(g));
            alpha_den = (A.operate(g).dotProduct(g));

            if (alpha_den == 0) {
                break;
            }

            double alpha = alpha_num / alpha_den;

            xn = xo.subtract(g.mapMultiply(alpha));

            for (int i = 0; i < size; ++i) {
                currentVariableValues[i] = xo.getEntry(i);
            }

            norm = findNorm(numOfNorm, size, previousVariableValues, currentVariableValues);

            previousVariableValues = currentVariableValues;

            xo = xn;

            ++iterationsNumber;
        } while (norm > Math.pow(10, -n));

        printResults(xo, iterationsNumber, size);
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

    public static void main(String[] args) {

        int[] degrees = new int[]{2, 3, 4, 5, 10, 12, 15}; // Степени десятки для точности ε


        List<RealMatrix> A = new ArrayList<>();
        List<RealVector> b = new ArrayList<>();
        List<RealVector> x0 = new ArrayList<>();

        prepareData(A, b, x0);

        for (int i = 0; i < A.size(); ++i) {
            System.out.println("\nСистема № " + (i + 1));
            for (int degree : degrees) {
                for (int j = 0; j < NORMS_NUMBER; ++j) {
                    System.out.println("\nНорма " + LIST_OF_NORMS[j]);
                    System.out.println("ε = 10^(-" + degree + ")");
                    // Вывод результатов
                    System.out.println("Точка минимума функции:");
                    speedyDescent(A.get(i), b.get(i), x0.get(i), degree, (j + 1));
                }
            }
        }
        System.out.println();
    }

    private static void printResults(RealVector xo, int iterationsNumber, int size) {
        for (int i = 0; i < size; ++i) {
            System.out.print("x" + (i + 1) + " = " + xo.getEntry(i) + " ");
        }
        System.out.println("\nКол-во итераций: " + iterationsNumber);
    }

    private static void prepareData(List<RealMatrix> A, List<RealVector> b, List<RealVector> x0) {
        A.add(MatrixUtils.createRealMatrix(new double[][]{{2, 0}, {0, 2}}));
        b.add(MatrixUtils.createRealVector(new double[]{2, 4}));
        x0.add(MatrixUtils.createRealVector(new double[]{2, 3}));

        A.add(MatrixUtils.createRealMatrix(new double[][]{{4, -2}, {-2, 2}}));
        b.add(MatrixUtils.createRealVector(new double[]{-2, 2}));
        x0.add(MatrixUtils.createRealVector(new double[]{0.5, 0.5}));

        A.add(MatrixUtils.createRealMatrix(new double[][]{{2, -2, 0}, {-2, 4, 0}, {0, 0, 2}}));
        b.add(MatrixUtils.createRealVector(new double[]{1, 0, -2}));
        x0.add(MatrixUtils.createRealVector(new double[]{2, 1, -1}));

        A.add(MatrixUtils.createRealMatrix(new double[][]{{2, -1, 0}, {-1, 2, 0}, {0, 0, 2}}));
        b.add(MatrixUtils.createRealVector(new double[]{-1, 0, 2}));
        x0.add(MatrixUtils.createRealVector(new double[]{-1, -1, 0}));

        A.add(MatrixUtils.createRealMatrix(new double[][]{
                {4, -1, 0, -1, 0, 0},
                {-1, 4, -1, 0, -1, 0},
                {0, -1, 4, 0, 0, -1},
                {-1, 0, 0, 4, -1, 0},
                {0, -1, 0, -1, 4, -1},
                {0, 0, -1, 0, -1, 4}
        }));
        b.add(MatrixUtils.createRealVector(new double[]{0, 5, 0, 6, -2, 6}));
        x0.add(MatrixUtils.createRealVector(new double[]{0.9, 1.9, 0.9, 1.9, 0.9, 1.9}));

        A.add(MatrixUtils.createRealMatrix(new double[][]{{2, -0.2}, {-0.2, 2}}));
        b.add(MatrixUtils.createRealVector(new double[]{2.2, -2.2}));
        x0.add(MatrixUtils.createRealVector(new double[]{0.5, -0.5}));

        A.add(MatrixUtils.createRealMatrix(new double[][]{{10, -9}, {-9, 8.15}}));
        b.add(MatrixUtils.createRealVector(new double[]{-1, 0}));
        x0.add(MatrixUtils.createRealVector(new double[]{16, -19}));

//        A.add(MatrixUtils.createRealMatrix(new double[][]{{400, 20, 0, -1200}, {20, 4 -96, 0}, {0, -96, 64, -10}}));
//        b.add(MatrixUtils.createRealVector(new double[]{532400, 0, 20, -20}));
//        x0.add(MatrixUtils.createRealVector(new double[]{-166, 89, 36, -499}));
    }
}
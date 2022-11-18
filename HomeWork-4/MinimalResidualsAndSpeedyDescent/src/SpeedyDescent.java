import java.util.Arrays;
public class SpeedyDescent {

    public void findSolution(double[][] initialMatrix, double[] freeMembers, int n, int numOfNorm) {

        String epsilon = "10^(-" + n + ")";
        double[] currentXValues = new double[initialMatrix.length];  // Текущие приближения
        double[] prevXValues; // Предыдущие приближения
        double[] rValues; // Вектор невязки
        double paramT; // Длина шага вдоль направления градиента
        int iterationsNumber = 0; // Количество итераций
        double norm; // Норма
        int size = initialMatrix.length; // Размерность исходной матрицы
        boolean convergenceConditionIsSatisfied = true;

        do {
            // Находим вектор невязок на текущем шаге
            rValues = subtracting(multiplyMatrices(initialMatrix, currentXValues), freeMembers);

            // Определяем длину шага вдоль направления градиента
            paramT = scalarProduct(rValues, rValues) / scalarProduct(multiplyMatrices(initialMatrix, rValues), rValues);

            if (paramT == 0 || iterationsNumber > 200) {
                convergenceConditionIsSatisfied = false;
                break;
            }

            // Инициализируем прерыдущие приближения текущими (до изменений)
            prevXValues = currentXValues;

            // Находим новое приближение на текущем шаге
            currentXValues = subtracting(currentXValues, findResidualVector(paramT, rValues));

            // Считаем норму
            norm = MRAndSDMethods.findNorm(numOfNorm, size, currentXValues, prevXValues);

            ++iterationsNumber;

        } while (norm > Math.pow(10, -n));

        // Вывод результатов вычислений
        if (convergenceConditionIsSatisfied) {
            System.out.println(printResult(currentXValues, epsilon, iterationsNumber));
        } else {
            System.out.println("Метод не сходится");
        }
    }

    // Вычисление разности двух матриц
    private double[] subtracting(double[] first, double[] second) {
        double[] result = new double[first.length];
        for (int i = 0; i < first.length; ++i) {
            result[i] = first[i] - second[i];
        }
        return result;
    }

    // Нахождение скалярного произведения
    private double scalarProduct(double[] first, double[] second) {
        double result = 0;
        for (int i = 0; i < first.length; ++i) {
            result += first[i] * second[i];
        }
        return result;
    }

    // Вычисление вектора невязки путем умножения матрицы на вектор
    private double[] findResidualVector(double param, double[] actual) {
        double[] residualVector = new double[actual.length];
        for (int i = 0; i < actual.length; ++i) {
            residualVector[i] = param * actual[i];
        }
        return residualVector;
    }

    // Нахождение результата перемножения двух матрицы
    private double[] multiplyMatrices(double[][] firstMatrix, double[] secondMatrix) {
        double[] result = new double[firstMatrix.length];

        for (int row = 0; row < result.length; row++) {
            result[row] = multiplyMatricesCell(firstMatrix, secondMatrix, row);
        }

        return result;
    }

    double multiplyMatricesCell(double[][] firstMatrix, double[] secondMatrix, int row) {
        double cell = 0;
        for (int i = 0; i < secondMatrix.length; i++) {
            cell += firstMatrix[row][i] * secondMatrix[i];
        }
        return cell;
    }

    // Вывод полученных результатов
    public String printResult(double[] previousVariableValues, String epsilon, int iterationsNumber) {
        return Arrays.toString(previousVariableValues) + " ε = " + epsilon + " итераций: " + iterationsNumber;
    }
}

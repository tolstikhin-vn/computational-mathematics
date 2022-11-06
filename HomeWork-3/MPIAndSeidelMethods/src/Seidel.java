import java.util.Arrays;

public class Seidel {
    public void findSolution(double[][] matrix, int n, int numOfNorm) {
        String epsilon = "10^(-" + n + ")";
        int size = matrix.length;
        double[] previousVariableValues = new double[size];
        int iterationsNumber = 0;
        double norm;
        // Будем выполнять итерационный процесс до тех пор, пока не будет достигнута необходимая точность
        do {
            // Введем вектор значений неизвестных на текущем шаге
            double[] currentVariableValues = new double[size];

            // Посчитаем значения неизвестных на текущей итерации
            for (int i = 0; i < size; ++i) {
                // Инициализируем i-ую неизвестную значением свободного члена i-ой строки матрицы
                currentVariableValues[i] = matrix[i][size];

                // Вычитаем сумму по всем отличным от i-ой неизвестным
                for (int j = 0; j < size; ++j) {
                    // При j < i можем использовать уже посчитанные на этой итерации значения неизвестных
                    if (j < i) {
                        currentVariableValues[i] -= matrix[i][j] * currentVariableValues[j];
                    }
                    // При j > i используем значения с прошлой итерации
                    if (j > i) {
                        currentVariableValues[i] -= matrix[i][j] * previousVariableValues[j];
                    }
                }
                // Делим на коэффициент при i-ой неизвестной
                currentVariableValues[i] /= matrix[i][i];
            }
            norm = MPIAndSeidel.findNorm(numOfNorm, size, currentVariableValues, previousVariableValues);
            // Переходим к следующей итерации, так что текущие значения неизвестных становятся значениями на предыдущей итерации
            previousVariableValues = currentVariableValues;
            ++iterationsNumber;
        } while (norm > Math.pow(10, -n));
        System.out.println(printResult(previousVariableValues, epsilon, iterationsNumber));
    }

    // Вывод полученных результатов
    public String printResult(double[] previousVariableValues, String epsilon, int iterationsNumber) {
        return Arrays.toString(previousVariableValues) + " ε = " + epsilon + " итераций: " + iterationsNumber;
    }
}
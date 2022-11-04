import java.util.Arrays;

public class Seidel {

    double[] previousVariableValues;
    int iterationsNumber;
    String epsilon;

    public void findSolution(double[][] matrix, int n) {
        epsilon = "10^(-" + n + ")";
        int size = matrix.length;
        previousVariableValues = new double[size];
        iterationsNumber = 0;
        double d;
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
            d = 0.0;
            for (int i = 0; i < size; ++i) {
                d += Math.abs(currentVariableValues[i] - previousVariableValues[i]);
            }
            // Переходим к следующей итерации, так что текущие значения неизвестных становятся значениями на предыдущей итерации
            previousVariableValues = currentVariableValues;
            ++iterationsNumber;
        } while (d > Math.pow(10, -n));
        System.out.println(this);
    }

    @Override
    public String toString() {
        return Arrays.toString(previousVariableValues) + " ε = " + epsilon + " итераций: " + iterationsNumber;
    }
}
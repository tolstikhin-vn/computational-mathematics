import java.util.Arrays;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

public class SpeedyDescent {

    public void findSolution(RealMatrix initialMatrix, RealVector freeMembers, RealVector solution, int n, int numOfNorm) {

        String epsilon = "10^(-" + n + ")";
        RealVector prevXValues; // Предыдущие приближения
        RealVector rValues; // Вектор невязки
        double paramT; // Длина шага вдоль направления градиента
        int iterationsNumber = 0; // Количество итераций
        double norm; // Норма
        int size = initialMatrix.getRowDimension(); // Размерность исходной матрицы
        RealVector currentXValues = solution;  // Текущие приближения

        do {
            // Находим вектор невязок на текущем шаге
            rValues = (initialMatrix.operate(currentXValues)).subtract(freeMembers);

            // Определяем длину шага вдоль направления градиента
            paramT = rValues.dotProduct(rValues) / (initialMatrix.operate(rValues)).dotProduct(rValues);

            // Инициализируем предыдущие приближения текущими (до изменений)
            prevXValues = currentXValues;

            // Находим новое приближение на текущем шаге
            currentXValues = currentXValues.subtract(rValues.mapMultiply(paramT));

            // Считаем норму
            norm = MRAndSDMethods.findNorm(numOfNorm, size, currentXValues, prevXValues);

            ++iterationsNumber;

        } while (norm > Math.pow(10, -n));

        // Вывод результатов вычислений
        System.out.println(printResult(currentXValues, epsilon, iterationsNumber));
    }

    // Вывод полученных результатов
    private String printResult(RealVector previousVariableValues, String epsilon, int iterationsNumber) {
        return Arrays.toString(previousVariableValues.toArray()) + " ε = " + epsilon + " итераций: " + iterationsNumber;
    }
}
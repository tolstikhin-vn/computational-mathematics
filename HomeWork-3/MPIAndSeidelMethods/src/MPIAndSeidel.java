/*Для заданных систем линейных алгебраических уравнений реализуйте метод поиска численного решения методами
простой итерации и методом Зайделя.*/

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

public class MPIAndSeidel {

    public static LinkedList<double[][]> listOfMatrices;

    public static void main(String[] args) {
        int[] degrees = new int[]{2, 3, 4, 5, 10, 12, 15}; // Степени десятки для точности ε
        listOfMatrices = new LinkedList<>(); // Коллекция из массивов систем

        fileReading();

        Seidel seidel = new Seidel();
        MPI mpi = new MPI();

        for (int i = 0; i < listOfMatrices.size(); ++i) {
            System.out.println("Система № " + (i + 1));
            for (int degree : degrees) {
                // Вывод результатов
                System.out.println("Зейдель");
                seidel.findSolution(listOfMatrices.get(i), degree);
                System.out.println("МПИ");
                mpi.findSolution(listOfMatrices.get(i), degree);
                System.out.println();
            }
            System.out.println();
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
                // Заполнение последним массивов
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
}
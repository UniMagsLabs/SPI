import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main {
    private static int[][] distances;
    private static int n; // Количество городов
    private static double[][] pheromones; // Матрица феромонов
    private static final double ALPHA = 1.0; // Влияние феромонов
    private static final double BETA = 5.0; // Влияние расстояний
    private static final double EVAPORATION_RATE = 0.5; // Скорость испарения феромонов
    private static final double Q = 100; // Количество феромонов, производимых
    private static Random random;
    private static ArrayList<Integer> bestPath;
    private static double bestPathLength;

    public static void main(String[] args) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("results.txt"))) {

            for (n = 5; n < 150; n++) {

                distances = generateDistanceMatrix(n);
                random = new Random();
                pheromones = new double[n][n];

                System.out.println("N = " + n);
                long startTime = System.nanoTime();

                // Инициализация феромонов
                for (int i = 0; i < n; i++) {
                    for (int j = 0; j < n; j++) {
                        pheromones[i][j] = 1.0;
                    }
                }

                runAlgorithm();

                long endTime = System.nanoTime();
                long time = (endTime - startTime) / 1_000; // в микросекундах

                // Консоль
                System.out.println("Лучший путь: " + bestPath);
                System.out.println("Минимальная стоимость: " + bestPathLength);
                System.out.println("Время выполнения: " + time + " мкс - " + n);

                // Запись в файл
                writer.write(n + " " + time);
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void runAlgorithm() {
        int iterations = 100; // Количество итераций
        bestPathLength = Double.MAX_VALUE;

        for (int iteration = 0; iteration < iterations; iteration++) {
            List<Integer> currentPath = new ArrayList<>();
            double currentLength = 0;

            // Генерация пути муравья
            int startCity = random.nextInt(n);
            currentPath.add(startCity);
            boolean[] visited = new boolean[n];
            visited[startCity] = true;

            for (int step = 1; step < n; step++) {
                int nextCity = selectNextCity(currentPath.get(step - 1), visited);
                currentPath.add(nextCity);
                visited[nextCity] = true;
                currentLength += distances[currentPath.get(step - 1)][nextCity];
            }

            // Добавляем расстояние до начального города для завершения пути
            currentLength += distances[currentPath.get(currentPath.size() - 1)][startCity];

            // Если найден лучший путь, обновляем его
            if (currentLength < bestPathLength) {
                bestPathLength = currentLength;
                bestPath = new ArrayList<>(currentPath);
                bestPath.add(startCity); // Возвращаемся в начальную точку
            }

            // Обновляем феромоны на основании текущего пути
            updatePheromones(currentPath, currentLength);
            evaporatePheromones();
        }
    }

    private static int selectNextCity(int currentCity, boolean[] visited) {
        double total = 0.0;
        double[] probabilities = new double[n];

        for (int city = 0; city < n; city++) {
            if (!visited[city]) {
                probabilities[city] = Math.pow(pheromones[currentCity][city], ALPHA) *
                        Math.pow(1.0 / distances[currentCity][city], BETA);
                total += probabilities[city];
            }
        }

        double rand = random.nextDouble() * total;
        total = 0.0;

        for (int city = 0; city < n; city++) {
            if (!visited[city]) {
                total += probabilities[city];
                if (total >= rand) {
                    return city;
                }
            }
        }

        return -1; // Это не должно происходить
    }

    private static void updatePheromones(List<Integer> path, double length) {
        double pheromoneAmount = Q / length;
        for (int i = 0; i < path.size() - 1; i++) {
            int cityFrom = path.get(i);
            int cityTo = path.get(i + 1);
            pheromones[cityFrom][cityTo] += pheromoneAmount;
            pheromones[cityTo][cityFrom] += pheromoneAmount; // Симметричный граф
        }
    }

    private static void evaporatePheromones() {
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                pheromones[i][j] *= (1 - EVAPORATION_RATE);
            }
        }
    }

    private static int[][] generateDistanceMatrix(int size) {
        Random rand = new Random();
        int[][] matrix = new int[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = i + 1; j < size; j++) {
                matrix[i][j] = matrix[j][i] = rand.nextInt(90) + 10; // Расстояния от 10 до 99
            }
        }
        return matrix;
    }
}
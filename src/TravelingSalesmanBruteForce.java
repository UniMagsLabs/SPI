import java.util.*;

public class TravelingSalesmanBruteForce {
    private static int[][] distances;
    private static int n;
    private static int minCost = Integer.MAX_VALUE;
    private static List<Integer> bestPath;

    public static void main(String[] args) {
        for (n = 6; n < 18; n++) {
            System.out.println("N = " + n);
            distances = generateDistanceMatrix(n);
            long startTime = System.nanoTime();
            minCost = Integer.MAX_VALUE;
            bestPath = null;
            solveTSP();
            long endTime = System.nanoTime();

            System.out.println("Минимальный путь: " + bestPath);
            System.out.println("Минимальная стоимость: " + minCost);
            System.out.println("Время выполнения: " + (endTime - startTime) / 1_000_000 + " мс");
        }
    }

    private static void solveTSP() {
        List<Integer> cities = new ArrayList<>();
        for (int i = 1; i < n; i++) cities.add(i);

        bestPath = new ArrayList<>();
        permute(cities, new ArrayList<>(), 0);
    }

    private static void permute(List<Integer> cities, List<Integer> currentPath, int currentCost) {
        if (cities.isEmpty()) {
            int fullCost = currentCost + distances[currentPath.getLast()][0];
            if (fullCost < minCost) {
                minCost = fullCost;
                bestPath = new ArrayList<>(currentPath);
                bestPath.addFirst(0); // Добавляем начальный город
                bestPath.add(0); // Возвращаемся в начальный город
            }
            return;
        }

        for (int i = 0; i < cities.size(); i++) {
            int nextCity = cities.get(i);
            int newCost = currentCost + (currentPath.isEmpty() ? distances[0][nextCity] : distances[currentPath.get(currentPath.size() - 1)][nextCity]);

            if (newCost >= minCost) continue; // Обрезка бесперспективных ветвей

            List<Integer> nextCities = new ArrayList<>(cities);
            nextCities.remove(i);
            List<Integer> newPath = new ArrayList<>(currentPath);
            newPath.add(nextCity);
            permute(nextCities, newPath, newCost);
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
package amod.heuristic;

public class NearestNeighbor {

    public static int[] findTour(double[][] distances) {

        int n = distances.length;

        int[] tour = new int[n + 1];

        boolean[] visited = new boolean[n];

        int currentCity = 0;

        tour[0] = currentCity;
        visited[currentCity] = true;

        for (int i = 1; i < n; i++) {

            int nearestCity = -1;
            double minDistance = Double.MAX_VALUE;

            for (int j = 0; j < n; j++) {

                if (!visited[j] && distances[currentCity][j] < minDistance) {

                    minDistance = distances[currentCity][j];
                    nearestCity = j;

                }
            }

            currentCity = nearestCity;
            tour[i] = currentCity;
            visited[currentCity] = true;
        }

        tour[n] = tour[0];

        return tour;
    }

    public static int[] findTour(double[][] distances, int startCity) {

        int n = distances.length;

        if (startCity < 0 || startCity >= n) {
            throw new IllegalArgumentException("Città di partenza non valida");
        }

        int[] tour = new int[n + 1];
        boolean[] visited = new boolean[n];

        int currentCity = startCity;

        tour[0] = currentCity;
        visited[currentCity] = true;

        for (int i = 1; i < n; i++) {

            int nearestCity = -1;
            double minDistance = Double.MAX_VALUE;

            for (int j = 0; j < n; j++) {

                if (!visited[j] && distances[currentCity][j] < minDistance) {

                    minDistance = distances[currentCity][j];
                    nearestCity = j;

                }
            }

            currentCity = nearestCity;
            tour[i] = currentCity;
            visited[currentCity] = true;
        }

        tour[n] = tour[0];

        return tour;

    }



    public static double calculateCost(int[] tour, double[][] distances) {

        double cost = 0;

        for (int i = 0; i < tour.length - 1; i++) {
            cost += distances[tour[i]][tour[i + 1]];
        }

        return cost;
    }

}

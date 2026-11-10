package amod;

public class DistanceMatrix {

    public static double[][] calculate(double[][] coordinates) {

        int n = coordinates.length;
        double[][] distances = new double[n][n];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {

                double dx = coordinates[i][0] - coordinates[j][0];
                double dy = coordinates[i][1] - coordinates[j][1];

                distances[i][j] = Math.floor(Math.sqrt(dx * dx + dy * dy) + 0.5);//In questo modo rispettiamo la regola di arrotondamento EUC_2D di TSPLIB.
            }
        }

        return distances;
    }
}
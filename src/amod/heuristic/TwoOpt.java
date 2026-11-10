
package amod.heuristic;

public class TwoOpt {

    // INVERSIONE DI UN SEGMENTO
    public static void reverseSegment(int[] tour, int i, int j) {

        while (i < j) {

            int temp = tour[i];
            tour[i] = tour[j];
            tour[j] = temp;

            i++;
            j--;
        }
    }


    // ALGORITMO 2-OPT
    public static int[] twoOpt(int[] tour, double[][] distances) {

        boolean improved = true;

        while (improved) {

            improved = false;

            for (int i = 0; i < tour.length - 2; i++) {

                for (int j = i + 2; j < tour.length - 1; j++) {

                    double delta =
                            distances[tour[i]][tour[j]]
                                    + distances[tour[i + 1]][tour[j + 1]]
                                    - distances[tour[i]][tour[i + 1]]
                                    - distances[tour[j]][tour[j + 1]];

                    if (delta < 0) {

                        reverseSegment(tour, i + 1, j);

                        improved = true;
                    }
                }
            }
        }

        return tour;
    }
}

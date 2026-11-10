
package amod.results;

public class DfjResults {

    private static final SolverResult[] RESULTS = {

            new SolverResult(
                    "eil51", 51,
                    426, 426, 0,
                    0.063006, 10, 10
            ),

            new SolverResult(
                    "st70", 70,
                    675, 675, 0,
                    0.132885, 10, 10
            ),

            new SolverResult(
                    "kroA100", 100,
                    21282, 21282, 0,
                    1.154330, 10, 10
            ),

            new SolverResult(
                    "ch150", 150,
                    6528, 6528, 0,
                    5.488525, 10, 10
            ),

            new SolverResult(
                    "d198", 198,
                    15780, 15779, 0.006337,
                    5.100506, 0, 10
            )
    };

    public static SolverResult[] getResults() {
        return RESULTS.clone();
    }
}

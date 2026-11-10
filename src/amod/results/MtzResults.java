
package amod.results;

public class MtzResults {

    private static final SolverResult[] RESULTS = {

            new SolverResult(
                    "eil51", 51,
                    426, 426, 0,
                    0.569699, 10, 10
            ),

            new SolverResult(
                    "st70", 70,
                    675, 675, 0,
                    284.739833, 10, 10
            ),

            new SolverResult(
                    "kroA100", 100,
                    21282, 21280, 0.009398,
                    255.180257, 0, 10
            ),

            new SolverResult(
                    "ch150", 150,
                    6554, 6411, 2.181874,
                    300.697635, 0, 10
            ),

            new SolverResult(
                    "d198", 198,
                    16342, 12234, 25.137682,
                    300.232270, 0, 10
            )
    };

    public static SolverResult[] getResults() {
        return RESULTS.clone();
    }
}

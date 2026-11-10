
package amod;

import amod.results.*;

public class TspComparison {

    public static void main(String[] args) {

        // Recuperiamo i risultati dei tre approcci.
        SolverResult[] dfjResults = DfjResults.getResults();
        SolverResult[] mtzResults = MtzResults.getResults();
        HeuristicResult[] heuristicResults = HeuristicResults.getResults();

        printComparison(dfjResults, mtzResults, heuristicResults);
    }

    private static void printComparison(
            SolverResult[] dfjResults,
            SolverResult[] mtzResults,
            HeuristicResult[] heuristicResults) {

        System.out.println("==============================================================");
        System.out.println("              CONFRONTO QUALITA' DELLE SOLUZIONI");
        System.out.println("==============================================================");

        System.out.printf(
                "%-10s %-6s %-12s %-12s %-12s %-12s%n",
                "Istanza", "N", "Ottimo", "NN+2opt", "DFJ", "MTZ"
        );

        System.out.println("--------------------------------------------------------------------------");

        for (int i = 0; i < heuristicResults.length; i++) {

            HeuristicResult heuristic = heuristicResults[i];
            SolverResult dfj = dfjResults[i];
            SolverResult mtz = mtzResults[i];

            System.out.printf(
                    "%-10s %-6d %-12.0f %-12.0f %-12.0f %-12.2f%n",
                    heuristic.getInstance(),
                    heuristic.getCities(),
                    heuristic.getKnownOptimum(),
                    heuristic.getCostTwoOpt(),
                    dfj.getAverageObjective(),
                    mtz.getAverageObjective()
            );
        }

        printGapComparison(heuristicResults, dfjResults, mtzResults);

        printSolverBoundsComparison(dfjResults, mtzResults);

        printTimeComparison(heuristicResults, dfjResults, mtzResults);

        printOptimalityComparison(dfjResults, mtzResults);
    }

    private static void printGapComparison(
            HeuristicResult[] heuristicResults,
            SolverResult[] dfjResults,
            SolverResult[] mtzResults) {

        System.out.println();
        System.out.println("==============================================================");
        System.out.println("             GAP RISPETTO ALL'OTTIMO NOTO (%)");
        System.out.println("==============================================================");

        System.out.printf(
                "%-10s %-12s %-12s %-12s%n",
                "Istanza", "NN+2opt", "DFJ", "MTZ"
        );

        System.out.println("--------------------------------------------------------------");

        for (int i = 0; i < heuristicResults.length; i++) {

            HeuristicResult heuristic = heuristicResults[i];
            SolverResult dfj = dfjResults[i];
            SolverResult mtz = mtzResults[i];

            double dfjGap =
                    calculateKnownOptimumGap(
                            dfj.getAverageObjective(),
                            heuristic.getKnownOptimum()
                    );

            double mtzGap =
                    calculateKnownOptimumGap(
                            mtz.getAverageObjective(),
                            heuristic.getKnownOptimum()
                    );

            System.out.printf(
                    "%-10s %-12.2f %-12.2f %-12.2f%n",
                    heuristic.getInstance(),
                    heuristic.getGap(),
                    dfjGap,
                    mtzGap
            );
        }
    }

    private static double calculateKnownOptimumGap(
            double objective,
            double knownOptimum) {

        return ((objective - knownOptimum) / knownOptimum) * 100.0;
    }

    private static void printTimeComparison(
            HeuristicResult[] heuristicResults,
            SolverResult[] dfjResults,
            SolverResult[] mtzResults) {

        System.out.println();
        System.out.println("==============================================================");
        System.out.println("              CONFRONTO TEMPI DI ESECUZIONE");
        System.out.println("==============================================================");

        System.out.printf(
                "%-10s %-15s %-15s %-15s%n",
                "Istanza", "NN+2opt (ms)", "DFJ (ms)", "MTZ (ms)"
        );

        System.out.println("--------------------------------------------------------------");

        for (int i = 0; i < heuristicResults.length; i++) {

            HeuristicResult heuristic = heuristicResults[i];
            SolverResult dfj = dfjResults[i];
            SolverResult mtz = mtzResults[i];

            System.out.printf(
                    "%-10s %-15.6f %-15.3f %-15.3f%n",
                    heuristic.getInstance(),
                    heuristic.getAverageTotal(),
                    dfj.getAverageTime() * 1000,
                    mtz.getAverageTime() * 1000
            );
        }
    }

    private static void printOptimalityComparison(
            SolverResult[] dfjResults,
            SolverResult[] mtzResults) {

        System.out.println();
        System.out.println("==============================================================");
        System.out.println("             OTTIMALITA' DIMOSTRATA");
        System.out.println("==============================================================");

        for (int i = 0; i < dfjResults.length; i++) {

            SolverResult dfj = dfjResults[i];
            SolverResult mtz = mtzResults[i];

            System.out.printf(
                    "%-10s DFJ: %d/%d | MTZ: %d/%d%n",
                    dfj.getInstance(),
                    dfj.getOptimalRuns(),
                    dfj.getTotalRuns(),
                    mtz.getOptimalRuns(),
                    mtz.getTotalRuns()
            );
        }
    }

    private static void printSolverBoundsComparison(
            SolverResult[] dfjResults,
            SolverResult[] mtzResults) {

        System.out.println();
        System.out.println("==============================================================");
        System.out.println("          LOWER BOUND E MIP GAP DEI SOLVER");
        System.out.println("==============================================================");

        System.out.printf(
                "%-10s %-14s %-12s %-14s %-12s%n",
                "Istanza", "DFJ LB", "DFJ Gap %",
                "MTZ LB", "MTZ Gap %"
        );

        System.out.println("--------------------------------------------------------------");

        for (int i = 0; i < dfjResults.length; i++) {

            SolverResult dfj = dfjResults[i];
            SolverResult mtz = mtzResults[i];

            System.out.printf(
                    "%-10s %-14.2f %-12.6f %-14.2f %-12.6f%n",
                    dfj.getInstance(),
                    dfj.getAverageLowerBound(),
                    dfj.getAverageGap(),
                    mtz.getAverageLowerBound(),
                    mtz.getAverageGap()
            );
        }

        System.out.println();
        System.out.println("I valori rappresentano le medie delle 10 esecuzioni.");
        System.out.println("Il MIP Gap misura la distanza tra incumbent e lower bound.");
    }
}


package amod.results;

public class SolverResult {

    //Questa classe è un modello dati: non esegue Gurobi, non risolve il TSP e non calcola i risultati.
    //Serve semplicemente a rappresentare in modo uniforme i risultati di una singola istanza.
    private final String instance;
    private final int cities;
    private final double averageObjective;
    private final double averageLowerBound;
    private final double averageGap;
    private final double averageTime;
    private final int optimalRuns;
    private final int totalRuns;

    public SolverResult(
            String instance,
            int cities,
            double averageObjective,
            double averageLowerBound,
            double averageGap,
            double averageTime,
            int optimalRuns,
            int totalRuns) {

        this.instance = instance;
        this.cities = cities;
        this.averageObjective = averageObjective;
        this.averageLowerBound = averageLowerBound;
        this.averageGap = averageGap;
        this.averageTime = averageTime;
        this.optimalRuns = optimalRuns;
        this.totalRuns = totalRuns;
    }

    public String getInstance() {
        return instance;
    }

    public int getCities() {
        return cities;
    }

    public double getAverageObjective() {
        return averageObjective;
    }

    public double getAverageLowerBound() {
        return averageLowerBound;
    }

    public double getAverageGap() {
        return averageGap;
    }

    public double getAverageTime() {
        return averageTime;
    }

    public int getOptimalRuns() {
        return optimalRuns;
    }

    public int getTotalRuns() {
        return totalRuns;
    }
}

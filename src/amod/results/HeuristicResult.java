
package amod.results;

public class HeuristicResult {

    private final String instance;
    private final int cities;
    private final double knownOptimum;

    private final double costNN;
    private final double costTwoOpt;

    private final double improvement;
    private final double gap;

    private final double averageNN;
    private final double medianNN;

    private final double averageTwoOpt;
    private final double medianTwoOpt;

    private final double averageTotal;
    private final double medianTotal;

    public HeuristicResult(
            String instance,
            int cities,
            double knownOptimum,
            double costNN,
            double costTwoOpt,
            double improvement,
            double gap,
            double averageNN,
            double medianNN,
            double averageTwoOpt,
            double medianTwoOpt,
            double averageTotal,
            double medianTotal) {

        this.instance = instance;
        this.cities = cities;
        this.knownOptimum = knownOptimum;
        this.costNN = costNN;
        this.costTwoOpt = costTwoOpt;
        this.improvement = improvement;
        this.gap = gap;
        this.averageNN = averageNN;
        this.medianNN = medianNN;
        this.averageTwoOpt = averageTwoOpt;
        this.medianTwoOpt = medianTwoOpt;
        this.averageTotal = averageTotal;
        this.medianTotal = medianTotal;
    }

    public String getInstance() {
        return instance;
    }

    public int getCities() {
        return cities;
    }

    public double getKnownOptimum() {
        return knownOptimum;
    }

    public double getCostNN() {
        return costNN;
    }

    public double getCostTwoOpt() {
        return costTwoOpt;
    }

    public double getImprovement() {
        return improvement;
    }

    public double getGap() {
        return gap;
    }

    public double getAverageNN() {
        return averageNN;
    }

    public double getMedianNN() {
        return medianNN;
    }

    public double getAverageTwoOpt() {
        return averageTwoOpt;
    }

    public double getMedianTwoOpt() {
        return medianTwoOpt;
    }

    public double getAverageTotal() {
        return averageTotal;
    }

    public double getMedianTotal() {
        return medianTotal;
    }
}


package amod.heuristic;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Locale;

public class ExperimentRunner {

    private static final int WARMUP_RUNS = 1000;
    private static final int MEASURED_RUNS = 5000;
    private static final int BATCH_SIZE = 100;

    private static final int NUMBER_OF_BATCHES =
            MEASURED_RUNS / BATCH_SIZE;

    public static void runExperiment(
            String instanceName,
            double[][] distances,
            double knownOptimum) throws IOException {

        int n = distances.length;

        // 1. WARM-UP
        for (int i = 0; i < WARMUP_RUNS; i++) {

            int[] tour = NearestNeighbor.findTour(distances);

            int[] tourCopy = tour.clone();

            TwoOpt.twoOpt(tourCopy, distances);
        }

        // 2. ARRAY PER MEMORIZZARE I TEMPI DEI BATCH
        double[] batchTimesNN =
                new double[NUMBER_OF_BATCHES];

        double[] batchTimesTwoOpt =
                new double[NUMBER_OF_BATCHES];

        double[] batchTimesTotal =
                new double[NUMBER_OF_BATCHES];

        int[] finalTour = null;
        int[] improvedTour = null;

        // 3. ESECUZIONI MISURATE
        for (int batch = 0;
             batch < NUMBER_OF_BATCHES;
             batch++) {

            int[][] toursForBatch =
                    new int[BATCH_SIZE][];

            // A. MISURAZIONE NEAREST NEIGHBOR
            long startNN = System.nanoTime();

            for (int run = 0; run < BATCH_SIZE; run++) {

                int[] tour =
                        NearestNeighbor.findTour(distances);

                toursForBatch[run] = tour;

                if (batch == 0 && run == 0) {
                    finalTour = tour;
                }
            }

            long endNN = System.nanoTime();

            batchTimesNN[batch] =
                    (endNN - startNN) / (double) BATCH_SIZE;


            // B. PREPARAZIONE DEI TOUR PER 2-OPT
            // La copia viene effettuata FUORI dal cronometro.

            int[][] toursForTwoOpt =
                    new int[BATCH_SIZE][];

            for (int run = 0; run < BATCH_SIZE; run++) {

                toursForTwoOpt[run] =
                        toursForBatch[run].clone();
            }


            // C. MISURAZIONE 2-OPT
            long startTwoOpt = System.nanoTime();

            for (int run = 0; run < BATCH_SIZE; run++) {

                int[] candidateTour =
                        TwoOpt.twoOpt(
                                toursForTwoOpt[run],
                                distances
                        );

                if (batch == 0 && run == 0) {
                    improvedTour = candidateTour;
                }
            }

            long endTwoOpt = System.nanoTime();

            batchTimesTwoOpt[batch] =
                    (endTwoOpt - startTwoOpt)
                            / (double) BATCH_SIZE;


            // D. TEMPO TOTALE DEL BATCH
            batchTimesTotal[batch] =
                    batchTimesNN[batch]
                            + batchTimesTwoOpt[batch];
        }

        // 4. CALCOLO MEDIE E MEDIANE

        double averageNN =
                calculateAverage(batchTimesNN);

        double medianNN =
                calculateMedian(batchTimesNN);

        double averageTwoOpt =
                calculateAverage(batchTimesTwoOpt);

        double medianTwoOpt =
                calculateMedian(batchTimesTwoOpt);

        double averageTotal =
                calculateAverage(batchTimesTotal);

        double medianTotal =
                calculateMedian(batchTimesTotal);


        // Conversione da nanosecondi a millisecondi

        averageNN /= 1_000_000.0;
        medianNN /= 1_000_000.0;

        averageTwoOpt /= 1_000_000.0;
        medianTwoOpt /= 1_000_000.0;

        averageTotal /= 1_000_000.0;
        medianTotal /= 1_000_000.0;


        // 5. CALCOLO DEI COSTI

        double costNN =
                NearestNeighbor.calculateCost(
                        finalTour, distances
                );

        double costTwoOpt =
                NearestNeighbor.calculateCost(
                        improvedTour, distances
                );


        // 6. MIGLIORAMENTO E GAP

        double improvement =
                ((costNN - costTwoOpt) / costNN) * 100;

        double gap =
                ((costTwoOpt - knownOptimum)
                        / knownOptimum) * 100;


        // 7. STAMPA RISULTATI

        System.out.println("\n===== RISULTATI SPERIMENTALI =====");

        System.out.println("Numero di warm-up: " + WARMUP_RUNS);
        System.out.println("Numero di esecuzioni misurate: "
                + MEASURED_RUNS);

        System.out.println("Dimensione batch: " + BATCH_SIZE);
        System.out.println("Numero di batch: "
                + NUMBER_OF_BATCHES);


        System.out.println("\n--- NEAREST NEIGHBOR ---");

        printTour(finalTour);

        System.out.println("Costo: " + costNN);

        System.out.printf(Locale.US,
                "Tempo medio: %.6f ms%n", averageNN);

        System.out.printf(Locale.US,
                "Tempo mediano: %.6f ms%n", medianNN);


        System.out.println("\n--- NEAREST NEIGHBOR + 2-OPT ---");

        printTour(improvedTour);

        System.out.println("Costo: " + costTwoOpt);

        System.out.printf(Locale.US,
                "Tempo medio 2-opt: %.6f ms%n",
                averageTwoOpt);

        System.out.printf(Locale.US,
                "Tempo mediano 2-opt: %.6f ms%n",
                medianTwoOpt);


        System.out.println("\n--- CONFRONTO ---");

        System.out.printf(Locale.US,
                "Tempo medio totale: %.6f ms%n",
                averageTotal);

        System.out.printf(Locale.US,
                "Tempo mediano totale: %.6f ms%n",
                medianTotal);

        System.out.printf(Locale.US,
                "Miglioramento rispetto a NN: %.2f%%%n",
                improvement);

        System.out.println("Ottimo conosciuto: " + knownOptimum);

        System.out.printf(Locale.US,
                "Gap rispetto all'ottimo: %.2f%%%n",
                gap);


        // 8. SALVATAGGIO CSV

        saveResults(
                instanceName,
                n,
                knownOptimum,
                costNN,
                costTwoOpt,
                improvement,
                gap,
                averageNN,
                medianNN,
                averageTwoOpt,
                medianTwoOpt,
                averageTotal,
                medianTotal
        );
    }


    private static double calculateAverage(double[] values) {

        double sum = 0;

        for (double value : values) {
            sum += value;
        }

        return sum / values.length;
    }


    private static double calculateMedian(double[] values) {

        double[] sorted = values.clone();

        Arrays.sort(sorted);

        int middle = sorted.length / 2;

        if (sorted.length % 2 == 0) {

            return (sorted[middle - 1] + sorted[middle])
                    / 2.0;
        }

        return sorted[middle];
    }


    private static void saveResults(
            String instanceName,
            int numberOfCities,
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
            double medianTotal) throws IOException {

        Path directory = Path.of("results");

        // File separato per il benchmark corretto
        Path file = directory.resolve(
                "experiments_benchmark_corrected.csv"
        );

        Files.createDirectories(directory);

        boolean fileExists = Files.exists(file);

        StringBuilder content = new StringBuilder();

        if (!fileExists || Files.size(file) == 0) {

            content.append(
                    "Instance,Cities,KnownOptimum,"
                            + "CostNN,CostNN2Opt,"
                            + "ImprovementPercent,GapPercent,"
                            + "AverageNN_ms,MedianNN_ms,"
                            + "Average2Opt_ms,Median2Opt_ms,"
                            + "AverageTotal_ms,MedianTotal_ms,"
                            + "WarmupRuns,MeasuredRuns,BatchSize\n"
            );
        }

        content.append(String.format(
                Locale.US,
                "%s,%d,%.2f,%.2f,%.2f,%.2f,%.2f,"
                        + "%.6f,%.6f,%.6f,%.6f,%.6f,%.6f,"
                        + "%d,%d,%d%n",
                instanceName,
                numberOfCities,
                knownOptimum,
                costNN,
                costTwoOpt,
                improvement,
                gap,
                averageNN,
                medianNN,
                averageTwoOpt,
                medianTwoOpt,
                averageTotal,
                medianTotal,
                WARMUP_RUNS,
                MEASURED_RUNS,
                BATCH_SIZE
        ));

        Files.writeString(
                file,
                content.toString(),
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND
        );

        System.out.println(
                "\nRisultati salvati in: " + file
        );
    }


    private static void printTour(int[] tour) {

        System.out.print("Tour: ");

        for (int city : tour) {
            System.out.print((city + 1) + " ");
        }

        System.out.println();
    }
}

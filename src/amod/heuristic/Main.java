
package amod.heuristic;

import amod.DistanceMatrix;
import amod.TspReader;

import java.io.IOException;
import java.util.Scanner;

public class Main {

    private static final String INSTANCE_DIRECTORY = "instances/";

    private static final String[][] INSTANCES = {
            {"eil51", "426"},
            {"st70", "675"},
            {"kroA100", "21282"},
            {"ch150", "6528"},
            {"d198", "15780"}
    };

    public static void main(String[] args) throws IOException {

        Scanner scanner = new Scanner(System.in);

        System.out.println("===== TSP SOLVER =====");
        System.out.println("1. Modalità manuale");
        System.out.println("2. Modalità sperimentale");
        System.out.print("Seleziona modalità: ");

        int choice = scanner.nextInt();

        switch (choice) {

            case 1:
                runManualMode(scanner);
                break;

            case 2:
                runExperimentalMode();
                break;

            default:
                System.out.println("Scelta non valida.");
        }

        scanner.close();
    }

    // MODALITÀ MANUALE
    private static void runManualMode(Scanner scanner)
            throws IOException {

        System.out.println("\n--- MODALITÀ MANUALE ---");

        System.out.println("Istanze disponibili:");

        for (int i = 0; i < INSTANCES.length; i++) {
            System.out.println((i + 1) + ". " + INSTANCES[i][0]);
        }

        System.out.print("Seleziona istanza: ");
        int instanceChoice = scanner.nextInt();

        if (instanceChoice < 1 || instanceChoice > INSTANCES.length) {
            System.out.println("Istanza non valida.");
            return;
        }

        String instanceName = INSTANCES[instanceChoice - 1][0];
        String filePath = INSTANCE_DIRECTORY
                + instanceName + ".tsp";

        double[][] coordinates =
                TspReader.readCoordinates(filePath);

        double[][] distances =
                DistanceMatrix.calculate(coordinates);

        System.out.println("Istanza: " + instanceName);
        System.out.println("Numero città: " + coordinates.length);

        System.out.print("Inserisci città di partenza (1-"
                + coordinates.length + "): ");

        int startCity = scanner.nextInt();

        if (startCity < 1 || startCity > coordinates.length) {
            System.out.println("Città di partenza non valida.");
            return;
        }

        int[] tour =
                NearestNeighbor.findTour(distances, startCity - 1);

        double costNN =
                NearestNeighbor.calculateCost(tour, distances);

        int[] improvedTour =
                TwoOpt.twoOpt(tour.clone(), distances);

        double improvedCost =
                NearestNeighbor.calculateCost(improvedTour, distances);

        System.out.println("\n--- NEAREST NEIGHBOR ---");
        printTour(tour);
        System.out.println("Costo: " + costNN);

        System.out.println("\n--- NEAREST NEIGHBOR + 2-OPT ---");
        printTour(improvedTour);
        System.out.println("Costo: " + improvedCost);
    }

    // MODALITÀ SPERIMENTALE
    private static void runExperimentalMode()
            throws IOException {

        System.out.println("\n--- MODALITÀ SPERIMENTALE ---");

        for (String[] instance : INSTANCES) {

            String instanceName = instance[0];
            double knownOptimum = Double.parseDouble(instance[1]);

            String filePath = INSTANCE_DIRECTORY
                    + instanceName + ".tsp";

            System.out.println("\n================================");
            System.out.println("Istanza: " + instanceName);
            System.out.println("================================");

            double[][] coordinates =
                    TspReader.readCoordinates(filePath);

            double[][] distances =
                    DistanceMatrix.calculate(coordinates);

            System.out.println("Numero città: " + coordinates.length);

            ExperimentRunner.runExperiment(
                    instanceName,
                    distances,
                    knownOptimum
            );
        }

        System.out.println("\n===== ESPERIMENTI COMPLETATI =====");
    }

    private static void printTour(int[] tour) {

        System.out.print("Tour: ");

        for (int city : tour) {
            System.out.print((city + 1) + " ");
        }

        System.out.println();
    }
}

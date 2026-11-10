
package amod.mtz;

import amod.DistanceMatrix;
import amod.TspReader;
import com.gurobi.gurobi.*;

import java.io.IOException;
import java.util.Arrays;

public class TspMtzTsplibTest {

    // Numero di esecuzioni preliminari
    private static final int WARMUP_RUNS = 2;

    // Numero di esecuzioni misurate
    private static final int NUMBER_OF_RUNS = 10;


    // CLASSE PER MEMORIZZARE I RISULTATI DI UNA SINGOLA ESECUZIONE
    private static class MtzResult {

        double runtime;
        double objective;
        double bestBound;
        double gap;
        int status;
        int[] tour;

        MtzResult(double runtime, double objective,
                  double bestBound, double gap,
                  int status, int[] tour) {

            this.runtime = runtime;
            this.objective = objective;
            this.bestBound = bestBound;
            this.gap = gap;
            this.status = status;
            this.tour = tour;
        }
    }


    public static void main(String[] args)
            throws IOException, GRBException {

        // Leggiamo le coordinate dell'istanza
        double[][] coordinates =
                TspReader.readCoordinates("instances/kroA100.tsp");

        // Calcoliamo la matrice delle distanze
        double[][] distances =
                DistanceMatrix.calculate(coordinates);

        System.out.println("================================");
        System.out.println(" BENCHMARK MTZ - TSPLIB");
        System.out.println("================================");
        System.out.println("Istanza: eil51");
        System.out.println("Numero città: " + distances.length);
        System.out.println("Warm-up: " + WARMUP_RUNS);
        System.out.println("Esecuzioni misurate: " + NUMBER_OF_RUNS);


        // WARM-UP
        System.out.println("\nEsecuzioni preliminari...");

        for (int i = 0; i < WARMUP_RUNS; i++) {

            runMtz(distances);
        }

        System.out.println("Warm-up completato.");


        // ARRAY PER MEMORIZZARE I RISULTATI
        double[] times = new double[NUMBER_OF_RUNS];
        double[] costs = new double[NUMBER_OF_RUNS];
        double[] bounds = new double[NUMBER_OF_RUNS];
        double[] gaps = new double[NUMBER_OF_RUNS];

        // Conserviamo il risultato dell'ultima esecuzione
        MtzResult lastResult = null;


        // ESECUZIONI MISURATE
        System.out.println("\n--- ESECUZIONI MISURATE ---");

        for (int i = 0; i < NUMBER_OF_RUNS; i++) {

            MtzResult result = runMtz(distances);

            lastResult = result;

            times[i] = result.runtime;
            costs[i] = result.objective;
            bounds[i] = result.bestBound;
            gaps[i] = result.gap;

            System.out.println("\nEsecuzione " + (i + 1));

            System.out.println("Tempo: "
                    + result.runtime + " secondi");

            if (!Double.isNaN(result.objective)) {

                System.out.println("Costo: " + result.objective);
                System.out.println("Lower Bound: " + result.bestBound);
                System.out.println("MIP Gap: " + result.gap + "%");

            } else {

                System.out.println("Nessuna soluzione ammissibile trovata.");
            }
        }


        // CALCOLO DELLA MEDIA DEI TEMPI
        double sum = 0.0;

        for (double time : times) {
            sum += time;
        }

        double average = sum / NUMBER_OF_RUNS;


        // CALCOLO DELLA MEDIANA DEI TEMPI
        double[] sortedTimes = times.clone();

        Arrays.sort(sortedTimes);

        double median;

        if (NUMBER_OF_RUNS % 2 == 0) {

            median = (
                    sortedTimes[NUMBER_OF_RUNS / 2 - 1]
                            + sortedTimes[NUMBER_OF_RUNS / 2]
            ) / 2.0;

        } else {

            median = sortedTimes[NUMBER_OF_RUNS / 2];
        }


        // RISULTATI FINALI
        System.out.println("\n================================");
        System.out.println(" RISULTATI FINALI MTZ");
        System.out.println("================================");

        System.out.println("Tempo medio: "
                + average + " secondi");

        System.out.println("Tempo mediano: "
                + median + " secondi");

        System.out.println("Costo medio delle soluzioni: "
                + averageValid(costs));

        System.out.println("Lower Bound medio: "
                + averageValid(bounds));

        System.out.println("MIP Gap medio: "
                + averageValid(gaps) + "%");


        // STAMPA DEL TOUR DELL'ULTIMA ESECUZIONE
        System.out.println("\n--- TOUR DELL'ULTIMA ESECUZIONE ---");

        if (lastResult != null && lastResult.tour != null) {

            if (lastResult.status == GRB.OPTIMAL) {

                System.out.println("Tour ottimo dimostrato:");

            } else {

                System.out.println("Miglior tour ammissibile trovato:");
            }

            for (int i = 0; i < lastResult.tour.length; i++) {

                // Convertiamo gli indici Java negli identificativi TSPLIB
                System.out.print(lastResult.tour[i] + 1);

                if (i < lastResult.tour.length - 1) {
                    System.out.print(" -> ");
                }
            }

            System.out.println();

        } else {

            System.out.println("Nessun tour ammissibile disponibile.");
        }

        System.out.println("================================");
    }


    // METODO CHE ESEGUE UNA SINGOLA RISOLUZIONE MTZ
    private static MtzResult runMtz(double[][] distances)
            throws GRBException {

        // Numero di città
        int n = distances.length;

        // Creiamo l'ambiente Gurobi
        GRBEnv env = new GRBEnv(true);

        env.set(GRB.IntParam.OutputFlag, 0);
        env.start();

        // Creiamo il modello
        GRBModel model = new GRBModel(env);

        try {

            // Parametri del solver
            model.set(GRB.IntParam.Threads, 1);
            model.set(GRB.IntParam.Seed, 1);
            model.set(GRB.DoubleParam.TimeLimit, 300.0);


            // MATRICE DELLE VARIABILI DECISIONALI
            GRBVar[][] x = new GRBVar[n][n];

            // Creazione variabili x[i][j]
            for (int i = 0; i < n; i++) {

                for (int j = 0; j < n; j++) {

                    if (i != j) {

                        x[i][j] = model.addVar(
                                0.0,
                                1.0,
                                distances[i][j],
                                GRB.BINARY,
                                "x_" + i + "_" + j
                        );
                    }
                }
            }


            // VINCOLI DI GRADO USCENTE
            for (int i = 0; i < n; i++) {

                GRBLinExpr outgoing = new GRBLinExpr();

                for (int j = 0; j < n; j++) {

                    if (i != j) {
                        outgoing.addTerm(1.0, x[i][j]);
                    }
                }

                model.addConstr(
                        outgoing,
                        GRB.EQUAL,
                        1.0,
                        "out_" + i
                );
            }


            // VINCOLI DI GRADO ENTRANTE
            for (int j = 0; j < n; j++) {

                GRBLinExpr incoming = new GRBLinExpr();

                for (int i = 0; i < n; i++) {

                    if (i != j) {
                        incoming.addTerm(1.0, x[i][j]);
                    }
                }

                model.addConstr(
                        incoming,
                        GRB.EQUAL,
                        1.0,
                        "in_" + j
                );
            }


            // CREAZIONE VARIABILI AUSILIARIE MTZ
            GRBVar[] u = new GRBVar[n];

            for (int i = 1; i < n; i++) {

                u[i] = model.addVar(
                        1.0,
                        n - 1,
                        0.0,
                        GRB.CONTINUOUS,
                        "u_" + i
                );
            }


            // VINCOLI MTZ PER ELIMINARE I SOTTOCICLI
            for (int i = 1; i < n; i++) {

                for (int j = 1; j < n; j++) {

                    if (i != j) {

                        GRBLinExpr mtz = new GRBLinExpr();

                        mtz.addTerm(1.0, u[i]);
                        mtz.addTerm(-1.0, u[j]);
                        mtz.addTerm(n, x[i][j]);

                        model.addConstr(
                                mtz,
                                GRB.LESS_EQUAL,
                                n - 1,
                                "mtz_" + i + "_" + j
                        );
                    }
                }
            }


            // AVVIO OTTIMIZZAZIONE
            model.optimize();


            // LETTURA RISULTATI
            int status = model.get(GRB.IntAttr.Status);

            int solutionCount =
                    model.get(GRB.IntAttr.SolCount);

            double runtime =
                    model.get(GRB.DoubleAttr.Runtime);

            double objective = Double.NaN;

            double bestBound =
                    model.get(GRB.DoubleAttr.ObjBound);

            double gap = Double.NaN;

            int[] tour = null;


            if (solutionCount > 0) {

                objective =
                        model.get(GRB.DoubleAttr.ObjVal);

                gap =
                        model.get(GRB.DoubleAttr.MIPGap) * 100.0;


                // RICOSTRUZIONE DEL TOUR
                tour = new int[n + 1];

                boolean[] visited = new boolean[n];

                int current = 0;

                boolean validTour = true;


                for (int k = 0; k < n; k++) {

                    if (visited[current]) {

                        validTour = false;
                        break;
                    }

                    tour[k] = current;

                    visited[current] = true;

                    int next = -1;

                    for (int j = 0; j < n; j++) {

                        if (current != j &&
                                x[current][j].get(GRB.DoubleAttr.X) > 0.5) {

                            next = j;
                            break;
                        }
                    }

                    if (next == -1) {

                        validTour = false;
                        break;
                    }

                    current = next;
                }


                // Verifichiamo il ritorno alla città iniziale
                if (validTour && current == 0) {

                    tour[n] = 0;

                } else {

                    tour = null;
                }
            }


            // Restituiamo i risultati dell'esecuzione
            return new MtzResult(
                    runtime,
                    objective,
                    bestBound,
                    gap,
                    status,
                    tour
            );

        } finally {

            // LIBERIAMO LE RISORSE
            model.dispose();
            env.dispose();
        }
    }


    // CALCOLO DELLA MEDIA IGNORANDO I VALORI NON DISPONIBILI
    private static double averageValid(double[] values) {

        double sum = 0.0;

        int count = 0;

        for (double value : values) {

            if (!Double.isNaN(value)) {

                sum += value;
                count++;
            }
        }

        if (count == 0) {

            return Double.NaN;
        }

        return sum / count;
    }
}

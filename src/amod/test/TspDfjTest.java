
package amod.test;

import amod.dfj.DfjLazyCallback;
import com.gurobi.gurobi.*;

public class TspDfjTest {

    public static void main(String[] args) {

        double[][] distances = {
                {0, 10, 15, 20},
                {10, 0, 35, 25},
                {15, 35, 0, 14},
                {20, 25, 14, 0}
        };

        try {
            GRBEnv env = new GRBEnv(true);
            env.set(GRB.IntParam.OutputFlag, 0);
            env.start();

            GRBModel model = new GRBModel(env);

            int n = distances.length;

            GRBVar[][] x = new GRBVar[n][n];

            // Creazione delle variabili decisionali
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (i != j) {
                        x[i][j] = model.addVar(
                                0, 1, 0,
                                GRB.BINARY,
                                "x_" + i + "_" + j
                        );
                    }
                }
            }

            // Funzione obiettivo
            GRBLinExpr objective = new GRBLinExpr();

            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (i != j) {
                        objective.addTerm(distances[i][j], x[i][j]);
                    }
                }
            }

            model.setObjective(objective, GRB.MINIMIZE);

            // Vincoli: un arco uscente per ogni città
            for (int i = 0; i < n; i++) {
                GRBLinExpr outgoing = new GRBLinExpr();

                for (int j = 0; j < n; j++) {
                    if (i != j) {
                        outgoing.addTerm(1.0, x[i][j]);
                    }
                }

                model.addConstr(outgoing, GRB.EQUAL, 1,
                        "out_" + i);
            }

            // Vincoli: un arco entrante per ogni città
            for (int j = 0; j < n; j++) {
                GRBLinExpr incoming = new GRBLinExpr();

                for (int i = 0; i < n; i++) {
                    if (i != j) {
                        incoming.addTerm(1.0, x[i][j]);
                    }
                }

                model.addConstr(incoming, GRB.EQUAL, 1,
                        "in_" + j);
            }


            // Vincoli DFJ: eliminazione dei sottocicli(I vincoli creati all'inzio richiederebbe troppo tempo)
            /*for (int subset = 1; subset < (1 << n) - 1; subset++) {

                int subsetSize = Integer.bitCount(subset);

                if (subsetSize >= 2) {

                    GRBLinExpr subtour = new GRBLinExpr();

                    for (int i = 0; i < n; i++) {
                        for (int j = 0; j < n; j++) {

                            boolean iInSubset = (subset & (1 << i)) != 0;
                            boolean jInSubset = (subset & (1 << j)) != 0;

                            if (i != j && iInSubset && jInSubset) {
                                subtour.addTerm(1.0, x[i][j]);
                            }
                        }
                    }

                    model.addConstr(
                            subtour,
                            GRB.LESS_EQUAL,
                            subsetSize - 1,
                            "DFJ_" + subset
                    );
                }
            }*/

            // Attiviamo la gestione dei Lazy Constraints
            model.set(GRB.IntParam.LazyConstraints, 1);

            // Colleghiamo il callback DFJ
            model.setCallback(new DfjLazyCallback(x));

            // Avviamo l'ottimizzazione
            model.optimize();

            System.out.println("Costo trovato: "
                    + model.get(GRB.DoubleAttr.ObjVal));

            System.out.println("Archi selezionati:");

            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (i != j &&
                            x[i][j].get(GRB.DoubleAttr.X) > 0.5) {

                        System.out.println(i + " -> " + j);
                    }
                }
            }

            model.dispose();
            env.dispose();

        } catch (GRBException e) {
            System.out.println("Errore Gurobi: " + e.getMessage());
        }
    }
}

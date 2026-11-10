package amod.test;

import com.gurobi.gurobi.*;

public class TspMtzTest {

    public static void main(String[] args) {

    double[][] distances = {
            {0, 10, 15, 20},
            {10, 0, 35, 25},
            {15, 35, 0, 14},
            {20, 25, 14, 0}
    };

            try

    {
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

        // Variabili ausiliarie MTZ
        GRBVar[] u = new GRBVar[n];

        for (int i = 1; i < n; i++) {
            u[i] = model.addVar(
                    1, n - 1, 0,
                    GRB.CONTINUOUS,
                    "u_" + i
            );
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
        // Vincoli MTZ
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
                            "MTZ_" + i + "_" + j
                    );
                }
            }
        }

        // Risoluzione
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

    } catch(
    GRBException e)

    {
        System.out.println("Errore Gurobi: " + e.getMessage());
    }
  }
}

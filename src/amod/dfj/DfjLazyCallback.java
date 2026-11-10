
package amod.dfj;

import com.gurobi.gurobi.*;
import java.util.ArrayList;
import java.util.List;

public class DfjLazyCallback extends GRBCallback {

    private GRBVar[][] x;

    public DfjLazyCallback(GRBVar[][] x) {
        this.x = x;
    }

    @Override
    protected void callback() {

        try {

            // Controlliamo le soluzioni intere candidate
            if (where == GRB.CB_MIPSOL) {

                int n = x.length;

                //Leggiamo soltanto le variabili effettivamente create da Gurobi.
                //Saltiamo la diagonale (i == j), dove non esistono variabili.
                //La diagonale della matrice solution rimane a zero, come è corretto.
                double[][] solution = new double[n][n];

                for (int i = 0; i < n; i++) {
                    for (int j = 0; j < n; j++) {

                        if (i != j) {
                            solution[i][j] = getSolution(x[i][j]);
                        }

                    }
                }

                boolean[] visited = new boolean[n];

                // Cerchiamo tutti i cicli presenti
                for (int start = 0; start < n; start++) {

                    if (!visited[start]) {

                        List<Integer> cycle = new ArrayList<>();

                        int current = start;

                        while (!visited[current]) {

                            visited[current] = true;
                            cycle.add(current);

                            int next = -1;

                            for (int j = 0; j < n; j++) {
                                if (current != j &&
                                        solution[current][j] > 0.5) {

                                    next = j;
                                    break;
                                }
                            }

                            if (next == -1) {
                                throw new IllegalStateException(
                                        "Arco uscente non trovato"
                                );
                            }

                            current = next;
                        }

                        // Se il ciclo non comprende tutte le città,
                        // aggiungiamo il relativo vincolo DFJ
                        if (cycle.size() < n) {

                            GRBLinExpr subtour = new GRBLinExpr();

                            for (int i : cycle) {
                                for (int j : cycle) {
                                    if (i != j) {
                                        subtour.addTerm(1.0, x[i][j]);
                                    }
                                }
                            }

                            addLazy(
                                    subtour,
                                    GRB.LESS_EQUAL,
                                    cycle.size() - 1
                            );

                            /*System.out.println(
                                    "Sottociclo individuato: " + cycle
                            );*/
                        }
                    }
                }
            }

        } catch (GRBException e) {

            System.out.println(
                    "Errore nel callback: " + e.getMessage()
            );

            abort();
        }
    }
}

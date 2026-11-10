
package amod.test;

import com.gurobi.gurobi.GRB;
import com.gurobi.gurobi.GRBEnv;
import com.gurobi.gurobi.GRBException;
import com.gurobi.gurobi.GRBModel;

public class GurobiTest {

    public static void main(String[] args) {

        try {
            GRBEnv env = new GRBEnv(true);
            env.set(GRB.IntParam.OutputFlag, 1);
            env.start();

            GRBModel model = new GRBModel(env);

            System.out.println("Gurobi collegato correttamente a Java!");

            model.dispose();
            env.dispose();

        } catch (GRBException e) {
            System.out.println("Errore Gurobi: " + e.getMessage());
        }
    }
}

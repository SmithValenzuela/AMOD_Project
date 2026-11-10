
package amod.results;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HeuristicResults {

    private static final String CSV_PATH =
            "results/experiments_benchmark_corrected.csv";

    public static HeuristicResult[] getResults() {

        List<HeuristicResult> results = new ArrayList<>();

        try (BufferedReader reader =
                     new BufferedReader(new FileReader(CSV_PATH))) {

            String line;

            // Leggiamo l'intestazione.
            reader.readLine();

            // Leggiamo una riga alla volta.
            while ((line = reader.readLine()) != null) {

                String[] values = line.split(",");

                HeuristicResult result = new HeuristicResult(
                        values[0],
                        Integer.parseInt(values[1]),
                        Double.parseDouble(values[2]),
                        Double.parseDouble(values[3]),
                        Double.parseDouble(values[4]),
                        Double.parseDouble(values[5]),
                        Double.parseDouble(values[6]),
                        Double.parseDouble(values[7]),
                        Double.parseDouble(values[8]),
                        Double.parseDouble(values[9]),
                        Double.parseDouble(values[10]),
                        Double.parseDouble(values[11]),
                        Double.parseDouble(values[12])
                );

                results.add(result);
            }

        } catch (IOException e) {

            throw new RuntimeException(
                    "Errore durante la lettura del CSV: " + CSV_PATH, e
            );
        }

        return results.toArray(new HeuristicResult[0]);
    }
}

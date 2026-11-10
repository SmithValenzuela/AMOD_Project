package amod;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class TspReader {

    public static double[][] readCoordinates(String filePath) throws IOException {

        BufferedReader reader = new BufferedReader(new FileReader(filePath));

        String line;
        int dimension = 0;
        boolean readingCoordinates = false;

        double[][] coordinates = null;

        while ((line = reader.readLine()) != null) {

            line = line.trim();

            if (line.startsWith("DIMENSION")) {
                dimension = Integer.parseInt(line.split(":")[1].trim());
                coordinates = new double[dimension][2];
            }

            if (line.equals("NODE_COORD_SECTION")) {
                readingCoordinates = true;
                continue;
            }

            if (readingCoordinates) {
                if (line.equals("EOF")) {
                    break;
                }

                String[] parts = line.split("\\s+");

                int city = Integer.parseInt(parts[0]) - 1;
                double x = Double.parseDouble(parts[1]);
                double y = Double.parseDouble(parts[2]);

                coordinates[city][0] = x;
                coordinates[city][1] = y;
            }
        }

        reader.close();

        return coordinates;
    }
}
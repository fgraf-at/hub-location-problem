package at.fgraf;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class TestDataGenerator {

    public static void main(String[] args) {
        int numberOfOrigins = 42;
        int numberOfHubs = 24;
        int numberOfDestinations = 128;


        Random random = new Random();
        List<Point> origins = new ArrayList<>();
        List<Point> hubs = new ArrayList<>();
        List<Point> destinations = new ArrayList<>();


        // Generieren von zufälligen Quellen-Punkten
        for (int i = 0; i < numberOfOrigins; i++) {
            int x = random.nextInt(300); // Generiert zufällige x-Koordinate im Bereich von 0 bis 299
            int y = random.nextInt(100); // Generiert zufällige y-Koordinate im Bereich von 0 bis 99
            origins.add(new Point(x, y));
        }

        // Generieren von zufälligen Hubs-Punkten
        for (int i = 0; i < numberOfHubs; i++) {
            int x = 300 + random.nextInt(300); // Generiert zufällige x-Koordinate im Bereich von 300 bis 599
            int y = random.nextInt(100); // Generiert zufällige y-Koordinate im Bereich von 0 bis 99
            hubs.add(new Point(x, y));
        }

        // Generieren von zufälligen Senken-Punkten
        for (int i = 0; i < numberOfDestinations; i++) {
            int x = 600 + random.nextInt(300); // Generiert zufällige x-Koordinate im Bereich von 600 bis 899
            int y = random.nextInt(100); // Generiert zufällige y-Koordinate im Bereich von 0 bis 99
            destinations.add(new Point(x, y));
        }


        var distOriginsHubs = createDistanceMatrix(origins, hubs);
        var distHubsHubs = createDistanceMatrix(hubs, hubs);
        var distHubsDest = createDistanceMatrix(hubs, destinations);
        for (int  i= 0; i < numberOfOrigins; i++) {
            System.out.print("{ ");
            for (int j = 0; j < numberOfHubs; j++) {
                System.out.print(distOriginsHubs[i][j] + ", ");
            }
            System.out.println("},");
        }
        System.out.println();

        System.out.println();


        for (int  i= 0; i < numberOfHubs; i++) {
            System.out.print("{ ");
            for (int j = 0; j < numberOfHubs; j++) {
                System.out.print(distHubsHubs[i][j] + ", ");
            }
            System.out.println("},");
        }

        System.out.println();

        System.out.println();


        for (int  i= 0; i < numberOfHubs; i++) {
            System.out.print("{ ");
            for (int j = 0; j < numberOfDestinations; j++) {
                System.out.print(distHubsDest[i][j] + ", ");
            }
            System.out.println("},");
        }

        System.out.println();

        System.out.println();
        // Ausgabe der generierten Punkte
        System.out.println("Origins: " + origins);
        System.out.println("Hubs: " + hubs);
        System.out.println("Destinations: " + destinations);

    }


    // Methode zum Erstellen einer Distanzmatrix aus einer Liste von Punkten
    public static double[][] createDistanceMatrix(List<Point> points) {
        int size = points.size();
        double[][] matrix = new double[size][size];

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                matrix[i][j] = distance(points.get(i), points.get(j));
            }
        }

        return matrix;
    }

    public static double distance(Point p1, Point p2) {
        int dx = p1.x - p2.x;
        int dy = p1.y - p2.y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    public static double[][] createDistanceMatrix(List<Point> points1, List<Point> points2) {
        int size1 = points1.size();
        int size2 = points2.size();
        double[][] matrix = new double[size1][size2];

        for (int i = 0; i < size1; i++) {
            for (int j = 0; j < size2; j++) {
                matrix[i][j] = distance(points1.get(i), points2.get(j));
            }
        }

        return matrix;
    }

}

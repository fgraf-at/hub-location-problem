//package at.fgraf;
//
//import com.google.ortools.Loader;
//import com.google.ortools.linearsolver.MPConstraint;
//import com.google.ortools.linearsolver.MPObjective;
//import com.google.ortools.linearsolver.MPSolver;
//import com.google.ortools.linearsolver.MPVariable;
//
//import java.awt.*;
//import java.time.Duration;
//import java.time.Instant;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Random;
//
//public class HubLocationProblemBig {
//
//
//
//    public static double distance(Point p1, Point p2) {
//        int dx = p1.x - p2.x;
//        int dy = p1.y - p2.y;
//        return Math.sqrt(dx * dx + dy * dy);
//    }
//
//    public static double[][] createDistanceMatrix(List<Point> points1, List<Point> points2) {
//        int size1 = points1.size();
//        int size2 = points2.size();
//        double[][] matrix = new double[size1][size2];
//
//        for (int i = 0; i < size1; i++) {
//            for (int j = 0; j < size2; j++) {
//                matrix[i][j] = distance(points1.get(i), points2.get(j));
//            }
//        }
//
//        return matrix;
//    }
//
//
//
//    public static void main(String[] args) {
//        Instant startTime = Instant.now();
//        Loader.loadNativeLibraries();
//
//        // creation of a solver
//        MPSolver solver = new MPSolver("Hub Location Problem", MPSolver.OptimizationProblemType.CBC_MIXED_INTEGER_PROGRAMMING);
//
//        int amountOrigins = 100;
//        int amountDestinations = 110;
//        int amountHubs = 60;
//
//
//        int numberOfPoints = 270;
//        int numberOfOrigins = 100;
//        int numberOfHubs = 60;
//        int numberOfDestinations = 110;
//        int[] Fk = {
//                5000, 6000, 5500, 4000, 5550, 5200, 4800, 5300, 6200, 5700,
//                6200, 5700, 5100, 4700, 4200, 5800, 5900, 5600, 6300, 4900,
//                4300, 4600, 5000, 5400, 6000, 6100, 6400, 6500, 6600, 6700,
//                6800, 6900, 7000, 7100, 5000, 6000, 5500, 4000, 5532, 5200,
//                4800, 5300, 6200, 5700, 5100, 4700, 4200, 5800, 5900, 5600,
//                6300, 4900, 4300, 4600, 5000, 5400, 6000, 6100, 6400, 6500 };
//        //Demand
//        int[] demandOrigins = new int[100];
//        for (int i = 0; i < demandOrigins.length; i++) {
//            demandOrigins[i] = 0;
//        }
//
//        int[] demandDestinations = new int[110];
//        for (int i = 0; i < demandDestinations.length; i++) {
//            demandDestinations[i] = (int) (Math.random() * (9999 - 1000 + 1)) + 1000;
//        }
//
//
//
//        Random random = new Random();
//        List<Point> origins = new ArrayList<>();
//        List<Point> hubs = new ArrayList<>();
//        List<Point> destinations = new ArrayList<>();
//
//
//        // Generieren von zufälligen Quellen-Punkten
//        for (int i = 0; i < numberOfOrigins; i++) {
//            int x = random.nextInt(300); // Generiert zufällige x-Koordinate im Bereich von 0 bis 299
//            int y = random.nextInt(100); // Generiert zufällige y-Koordinate im Bereich von 0 bis 99
//            origins.add(new Point(x, y));
//        }
//
//        // Generieren von zufälligen Hubs-Punkten
//        for (int i = 0; i < numberOfHubs; i++) {
//            int x = 300 + random.nextInt(300); // Generiert zufällige x-Koordinate im Bereich von 300 bis 599
//            int y = random.nextInt(100); // Generiert zufällige y-Koordinate im Bereich von 0 bis 99
//            hubs.add(new Point(x, y));
//        }
//
//        // Generieren von zufälligen Senken-Punkten
//        for (int i = 0; i < numberOfDestinations; i++) {
//            int x = 600 + random.nextInt(300); // Generiert zufällige x-Koordinate im Bereich von 600 bis 899
//            int y = random.nextInt(100); // Generiert zufällige y-Koordinate im Bereich von 0 bis 99
//            destinations.add(new Point(x, y));
//        }
//
//
//        var distancesFromOriginsToHubs = createDistanceMatrix(origins, hubs);
//        var distancesToHubs = createDistanceMatrix(hubs, hubs);
//        var distanceToDest = createDistanceMatrix(hubs, destinations);
//
//
//
//
//
//    // Create the decision variables
//        List<List<List<List<MPVariable>>>> Xijkm = new ArrayList<>(); // assignmentVars // X_{ijkm}
//        for (int i = 0; i < amountOrigins; i++) {
//            Xijkm.add(new ArrayList<>()); // List<List<MPVariable>>
//            for (int j = 0; j < amountDestinations; j++) {
//                Xijkm.get(i).add(new ArrayList<>()); // List<MPVariable>
//                for (int k = 0; k < amountHubs; k++) {
//                    Xijkm.get(i).get(j).add(new ArrayList<>()); // MPVariable
//                    for (int m = 0; m < amountHubs; m++) {
//                        Xijkm.get(i).get(j).get(k).add(solver.makeNumVar(0.0, 1.0, "X_" + i + "_" + j + "_" + k + "_" + m));
//                    }
//                }
//            }
//        } // Xijkm
//
//
//        double[][] Wij =  new double[demandOrigins.length][demandDestinations.length];
//
//
//        for (int i = 0; i < demandOrigins.length; i++) {
//            for (int j = 0; j < demandDestinations.length; j++) {
//                Wij[i][j] = demandOrigins[i] + demandDestinations[j];
//            }
//        }
//
//        // assignment cost of each OD pair
//        double[][][][] Cijkm  = new double[amountOrigins][amountDestinations][amountHubs][amountHubs];
//
//        //calculation of Cijkm
//        for (int i = 0; i < amountOrigins; i++) {
//            for (int j = 0; j < amountDestinations; j++) {
//                for (int k = 0; k < amountHubs; k++) {
//                    for (int m = 0; m < amountHubs; m++) {
//
//                        var Cik = distancesFromOriginsToHubs[i][k];  //+ distanceOrigins[i];
//
//                        var Ckm = distancesToHubs[k][m];
//
//                        var Cmj = distanceToDest[m][j];
//
//                        Cijkm[i][j][k][m] =  (Cik + Ckm + Cmj);
//                    }
//                }
//            }
//        } // Cijkm
//
//
//
//
//        // Creation of the Hubopeningvariable
//        MPVariable[] Yk = new MPVariable[amountHubs]; // Y_k
//        for (int k = 0; k < amountHubs; k++) {
//            Yk[k]= solver.makeBoolVar( "Y_k_"+ k);
//        }
//
//        // Set the objective function
//        MPObjective obj = solver.objective();
//        for (int i = 0; i < amountOrigins; i++) {
//            for (int j = 0; j < amountDestinations; j++) {
//                for (int k = 0; k < amountHubs; k++) {
//                    for (int m = 0; m < amountHubs; m++) {
//                        obj.setCoefficient(Xijkm.get(i).get(j).get(k).get(m), Wij[i][j] * Cijkm[i][j][k][m]);
//                    }
//                }
//            }
//        }
//
//        for (int k = 0; k < amountHubs ; k++) {
//           obj.setCoefficient(Yk[k], Fk[k]);
//
//        }
//
//        obj.setMinimization();
//
//        // Constraint 1: Sum of X_ijkm over Hubs k and m needs to be 1 1 for all i, j
//        for (int i = 0; i < amountOrigins; i++) {
//            for (int j = 0; j < amountDestinations; j++) {
//                MPConstraint c1 = solver.makeConstraint(1.0, 1.0, "c1_" + i + "_" + j);
//                for (int k = 0; k < amountHubs; k++) {
//                    for (int m = 0; m < amountHubs; m++) {
//                        c1.setCoefficient(Xijkm.get(i).get(j).get(k).get(m), 1);
//                    }
//                }
//            }
//        } // c1
//
//
//        // Constraint 2: X_ijkm less or equal Y_k for all i, j, k, m
//        for (int i = 0; i < amountOrigins; i++) {
//            for (int j = 0; j < amountDestinations; j++) {
//                for (int k = 0; k < amountHubs; k++) {
//                    for (int m = 0; m < amountHubs; m++) {
//                        MPConstraint c2 = solver.makeConstraint(-MPSolver.infinity(), 0.0, "c2_" + i + "_" + j + "_" + k + "_" + m);
//                        c2.setCoefficient(Xijkm.get(i).get(j).get(k).get(m), 1);
//                        c2.setCoefficient(Yk[k], -1);
//
//                    }
//                }
//            }
//        } // c2
//
//        // Constraint 3: X_ijkm less or equal Y_m  i, j, for all i, j, k, m
//        for (int i = 0; i < amountOrigins; i++) {
//            for (int j = 0; j < amountDestinations; j++) {
//                for (int k = 0; k < amountHubs; k++) {
//                    for (int m = 0; m < amountHubs; m++) {
//                        MPConstraint c3 = solver.makeConstraint(-MPSolver.infinity(), 0.0, "c_3" + i + "_" + j + "_" + k + "_" + m);
//                        c3.setCoefficient(Xijkm.get(i).get(j).get(k).get(m), 1);
//                        c3.setCoefficient(Yk[m], -1);
//                    }
//                }
//            }
//        } // c3
//
//
//
//        // Solve the problem
//        MPSolver.ResultStatus resultStatus = solver.solve();
//        if (resultStatus != MPSolver.ResultStatus.OPTIMAL) {
//            System.err.println("The problem does not have a solution.");
//            return;
//        }
//
////        for (int i = 0; i < amountOrigins; i++) {
////            for (int j = 0; j < amountDestinations; j++) {
////
////                for (int k = 0; k < amountHubs; k++) {
////                    for (int m = 0; m < amountHubs; m++) {
////                        if( Xijkm.get(i).get(j).get(k).get(m).solutionValue() > 0) {
////                            System.out.println("OD pair (" + i + ", " + (amountOrigins+ j) + ") routed through hubs " + k + " and " + (m));
////                        }
////                    }
////                }
////            }
////        }
//
//
//        System.out.println("\nSolution:");
//        System.out.println("Objective value = " + solver.objective().value());
//
//// Print the assignment variables Xijkm
//        System.out.println("\nXijkm variables:");
//        for (int i = 0; i < amountOrigins; i++) {
//            for (int j = 0; j < amountDestinations; j++) {
//                for (int k = 0; k < amountHubs; k++) {
//                    for (int m = 0; m < amountHubs; m++) {
//                        double value = Xijkm.get(i).get(j).get(k).get(m).solutionValue();
//                        if (value > 0) {
//                            System.out.printf("X_%d_%d_%d_%d = %.2f\n", i, j, k, m, value);
//                        }
//                    }
//                }
//            }
//        }
//
//        for (int k = 0; k < amountHubs; k++) {
//            if (Yk[k].solutionValue() > 0) {
//                System.out.println("Hub " + k + " is opened with cost " + Fk[k]);
//            }
//
//        }
//
//        Instant endTime = Instant.now();
//        Duration elapsedTime = Duration.between(startTime, endTime);
//        System.out.println("Time taken to solve the problem: " + elapsedTime.toMillis() + " milliseconds");
//
//    }
//}

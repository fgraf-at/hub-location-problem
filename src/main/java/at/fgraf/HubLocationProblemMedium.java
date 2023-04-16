package at.fgraf;

import com.google.ortools.Loader;
import com.google.ortools.linearsolver.MPConstraint;
import com.google.ortools.linearsolver.MPObjective;
import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.linearsolver.MPVariable;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class HubLocationProblemMedium {
    public static void main(String[] args) {
        Instant startTime = Instant.now();
        Loader.loadNativeLibraries();

        // creation of a solver
        MPSolver solver = new MPSolver("Hub Location Problem", MPSolver.OptimizationProblemType.SCIP_MIXED_INTEGER_PROGRAMMING);

        int amountOrigins = 8;
        int amountDestinations = 9;
        int amountHubs = 7;


        //Demand
        int[] demandOrigins =       {0, 0, 0, 0, 0, 0, 0, 0, 0}; //for completness, according to model, origins have 0 demand.
        int[] demandDestinations = {150, 110, 100, 95, 135, 90, 120, 105, 125};


        int[][] Wij =  new int[demandOrigins.length][demandDestinations.length];


        for (int i = 0; i < demandOrigins.length; i++) {
            for (int j = 0; j < demandDestinations.length; j++) {
                Wij[i][j] = demandOrigins[i] + demandDestinations[j]; // demandOrigins[i] redundant but for completeness
            }
        }

        // assignment cost of each OD pair
        int[][][][] Cijkm  = new int[amountOrigins][amountDestinations][amountHubs][amountHubs];


        int[] distancesFromOriginAToHubs = {95, 110, 120, 190, 185,  60,  70};
        int[] distancesFromOriginBToHubs = {100, 90, 60,  170, 180,  43,  29};
        int[] distancesFromOriginCToHubs = {120, 75, 95,  190, 170,  34,  51};
        int[] distancesFromOriginDToHubs = {55,  70, 15,   65, 175,  76,  83};
        int[] distancesFromOriginEToHubs = {75,  30, 65,  225, 140,  78,  81};
        int[] distancesFromOriginFToHubs = {70,  60, 55,   85,  75,  65, 100};
        int[] distancesFromOriginGToHubs = {80,  90, 110, 120, 130, 140, 150};
        int[] distancesFromOriginHToHubs = {100, 110, 90, 80, 70,    60,  50};

        int[][] distancesFromOriginsToHubs = {
                distancesFromOriginAToHubs,
                distancesFromOriginBToHubs,
                distancesFromOriginCToHubs,
                distancesFromOriginDToHubs,
                distancesFromOriginEToHubs,
                distancesFromOriginFToHubs,
                distancesFromOriginGToHubs,
                distancesFromOriginHToHubs
        };

        int[] distHubAToHubs = {0, 50, 70, 85, 95, 105, 120};
        int[] distHubBToHubs = {40, 0, 65, 80, 100, 72, 33};
        int[] distHubCToHubs = {70, 20, 0, 25, 15,  35, 32};
        int[] distHubDToHubs = {75, 15, 70, 0, 95, 72,  82};
        int[] distHubEToHubs = {70, 20, 65, 85, 0, 52,  92};
        int[] distHubFToHubs = {60, 45, 50, 25, 35, 0,  75};
        int[] distHubGToHubs = {40, 50, 60, 70, 80, 90, 0};
        int[][] distancesToHubs = {
                distHubAToHubs,
                distHubBToHubs,
                distHubCToHubs,
                distHubDToHubs,
                distHubEToHubs,
                distHubFToHubs,
                distHubGToHubs
        };





        int[] distHubAToDep = {70, 20, 60, 85, 75, 45, 24, 67, 56};
        int[] distHubBToDep = {80, 30, 70, 70, 65, 69, 16, 98, 84};
        int[] distHubCToDep = {75, 15, 70, 90, 95, 41, 25, 56, 51};
        int[] distHubDToDep = {40, 25, 60, 55, 40, 23, 47, 57, 21};
        int[] distHubEToDep = {35, 50, 75, 30, 30, 78, 43,  6, 45};
        int[] distHubFToDep = {85, 40, 70, 50, 45, 60, 65, 30, 55};
        int[] distHubGToDep = {80, 70, 60, 50, 40, 30, 20, 10, 60};
        int[] distHubHToDep = {60, 50, 40, 30, 20, 10, 20, 30, 40};
        int[][] distanceToDep = {
                distHubAToDep,
                distHubBToDep,
                distHubCToDep,
                distHubDToDep,
                distHubEToDep,
                distHubFToDep,
                distHubGToDep,
                distHubHToDep
        };

        // Hub openingCosts
        int[] Fk = {25, 40, 45, 30, 20, 67, 50};


        // Create the decision variables
        List<List<List<List<MPVariable>>>> Xijkm = new ArrayList<>(); // assignmentVars // X_{ijkm}
        for (int i = 0; i < amountOrigins; i++) {
            Xijkm.add(new ArrayList<>()); // List<List<MPVariable>>
            for (int j = 0; j < amountDestinations; j++) {
                Xijkm.get(i).add(new ArrayList<>()); // List<MPVariable>
                for (int k = 0; k < amountHubs; k++) {
                    Xijkm.get(i).get(j).add(new ArrayList<>()); // MPVariable
                    for (int m = 0; m < amountHubs; m++) {
                        Xijkm.get(i).get(j).get(k).add(solver.makeNumVar(0.0, 1.0, "X_" + i + "_" + j + "_" + k + "_" + m));
                    }
                }
            }
        } // Xijkm




        //calculation of Cijkm
        for (int i = 0; i < amountOrigins; i++) {
            for (int j = 0; j < amountDestinations; j++) {
                for (int k = 0; k < amountHubs; k++) {
                    for (int m = 0; m < amountHubs; m++) {

                        var Cik = distancesFromOriginsToHubs[i][k];

                        var Ckm = distancesToHubs[k][m];

                        var Cmj = distanceToDep[m][j];

                        Cijkm[i][j][k][m] =  (Cik + Ckm + Cmj);
                    }
                }
            }
        } // Cijkm




        // Creation of the Hubopeningvariable
        MPVariable[] Yk = new MPVariable[amountHubs]; // Y_k
        for (int k = 0; k < amountHubs; k++) {
                Yk[k]= solver.makeNumVar(0.0, 1, "Y_k_"+ k);
        }





        // Set the objective function
        MPObjective obj = solver.objective();
        for (int i = 0; i < amountOrigins; i++) {
            for (int j = 0; j < amountDestinations; j++) {
                for (int k = 0; k < amountHubs; k++) {
                    for (int m = 0; m < amountHubs; m++) {
                        obj.setCoefficient(Xijkm.get(i).get(j).get(k).get(m), Wij[i][j] * Cijkm[i][j][k][m]);
                    }
                }
            }
        }

        for (int k = 0; k < amountHubs ; k++) {
           obj.setCoefficient(Yk[k], Fk[k]);

        }
//        for (int m = 0; m < amountHubsm ; m++) {
//            obj.setCoefficient(Ym[m], Fk[amountHubsk + m]);
//
//        }
        obj.setMinimization();

        // Constraint 1: Sum of X_ijkm over Hubs k and m needs to be 1 1 for all i, j
        for (int i = 0; i < amountOrigins; i++) {
            for (int j = 0; j < amountDestinations; j++) {
                MPConstraint c1 = solver.makeConstraint(1.0, 1.0, "c1_" + i + "_" + j);
                for (int k = 0; k < amountHubs; k++) {
                    for (int m = 0; m < amountHubs; m++) {
                        c1.setCoefficient(Xijkm.get(i).get(j).get(k).get(m), 1);
                    }
                }
            }
        } // c1


        // Constraint 2: X_ijkm less or equal Y_k for all i, j, k, m
        for (int i = 0; i < amountOrigins; i++) {
            for (int j = 0; j < amountDestinations; j++) {
                for (int k = 0; k < amountHubs; k++) {
                    for (int m = 0; m < amountHubs; m++) {
                        MPConstraint c2 = solver.makeConstraint(-MPSolver.infinity(), 0.0, "c2_" + i + "_" + j + "_" + k + "_" + m);
                        c2.setCoefficient(Xijkm.get(i).get(j).get(k).get(m), 1);
                        c2.setCoefficient(Yk[k], -1);

                    }
                }
            }
        } // c2

        // Constraint 3: X_ijkm less or equal Y_m  i, j, for all i, j, k, m
        for (int i = 0; i < amountOrigins; i++) {
            for (int j = 0; j < amountDestinations; j++) {
                for (int k = 0; k < amountHubs; k++) {
                    for (int m = 0; m < amountHubs; m++) {
                        MPConstraint c3 = solver.makeConstraint(-MPSolver.infinity(), 0.0, "c_3" + i + "_" + j + "_" + k + "_" + m);
                        c3.setCoefficient(Xijkm.get(i).get(j).get(k).get(m), 1);
                        c3.setCoefficient(Yk[m], -1);
                    }
                }
            }
        } // c3



        // Solve the problem
        MPSolver.ResultStatus resultStatus = solver.solve();
        if (resultStatus != MPSolver.ResultStatus.OPTIMAL) {
            System.err.println("The problem does not have a solution.");
            return;
        }



        System.out.println("\nSolution:");
        System.out.println("Objective value = " + solver.objective().value());

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

        Instant endTime = Instant.now();
        Duration elapsedTime = Duration.between(startTime, endTime);
        System.out.println("Time taken to solve the problem: " + elapsedTime.toMillis() + " milliseconds");

    }
}

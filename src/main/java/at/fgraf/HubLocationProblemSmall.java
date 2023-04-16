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

public class HubLocationProblemSmall {
    public static void main(String[] args) {
        Instant startTime = Instant.now();
        Loader.loadNativeLibraries();

        // creation of a solver
        MPSolver solver = new MPSolver("Hub Location Problem", MPSolver.OptimizationProblemType.CBC_MIXED_INTEGER_PROGRAMMING);

        int amountOrigins = 3;
        int amountDestinations = 3;
        int amountHubs = 5;
        //Demand
        int[] demandOrigins = {0, 0, 0}; //for completness, according to model, origins have 0 demand.
        int[] demandDestinations = {150, 110, 100};


        int[][] Wij =  new int[demandOrigins.length][demandDestinations.length];


        for (int i = 0; i < demandOrigins.length; i++) {
            for (int j = 0; j < demandDestinations.length; j++) {
                Wij[i][j] = demandOrigins[i] + demandDestinations[j]; // demandOrigins[i] redundant but for completeness
            }
        }

        // assignment cost of each OD pair
        int[][][][] Cijkm  = new int[amountOrigins][amountDestinations][amountHubs][amountHubs];


        int[] distancesFromOriginAToHubs = {95, 110, 120, 190, 185};
        int[] distancesFromOriginBToHubs = {100, 90, 60, 170, 180};
        int[] distancesFromOriginCToHubs = {120, 75, 95, 190, 170};
        int[] distancesFromOriginDToHubs = {55, 70, 15, 65, 175};
        int[] distancesFromOriginEToHubs = {75, 30, 65, 225, 140};

        int[] distHubAToHubs = {0, 20, 70, 85, 95};
        int[] distHubBToHubs = {40, 0, 35, 80, 100};
        int[] distHubCToHubs = {70, 20, 0, 25, 15};
        int[] distHubDToHubs = {75, 15, 20, 0, 95};
        int[] distHubEToHubs = {10, 20, 25, 15, 0};

        int[] distHubAToDep = {98, 50, 60};
        int[] distHubBToDep = {80, 30, 70};
        int[] distHubCToDep = {75, 15, 70};
        int[] distHubDToDep = {40, 25, 60};
        int[] distHubEToDep = {35, 50, 75};

        int[][] distancesFromOriginsToHubs = {
                distancesFromOriginAToHubs,
                distancesFromOriginBToHubs,
                distancesFromOriginCToHubs,
                distancesFromOriginDToHubs,
                distancesFromOriginEToHubs
        };





        int[][] distancesToHubs = {
                distHubAToHubs,
                distHubBToHubs,
                distHubCToHubs,
                distHubDToHubs,
                distHubEToHubs
        };



        int[][] distanceToDep = {
                distHubAToDep,
                distHubBToDep,
                distHubCToDep,
                distHubDToDep,
                distHubEToDep
        };

        // Hub openingCosts
        int[] Fk = {25, 40, 45, 30, 20};


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

                        var Cik = distancesFromOriginsToHubs[i][k];  //+ distanceOrigins[i];

                        var Ckm = distancesToHubs[k][m];

                        var Cmj = distanceToDep[m][j];

                        Cijkm[i][j][k][m] =  (Cik + Ckm + Cmj);
                    }
                }
            }
        } // Cijkm




        // Creation of the Hubopeningvariable
        MPVariable[] Yk = new MPVariable[5]; // Y_k
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

        for (int k = 0; k < 5 ; k++) {
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

//        for (int i = 0; i < amountOrigins; i++) {
//            for (int j = 0; j < amountDestinations; j++) {
//
//                for (int k = 0; k < amountHubs; k++) {
//                    for (int m = 0; m < amountHubs; m++) {
//                        if( Xijkm.get(i).get(j).get(k).get(m).solutionValue() > 0) {
//                            System.out.println("OD pair (" + i + ", " + (amountOrigins+ j) + ") routed through hubs " + k + " and " + (m));
//                        }
//                    }
//                }
//            }
//        }


        System.out.println("\nSolution:");
        System.out.println("Objective value = " + solver.objective().value());

// Print the assignment variables Xijkm
        System.out.println("\nXijkm variables:");
        for (int i = 0; i < amountOrigins; i++) {
            for (int j = 0; j < amountDestinations; j++) {
                for (int k = 0; k < amountHubs; k++) {
                    for (int m = 0; m < amountHubs; m++) {
                        double value = Xijkm.get(i).get(j).get(k).get(m).solutionValue();
                        if (value > 0) {
                            System.out.printf("X_%d_%d_%d_%d = %.2f\n", i, j, k, m, value);
                        }
                    }
                }
            }
        }

        for (int k = 0; k < amountHubs; k++) {
            if (Yk[k].solutionValue() > 0) {
                System.out.println("Hub " + k + " is opened with cost " + Fk[k]);
            }

        }

        Instant endTime = Instant.now();
        Duration elapsedTime = Duration.between(startTime, endTime);
        System.out.println("Time taken to solve the problem: " + elapsedTime.toMillis() + " milliseconds");

    }
}

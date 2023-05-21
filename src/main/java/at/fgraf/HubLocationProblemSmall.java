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
        int[] demandOrigins = {0, 0, 0}; //for completness, according to model, origins have 0 demand, (no demand)
        int[] demandDestinations = {23000, 11000, 14896};


        int[][] Wij =  new int[demandOrigins.length][demandDestinations.length];


        for (int i = 0; i < demandOrigins.length; i++) {
            for (int j = 0; j < demandDestinations.length; j++) {
                Wij[i][j] = demandOrigins[i] + demandDestinations[j]; // demandOrigins[i] redundant but for completeness
            }
        }

        // assignment cost of each OD pair
        double[][][][] Cijkm  = new double[amountOrigins][amountDestinations][amountHubs][amountHubs];

/*


235.0914715594762 235.58013498595335 276.5863337187866 156.54072952429985 109.17875251164945
172.19175357722565 164.19500601419034 207.2920644887305 109.60383204979651 38.07886552931954
180.71247881648904 171.74690681348528 214.84180226389836 119.81652640600127 48.25971404805462


0.0 25.495097567963924 43.9089968002003 80.00624975587844 136.7186892857008
25.495097567963924 0.0 43.104524124504614 86.83893136145792 131.4610208388783
43.9089968002003 43.104524124504614 0.0 123.30855606972291 174.18381095842403
80.00624975587844 86.83893136145792 123.30855606972291 0.0 71.56116265125938
136.7186892857008 131.4610208388783 174.18381095842403 71.56116265125938 0.0


183.19934497699495 400.0799920015996 271.66523517005265
194.81273059017474 406.34222030205035 279.48524111301475
153.62942426501507 363.24096685258394 236.57979626333267
255.38402455909414 476.3034746881446 347.0518693221519
319.4276130831522 536.4513025429242 408.3356462519529


Origins: [java.awt.Point[x=223,y=3], java.awt.Point[x=286,y=74], java.awt.Point[x=279,y=82]]
Hubs: [java.awt.Point[x=455,y=41], java.awt.Point[x=450,y=66], java.awt.Point[x=493,y=63], java.awt.Point[x=379,y=16], java.awt.Point[x=319,y=55]]
Destinations: [java.awt.Point[x=634,y=2], java.awt.Point[x=855,y=33], java.awt.Point[x=726,y=22]]
 */

        double[] distancesFromOriginAToHubs = { 235.0914715594762, 235.58013498595335, 276.5863337187866, 156.54072952429985, 109.17875251164945};
        double[] distancesFromOriginBToHubs = { 172.19175357722565, 164.19500601419034, 207.2920644887305, 109.60383204979651, 38.07886552931954};
        double[] distancesFromOriginCToHubs = { 180.71247881648904, 171.74690681348528, 214.84180226389836, 119.81652640600127, 48.25971404805462};


        double[] distHubAToHubs = {0.0, 25.495097567963924, 43.9089968002003, 80.00624975587844, 136.7186892857008};
        double[] distHubBToHubs = {25.495097567963924, 0.0, 43.104524124504614, 86.83893136145792, 131.4610208388783};
        double[] distHubCToHubs = { 43.9089968002003, 43.104524124504614, 0.0, 123.30855606972291, 174.18381095842403};
        double[] distHubDToHubs = {80.00624975587844, 86.83893136145792, 123.30855606972291, 0.0, 71.56116265125938};
        double[] distHubEToHubs = {136.7186892857008, 131.4610208388783, 174.18381095842403, 71.56116265125938, 0.0};

        double[] distHubAToDep = {183.19934497699495, 400.0799920015996, 271.66523517005265};
        double[] distHubBToDep = {194.81273059017474, 406.34222030205035, 279.48524111301475};
        double[] distHubCToDep = {153.62942426501507, 363.24096685258394, 236.57979626333267};
        double[] distHubDToDep = { 255.38402455909414, 476.3034746881446, 347.0518693221519};
        double[] distHubEToDep = {319.4276130831522, 536.4513025429242, 408.3356462519529};

        double[][] distancesFromOriginsToHubs = {
                distancesFromOriginAToHubs,
                distancesFromOriginBToHubs,
                distancesFromOriginCToHubs,
        };

        double[][] distancesToHubs = {
                distHubAToHubs,
                distHubBToHubs,
                distHubCToHubs,
                distHubDToHubs,
                distHubEToHubs
        };



        double[][] distanceToDep = {
                distHubAToDep,
                distHubBToDep,
                distHubCToDep,
                distHubDToDep,
                distHubEToDep
        };

        // Hub openingCosts
        double[] Fk = {120000, 80000, 50000, 40000, 50000};



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


        // Create the decision variables
        List<List<List<List<MPVariable>>>> Xijkm = new ArrayList<>(); // assignmentVars
        for (int i = 0; i < amountOrigins; i++) {
            Xijkm.add(new ArrayList<>()); // List<List<List<MPVariable>>>
            for (int j = 0; j < amountDestinations; j++) {
                Xijkm.get(i).add(new ArrayList<>()); // List<List<MPVariable>>
                for (int k = 0; k < amountHubs; k++) {
                    Xijkm.get(i).get(j).add(new ArrayList<>()); // List<MPVariable>
                    for (int m = 0; m < amountHubs; m++) {
                        // MPVariable
                        Xijkm.get(i).get(j).get(k).add(solver.makeNumVar(0.0, 1.0, "X_" + i + "_" + j + "_" + k + "_" + m));
                    }
                }
            }
        } // Xijkm

        // Creation of the Hubopeningvariable
        MPVariable[] Yk = new MPVariable[amountHubs]; // Y_k
        for (int k = 0; k < amountHubs; k++) {
                Yk[k]= solver.makeBoolVar( "Y_k_"+ k);
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

                        MPConstraint c3 = solver.makeConstraint(-MPSolver.infinity(), 0.0, "c_3" + i + "_" + j + "_" + k + "_" + m);
                        c3.setCoefficient(Xijkm.get(i).get(j).get(k).get(m), 1);
                        c3.setCoefficient(Yk[m], -1);

                    }
                }
            }
        } // c2

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

package sat;

import java.util.ArrayList;
import java.util.Collections;

import sat.env.Bool;
import immutable.EmptyImList;
import immutable.ImList;
import sat.env.Environment;
import sat.env.Variable;
import sat.formula.Clause;
import sat.formula.Formula;
import sat.formula.Literal;
import sat.formula.PosLiteral;

/**
 * Created by jaelkw on 2/11/17.
 */

public class SATSolverArrList {

    public static boolean solve(ArrayList<ArrayList<Integer>> fm, ArrayList<Bool> pa) {

        if (fm.size() == 0) {
            // it's reached the end
            for (int i = 0; i < pa.size(); i++){
                System.out.println(i + 1 + " : " + pa.get(i));
            }
            return true;
        } else if (fm.contains(new ArrayList<Integer>())) {
            // Contains empty clause
            return false;
        }


        int fm_size = fm.size();
        int outerCounter = 0;

        while(outerCounter < fm_size){
            if(fm.get(outerCounter).size() == 1) {
                // Go look at the unit clauses now
                // iterate through the whole arraylist to find the unit clauses where they exist
                int clauseVal = fm.get(outerCounter).get(0);

                // Case if the unit clause is negative
                if(clauseVal < 0) {
                    int newVal = clauseVal * (-1); // To make it positive
                    pa.set(newVal - 1, Bool.FALSE); // Note that my fm integers start from 1, but pa starts from index 0.
                } else {
                    // Unit clause is not negative, so can keep it as it is
                    pa.set(clauseVal - 1 , Bool.TRUE);
                }

                // Now go remove all the instances of clauses with clauseVal including clauseVal itself (Since our formula is satisfiable if fa goes to null)
                int innerCounter = 0;
                while(innerCounter < fm_size) {
                    // Note, nested loop - potentially can see if can optimize this
                    if(fm.get(innerCounter).contains(clauseVal)) {
                        // Remove the entire clause
                        fm.remove(fm.indexOf(innerCounter));
                        fm_size -= 1; // keep innerCounter at same pointer since another element is now there, but total size has fallen
                    } else if (fm.get(innerCounter).contains(clauseVal * (-1))) {
                        // Remove only the literal, since it's the negative.
                        fm.get(innerCounter).removeAll(Collections.singleton(clauseVal * (-1)));
                        innerCounter += 1;
                    }
                }

                outerCounter = 0; // Restart since may have new unit clauses
            } else {
                outerCounter += 1; // didnt have any unit clause so go search the next clause
            }
        }

        // now, choose literal and set it to true. the final part of the algorithm
        int randLiteral = pa.indexOf(Bool.UNDEFINED); // potentially may have better ways of selecting, but just stick with this first
        pa.set(randLiteral, Bool.TRUE);

        // Test if it's all good if Bool.TRUE, so need to create deepcopy first

        // Deepcopy of fm
        ArrayList<ArrayList<Integer>> trueFm = new ArrayList<>();
        for(int i = 0; i < fm.size(); i++) {
            ArrayList<Integer> internal = new ArrayList<>();
            for(int j = 0; j < fm.get(i).size(); j++) {
                internal.add(fm.get(i).get(j));
            }
            trueFm.add(internal);
        }

        // Copy of pa
        ArrayList<Bool> truePa = new ArrayList<>();
        for(int i = 0; i < pa.size(); i++) {
            truePa.add(pa.get(i));
        }

        if(solve(trueFm, truePa)) {
            return true;
        }

        // This means the True version didn't work, so test the false version
        pa.set(randLiteral, Bool.FALSE);
        return solve(fm,pa);

    }



}

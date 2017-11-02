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

        int ctr = 0;
        int len = fm.size();

        while (ctr < len) {
            if (fm.get(ctr).size() == 1) {
                if (fm.get(ctr).get(0) < 0) {
                    int temp = fm.get(ctr).get(0) * (-1);
                    pa.set(temp - 1, Bool.FALSE);
                } else {
                    pa.set(fm.get(ctr).get(0) - 1, Bool.TRUE);
                }
                int variab = fm.get(ctr).get(0);
                ArrayList<ArrayList<Integer>> toDel = new ArrayList<>();
                for (int i = 0; i < fm.size(); i++) {
                    if (fm.get(i).contains(variab)) {
                        // System.out.println("Deleting clause: "+fm.get(i));
                        toDel.add(fm.get(i));
                    } else if (fm.get(i).contains(-1 * variab)) {
                        fm.get(i).removeAll(Collections.singleton(variab * -1));

                    }
                }
                for (ArrayList<Integer> i : toDel) {
                    fm.remove(fm.indexOf(i));
                }
                ctr = 0;
                len = fm.size();
            } else
                ctr++;
        }

        for (int i = 0; i < pa.size(); i++) {
            for (int j = 0; j < fm.size(); j++) {
                Bool pagetI = pa.get(i);
                ArrayList<Integer> fmgetJ = fm.get(j);
                if (pagetI == Bool.UNDEFINED) {
                    // idk what to do here
                } else if (pagetI == Bool.TRUE && fmgetJ.contains(i + 1)) { // if
                    // literal
                    // is
                    // true
                    // &
                    // clause
                    // contains
                    // literal
                    fm.remove(j); // remove the clause
                } else if (pagetI == Bool.FALSE && fmgetJ.contains(i + 1)) { // if
                    // literal
                    // is
                    // false
                    // &
                    // clause
                    // contains
                    // literal
                    fmgetJ.remove(fmgetJ.indexOf(i + 1)); // remove
                    // literal
                } else if (pagetI == Bool.TRUE && fmgetJ.contains((i + 1) * -1)) { // if
                    // literal
                    // is
                    // true
                    // &
                    // clause
                    // contains
                    // inverse
                    // of
                    // literal
                    fmgetJ.remove(fmgetJ.indexOf((i + 1) * -1));// remove
                    // negative
                    // literal
                } else if (pagetI == Bool.FALSE
                        && fmgetJ.contains((i + 1) * -1)) { // literal is false
                    // & clause contains
                    // inverse of
                    // literal
                    fm.remove(j);// remove clause
                }
            }
        }

        // now, choose literal and set it to true. the final part of the algorithm
        int randLiteral = pa.indexOf(Bool.UNDEFINED); // potentially may have better ways of selecting, but just stick with this first
        System.out.println("Rand literal: " + randLiteral + " // FM: " + fm + " // PA: " + pa);
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

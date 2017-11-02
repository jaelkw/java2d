package submit;

import sat.env.Bool;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Aditya on 3/11/16.
 */

public class SatSolver2 {

    public static ArrayList<Bool> res;
    static boolean printed = false;

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static boolean solve(ArrayList<ArrayList<Integer>> fm,
                                ArrayList<Bool> pa) {
        ArrayList<ArrayList> ret = new ArrayList<ArrayList>();
        ret = unitP(fm, pa); //unit propagation
        fm = ret.get(0);
        pa = ret.get(1);

        int checkr = solvable(fm, pa);
        if (checkr == -1)
            return false;
        if (checkr == 1) {
            res = pa;
            if (!printed) {
                for (int i = 0; i < res.size(); i++) {
                    System.out.println(i + 1 + " : " + res.get(i));
                }
                printed = true;
            }
            return true;
        }
        int tmp = pa.indexOf(Bool.UNDEFINED);
        pa.set(tmp, Bool.TRUE);

        ArrayList<ArrayList<Integer>> copy = new ArrayList<>(); //this block creates a copy of fm
        for (int i = 0; i < fm.size(); i++) {
            ArrayList<Integer> temp = new ArrayList<>();
            for (int p = 0; p < fm.get(i).size(); p++)
                temp.add(fm.get(i).get(p));
            copy.add(temp);
        }

        ArrayList<Bool> paT = new ArrayList<>();

        for (int i = 0; i < pa.size(); i++) { //this block creates a copy of pa
            paT.add(pa.get(i));
        }

        if (solve(copy, paT)) {
            return true;
        }
        pa.set(tmp, Bool.FALSE);
        return solve(fm, pa);

    }

    public static int solvable(ArrayList<ArrayList<Integer>> fm,
                               ArrayList<Bool> pa) {
        if (fm.size() == 0) {
            return 1;//solved
        } else if (fm.contains(new ArrayList<Integer>())) {
            return -1;//not solvable
        }
        return 0;//not yet done
    }

    public static ArrayList<ArrayList> unitP(
            ArrayList<ArrayList<Integer>> fm, ArrayList<Bool> pa) {

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
                    // System.out.println(fm);
                    ArrayList<ArrayList> ret = new ArrayList<ArrayList>();
                    ret.add(fm);
                    ret.add(pa);
                    return ret;
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
        ArrayList<ArrayList> ret = new ArrayList<ArrayList>();
        ret.add(fm);
        ret.add(pa);
        return ret;
    }

}
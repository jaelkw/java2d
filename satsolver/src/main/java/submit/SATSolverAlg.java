package submit;

import sat.env.Bool;

import java.util.*;

/**
 * Created by Aditya on 3/11/16.
 */

public class SATSolverAlg {
    static ArrayList<ArrayList<Integer>> conflict = new ArrayList<ArrayList<Integer>>(); // contains clauses that cause conflict in CNF
    static boolean initCreated = false;
    static ArrayList<ArrayList<Integer>> initial = new ArrayList<ArrayList<Integer>>();    // initial formula list
    static HashMap<Integer, Integer> levels = new HashMap<Integer, Integer>();
    static Integer currentLev = new Integer(0);
    static Integer requiredLev = new Integer(0);
    static Integer toChange = new Integer(0);

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static boolean solve(ArrayList<ArrayList<Integer>> fm,
                                ArrayList<Bool> pa, HashMap<Integer, ArrayList<Integer>> imp, ArrayList<Integer> deleted) {
        currentLev += 1;
        requiredLev = currentLev;

        if (!initCreated) {
            for (int i = 0; i < fm.size(); i++) {    //this block creates a copy of fm as initial
                ArrayList<Integer> temp = new ArrayList<>();
                for (int p = 0; p < fm.get(i).size(); p++) {
                    int wth = fm.get(i).get(p);
                    temp.add(wth);
                    initial.add(temp);
                }
            }
            initCreated = true;
        }

        ArrayList ret = new ArrayList();
        ret = unitP(fm, pa, imp, deleted); //unit prop
        fm = (ArrayList<ArrayList<Integer>>) ret.get(0);
        pa = (ArrayList<Bool>) ret.get(1);
        imp = (HashMap<Integer, ArrayList<Integer>>) ret.get(2);
        // TODO analyse imp and get conflict-causing clauses, implement backward jump

        Iterator iter = imp.entrySet().iterator();
        while (iter.hasNext()) {
            HashMap<Integer, Boolean> checkNeg = new HashMap<Integer, Boolean>();
            Map.Entry pair = (Map.Entry) iter.next();
            ArrayList<Integer> loopover = (ArrayList<Integer>) pair.getValue();
            for (int i = 0; i < loopover.size(); i++) {
                Integer toCheck = new Integer(loopover.get(i));    // value that causes conflict
                if (toCheck < 0) {
                    toCheck = -toCheck;
                }
                if (checkNeg.get(toCheck) == null) {
                    checkNeg.put(toCheck, true);
                } else {
                    ArrayList<Integer> newConflict = new ArrayList<Integer>();
                    Iterator iter2 = imp.entrySet().iterator();
                    Integer maxLev = new Integer(0);
                    while (iter2.hasNext()) {
                        Map.Entry pair2 = (Map.Entry) iter2.next();
                        ArrayList<Integer> checkList = imp.get(pair2.getKey());
                        if (checkList.contains(toCheck) || checkList.contains(-toCheck)) {
                            newConflict.add(-(Integer) pair2.getKey());
                        }
                        if (levels.get(pair2.getKey()) >= maxLev && levels.get(pair2.getKey()) < currentLev) {
                            maxLev = levels.get(pair2.getKey());
                        }
                    }
                    toChange = loopover.get(i);
                    requiredLev = maxLev;
                    conflict.add(newConflict);
                    return false;
                }
            }
        }

        int checkr = solvable(fm, pa);
        if (checkr == -1)
            return false;
        if (checkr == 1) {

            for (int i = 0; i < pa.size(); i++) {
                System.out.println(i + 1 + " : " + pa.get(i));
            }

            return true;
        }
        int tmp = pa.indexOf(Bool.UNDEFINED);
        pa.set(tmp, Bool.TRUE);

        ArrayList<ArrayList<Integer>> copy = new ArrayList<>(); //this block creates a copy of fm
        for (int i = 0; i < fm.size(); i++) {
            ArrayList<Integer> temp = new ArrayList<>();
            for (int p = 0; p < fm.get(i).size(); p++) {
                int wth = fm.get(i).get(p);
                //Integer wtf = new Integer(fm.get(i).get(p));
                temp.add(wth);
                copy.add(temp);
            }
        }

        HashMap<Integer, ArrayList<Integer>> impCopy = (HashMap<Integer, ArrayList<Integer>>) imp.clone();

        ArrayList<Integer> deleteCopy = new ArrayList<>();

        for (int i = 0; i < deleted.size(); i++) { //this block creates a copy of pa
            deleteCopy.add(deleted.get(i));
        }

        ArrayList<Bool> paT = new ArrayList<>();
        for (int i = 0; i < pa.size(); i++) { //this block creates a copy of pa
            if (pa.get(i) == Bool.TRUE) {
                paT.add(Bool.TRUE);
            } else if (pa.get(i) == Bool.FALSE) {
                paT.add(Bool.FALSE);
            } else {
                paT.add(Bool.UNDEFINED);
            }
        }
        fm.addAll(conflict);
        if (solve(copy, paT, impCopy, deleted)) {
            return true;
        }

        if (requiredLev != currentLev) {
            currentLev -= 1;
            Iterator iterable = levels.entrySet().iterator();
            ArrayList<Integer> toRemove = new ArrayList<Integer>();
            while (iterable.hasNext()) {
                Map.Entry paired = (Map.Entry) iterable.next();
                if ((Integer) paired.getValue() > currentLev) {
                    toRemove.add((Integer) paired.getValue());
                }
            }
            for (int i = 0; i < toRemove.size(); i++) {
                levels.remove(toRemove.get(i));
            }
            if (toChange < 0) {
                pa.add(-toChange, Bool.TRUE);
            } else {
                pa.add(-toChange, Bool.FALSE);
            }
            return false;
        }

        fm.addAll(conflict);
        pa.set(tmp, Bool.FALSE);
        return solve(fm, pa, imp, deleted);

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
            ArrayList<ArrayList<Integer>> fm, ArrayList<Bool> pa, HashMap<Integer, ArrayList<Integer>> imp, ArrayList<Integer> deleted) {

        long now = System.nanoTime();
        int ctr = 0;
        int len = fm.size();

        while (ctr < len) { // find unit clauses and propagate
            if (fm.get(ctr).size() == 1) {
                int temp = fm.get(ctr).get(0);    // literal causing the implication
                if (fm.get(ctr).get(0) < 0) {
                    temp = fm.get(ctr).get(0) * (-1);
                    pa.set(temp - 1, Bool.FALSE);
                } else {
                    pa.set(temp - 1, Bool.TRUE);
                }
                ArrayList<ArrayList<Integer>> toDel = new ArrayList<>();
                for (int i = 0; i < fm.size(); i++) {
                    if (fm.get(i).contains(temp)) {
                        deleted.add(temp);    // record that temp was deleted
                        toDel.add(fm.get(i));    // delete entire unit clause
                    } else if (fm.get(i).contains(-1 * temp)) {    // contains negated form of lit in unit clause
                        fm.get(i).removeAll(Collections.singleton(temp * -1));
                        if (fm.get(i).size() == 1) {
                            if (imp.get(temp) == null) {
                                ArrayList<Integer> firstEntry = new ArrayList<Integer>();
                                firstEntry.add(fm.get(i).get(0));
                                imp.put(temp, firstEntry);
                                for (int j = 0; j < firstEntry.size(); j++) {
                                    levels.put(j, currentLev);
                                }
                            } else {
                                levels.put(fm.get(i).get(0), currentLev);
                                imp.get(temp).add(fm.get(i).get(0));    // temp implies fm.get(i).get(0)
                            }
                            for (int j = 0; j < initial.size(); j++) {
                                if (initial.get(j).contains(temp) && initial.get(j).contains(initial.get(i).get(0))) {
                                    for (int k = 0; k < deleted.size(); k++) {
                                        if (initial.get(j).contains(deleted.get(k))) {
                                            levels.put(deleted.get(k), currentLev);
                                            imp.get(temp).add(deleted.get(k));    // put all items that are both in deleted and in the same original clause as temp and fm.get(i).get(0)
                                        }
                                    }
                                }
                            }
                        }
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
                    ArrayList ret = new ArrayList();
                    ret.add(fm);
                    ret.add(pa);
                    ret.add(imp);
                    return ret;
                } else if (pagetI == Bool.TRUE && fmgetJ.contains(i + 1)) { // if
                    // literal
                    // is
                    // true
                    // &
                    // clause
                    // contains
                    // literal
                    for (int k = 0; k < fm.get(j).size(); k++) {
                        deleted.add(fmgetJ.get(k));
                    }
                    fm.remove(j); // remove the clause
                } else if (pagetI == Bool.FALSE && fmgetJ.contains(i + 1)) { // if
                    // literal
                    // is
                    // false
                    // &
                    // clause
                    // contains
                    // literal
                    deleted.add(fmgetJ.get(fmgetJ.indexOf(i + 1)));
                    fmgetJ.remove(fmgetJ.indexOf(i + 1)); // remove literal
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
                    deleted.add(fmgetJ.get(fmgetJ.indexOf((i + 1) * -1)));
                    fmgetJ.remove(fmgetJ.indexOf((i + 1) * -1));// remove
                    // negative
                    // literal
                } else if (pagetI == Bool.FALSE
                        && fmgetJ.contains((i + 1) * -1)) { // literal is false
                    // & clause contains
                    // inverse of
                    // literal
                    for (int k = 0; k < fm.get(j).size(); k++) {
                        deleted.add(fmgetJ.get(k));
                    }
                    fm.remove(j);// remove clause
                }
            }
        }
        ArrayList ret = new ArrayList();
        ret.add(fm);
        ret.add(pa);
        ret.add(imp);
        long end = (System.nanoTime() - now) / 1000000;
        System.out.println("UnitP took " + end + "ms");
        return ret;
    }

}
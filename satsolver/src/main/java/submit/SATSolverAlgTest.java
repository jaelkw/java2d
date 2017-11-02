package submit;

/*
import static org.junit.Assert.*;

import org.junit.Test;
*/

import sat.env.Bool;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import static submit.SATSolverAlg.solve;

public class SATSolverAlgTest {
    static String fileLoc = "sampleCNF/unsat3Large.cnf";


    public static void main(String[] args) {


        int varcnt = 0;
        ArrayList<ArrayList<Integer>> hm = new ArrayList<>();
        Scanner reader;
        // CNF PARSER
        try {
            BufferedReader br = Files.newBufferedReader(Paths.get(fileLoc));
            String[] lineS = {};
            reader = new Scanner(br);
            while (reader.hasNext()) { //reading line by line
                ArrayList<Integer> al = new ArrayList<Integer>();
                lineS = reader.nextLine().trim().split(" ", -1);
                if (lineS.length > 1) {
                    if (lineS[0].charAt(0) == '-' || lineS[0].charAt(0) >= '0' && lineS[0].charAt(0) <= '9') {
                        for (String pt : lineS) {
                            if (!pt.equals("0") && pt.length() > 0)
                                al.add(Integer.parseInt(pt));
                        }
                        hm.add(al);
                    } else if (lineS[0].charAt(0) == 'p') {
                        varcnt = Integer.parseInt(lineS[2]);
                    }
                }
                // Create an immutable list of literals and add those literals to a clause
            }
            reader.close();
        } catch (IOException e) {
            System.err.println("file 404");
        }


        System.out.println("SAT solver starts!!!");
        long started = System.nanoTime();
        ArrayList<Bool> res = new ArrayList<>();
        HashMap<Integer, ArrayList<Integer>> impGraph = new HashMap<Integer, ArrayList<Integer>>();
        ArrayList<Integer> deleted = new ArrayList<Integer>();    // contains all deleted items in pure literal form
        for (int i = 0; i < varcnt; i++)
            res.add(Bool.UNDEFINED);
        if (solve(hm, res, impGraph, deleted))
            System.out.println("CAN");
        else {
            System.out.println("CANOT BRO");
        }

        long time = System.nanoTime();
        long timeTaken = time - started;
        System.out.println("Time:" + timeTaken / 1000000.0 + "ms");
    }
}
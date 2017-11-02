package sat;

import sat.env.*;
import sat.formula.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;
import static sat.SATSolverArrList.solve;


/**
 * Created by jaelkw on 2/11/17.
 */

public class SatSolverArrListTest {

    //    private static final String INPUT_FILENAME = "/Users/jaelkw/AndroidStudioProjects/java2d/satsolver/src/main/java/com/sampleCNF/largeSat.cnf";
    static String fileLoc = "/Users/jaelkw/AndroidStudioProjects/java2d/satsolver/src/main/java/com/sampleCNF/largeSat.cnf";


    public static void main(String[] args) {


        int varcnt = 0;
        ArrayList<ArrayList<Integer>> hm = new ArrayList<>();
        Scanner reader;
        // CNF PARSER
        try {
//            BufferedReader reader = new BufferedReader(new FileReader(INPUT_FILENAME));
            BufferedReader br = Files.newBufferedReader(Paths.get(fileLoc));

            String[] lineS = {};
            reader = new Scanner(br);
            while (reader.hasNext()) { //reading line by line
                ArrayList<Integer> al = new ArrayList<Integer>();
                lineS = reader.nextLine().trim().split(" ", -1);
                if (lineS.length > 0) {
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
            //e.printStackTrace();
        }


        System.out.println("SAT solver starts!!!");
        long started = System.nanoTime();
        ArrayList<Bool> res = new ArrayList<>();
        for (int i = 0; i < varcnt; i++)
            res.add(Bool.UNDEFINED);
        if (solve(hm, res))
            System.out.println("CAN");
        else {
            System.out.println("CANOT BRO");
        }

        long time = System.nanoTime();
        long timeTaken = time - started;
        System.out.println("Time:" + timeTaken / 1000000.0 + "ms");
    }


}

package sat;

import sat.env.Environment;
import sat.formula.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class SATSolverTest {
    Literal a = PosLiteral.make("a");
    Literal b = PosLiteral.make("b");
    Literal c = PosLiteral.make("c");
    Literal na = a.getNegation();
    Literal nb = b.getNegation();
    Literal nc = c.getNegation();

	// TODO: add the main method that reads the .cnf file and calls SATSolver.solve to determine the satisfiability

    private static final String INPUT_FILENAME = "/Users/jaelkw/AndroidStudioProjects/java2d/satsolver/src/main/java/com/sampleCNF/largeSat.cnf";
//    private static final String OUTPUT_FILENAME = "D:/50.001-Introduction-to-Information-Systems-and-Programming/SATSolver/project-2d-starting/sampleCNF/BoolAssignment.txt";
    public static void main(String[] args) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(INPUT_FILENAME));

            String line;

            int numberOfClauses = 0;
            int numberOfLiterals = 0;

            Formula formula = new Formula();
            Clause clause = new Clause();

            while ((line = reader.readLine()) != null) {
//                System.out.println(line);
                String[] splitLine = line.split(" ");
                switch (splitLine[0]) {
                    case "c":
                        continue;
                    case "p":
                        numberOfClauses = Integer.parseInt(splitLine[2]);
                        numberOfLiterals = Integer.parseInt(splitLine[3]);
                    case "":
                        continue;
                    default:
                        for (String literal : splitLine) {
                            if (literal.equals("0")) {
                                formula = formula.addClause(clause);
                                clause = new Clause();
                            } else if (literal.charAt(0) == '-') {
                                clause = clause.add(NegLiteral.make(literal));
                            } else {
                                clause = clause.add(PosLiteral.make(literal));
                            }
                        }
                }
            }

            System.out.println("SAT solver starts!!!");
            long started = System.nanoTime();
            Environment env = SATSolver.solve(formula);
            long time = System.nanoTime();
            long timeTaken= time - started;
            System.out.println("Time:" + timeTaken/1000000.0 + "ms");


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void testSATSolver1(){
    	// (a v b)
    	Environment e = SATSolver.solve(makeFm(makeCl(a,b))	);

//    	assertTrue( "one of the literals should be set to true",
//    			Bool.TRUE == e.get(a.getVariable())
//    			|| Bool.TRUE == e.get(b.getVariable())	);
//

    }
    
    
    public void testSATSolver2(){
    	// (~a)
    	Environment e = SATSolver.solve(makeFm(makeCl(na)));

//    	assertEquals( Bool.FALSE, e.get(na.getVariable()));

    }
    
    private static Formula makeFm(Clause... e) {
        Formula f = new Formula();
        for (Clause c : e) {
            f = f.addClause(c);
        }
        return f;
    }
    
    private static Clause makeCl(Literal... e) {
        Clause c = new Clause();
        for (Literal l : e) {
            c = c.add(l);
        }
        return c;
    }
    
    
    
}
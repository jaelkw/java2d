package sat;

import immutable.EmptyImList;
import immutable.ImList;
import sat.env.Environment;
import sat.env.Variable;
import sat.formula.Clause;
import sat.formula.Formula;
import sat.formula.Literal;
import sat.formula.PosLiteral;

/**
 * A simple DPLL SAT solver. See http://en.wikipedia.org/wiki/DPLL_algorithm
 */
public class SATSolver {
    /**
     * Solve the problem using a simple version of DPLL with backtracking and
     * unit propagation. The returned environment binds literals of class
     * bool.Variable rather than the special literals used in clausification of
     * class clausal.Literal, so that clients can more readily use it.
     * 
     * @return an environment for which the problem evaluates to Bool.TRUE, or
     *         null if no such environment exists.
     */
    public static Environment solve(Formula formula) {
        // TODO: implement this.
        Environment env = new Environment();
        ImList<Clause> clauses = formula.getClauses();
        return solve(clauses, env);

        //throw new RuntimeException("not yet implemented.");
    }

    /**
     * Takes a partial assignment of variables to values, and recursively
     * searches for a complete satisfying assignment.
     * 
     * @param clauses
     *            formula in conjunctive normal form
     * @param env
     *            assignment of some or all variables in clauses to true or
     *            false values.
     * @return an environment for which all the clauses evaluate to Bool.TRUE,
     *         or null if no such environment exists.
     */
    private static Environment solve(ImList<Clause> clauses, Environment env) {
        // TODO: implement this.
        if (clauses.isEmpty()) {
            return env;
        }

        Clause smallestClause = null;
        int smallestClauseSize = Integer.MAX_VALUE;
        for (Clause clause : clauses) {
            if (clause.isEmpty()) {
                System.out.println("Clause is empty");
                return null;
            } else if (clause.size() < smallestClauseSize) {
                smallestClause = clause;
                smallestClauseSize = clause.size();
            }
        }

        Literal literal = smallestClause.chooseLiteral();
        Variable var = literal.getVariable();
        if (smallestClause.isUnit()) {
            if (literal instanceof PosLiteral) {
                env = env.putTrue(var);
            } else {
                env = env.putFalse(var);
            }
            return solve(substitute(clauses, literal), env);
        } else {
            if (literal instanceof PosLiteral) {
                env = env.putTrue(var);
                Environment solution = solve(substitute(clauses, literal), env);
                if (solution != null) {
                    return solution;
                } else {
                    env = env.putFalse(var);
                    return solve(substitute(clauses, literal), env);
                }
            } else {
                literal = literal.getNegation();
                env = env.putFalse(var);
                return solve(substitute(clauses, literal), env);
            }
        }


        //throw new RuntimeException("not yet implemented.");
    }

    /**
     * given a clause list and literal, produce a new list resulting from
     * setting that literal to true
     * 
     * @param clauses
     *            , a list of clauses
     * @param l
     *            , a literal to set to true
     * @return a new list of clauses resulting from setting l to true
     */
    private static ImList<Clause> substitute(ImList<Clause> clauses,
            Literal l) {
        // TODO: implement this.
        ImList<Clause> newClauses = new EmptyImList<Clause>();
        for (Clause clause : clauses) {
            Clause newClause = clause.reduce(l);
            if (newClause != null) {
                newClauses = newClauses.add(newClause);
            }
        }
        return newClauses;
        //throw new RuntimeException("not yet implemented.");
    }

}

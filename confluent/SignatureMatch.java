import java.util.*;

/**
 * Confluent onsite (PracHub "Solve Signature, File, and Queue Problems", 2026), problem 1.
 *
 * Each function is an ordered parameter list; each parameter is (type, kind) where kind is
 *   "required", "optional" (may be skipped, may appear anywhere before the variadic), or
 *   "variadic" (at most one, always last, matches ZERO or more trailing args of its type).
 * Matching is positional with exact, case-sensitive types. Return true if the call
 * signature is accepted by at least one function.
 *
 *   f = [(int, required), (str, optional), (bool, required)], call [int, bool] -> true
 *   f1 = [(int, required), (str, variadic)], f2 = [(int, required), (bool, required)],
 *   call [int, str, str] -> true (f1)
 *
 * Why not greedy: [(int, optional), (int, required)] with call [int]. Greedy gives the
 * int to the optional param, then the required int has nothing left -> wrong "false".
 *
 * Main solution: recursion + memo. match(i, j) = can params[i..] accept args[j..]?
 *   i == m:      true iff j == n (all args used up)
 *   required T:  args[j] == T && match(i+1, j+1)
 *   optional T:  match(i+1, j)                        (skip it)
 *                || (args[j] == T && match(i+1, j+1))  (use it)
 *   variadic T:  args[j..n-1] are all T (zero args is fine; it is always last)
 * Without memo each optional doubles the paths -> O(2^k). match only depends on (i, j),
 * so cache it: O(m * n) time and space per function (m params, n args).
 * Limits: total params 4000, n 2000 -> at most ~8 * 10^6 states overall.
 *
 * matchesDp below is the same recurrence written as a loop (O(n) space, no recursion).
 */
public class SignatureMatch {

    static class Param {
        final String type, kind;
        Param(String type, String kind) { this.type = type; this.kind = kind; }
    }

    static Param p(String type, String kind) { return new Param(type, kind); }

    public static boolean canAccept(List<List<Param>> functions, List<String> args) {
        for (List<Param> f : functions) {
            if (matches(f, args)) return true;
        }
        return false;
    }

    // ---------------- Main: recursion + memo ----------------
    static boolean matches(List<Param> params, List<String> args) {
        // memo[i][j]: 0 = not computed, 1 = true, 2 = false
        byte[][] memo = new byte[params.size() + 1][args.size() + 1];
        return match(params, args, 0, 0, memo);
    }

    private static boolean match(List<Param> params, List<String> args, int i, int j, byte[][] memo) {
        if (i == params.size()) return j == args.size();   // params used up: args must be too
        if (memo[i][j] != 0) return memo[i][j] == 1;

        Param p = params.get(i);
        boolean typeOk = j < args.size() && args.get(j).equals(p.type);
        boolean res;
        if (p.kind.equals("required")) {
            res = typeOk && match(params, args, i + 1, j + 1, memo);
        } else if (p.kind.equals("optional")) {
            res = match(params, args, i + 1, j, memo)                       // skip it
               || (typeOk && match(params, args, i + 1, j + 1, memo));      // use it
        } else { // variadic, always last: every remaining arg must be p.type
            res = true;
            for (int k = j; k < args.size(); k++) {
                if (!args.get(k).equals(p.type)) { res = false; break; }
            }
        }
        memo[i][j] = (byte) (res ? 1 : 2);
        return res;
    }

    // ---------------- Alternative: same recurrence as a loop ----------------
    // reach[j] = "after the params seen so far, exactly the first j args can be used up".
    static boolean matchesDp(List<Param> params, List<String> args) {
        int n = args.size();
        boolean[] reach = new boolean[n + 1];
        reach[0] = true;
        for (Param prm : params) {
            if (prm.kind.equals("variadic")) {
                // allFrom[j]: args[j..n-1] are all prm.type (true for j == n, zero args)
                boolean[] allFrom = new boolean[n + 1];
                allFrom[n] = true;
                for (int j = n - 1; j >= 0; j--) allFrom[j] = allFrom[j + 1] && args.get(j).equals(prm.type);
                for (int j = 0; j <= n; j++) if (reach[j] && allFrom[j]) return true;
                return false;
            }
            boolean optional = prm.kind.equals("optional");
            boolean[] next = new boolean[n + 1];
            for (int j = 0; j <= n; j++) {
                if (!reach[j]) continue;
                if (optional) next[j] = true;
                if (j < n && args.get(j).equals(prm.type)) next[j + 1] = true;
            }
            reach = next;
        }
        return reach[n];
    }

    public static void main(String[] args) {
        // Example 1: optional in the middle is skipped
        List<List<Param>> fs1 = List.of(
            List.of(p("int", "required"), p("str", "optional"), p("bool", "required")));
        System.out.println(canAccept(fs1, List.of("int", "bool")));          // true
        System.out.println(canAccept(fs1, List.of("int", "str", "bool")));   // true
        System.out.println(canAccept(fs1, List.of("int", "str")));           // false

        // Example 2: first function matches with two variadic strs
        List<List<Param>> fs2 = List.of(
            List.of(p("int", "required"), p("str", "variadic")),
            List.of(p("int", "required"), p("bool", "required")));
        System.out.println(canAccept(fs2, List.of("int", "str", "str")));    // true
        System.out.println(canAccept(fs2, List.of("int")));                  // true (zero variadic)
        System.out.println(canAccept(fs2, List.of("int", "str", "bool")));   // false

        // Greedy trap: optional int followed by required int
        List<List<Param>> fs3 = List.of(
            List.of(p("int", "optional"), p("int", "required")));
        System.out.println(canAccept(fs3, List.of("int")));                  // true
        System.out.println(canAccept(fs3, List.of("int", "int")));           // true
        System.out.println(canAccept(fs3, List.of("int", "int", "int")));    // false

        // Optional right before variadic of the same type
        List<List<Param>> fs4 = List.of(
            List.of(p("str", "required"), p("int", "optional"), p("int", "variadic")));
        System.out.println(canAccept(fs4, List.of("str")));                  // true
        System.out.println(canAccept(fs4, List.of("str", "int", "int")));    // true
        System.out.println(canAccept(fs4, List.of("str", "bool")));          // false

        // Edge cases: no functions; empty call vs empty signature; case-sensitive types
        System.out.println(canAccept(List.of(), List.of()));                           // false
        System.out.println(canAccept(List.of(List.of()), List.of()));                  // true
        System.out.println(canAccept(List.of(List.of(p("Int", "required"))), List.of("int"))); // false

        // Both versions must agree
        boolean same = true;
        for (List<List<Param>> fs : List.of(fs1, fs2, fs3, fs4))
            for (List<Param> f : fs)
                for (List<String> call : List.of(List.<String>of(), List.of("int"), List.of("str"),
                        List.of("int", "int"), List.of("int", "str", "str"), List.of("str", "int", "int")))
                    same &= matches(f, call) == matchesDp(f, call);
        System.out.println("recursion == loop: " + same);                    // true
    }
}

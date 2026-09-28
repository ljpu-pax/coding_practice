import java.util.*;

/**
 * Confluent tech screen: FunctionLibrary register / findMatches.
 *
 * Function(name, argumentTypes, isVariadic). If isVariadic, the LAST argument type
 * can occur ONE or more times. findMatches(args) returns ALL functions that accept args.
 *
 * FuncA: [String, Integer, Integer]  false     findMatches([String])                   -> [FuncF]
 * FuncB: [String, Integer]           true      findMatches([Integer])                  -> [FuncC, FuncG]
 * FuncC: [Integer]                   true      findMatches([Integer x4])               -> [FuncC, FuncD]
 * FuncD: [Integer, Integer]          true      findMatches([Integer x3])               -> [FuncC, FuncD, FuncE]
 * FuncE: [Integer, Integer, Integer] false     findMatches([String, Integer x3])       -> [FuncB]
 * FuncF: [String]                    false     findMatches([String, Integer, Integer]) -> [FuncA, FuncB]
 * FuncG: [Integer]                   false
 *
 * Brute force: check every function against the query. O(F * k) per query.
 *
 * Optimal: O(k + output) per query, k = number of query args.
 * 1) Non-variadic: exact match -> HashMap<List<String>, List<Function>>.
 * 2) Variadic: Trie keyed by argument types; a variadic function with d params is stored
 *    at the trie node at depth d (its full type path, last type included once).
 *    Let j = start index of the final run of identical types in args
 *    (args[j..k-1] all equal args[k-1], and args[j-1] differs).
 *    A variadic function of length d matches  <=>  args[0..d-1] equals its types
 *    AND args[d-1..k-1] are all the same type  <=>  trie path matches AND d - 1 >= j.
 *    So walk the trie along args once; at every depth d >= j + 1, collect the
 *    variadic functions stored at that node.
 *
 * Register: O(total number of param types).
 */
public class FunctionLibrary {

    static class Function {
        String name;
        List<String> argumentTypes;
        boolean isVariadic;

        Function(String name, List<String> argumentTypes, boolean isVariadic) {
            this.name = name;
            this.argumentTypes = argumentTypes;
            this.isVariadic = isVariadic;
        }

        @Override
        public String toString() { return name; }
    }

    static class TrieNode {
        final Map<String, TrieNode> children = new HashMap<>();
        final List<Function> variadic = new ArrayList<>();
    }

    private final Map<List<String>, List<Function>> exact = new HashMap<>();
    private final TrieNode root = new TrieNode();

    public void register(Set<Function> functionSet) {
        for (Function f : functionSet) {
            if (!f.isVariadic) {
                exact.computeIfAbsent(new ArrayList<>(f.argumentTypes), k -> new ArrayList<>()).add(f);
            } else {
                if (f.argumentTypes.isEmpty()) continue; // variadic needs a type to repeat
                TrieNode node = root;
                for (String t : f.argumentTypes) {
                    node = node.children.computeIfAbsent(t, k -> new TrieNode());
                }
                node.variadic.add(f);
            }
        }
    }

    public List<Function> findMatches(List<String> argumentTypes) {
        List<Function> res = new ArrayList<>(exact.getOrDefault(argumentTypes, Collections.emptyList()));
        int k = argumentTypes.size();
        if (k > 0) {
            // j = start of the last run of identical types
            int j = k - 1;
            while (j > 0 && argumentTypes.get(j - 1).equals(argumentTypes.get(k - 1))) j--;

            TrieNode node = root;
            for (int d = 1; d <= k; d++) {
                node = node.children.get(argumentTypes.get(d - 1));
                if (node == null) break;
                if (d - 1 >= j) res.addAll(node.variadic);
            }
        }
        res.sort(Comparator.comparing(f -> f.name)); // deterministic output
        return res;
    }

    // ---------------- Brute force (for comparison / validation) ----------------
    static boolean matches(Function f, List<String> args) {
        List<String> p = f.argumentTypes;
        if (!f.isVariadic) return p.equals(args);
        if (p.isEmpty() || args.size() < p.size()) return false;
        for (int i = 0; i < args.size(); i++) {
            String expected = p.get(Math.min(i, p.size() - 1));
            if (!expected.equals(args.get(i))) return false;
        }
        return true;
    }

    public static void main(String[] args) {
        String S = "String", I = "Integer";
        Set<Function> fs = new LinkedHashSet<>(List.of(
            new Function("FuncA", List.of(S, I, I), false),
            new Function("FuncB", List.of(S, I), true),
            new Function("FuncC", List.of(I), true),
            new Function("FuncD", List.of(I, I), true),
            new Function("FuncE", List.of(I, I, I), false),
            new Function("FuncF", List.of(S), false),
            new Function("FuncG", List.of(I), false)
        ));
        FunctionLibrary lib = new FunctionLibrary();
        lib.register(fs);

        List<List<String>> queries = List.of(
            List.of(S), List.of(I), List.of(I, I, I, I), List.of(I, I, I),
            List.of(S, I, I, I), List.of(S, I, I), List.of(), List.of(S, S), List.of(I, S)
        );
        for (List<String> q : queries) {
            List<Function> fast = lib.findMatches(q);
            List<Function> brute = new ArrayList<>();
            for (Function f : fs) if (matches(f, q)) brute.add(f);
            brute.sort(Comparator.comparing(f -> f.name));
            System.out.printf("%-40s -> %-24s %s%n", q, fast,
                fast.toString().equals(brute.toString()) ? "OK" : "MISMATCH brute=" + brute);
        }
    }
}

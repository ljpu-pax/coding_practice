import java.util.*;

/**
 * Confluent tech screen: function signature matching.
 *
 * Functions are registered with a list of parameter types. Given a query
 * (list of argument types), find a registered function that can accept it.
 *
 * Part 1: exact match  -> hash the type list: Map<List<String>, String>. O(k) per query.
 * Follow-up 1: optional arguments (trailing params with defaults).
 *   A function with r required + o optional params accepts arg counts r..r+o.
 *   Simple: at registration, register every prefix length r..r+o into the exact map.
 * Follow-up 2: variable number of arguments (last param is varargs "T...").
 *   Fixed params must match the prefix; every remaining arg must be T (0 or more).
 *   Store varargs functions in a Trie keyed by the fixed prefix; walk the query,
 *   at each trie node check whether a varargs function starting here accepts the tail.
 *
 * Ambiguity: report whichever policy the interviewer wants. Here: exact / optional
 * matches win over varargs matches; among varargs prefer the longest fixed prefix.
 */
public class FunctionSignatureMatcher {

    static class Param {
        final String type;
        final boolean optional, varargs;
        Param(String type, boolean optional, boolean varargs) {
            this.type = type; this.optional = optional; this.varargs = varargs;
        }
        static Param of(String t) { return new Param(t, false, false); }
        static Param opt(String t) { return new Param(t, true, false); }
        static Param var(String t) { return new Param(t, false, true); }
    }

    static class TrieNode {
        final Map<String, TrieNode> next = new HashMap<>();
        // varargs element type -> function name (for varargs functions whose fixed prefix ends here)
        final Map<String, String> varargs = new HashMap<>();
    }

    private final Map<List<String>, String> exact = new HashMap<>();
    private final TrieNode root = new TrieNode();

    public void register(String name, List<Param> params) {
        List<String> required = new ArrayList<>();
        List<String> optional = new ArrayList<>();
        String varType = null;
        for (int i = 0; i < params.size(); i++) {
            Param p = params.get(i);
            if (p.varargs) {
                if (i != params.size() - 1) throw new IllegalArgumentException("varargs must be last");
                varType = p.type;
            } else if (p.optional) {
                optional.add(p.type);
            } else {
                if (!optional.isEmpty()) throw new IllegalArgumentException("required after optional");
                required.add(p.type);
            }
        }
        if (varType != null) {
            // optional + varargs: each optional-prefix length gets its own varargs entry
            for (int k = 0; k <= optional.size(); k++) {
                List<String> fixed = new ArrayList<>(required);
                fixed.addAll(optional.subList(0, k));
                TrieNode node = root;
                for (String t : fixed) node = node.next.computeIfAbsent(t, x -> new TrieNode());
                node.varargs.putIfAbsent(varType, name);
            }
        } else {
            for (int k = 0; k <= optional.size(); k++) {
                List<String> sig = new ArrayList<>(required);
                sig.addAll(optional.subList(0, k));
                exact.putIfAbsent(sig, name);
            }
        }
    }

    /** Returns a matching function name, or null. */
    public String match(List<String> args) {
        String e = exact.get(args);
        if (e != null) return e;

        // suffixAllSame[i] = type if args[i..] are all the same type (or "" if empty)
        int n = args.size();
        String[] suffix = new String[n + 1];
        suffix[n] = "";
        for (int i = n - 1; i >= 0; i--) {
            String rest = suffix[i + 1];
            suffix[i] = (rest != null && (rest.isEmpty() || rest.equals(args.get(i)))) ? args.get(i) : null;
        }

        String best = null;
        TrieNode node = root;
        for (int i = 0; node != null; i++) {
            String tail = suffix[i];
            if (tail != null) {
                if (tail.isEmpty()) {
                    // zero varargs: any varargs function ending here matches
                    if (!node.varargs.isEmpty()) best = node.varargs.values().iterator().next();
                } else if (node.varargs.containsKey(tail)) {
                    best = node.varargs.get(tail);
                }
            }
            if (i == n) break;
            node = node.next.get(args.get(i));
        }
        return best; // last assignment = longest fixed prefix
    }

    public static void main(String[] args) {
        FunctionSignatureMatcher m = new FunctionSignatureMatcher();
        m.register("add", List.of(Param.of("int"), Param.of("int")));
        m.register("concat", List.of(Param.of("str"), Param.of("str")));
        m.register("log", List.of(Param.of("str"), Param.opt("int"), Param.opt("bool")));
        m.register("sum", List.of(Param.var("int")));
        m.register("printf", List.of(Param.of("str"), Param.var("any")));
        m.register("format", List.of(Param.of("str"), Param.var("str")));

        System.out.println(m.match(List.of("int", "int")));               // add
        System.out.println(m.match(List.of("str", "str")));               // concat
        System.out.println(m.match(List.of("str")));                      // log
        System.out.println(m.match(List.of("str", "int", "bool")));       // log
        System.out.println(m.match(List.of("int", "int", "int")));        // sum
        System.out.println(m.match(List.of()));                           // sum (zero varargs)
        System.out.println(m.match(List.of("str", "str", "str")));        // format
        System.out.println(m.match(List.of("str", "any", "any")));        // printf
        System.out.println(m.match(List.of("bool")));                     // null
    }
}

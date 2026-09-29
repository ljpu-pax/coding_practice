import java.util.*;

/**
 * SignatureMatch adapted to an online-judge template: solution(Object functions, Object callArgs).
 * Inputs may arrive as List or arrays; example 1 passes a single function, example 2 a list.
 * Same recursion + memo as SignatureMatch.java. Run: java SignatureMatchOJ
 */

class Solution {
    public static boolean solution(Object functions, Object callArgs) {
        List<String> args = new ArrayList<>();
        for (Object a : toList(callArgs)) args.add((String) a);

        List<Object> fs = toList(functions);
        // Example 1 passes ONE function: [('int','required'), ...]. Wrap it into a list of functions.
        if (!fs.isEmpty()) {
            List<Object> first = toList(fs.get(0));
            if (!first.isEmpty() && first.get(0) instanceof String) fs = List.of(functions);
        }

        for (Object f : fs) {
            List<Object> ps = toList(f);
            String[] types = new String[ps.size()], kinds = new String[ps.size()];
            for (int i = 0; i < ps.size(); i++) {
                List<Object> pair = toList(ps.get(i));   // (type, kind)
                types[i] = (String) pair.get(0);
                kinds[i] = (String) pair.get(1);
            }
            byte[][] memo = new byte[types.length + 1][args.size() + 1];
            if (match(types, kinds, args, 0, 0, memo)) return true;
        }
        return false;
    }

    // match(i, j): can params[i..] accept args[j..]?
    private static boolean match(String[] types, String[] kinds, List<String> args,
                                 int i, int j, byte[][] memo) {
        if (i == types.length) return j == args.size();
        if (memo[i][j] != 0) return memo[i][j] == 1;

        boolean typeOk = j < args.size() && args.get(j).equals(types[i]);
        boolean res;
        if (kinds[i].equals("required")) {
            res = typeOk && match(types, kinds, args, i + 1, j + 1, memo);
        } else if (kinds[i].equals("optional")) {
            res = match(types, kinds, args, i + 1, j, memo)                        // skip
               || (typeOk && match(types, kinds, args, i + 1, j + 1, memo));      // use
        } else { // variadic, always last
            res = true;
            for (int k = j; k < args.size(); k++) {
                if (!args.get(k).equals(types[i])) { res = false; break; }
            }
        }
        memo[i][j] = (byte) (res ? 1 : 2);
        return res;
    }

    // The judge may pass List or arrays; accept both.
    @SuppressWarnings("unchecked")
    private static List<Object> toList(Object o) {
        if (o == null) return new ArrayList<>();
        if (o instanceof List) return (List<Object>) o;
        if (o instanceof Object[]) return Arrays.asList((Object[]) o);
        throw new IllegalArgumentException("unexpected input type: " + o.getClass());
    }
}

public class SignatureMatchOJ {
    public static void main(String[] x) {
        // Example 1, as Lists: single function
        Object f1 = List.of(List.of("int", "required"), List.of("str", "optional"), List.of("bool", "required"));
        System.out.println(Solution.solution(f1, List.of("int", "bool")));                    // true
        // Example 2, as Lists: list of functions
        Object f2 = List.of(
            List.of(List.of("int", "required"), List.of("str", "variadic")),
            List.of(List.of("int", "required"), List.of("bool", "required")));
        System.out.println(Solution.solution(f2, List.of("int", "str", "str")));              // true
        System.out.println(Solution.solution(f2, List.of("int", "str", "bool")));             // false
        // Same inputs as arrays
        Object a1 = new Object[]{new String[]{"int", "required"}, new String[]{"str", "optional"}, new String[]{"bool", "required"}};
        System.out.println(Solution.solution(a1, new String[]{"int", "bool"}));                 // true
        Object a2 = new Object[]{
            new Object[]{new String[]{"int", "required"}, new String[]{"str", "variadic"}},
            new Object[]{new String[]{"int", "required"}, new String[]{"bool", "required"}}};
        System.out.println(Solution.solution(a2, new String[]{"int", "str", "str"}));           // true
        // Greedy trap, no functions, empty function
        Object f3 = List.of(List.of("int", "optional"), List.of("int", "required"));
        System.out.println(Solution.solution(f3, List.of("int")));                            // true
        System.out.println(Solution.solution(List.of(), List.of()));                          // false
        System.out.println(Solution.solution(List.of(List.of()), List.of()));                 // true
    }
}

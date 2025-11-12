import java.util.*;

public class EquationSolver {
    static int count = 0;
    static List<String> validEquations = new ArrayList<>();

    public static void main(String[] args) {
        solve();
        System.out.println("Total valid equations: " + count);
        runTests();
    }

    public static void solve() {
        count = 0;
        validEquations.clear();
        permute(new ArrayList<>(), new boolean[10]); // 1~9, use index 1-9
    }

    static void permute(List<Integer> current, boolean[] used) {
        if (current.size() == 9) {
            int abc = current.get(0) * 100 + current.get(1) * 10 + current.get(2);
            int def = current.get(3) * 100 + current.get(4) * 10 + current.get(5);
            int ghi = current.get(6) * 100 + current.get(7) * 10 + current.get(8);
            if (abc + def == ghi) {
                count++;
                validEquations.add(abc + " + " + def + " = " + ghi);
            }
            return;
        }

        for (int i = 1; i <= 9; i++) {
            if (!used[i]) {
                used[i] = true;
                current.add(i);
                permute(current, used);
                current.remove(current.size() - 1);
                used[i] = false;
            }
        }
    }

    // Test harness
    public static void runTests() {
        // ✅ Test 1: Total count
        assert count == 72 : "Expected 72 valid equations but got " + count;

        // ✅ Test 2: Contains a known valid equation
        assert validEquations.contains("123 + 456 = 579") : "Missing expected equation 123 + 456 = 579";

        // ✅ Test 3: No duplicate digits in each equation
        for (String eq : validEquations) {
            String digits = eq.replaceAll("[^0-9]", "");
            Set<Character> set = new HashSet<>();
            for (char c : digits.toCharArray()) {
                set.add(c);
            }
            assert set.size() == 9 : "Equation has duplicate digits: " + eq;
        }

        System.out.println("✅ All tests passed!");
    }
}


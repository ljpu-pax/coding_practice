import java.util.*;

/**
 * Confluent onsite (2024-2025): Get Best Price.
 * Source: FastPrep "confluent-get-best-price".
 *
 * public float getBestPrice(String[][] menu, String[] userWants)
 *   menu row = [price, "comma, separated, items"]  (single items or "Value Meal" bundles)
 *   Return the minimum total price to obtain every item in userWants.
 *   A bundle may include extra items the user doesn't want - that's fine.
 *   At most 3 unique wanted items; the input is always solvable.
 *
 *   menu = [["5.00","pizza"], ["8.00","sandwich, coke"], ["4.00","pasta"], ["2.00","coke"],
 *           ["6.00","pasta, coke, pizza"], ["8.00","burger, coke, pizza"], ["5.00","sandwich"]]
 *   userWants = ["burger","pasta"] -> 12.0  (pasta 4 + burger/coke/pizza 8)
 *
 * Solution: bitmask DP (set cover over at most k = 3 wanted items -> 8 states).
 *   For each menu row, mask = which wanted items it covers (skip rows covering none).
 *   dp[0] = 0; dp[s | mask] = min(dp[s] + price).  Answer dp[(1<<k) - 1].
 *   O(2^k * M) time, M = menu rows. Works for any k up to ~20.
 *
 * Talking points:
 * - Greedy (cheapest per item / biggest bundle first) is wrong: bundles overlap.
 * - Use cents (long) instead of float to avoid rounding errors; convert at the end.
 * - Duplicate wanted items ("coke","coke") -> dedupe unless quantities matter
 *   (then the state becomes a count vector instead of a bitmask).
 */
public class GetBestPrice {

    public float getBestPrice(String[][] menu, String[] userWants) {
        List<String> wanted = new ArrayList<>(new LinkedHashSet<>(Arrays.asList(userWants)));
        int k = wanted.size();
        Map<String, Integer> bit = new HashMap<>();
        for (int i = 0; i < k; i++) bit.put(wanted.get(i).trim(), i);

        int full = (1 << k) - 1;
        long[] dp = new long[1 << k];
        Arrays.fill(dp, Long.MAX_VALUE);
        dp[0] = 0;

        List<long[]> options = new ArrayList<>(); // {mask, cents}
        for (String[] row : menu) {
            long cents = Math.round(Double.parseDouble(row[0].trim()) * 100);
            int mask = 0;
            for (String item : row[1].split(",")) {
                Integer b = bit.get(item.trim());
                if (b != null) mask |= 1 << b;
            }
            if (mask != 0) options.add(new long[]{mask, cents});
        }

        // Increasing order of s works because s | mask >= s.
        for (int s = 0; s <= full; s++) {
            if (dp[s] == Long.MAX_VALUE) continue;
            for (long[] o : options) {
                int ns = s | (int) o[0];
                dp[ns] = Math.min(dp[ns], dp[s] + o[1]);
            }
        }
        return dp[full] == Long.MAX_VALUE ? -1f : dp[full] / 100f;
    }

    public static void main(String[] args) {
        String[][] menu = {
            {"5.00", "pizza"}, {"8.00", "sandwich, coke"}, {"4.00", "pasta"}, {"2.00", "coke"},
            {"6.00", "pasta, coke, pizza"}, {"8.00", "burger, coke, pizza"}, {"5.00", "sandwich"}
        };
        GetBestPrice g = new GetBestPrice();
        System.out.println(g.getBestPrice(menu, new String[]{"burger", "pasta"}));          // 12.0
        System.out.println(g.getBestPrice(menu, new String[]{"pizza", "pasta"}));           // 6.0 (bundle)
        System.out.println(g.getBestPrice(menu, new String[]{"sandwich", "coke"}));         // 7.0 (5 + 2 beats 8)
        System.out.println(g.getBestPrice(menu, new String[]{"pizza", "coke", "sandwich"})); // 11.0 (6 + 5)
        System.out.println(g.getBestPrice(menu, new String[]{"coke"}));                     // 2.0
    }
}

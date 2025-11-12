package robinhood;

import java.util.*;

public class RobinhoodReferralLeaderboard {

    public static List<String> getTopReferrers(String[] rh_users, String[] new_users) {
        // Step 1: Build the graph
        Map<String, List<String>> referralMap = new HashMap<>();
        Set<String> allUsers = new HashSet<>();

        for (int i = 0; i < rh_users.length; i++) {
            String referrer = rh_users[i];
            String referred = new_users[i];
            referralMap.computeIfAbsent(referrer, k -> new ArrayList<>()).add(referred);
            allUsers.add(referrer);
            allUsers.add(referred);
        }

        // Step 2: DFS to count total referrals
        Map<String, Integer> totalReferrals = new HashMap<>();

        for (String user : allUsers) {
            totalReferrals.put(user, dfs(user, referralMap, new HashMap<>()));
        }

        // Step 3: Filter out users with 0 referrals
        List<Map.Entry<String, Integer>> referralList = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : totalReferrals.entrySet()) {
            if (entry.getValue() > 0) {
                referralList.add(entry);
            }
        }

        // Step 4: Sort by referral count desc, then name asc
        referralList.sort((a, b) -> {
            if (!b.getValue().equals(a.getValue())) {
                return b.getValue() - a.getValue();
            } else {
                return a.getKey().compareTo(b.getKey());
            }
        });

        // Step 5: Format top 3
        List<String> result = new ArrayList<>();
        for (int i = 0; i < Math.min(3, referralList.size()); i++) {
            Map.Entry<String, Integer> entry = referralList.get(i);
            result.add(entry.getKey() + " " + entry.getValue());
        }

        return result;
    }

    private static int dfs(String user, Map<String, List<String>> graph, Map<String, Integer> memo) {
        if (memo.containsKey(user)) return memo.get(user);

        int count = 0;
        if (graph.containsKey(user)) {
            for (String child : graph.get(user)) {
                count += 1 + dfs(child, graph, memo);
            }
        }

        memo.put(user, count);
        return count;
    }

    // Example usage
    public static void main(String[] args) {
        String[] rh_users = {"A", "B", "C"};
        String[] new_users = {"B", "C", "D"};
        List<String> leaderboard = getTopReferrers(rh_users, new_users);
        for (String s : leaderboard) {
            System.out.println(s);
        }
    }
}


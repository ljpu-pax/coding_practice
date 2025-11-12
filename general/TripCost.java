public class TripCost {
    public int minRoundTripCost(int[] D, int[] R) {
        int n = D.length;
        if (n <= 1) return -1;

        int minDepart = D[0];
        int minCost = Integer.MAX_VALUE;

        for (int j = 1; j < n; j++) {
            minCost = Math.min(minCost, minDepart + R[j]);
            minDepart = Math.min(minDepart, D[j]);
        }

        return minCost;
    }

    public static void main(String[] args) {
        TripCost sol = new TripCost();
        int[] D = {10, 8, 9, 11, 7};
        int[] R = {8, 8, 10, 7, 9};
        System.out.println(sol.minRoundTripCost(D, R)); // Output: 15
    }
}

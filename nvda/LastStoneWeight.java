package nvda;

import java.util.*;

public class LastStoneWeight {

    public static int lastStoneWeight(int[] stones) {
        PriorityQueue<Integer> pq = new PriorityQueue<>(Comparator.reverseOrder());
        for (int s : stones) pq.add(s);
        while (pq.size() > 1) {
            int y = pq.poll();
            int x = pq.poll();
            if (y != x) pq.add(y - x);
        }
        return pq.isEmpty() ? 0 : pq.peek();
    }

    public static void main(String[] args) {
        System.out.println(lastStoneWeight(new int[]{2,7,4,1,8,1})); // 1
    }
}



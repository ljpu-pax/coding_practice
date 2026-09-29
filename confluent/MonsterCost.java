import java.util.*;

/**
 * Confluent onsite: Find Minimum Monster Cost (asked together with tail -n, 2026).
 * Source: PracHub "Implement Tail and Find Monster Cost".
 *
 * Grid symbols:
 *   'S' start, 'E' exit, '.' empty (cost 0), 'M' monster (cost 1 to enter), '#' wall.
 * Move up/down/left/right. Return the minimum total monster cost from S to E,
 * or -1 if E is unreachable.  1 <= rows, cols <= 300, exactly one S and one E.
 *
 *   ["S#", "#E"] -> -1
 *
 * Part 1: edge weights are only 0 or 1 -> 0-1 BFS with a deque:
 *   entering a cost-0 cell -> push front, cost-1 cell -> push back.
 *   O(R*C) time and space. (Plain BFS is wrong: fewest steps != fewest monsters.
 *   DFS enumerating paths is exponential.)
 * Follow-up 1: return one min-cost path as [row, col] list -> keep a parent pointer.
 * Follow-up 2: arbitrary non-negative monster costs given in a cost matrix
 *   -> Dijkstra with a min-heap, O(R*C log(R*C)).
 */
public class MonsterCost {

    static final int[][] DIRS = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

    // ---------------- Part 1: 0-1 BFS (cost only) ----------------
    public static int minCost(String[] grid) {
        int R = grid.length, C = grid[0].length();
        int[] s = find(grid, 'S');
        int[][] dist = new int[R][C];
        for (int[] row : dist) Arrays.fill(row, Integer.MAX_VALUE);
        Deque<int[]> dq = new ArrayDeque<>();
        dist[s[0]][s[1]] = 0;
        dq.add(s);
        while (!dq.isEmpty()) {
            int[] cur = dq.pollFirst();
            int r0 = cur[0], c0 = cur[1];
            if (grid[r0].charAt(c0) == 'E') return dist[r0][c0];
            for (int[] d : DIRS) {
                int r = r0 + d[0], c = c0 + d[1];
                if (r < 0 || c < 0 || r >= R || c >= C || grid[r].charAt(c) == '#') continue;
                int w = grid[r].charAt(c) == 'M' ? 1 : 0;
                if (dist[r0][c0] + w < dist[r][c]) {
                    dist[r][c] = dist[r0][c0] + w;
                    if (w == 0) dq.addFirst(new int[]{r, c});  // free step: same cost level
                    else dq.addLast(new int[]{r, c});          // monster: next cost level
                }
            }
        }
        return -1;
    }

    static class Result {
        final int cost;
        final List<int[]> path;
        Result(int cost, List<int[]> path) { this.cost = cost; this.path = path; }
    }

    // ---------------- Follow-up 1: 0-1 BFS + path ----------------
    public static Result minCostPath(String[] grid) {
        int R = grid.length, C = grid[0].length();
        int[] s = find(grid, 'S'), e = find(grid, 'E');
        int[] dist = new int[R * C], parent = new int[R * C];
        Arrays.fill(dist, Integer.MAX_VALUE);
        Arrays.fill(parent, -1);
        Deque<Integer> dq = new ArrayDeque<>();
        int start = s[0] * C + s[1], end = e[0] * C + e[1];
        dist[start] = 0;
        dq.add(start);
        while (!dq.isEmpty()) {
            int cur = dq.pollFirst();
            if (cur == end) break; // first time E is popped its distance is final
            int r0 = cur / C, c0 = cur % C;
            for (int[] d : DIRS) {
                int r = r0 + d[0], c = c0 + d[1];
                if (r < 0 || c < 0 || r >= R || c >= C || grid[r].charAt(c) == '#') continue;
                int w = grid[r].charAt(c) == 'M' ? 1 : 0;
                int id = r * C + c;
                if (dist[cur] + w < dist[id]) {
                    dist[id] = dist[cur] + w;
                    parent[id] = cur;
                    if (w == 0) dq.addFirst(id); else dq.addLast(id);
                }
            }
        }
        if (dist[end] == Integer.MAX_VALUE) return null;
        return new Result(dist[end], buildPath(parent, end, C));
    }

    // ---------------- Follow-up 2: arbitrary costs -> Dijkstra ----------------
    /** cost[r][c] = cost of entering (r,c); walls still come from grid. Returns -1 if unreachable. */
    public static Result minCostWeighted(String[] grid, int[][] cost) {
        int R = grid.length, C = grid[0].length();
        int[] s = find(grid, 'S'), e = find(grid, 'E');
        long[] dist = new long[R * C];
        int[] parent = new int[R * C];
        Arrays.fill(dist, Long.MAX_VALUE);
        Arrays.fill(parent, -1);
        int start = s[0] * C + s[1], end = e[0] * C + e[1];
        PriorityQueue<long[]> pq = new PriorityQueue<>(Comparator.comparingLong(a -> a[0]));
        dist[start] = 0;
        pq.add(new long[]{0, start});
        while (!pq.isEmpty()) {
            long[] top = pq.poll();
            int cur = (int) top[1];
            if (top[0] > dist[cur]) continue; // stale entry
            if (cur == end) break;
            int r0 = cur / C, c0 = cur % C;
            for (int[] d : DIRS) {
                int r = r0 + d[0], c = c0 + d[1];
                if (r < 0 || c < 0 || r >= R || c >= C || grid[r].charAt(c) == '#') continue;
                int id = r * C + c;
                long nd = dist[cur] + cost[r][c];
                if (nd < dist[id]) {
                    dist[id] = nd;
                    parent[id] = cur;
                    pq.add(new long[]{nd, id});
                }
            }
        }
        if (dist[end] == Long.MAX_VALUE) return new Result(-1, new ArrayList<>());
        return new Result((int) dist[end], buildPath(parent, end, C));
    }

    static List<int[]> buildPath(int[] parent, int end, int C) {
        LinkedList<int[]> path = new LinkedList<>();
        for (int id = end; id != -1; id = parent[id]) path.addFirst(new int[]{id / C, id % C});
        return path;
    }

    static int[] find(String[] grid, char target) {
        for (int r = 0; r < grid.length; r++) {
            int c = grid[r].indexOf(target);
            if (c >= 0) return new int[]{r, c};
        }
        throw new IllegalArgumentException("missing " + target);
    }

    static String fmt(List<int[]> path) {
        StringBuilder sb = new StringBuilder("[");
        for (int[] p : path) sb.append(sb.length() > 1 ? ", " : "").append(Arrays.toString(p));
        return sb.append("]").toString();
    }

    public static void main(String[] args) {
        System.out.println(minCost(new String[]{"S#", "#E"})); // -1

        String[] g = {
            "S.M..",
            ".#M#.",
            "M#.#.",
            "..M.E"
        };
        // Top row route S . M . . then down: 1 monster. Left column: M + M = 2.
        System.out.println(minCost(g)); // 1
        Result r = minCostPath(g);
        System.out.println("cost=" + r.cost + " path=" + fmt(r.path)); // cost=1

        String[] noMonster = {"S...", "###.", "E..."};
        System.out.println(minCost(noMonster)); // 0 (long detour but free)

        String[] forced = {"SME"};
        System.out.println(minCost(forced)); // 1

        // Follow-up 2: weighted costs; top-row monster at (0,2) is now very expensive
        int[][] cost = new int[4][5];
        cost[0][2] = 9; cost[1][2] = 9; cost[2][0] = 1; cost[3][2] = 2;
        Result w = minCostWeighted(g, cost);
        System.out.println("weighted cost=" + w.cost + " path=" + fmt(w.path)); // 3 via left column
    }
}

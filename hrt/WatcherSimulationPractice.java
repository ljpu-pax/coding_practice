import java.util.*;

/**
 * 问题：
 *   - N 个人，initial positions [w1...wN]，每个 tick 向右移动 1 单位
 *   - 1 个 watcher，initial position W，初始朝向左或右
 *   - Watcher 在 [t1...tC] 时刻换方向
 *   - 若一个人在 watcher 朝向的那一侧（严格），该人被 "看到"，无法移动
 *   - Watcher 与人重叠时，那个人可以移动
 *   - 问：T 个时间单位后有几个人到达位置 L？
 */
public class WatcherSimulationPractice {

    public static int solve(int[] personPos, int watcherPos, boolean facingRight,
                            int[] switchTimes, int T, int L) {
        // TODO
        int N = personPos.length;
        int[] pos = personPos.clone();
        boolean[] reached = new boolean[N];
        int wPos = watcherPos;
        boolean wRight = facingRight;

        Set<Integer> set = new HashSet<>();
        for (int i = 0; i < switchTimes.length; i++) set.add(switchTimes[i]);

        for (int i = 0; i < N; i++) {
            if (pos[i] >= L) reached[i] = true;
        }

        for (int t = 0; t < T; t++) {
            if (set.contains(t)) {
                wRight = !wRight;
            }
            
            for (int i = 0; i < N; i++) {
                if (reached[i]) continue;
                boolean blocked = wRight ? (pos[i] > wPos) : (pos[i] < wPos);
                if (!blocked) {
                    pos[i]++;
                    if (pos[i] >= L) reached[i] = true;
                }
            }

             wPos += wRight ? 1 : -1;
        }
        
        int count = 0;
        for (boolean r : reached) if (r) count++;
        return count;
    }

    public static void main(String[] args) {
        int[] people = {1, 4, 7};
        int watcher = 5;
        int[] change = {2, 5};
        int T = 8;
        int L = 10;

        int ans = solve(people, watcher, false, change, T, L);
        System.out.println(ans); // expected: 1
    }
}

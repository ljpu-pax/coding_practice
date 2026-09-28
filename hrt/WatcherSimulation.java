import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * 问题：
 *   - N 个人，initial positions [w1...wN]，每个 tick 向右移动 1 单位
 *   - 1 个 watcher，initial position W，初始朝向左或右
 *   - Watcher 在 [t1...tC] 时刻换方向（switchTimes 需已排序）
 *   - 若一个人在 watcher 朝向的那一侧（严格），该人被"看到"，无法移动
 *   - Watcher 与人重叠时，那个人可以移动
 *   - 问：T 个时间单位后有几个人到达位置 L？
 *
 * 解法：暴力模拟 O(T * N)
 *
 * Follow-up（continuous time，见 solveContinuous）：
 *   - 无离散 tick，时间连续，人与 watcher 以速度 1 连续移动
 *   - 按 switchTimes 把 [0, T] 切成 segment，每段内 watcher 方向固定
 *   - 每段内 watcher 位置线性：wPos(t) = wPos(segStart) ± (t - segStart)
 *   - 若人在 blocked 侧：watcher 追上人的时刻 t* = |p - w|
 *     - t* >= segLen → 整段被 blocked，人不动
 *     - t* <  segLen → 从 t* 起人自由移动 (segLen - t*) 的距离
 *   - 若人不在 blocked 侧：整段自由移动 segLen
 */
public class WatcherSimulation {

  /**
   * Discrete simulation: returns the number of people who reach position L within T ticks.
   *
   * @param personPos   initial positions of N people
   * @param watcherPos  initial watcher position
   * @param facingRight true if watcher initially faces right
   * @param switchTimes ticks at which watcher flips direction (must be sorted)
   * @param t           total number of ticks to simulate
   * @param l           target position
   */
  public static int solve(
      int[] personPos,
      int watcherPos,
      boolean facingRight,
      int[] switchTimes,
      int t,
      int l) {
    int n = personPos.length;
    int[] pos = personPos.clone();
    boolean[] reached = new boolean[n];
    int wPos = watcherPos;
    boolean wRight = facingRight;

    Set<Integer> switchSet = new HashSet<>();
    for (int time : switchTimes) {
      switchSet.add(time);
    }

    for (int i = 0; i < n; i++) {
      if (pos[i] >= l) {
        reached[i] = true;
      }
    }

    for (int tick = 0; tick < t; tick++) {
      if (switchSet.contains(tick)) {
        wRight = !wRight;
      }

      for (int i = 0; i < n; i++) {
        if (reached[i]) {
          continue;
        }
        boolean blocked = wRight ? (pos[i] > wPos) : (pos[i] < wPos);
        if (!blocked) {
          pos[i]++;
          if (pos[i] >= l) {
            reached[i] = true;
          }
        }
      }

      wPos += wRight ? 1 : -1;
    }

    int count = 0;
    for (boolean r : reached) {
      if (r) {
        count++;
      }
    }
    return count;
  }

  /**
   * Continuous-time version: O(C * N) where C = number of segments.
   *
   * @param personPos   initial positions of N people
   * @param watcherPos  initial watcher position
   * @param facingRight true if watcher initially faces right
   * @param switchTimes times at which watcher flips direction
   * @param t           total time
   * @param l           target position
   */
  public static int solveContinuous(
      double[] personPos,
      double watcherPos,
      boolean facingRight,
      double[] switchTimes,
      double t,
      double l) {
    int n = personPos.length;
    double[] pos = personPos.clone();
    boolean[] reached = new boolean[n];
    double wPos = watcherPos;
    boolean wRight = facingRight;

    double[] sorted = switchTimes.clone();
    Arrays.sort(sorted);

    for (int i = 0; i < n; i++) {
      if (pos[i] >= l) {
        reached[i] = true;
      }
    }

    double segStart = 0;
    for (int s = 0; s <= sorted.length; s++) {
      double segEnd = (s < sorted.length) ? sorted[s] : t;
      double segLen = segEnd - segStart;
      if (segLen <= 0) {
        continue;
      }

      for (int i = 0; i < n; i++) {
        if (reached[i]) {
          continue;
        }
        double p = pos[i];
        double freeTime;

        if (wRight) {
          if (p > wPos) {
            // blocked until watcher catches up: t* = p - wPos
            double tStar = p - wPos;
            freeTime = (tStar >= segLen) ? 0 : segLen - tStar;
          } else {
            freeTime = segLen;
          }
        } else {
          if (p < wPos) {
            // blocked until watcher catches up: t* = wPos - p
            double tStar = wPos - p;
            freeTime = (tStar >= segLen) ? 0 : segLen - tStar;
          } else {
            freeTime = segLen;
          }
        }

        pos[i] += freeTime;
        if (pos[i] >= l) {
          reached[i] = true;
        }
      }

      wPos += wRight ? segLen : -segLen;
      wRight = !wRight;
      segStart = segEnd;
    }

    int count = 0;
    for (boolean r : reached) {
      if (r) {
        count++;
      }
    }
    return count;
  }

  public static void main(String[] args) {
    int[] people = {1, 4, 7};
    int watcher = 5;
    int[] change = {2, 5};
    int totalTicks = 8;
    int target = 10;

    System.out.println("discrete:   " + solve(people, watcher, false, change, totalTicks, target));

    double[] peopleD = {1, 4, 7};
    double[] changeD = {2, 5};
    System.out.println(
        "continuous: " + solveContinuous(peopleD, watcher, false, changeD, totalTicks, target));
  }
}

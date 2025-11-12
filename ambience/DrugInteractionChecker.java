package ambience;

import java.util.*;

public class DrugInteractionChecker {

  // ===== 数据结构 =====
  public static class Interaction {
    int a, b;
    double risk;
    public Interaction(int a, int b, double risk) {
      this.a = a; this.b = b; this.risk = risk;
    }
  }

  // ===== 工具函数：无向图风险表 =====
  static Map<Integer, Map<Integer, Double>> buildRiskMap(List<Interaction> interactions) {
    Map<Integer, Map<Integer, Double>> m = new HashMap<>();
    for (Interaction it : interactions) {
      m.computeIfAbsent(it.a, k -> new HashMap<>()).put(it.b, it.risk);
      m.computeIfAbsent(it.b, k -> new HashMap<>()).put(it.a, it.risk);
    }
    return m;
  }

  static double riskBetween(int x, int y, Map<Integer, Map<Integer, Double>> m) {
    Map<Integer, Double> inner = m.get(x);
    if (inner == null) return 0.0;
    Double v = inner.get(y);
    return v == null ? 0.0 : v;
  }

  // ===== Part 1：是否存在 pair 风险 > 0.7（严格大于） =====
  static boolean hasHarmfulPair(Collection<Integer> current, int p,
                                Map<Integer, Map<Integer, Double>> m) {
    for (int d : current) {
      if (riskBetween(d, p, m) > 0.7) return true;
    }
    return false;
  }

  // ===== Part 2：是否存在三联总风险 > 0.7（严格大于）=====
  static boolean hasHarmfulTriplet(List<Integer> current, int p,
                                   Map<Integer, Map<Integer, Double>> m) {
    for (int i = 0; i < current.size(); i++) {
      int a = current.get(i);
      for (int j = i + 1; j < current.size(); j++) {
        int b = current.get(j);
        double total = riskBetween(a, p, m)
                     + riskBetween(b, p, m)
                     + riskBetween(a, b, m);
        if (total > 0.7) return true;
      }
    }
    return false;
  }

  // 当前方案是否安全（同时满足：无 pair 风险、无 triplet 风险）
  static boolean isSafe(List<Integer> current, int p,
                        Map<Integer, Map<Integer, Double>> m) {
    return !hasHarmfulPair(current, p, m) && !hasHarmfulTriplet(current, p, m);
  }

  /**
   * ===== Part 3：单次替换求解 =====
   * @param interactions  有害配对 (a,b,risk)
   * @param currentDrugs  当前用药列表
   * @param proposedDrug  拟加药 p
   * @param substitutes   等效药分组，如 [[2,4], [1,5]]
   * @return  若找到解，返回 int[]{replaceId, substituteId}；否则返回 null
   */
  public static int[] findSingleSubstitution(List<Interaction> interactions,
                                             List<Integer> currentDrugs,
                                             int proposedDrug,
                                             List<List<Integer>> substitutes) {
    Map<Integer, Map<Integer, Double>> m = buildRiskMap(interactions);

    // 若本来就安全，无需替换
    if (isSafe(currentDrugs, proposedDrug, m)) return null;

    // 把 current 放到一个可修改的列表中
    List<Integer> base = new ArrayList<>(currentDrugs);

    // 为了 O(1) 判断“某药是否在当前用药中”
    Set<Integer> currentSet = new HashSet<>(currentDrugs);

    // 遍历每个替换组；每组内任选“当前用药中的成员”替换成“同组其他成员”
    for (List<Integer> group : substitutes) {
      // 找出此组里目前正在服用的候选 replaceId
      for (int replaceId : group) {
        if (!currentSet.contains(replaceId)) continue; // 这组里此药不在当前用药中，跳过

        // 尝试组内的其他成员作为 substituteId
        for (int substituteId : group) {
          if (substituteId == replaceId) continue;

          // 应用一次替换：replaceId -> substituteId
          int pos = base.indexOf(replaceId); // 一般每个药出现一次
          if (pos == -1) continue; // 理论不会发生，容错
          int old = base.set(pos, substituteId);

          boolean ok = isSafe(base, proposedDrug, m);

          // 回滚
          base.set(pos, old);

          if (ok) return new int[]{replaceId, substituteId};
        }
      }
    }
    return null;
  }

  // ===== 演示 =====
  public static void main(String[] args) {
    List<Interaction> harmful = Arrays.asList(
        new Interaction(1, 3, 0.5),
        new Interaction(2, 3, 0.3),
        new Interaction(1, 4, 0.9)
    );
    List<Integer> current = Arrays.asList(1, 2);
    int proposed = 3;
    List<List<Integer>> subs = Arrays.asList(
        Arrays.asList(2, 4),
        Arrays.asList(1, 5)
    );

    int[] ans = findSingleSubstitution(harmful, current, proposed, subs);
    if (ans == null) {
      System.out.println("No substitution needed or possible.");
    } else {
      System.out.println("Substitute " + ans[0] + " for " + ans[1]);
      // 期望：Substitute 1 for 5
    }
  }
}


package anthropic;

import java.util.*;

class Sample {
    double ts;
    List<String> stack;
    Sample(double ts, List<String> stack) {
        this.ts = ts;
        this.stack = stack;
    }
}

public class StackTraceVariants {

    // ================== 原题：Prefix LCP ==================
    public static List<String> convertPrefix(List<Sample> samples) {
        List<String> res = new ArrayList<>();
        List<String> prev = new ArrayList<>();

        for (Sample s : samples) {
            List<String> curr = s.stack;

            // 找最长公共前缀
            int i = 0;
            while (i < prev.size() && i < curr.size() && prev.get(i).equals(curr.get(i))) {
                i++;
            }

            // 退出旧的
            for (int j = prev.size() - 1; j >= i; j--) {
                res.add("(e," + prev.get(j) + ")");
            }
            // 进入新的
            for (int j = i; j < curr.size(); j++) {
                res.add("(s," + curr.get(j) + ")");
            }

            prev = curr;
        }

        // 收尾退出
        for (int j = prev.size() - 1; j >= 0; j--) {
            res.add("(e," + prev.get(j) + ")");
        }

        return res;
    }

    // ================== Follow-up 1: N consecutive trace 消噪 ==================
    public static List<String> convertPrefixWithThreshold(List<Sample> samples, int N) {
		List<String> res = new ArrayList<>();
		Map<String, Integer> runLenByPath = new HashMap<>();
		Set<String> confirmedActive = new HashSet<>();
		List<String> prevPathKeys = new ArrayList<>();

		for (Sample s : samples) {
			List<String> curr = s.stack;

			// 构建当前样本的路径键（区分 a->b 与 c->b）
			List<String> currPathKeys = new ArrayList<>(curr.size());
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < curr.size(); i++) {
				if (i > 0) sb.append("->");
				sb.append(curr.get(i));
				currPathKeys.add(sb.toString());
			}

			// 更新连续计数（仅当同一深度的同一路径延续时才 +1）
			for (int i = 0; i < currPathKeys.size(); i++) {
				String key = currPathKeys.get(i);
				if (i < prevPathKeys.size() && prevPathKeys.get(i).equals(key)) {
					runLenByPath.put(key, runLenByPath.getOrDefault(key, 0) + 1);
				} else {
					runLenByPath.put(key, 1);
				}
			}

			// 达到阈值 N 才输出进入事件
			for (int i = 0; i < currPathKeys.size(); i++) {
				String key = currPathKeys.get(i);
				if (!confirmedActive.contains(key) && runLenByPath.getOrDefault(key, 0) >= N) {
					confirmedActive.add(key);
					res.add("(s," + curr.get(i) + ")");
				}
			}

			// 不再出现的路径要按从深到浅输出退出事件
			Set<String> currSet = new HashSet<>(currPathKeys);
			for (int i = prevPathKeys.size() - 1; i >= 0; i--) {
				String key = prevPathKeys.get(i);
				if (!currSet.contains(key)) {
					if (confirmedActive.remove(key)) {
						res.add("(e," + lastOfPath(key) + ")");
					}
					runLenByPath.remove(key);
				}
			}

			prevPathKeys = currPathKeys;
		}

		// 收尾：把仍然处于活跃状态的路径从深到浅退出
		for (int i = prevPathKeys.size() - 1; i >= 0; i--) {
			String key = prevPathKeys.get(i);
			if (confirmedActive.contains(key)) {
				res.add("(e," + lastOfPath(key) + ")");
			}
		}

		return res;
    }

	private static String lastOfPath(String key) {
		int idx = key.lastIndexOf("->");
		return idx == -1 ? key : key.substring(idx + 2);
	}

    // ================== Follow-up 2: Postfix LCSuffix ==================
    public static List<String> convertPostfix(List<Sample> samples) {
        List<String> res = new ArrayList<>();
        List<String> prev = new ArrayList<>();

        for (Sample s : samples) {
            List<String> curr = s.stack;

            // 找最长公共后缀
            int i = prev.size() - 1, j = curr.size() - 1;
            while (i >= 0 && j >= 0 && prev.get(i).equals(curr.get(j))) {
                i--; j--;
            }

            // prev[0..i] 要退出
            for (int k = i; k >= 0; k--) {
                res.add("(e," + prev.get(k) + ")");
            }
            // curr[0..j] 要进入
            for (int k = 0; k <= j; k++) {
                res.add("(s," + curr.get(k) + ")");
            }

            prev = curr;
        }

        // 收尾退出
        for (int j = prev.size() - 1; j >= 0; j--) {
            res.add("(e," + prev.get(j) + ")");
        }

        return res;
    }

    // ================== Main 测试 ==================
    public static void main(String[] args) {
        List<Sample> samples = Arrays.asList(
            new Sample(0.0, Arrays.asList("a","b","c")),
            new Sample(1.0, Arrays.asList("a","b","c")),
            new Sample(2.0, Arrays.asList("a","d")),
            new Sample(3.0, Arrays.asList("a"))
        );

        System.out.println("=== 原题 Prefix ===");
        System.out.println(convertPrefix(samples));

        System.out.println("=== Follow-up 1: Threshold N=2 ===");
        System.out.println(convertPrefixWithThreshold(samples, 2));

        System.out.println("=== Follow-up 2: Postfix ===");
        System.out.println(convertPostfix(samples));
    }
}

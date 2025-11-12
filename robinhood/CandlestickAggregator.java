package robinhood;

import java.util.*;

public class CandlestickAggregator {

    public static String aggregateCandles(String pricesToParse) {
        if (pricesToParse == null || pricesToParse.isEmpty()) return "";

        // Step 1: 解析 price:timestamp 列表
        List<int[]> prices = new ArrayList<>();
        String[] parts = pricesToParse.split(",");
        for (String p : parts) {
            String[] kv = p.split(":");
            prices.add(new int[]{Integer.parseInt(kv[0]), Integer.parseInt(kv[1])});
        }

        StringBuilder sb = new StringBuilder();
        int interval = 10;
        int idx = 0;
        int n = prices.size();
        Integer prevLast = null;

        // 从第一个时间戳所在的区间开始
        int startTime = (prices.get(0)[1] / interval) * interval;
        int lastTime = prices.get(n - 1)[1];

        for (int t = startTime; t <= lastTime; t += interval) {
            int first = 0, last = 0, max = Integer.MIN_VALUE, min = Integer.MAX_VALUE;
            boolean hasPrice = false;

            // 收集当前区间的数据
            while (idx < n && prices.get(idx)[1] < t + interval) {
                int price = prices.get(idx)[0];
                if (!hasPrice) {
                    first = price;
                    min = price;
                    max = price;
                }
                last = price;
                min = Math.min(min, price);
                max = Math.max(max, price);
                hasPrice = true;
                idx++;
            }

            if (hasPrice) {
                prevLast = last;
                sb.append(String.format("{%d,%d,%d,%d,%d}", t, first, last, max, min));
            } else if (prevLast != null) {
                // 用上一个区间的 last price 填充
                first = last = max = min = prevLast;
                sb.append(String.format("{%d,%d,%d,%d,%d}", t, first, last, max, min));
            }
            // 如果没有价格且 prevLast 为 null，跳过该区间
        }

        return sb.toString();
    }

    public static void main(String[] args) {
        System.out.println(aggregateCandles("1:0,3:10,2:12,4:19,5:35"));
        // 预期: {0,1,1,1,1}{10,3,4,4,2}{20,4,4,4,4}{30,5,5,5,5}

        System.out.println(aggregateCandles(
            "1:0,2:1,3:2,4:3,5:4,6:5,7:6,8:7,9:8,10:9," +
            "11:10,12:11,13:12,14:13,15:14,16:15,17:16,18:17,19:18,20:19"
        ));
        // 预期: {0,1,10,10,1}{10,11,20,20,11}
    }
}

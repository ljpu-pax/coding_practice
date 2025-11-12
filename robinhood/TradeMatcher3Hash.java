package robinhood;

import java.util.*;

public class TradeMatcher3Hash {

    static class Trade {
        String raw, symbol, type, quantity, id;
        Trade(String raw) {
            this.raw = raw;
            String[] p = raw.split(",", -1);
            symbol = p[0];
            type = p[1];
            quantity = p[2];
            id = p[3];
        }
    }

    public static List<String> unmatchedTrades(String[] houseArr, String[] streetArr) {
        List<Trade> house = toTrades(houseArr);
        List<Trade> street = toTrades(streetArr);

        // 排序保证 tie-break
        Comparator<Trade> cmp = Comparator.comparing(t -> t.raw);
        house.sort(cmp);
        street.sort(cmp);

        // Step1: Exact match
        exactMatch(house, street);
        // Step2: Fuzzy match
        fuzzyMatch(house, street);
        // Step3: Offsetting match
        offsettingMatch(house);
        offsettingMatch(street);

        // 收集结果并排序
        List<String> res = new ArrayList<>();
        for (Trade t : house) res.add(t.raw);
        for (Trade t : street) res.add(t.raw);
        Collections.sort(res);
        return res;
    }

    // 精确匹配 O(n)
    private static void exactMatch(List<Trade> house, List<Trade> street) {
        Map<String, Queue<Trade>> map = new HashMap<>();
        for (Trade s : street) {
            String key = s.symbol + "|" + s.type + "|" + s.quantity + "|" + s.id;
            map.computeIfAbsent(key, k -> new LinkedList<>()).add(s);
        }
        List<Trade> remainHouse = new ArrayList<>();
        for (Trade h : house) {
            String key = h.symbol + "|" + h.type + "|" + h.quantity + "|" + h.id;
            Queue<Trade> q = map.get(key);
            if (q != null && !q.isEmpty()) {
                q.poll(); // 匹配成功
            } else {
                remainHouse.add(h);
            }
        }
        house.clear();
        house.addAll(remainHouse);

        // 剩余 street
        List<Trade> remainStreet = new ArrayList<>();
        for (Queue<Trade> q : map.values()) {
            remainStreet.addAll(q);
        }
        street.clear();
        street.addAll(remainStreet);
    }

    // 模糊匹配（忽略 ID） O(n)
    private static void fuzzyMatch(List<Trade> house, List<Trade> street) {
        Map<String, Queue<Trade>> map = new HashMap<>();
        for (Trade s : street) {
            String key = s.symbol + "|" + s.type + "|" + s.quantity;
            map.computeIfAbsent(key, k -> new LinkedList<>()).add(s);
        }
        List<Trade> remainHouse = new ArrayList<>();
        for (Trade h : house) {
            String key = h.symbol + "|" + h.type + "|" + h.quantity;
            Queue<Trade> q = map.get(key);
            if (q != null && !q.isEmpty()) {
                q.poll();
            } else {
                remainHouse.add(h);
            }
        }
        house.clear();
        house.addAll(remainHouse);

        List<Trade> remainStreet = new ArrayList<>();
        for (Queue<Trade> q : map.values()) {
            remainStreet.addAll(q);
        }
        street.clear();
        street.addAll(remainStreet);
    }

    // 对冲匹配（同列表） O(n)
    private static void offsettingMatch(List<Trade> trades) {
        Map<String, Queue<Trade>> buys = new HashMap<>();
        Map<String, Queue<Trade>> sells = new HashMap<>();

        for (Trade t : trades) {
            String key = t.symbol + "|" + t.quantity;
            if (t.type.equals("B")) {
                buys.computeIfAbsent(key, k -> new LinkedList<>()).add(t);
            } else {
                sells.computeIfAbsent(key, k -> new LinkedList<>()).add(t);
            }
        }

        List<Trade> remain = new ArrayList<>();
        for (String key : buys.keySet()) {
            Queue<Trade> bq = buys.get(key);
            Queue<Trade> sq = sells.getOrDefault(key, new LinkedList<>());
            while (!bq.isEmpty() && !sq.isEmpty()) {
                bq.poll();
                sq.poll(); // 匹配 B-S
            }
            remain.addAll(bq);
            if (sells.containsKey(key)) {
                sells.put(key, sq);
            }
        }
        // 把卖单里没有配对的也加上
        for (Queue<Trade> q : sells.values()) {
            remain.addAll(q);
        }

        trades.clear();
        trades.addAll(remain);
    }

    private static List<Trade> toTrades(String[] arr) {
        List<Trade> list = new ArrayList<>();
        for (String s : arr) list.add(new Trade(s));
        return list;
    }

    // 测试
    public static void main(String[] args) {
        String[] house = {
            "AAPL,B,0100,ABC123",
            "AAPL,B,0100,ABC123",
            "GOOG,S,0050,CDC333"
        };
        String[] street = {
            "  FB,B,0100,GBGGGG",
            "AAPL,B,0100,ABC123"
        };
        System.out.println(unmatchedTrades(house, street));
    }
}

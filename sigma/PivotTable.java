package sigma;

import java.util.*;

public class PivotTable {

    public static class Row {
        public final Map<String, String> columns;
        public Row(Map<String, String> columns) {
            this.columns = columns;
        }
        public String get(String key) {
            return columns.get(key);
        }
    }

    public static class Spec {
        public final String valueCol; // column to sum
        public final String xCol;     // column for x-axis (columns)
        public final String yCol;     // column for y-axis (rows)
        public Spec(String valueCol, String xCol, String yCol) {
            this.valueCol = valueCol;
            this.xCol = xCol;
            this.yCol = yCol;
        }
    }

    public static class Table {
        public final List<String> header; // first column is label header, others are x-axis values
        public final List<List<String>> body; // rows: [y, v1, v2, ...]
        public Table(List<String> header, List<List<String>> body) {
            this.header = header;
            this.body = body;
        }
    }

    // Build pivot: sum(valueCol) grouped by (yCol, xCol)
    public static Table pivot(List<Row> rows, Spec spec) {
        // Collect distinct x and y in stable order of first appearance
        LinkedHashSet<String> xValues = new LinkedHashSet<>();
        LinkedHashSet<String> yValues = new LinkedHashSet<>();

        // sums[y][x] -> long
        Map<String, Map<String, Long>> sums = new LinkedHashMap<>();

        for (Row r : rows) {
            String x = r.get(spec.xCol);
            String y = r.get(spec.yCol);
            String vStr = r.get(spec.valueCol);
            if (x == null || y == null || vStr == null) continue; // skip incomplete rows

            long v;
            try {
                v = Long.parseLong(vStr);
            } catch (NumberFormatException e) {
                continue; // skip invalid numeric values
            }

            xValues.add(x);
            yValues.add(y);

            sums.computeIfAbsent(y, k -> new LinkedHashMap<>())
                .merge(x, v, Long::sum);
        }

        List<String> xList = new ArrayList<>(xValues);
        List<String> yList = new ArrayList<>(yValues);

        // Header: ["Sum of " + valueCol, x1, x2, ...]
        List<String> header = new ArrayList<>();
        header.add("Sum of " + spec.valueCol);
        header.addAll(xList);

        // Body rows ordered by y, filling missing pairs with 0
        List<List<String>> body = new ArrayList<>();
        for (String y : yList) {
            List<String> row = new ArrayList<>();
            row.add(y);
            Map<String, Long> rowSums = sums.getOrDefault(y, Collections.emptyMap());
            for (String x : xList) {
                long v = rowSums.getOrDefault(x, 0L);
                row.add(Long.toString(v));
            }
            body.add(row);
        }

        return new Table(header, body);
    }

    // Pretty print helper for demonstration
    public static void print(Table t) {
        // compute column widths
        List<Integer> widths = new ArrayList<>();
        for (int c = 0; c < t.header.size(); c++) {
            int w = t.header.get(c).length();
            for (List<String> row : t.body) w = Math.max(w, row.get(c).length());
            widths.add(w);
        }

        // print header
        StringBuilder sb = new StringBuilder();
        for (int c = 0; c < t.header.size(); c++) {
            if (c > 0) sb.append(" | ");
            sb.append(padRight(t.header.get(c), widths.get(c)));
        }
        System.out.println(sb.toString());

        // print rows
        for (List<String> row : t.body) {
            sb.setLength(0);
            for (int c = 0; c < row.size(); c++) {
                if (c > 0) sb.append(" | ");
                sb.append(padRight(row.get(c), widths.get(c)));
            }
            System.out.println(sb.toString());
        }
    }

    private static String padRight(String s, int width) {
        if (s.length() >= width) return s;
        StringBuilder sb = new StringBuilder(s);
        while (sb.length() < width) sb.append(' ');
        return sb.toString();
    }

    public static void main(String[] args) {
        List<Row> rows = Arrays.asList(
            new Row(mapOf("Store ID","abcd1234","Date","2021-05-25","Color","red","Size","XS","Shirts sold","1")),
            new Row(mapOf("Store ID","abcd1234","Date","2021-05-25","Color","blue","Size","L","Shirts sold","20")),
            new Row(mapOf("Store ID","abcd1234","Date","2021-05-25","Color","green","Size","M","Shirts sold","300")),
            new Row(mapOf("Store ID","abcd1234","Date","2021-05-26","Color","red","Size","XS","Shirts sold","4")),
            new Row(mapOf("Store ID","abcd1234","Date","2021-05-26","Color","black","Size","S","Shirts sold","50")),
            new Row(mapOf("Store ID","5678wxyz","Date","2021-05-25","Color","blue","Size","XL","Shirts sold","600")),
            new Row(mapOf("Store ID","5678wxyz","Date","2021-05-25","Color","green","Size","M","Shirts sold","7")),
            new Row(mapOf("Store ID","5678wxyz","Date","2021-05-25","Color","black","Size","L","Shirts sold","80")),
            new Row(mapOf("Store ID","5678wxyz","Date","2021-05-26","Color","blue","Size","S","Shirts sold","900")),
            new Row(mapOf("Store ID","e1f9g2h8","Date","2021-05-26","Color","red","Size","S","Shirts sold","1")),
            new Row(mapOf("Store ID","e1f9g2h8","Date","2021-05-27","Color","black","Size","M","Shirts sold","20"))
        );

        Spec spec = new Spec("Shirts sold", "Date", "Color");
        Table table = pivot(rows, spec);
        print(table);
    }

    // tiny helper to build maps inline
    private static Map<String, String> mapOf(String... kv) {
        Map<String, String> m = new LinkedHashMap<>();
        for (int i = 0; i + 1 < kv.length; i += 2) m.put(kv[i], kv[i+1]);
        return m;
    }
}



package mixpanel;

import java.util.*;

public class Pagination {
    public static List<String> foo(int current, int total, int visible) {
        Set<Integer> pageSet = new HashSet<>();

        pageSet.add(1);
        pageSet.add(total);
        pageSet.add(current);

        int left = current - 1;
        int right = current + 1;

        while (pageSet.size() < visible && (left >= 1 || right <= total)) {
            if (left >= 1) {
                pageSet.add(left);
                left--;
            }
            if (pageSet.size() < visible && right <= total) {
                pageSet.add(right);
                right++;
            }
        }

        List<Integer> sorted = new ArrayList<>(pageSet);
        Collections.sort(sorted);

        List<String> result = new ArrayList<>();
        Integer prev = null;

        for (int page : sorted) {
            if (prev != null && page != prev + 1) {
                result.add("...");
            }

            if (page == current) {
                result.add("[" + page + "]");
            } else {
                result.add(String.valueOf(page));
            }

            prev = page;
        }

        return result;
    }

    public static void main(String[] args) {
        System.out.println(foo(1, 10, 5));   // [ [1], 2, 3, 4, ..., 10 ]
        System.out.println(foo(3, 10, 5));   // [ 1, 2, [3], 4, ..., 10 ]
        System.out.println(foo(4, 10, 5));   // [ 1, ..., 3, [4], 5, ..., 10 ]
        System.out.println(foo(10, 10, 5));  // [ 1, ..., 7, 8, 9, [10] ]
    }
}

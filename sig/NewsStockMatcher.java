package sig;

import java.util.*;

public class NewsStockMatcher {
    public static List<Set<String>> findRelevantStockCodes(List<String> newsItems, Map<String, String> codes) {
            List<Set<String>> result = new ArrayList<>();
    
            for (String news : newsItems) {
                Set<String> matchedCodes = new HashSet<>();
                String lowerNews = news.toLowerCase();
    
                for (Map.Entry<String, String> entry : codes.entrySet()) {
                    String code = entry.getKey();
                    String companyName = entry.getValue().toLowerCase();
    
                    if (lowerNews.contains(companyName)) {
                        matchedCodes.add(code);
                    }
                }
    
                result.add(matchedCodes);
            }
    
            return result;
        }
    
        public static void main(String[] args) {
            List<String> newsItems = Arrays.asList(
                "Apple is going to buy Tesla stock.",
                "Apple Tree stock is not related to Apple stock."
            );
    
            Map<String, String> codes = new HashMap<>();
            codes.put("AAPL", "Apple");
            codes.put("APLT", "Apple Tree");
            codes.put("TSLA", "Tesla");
    
            List<Set<String>> results = NewsStockMatcher.findRelevantStockCodes(newsItems, codes);

        for (int i = 0; i < newsItems.size(); i++) {
            System.out.println("News: " + newsItems.get(i));
            System.out.println("Matched stock codes: " + results.get(i));
            System.out.println();
        }
    }
}

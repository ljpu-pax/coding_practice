package anthropic;
import java.util.*;
import java.util.concurrent.*;
/**
 * LeetCode 1236: Web Crawler
 * LeetCode 1242: Web Crawler Multithreaded
 * 
 * Problem 1236: Given a url startUrl and an interface HtmlParser, implement a web crawler to crawl all links 
 * that are under the same hostname as startUrl.
 * 
 * Problem 1242: Same as 1236 but with multithreading support for better performance.
 * 
 * Return all urls obtained by your web crawler in any order.
 * 
 * Your crawler should:
 * 1. Start from the page: startUrl
 * 2. Call HtmlParser.getUrls() to get all urls from a webpage of given url.
 * 3. Do not crawl the same link twice.
 * 4. Only crawl the links that are under the same hostname as startUrl.
 * 
 * As shown in the example url above, the hostname is example.org. For simplicity sake, 
 * you may assume all urls use http protocol without any port specified. For example, 
 * the urls http://leetcode.com/problems and http://leetcode.com/contest are under the 
 * same hostname, while urls http://example.org/test and http://example.com/abc are not 
 * under the same hostname.
 * 
 * The HtmlParser interface is defined as such:
 * 
 * interface HtmlParser {
 *   // Return a list of all urls from a webpage of given url.
 *   public List<String> getUrls(String url);
 * }
 * 
 * IMPLEMENTATION APPROACHES:
 * 
 * Version 0 (crawlSingle): Single-threaded BFS - Simple and correct
 * Version 1 (crawlFuture): PROBLEMATIC - Creates new threads recursively (avoid this approach)
 * Version 2 (crawlBlocking): CORRECTED - Uses work queue pattern with fixed thread pool
 * 
 * Key Insight: Avoid recursive thread creation. Use work queue pattern for multithreading.
 */
import java.net.*;
import java.util.stream.Collectors;

// Provided interface
interface HtmlParser {
    public List<String> getUrls(String url);
}


class WebCrawler {
    // ---------- Helper ----------
    private String getHost(String url) {
        try {
            return new URL(url).getHost();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
    
    // ---------- Version 0: Single-threaded BFS (LeetCode 1236) ----------
    // Time: O(N), Space: O(N) - Simple and reliable approach
    public List<String> crawlSingle(String startUrl, HtmlParser htmlParser) {
        String host = getHost(startUrl);
        Set<String> visited = new HashSet<>();
        Queue<String> q = new LinkedList<>();
        q.offer(startUrl);
        visited.add(startUrl);

        while (!q.isEmpty()) {
            String url = q.poll();
            for (String next : htmlParser.getUrls(url)) {
                if (getHost(next).equals(host) && visited.add(next)) {
                    q.offer(next);
                }
            }
        }
        return new ArrayList<>(visited);
    }

    // ---------- Version 2: CORRECTED - Work Queue Pattern (LeetCode 1242) ----------
    // SOLUTION: Uses fixed thread pool with work queue - no recursive thread creation
    // Time: O(N) with parallel processing, Space: O(N) - Efficient and scalable
    // This is the RECOMMENDED approach for multithreaded web crawling
    public List<String> crawlBlocking(String startUrl, HtmlParser htmlParser) {
        String host = getHost(startUrl);
        Set<String> visited = ConcurrentHashMap.newKeySet();
        visited.add(startUrl);

        BlockingQueue<String> q = new LinkedBlockingQueue<>();
        q.offer(startUrl);

        int nThreads = 8;
        ExecutorService pool = Executors.newFixedThreadPool(nThreads);
        CountDownLatch latch = new CountDownLatch(nThreads);

        Runnable worker = () -> {
            try {
                while (true) {
                    String url = q.poll(200, TimeUnit.MILLISECONDS);
                    if (url == null) break;
                    for (String next : htmlParser.getUrls(url)) {
                        if (getHost(next).equals(host) && visited.add(next)) {
                            // CORRECT: Add to work queue instead of creating new thread
                            q.offer(next);
                        }
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                latch.countDown();
            }
        };

        for (int i = 0; i < nThreads; i++) pool.submit(worker);

        try { latch.await(); } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        pool.shutdownNow();
        return new ArrayList<>(visited);
    }

    // ---------- Version 1: PROBLEMATIC - Recursive Thread Creation ----------
    // ISSUE: Creates new thread for each URL - can cause thread explosion
    // Time: O(N) but with high overhead, Space: O(N) + thread overhead
    // AVOID THIS APPROACH - Use work queue pattern instead
    public List<String> crawlFuture(String startUrl, HtmlParser htmlParser) {
        ExecutorService es = Executors.newFixedThreadPool(10, r -> {
            Thread t = new Thread(r);
            t.setDaemon(true);
            return t;
        });

        Set<String> visited = new HashSet<>();
        Queue<Future<List<String>>> q = new LinkedList<>();
        String host = startUrl.split("/")[2];
        q.add(es.submit(() -> htmlParser.getUrls(startUrl)));
        visited.add(startUrl);

        try {
            while (!q.isEmpty()) {
                List<String> urls = q.poll().get().stream()
                        .filter(u -> u.contains(host))
                        .collect(Collectors.toList());
                for (String u : urls) {
                    if (visited.add(u)) {
                        // PROBLEM: Creates new thread for each URL - causes thread explosion!
                        q.add(es.submit(() -> htmlParser.getUrls(u)));
                    }
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
        }
        es.shutdownNow();
        return new ArrayList<>(visited);
    }

    // ---------- Demo Test - Compare All Approaches ----------
    // Shows the difference between single-threaded, problematic multithreaded, and corrected multithreaded
    public static void main(String[] args) {
        HtmlParser parser = url -> {
            Map<String, List<String>> mock = new HashMap<>();
            mock.put("http://news.example.com", Arrays.asList("http://news.example.com/1", "http://news.example.com/2"));
            mock.put("http://news.example.com/1", Arrays.asList("http://news.example.com/3"));
            mock.put("http://news.example.com/2", Arrays.asList("http://news.example.com/3"));
            return mock.getOrDefault(url, Collections.emptyList());
        };

        WebCrawler sol = new WebCrawler();
        System.out.println("Single:   " + sol.crawlSingle("http://news.example.com", parser));
        System.out.println("Future:   " + sol.crawlFuture("http://news.example.com", parser));
        System.out.println("Blocking: " + sol.crawlBlocking("http://news.example.com", parser));
    }
}

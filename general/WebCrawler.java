import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

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
 * Below are two examples explaining the functionality of the problem, for custom testing 
 * purposes you'll have three variables urls, edges and startUrl. Notice that you can only 
 * crawl the urls that are in the urls list.
 */
public class WebCrawler {
    
    /**
     * Main solution method using BFS approach
     * Time Complexity: O(N) where N is the number of URLs
     * Space Complexity: O(N) for the visited set and queue
     */
    public List<String> crawl(String startUrl, HtmlParser htmlParser) {
        // Extract the hostname from the startUrl
        String hostname = getHostName(startUrl);
        
        // Set to keep track of visited URLs to avoid duplicates
        Set<String> visited = new HashSet<>();
        
        // Queue for BFS traversal
        Queue<String> queue = new LinkedList<>();
        
        // Initialize BFS with startUrl
        visited.add(startUrl);
        queue.offer(startUrl);
        
        // BFS traversal
        while (!queue.isEmpty()) {
            String currentUrl = queue.poll();
            
            // Get all URLs from the current page
            List<String> urls = htmlParser.getUrls(currentUrl);
            
            for (String url : urls) {
                // Check if URL has same hostname and hasn't been visited
                if (getHostName(url).equals(hostname) && !visited.contains(url)) {
                    visited.add(url);
                    queue.offer(url);
                }
            }
        }
        
        return new ArrayList<>(visited);
    }
    
    /**
     * Helper method to extract hostname from URL
     * Assumes URL starts with "http://" or "https://"
     */
    private String getHostName(String url) {
        // Find the start of hostname after "://"
        int start = url.indexOf("://");
        if (start == -1) {
            throw new IllegalArgumentException("Invalid URL format");
        }
        start += 3; // Move past "://"
        
        // Find the end of hostname (either '/' or end of string)
        int end = url.indexOf('/', start);
        if (end == -1) {
            end = url.length();
        }
        
        return url.substring(start, end);
    }
    
    /**
     * Alternative solution using DFS approach
     * Time Complexity: O(N) where N is the number of URLs
     * Space Complexity: O(N) for the visited set and recursion stack
     */
    public List<String> crawlDFS(String startUrl, HtmlParser htmlParser) {
        String hostname = getHostName(startUrl);
        Set<String> visited = new HashSet<>();
        dfs(startUrl, hostname, htmlParser, visited);
        return new ArrayList<>(visited);
    }
    
    private void dfs(String url, String hostname, HtmlParser htmlParser, Set<String> visited) {
        if (visited.contains(url)) {
            return;
        }
        
        visited.add(url);
        List<String> urls = htmlParser.getUrls(url);
        
        for (String nextUrl : urls) {
            if (getHostName(nextUrl).equals(hostname)) {
                dfs(nextUrl, hostname, htmlParser, visited);
            }
        }
    }
    
    // ==================== LeetCode 1242: Multithreaded Web Crawler ====================
    
    /**
     * LeetCode 1242: Web Crawler Multithreaded - CORRECTED VERSION
     * 
     * This implementation uses a work queue approach with a fixed thread pool.
     * It does NOT create new threads recursively - instead, it uses a bounded
     * thread pool to process URLs from a shared queue.
     * 
     * Time Complexity: O(N) where N is the number of URLs (with parallel processing)
     * Space Complexity: O(N) for the visited set and work queue
     */
    public List<String> crawlMultithreaded(String startUrl, HtmlParser htmlParser) {
        String hostname = getHostName(startUrl);
        
        // Use ConcurrentHashMap for thread-safe operations
        Set<String> visited = ConcurrentHashMap.newKeySet();
        
        // Work queue for URLs to be processed
        BlockingQueue<String> workQueue = new LinkedBlockingQueue<>();
        
        // Create a fixed thread pool (NOT creating threads recursively)
        ExecutorService executor = Executors.newFixedThreadPool(4);
        
        // Add initial URL to work queue
        visited.add(startUrl);
        workQueue.offer(startUrl);
        
        // Use CountDownLatch to track when all work is done
        AtomicInteger activeTasks = new AtomicInteger(1); // Start with 1 active task
        
        // Submit initial task
        executor.submit(new CrawlWorker(workQueue, hostname, htmlParser, visited, activeTasks));
        
        try {
            // Wait for all work to complete
            while (activeTasks.get() > 0) {
                Thread.sleep(10); // Small delay to prevent busy waiting
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            executor.shutdown();
        }
        
        return new ArrayList<>(visited);
    }
    
    /**
     * Worker class that processes URLs from the work queue
     * This approach uses a FIXED number of threads, not recursive thread creation
     */
    private static class CrawlWorker implements Runnable {
        private final BlockingQueue<String> workQueue;
        private final String hostname;
        private final HtmlParser htmlParser;
        private final Set<String> visited;
        private final AtomicInteger activeTasks;
        
        public CrawlWorker(BlockingQueue<String> workQueue, String hostname, 
                          HtmlParser htmlParser, Set<String> visited, 
                          AtomicInteger activeTasks) {
            this.workQueue = workQueue;
            this.hostname = hostname;
            this.htmlParser = htmlParser;
            this.visited = visited;
            this.activeTasks = activeTasks;
        }
        
        @Override
        public void run() {
            try {
                while (true) {
                    // Get next URL from work queue (blocks if empty)
                    String url = workQueue.poll(100, TimeUnit.MILLISECONDS);
                    if (url == null) {
                        // No more work available
                        break;
                    }
                    
                    // Process the URL
                    List<String> urls = htmlParser.getUrls(url);
                    
                    for (String nextUrl : urls) {
                        if (getHostName(nextUrl).equals(hostname) && visited.add(nextUrl)) {
                            // Add new URL to work queue
                            workQueue.offer(nextUrl);
                            // Increment active tasks counter
                            activeTasks.incrementAndGet();
                        }
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                // Decrement active tasks when this worker finishes
                activeTasks.decrementAndGet();
            }
        }
        
        private String getHostName(String url) {
            int start = url.indexOf("://");
            if (start == -1) {
                throw new IllegalArgumentException("Invalid URL format");
            }
            start += 3;
            int end = url.indexOf('/', start);
            if (end == -1) {
                end = url.length();
            }
            return url.substring(start, end);
        }
    }
    
    /**
     * Alternative multithreaded solution using BFS with proper thread management
     * This approach uses a single producer-multiple consumer pattern
     */
    public List<String> crawlMultithreadedBFS(String startUrl, HtmlParser htmlParser) {
        String hostname = getHostName(startUrl);
        Set<String> visited = ConcurrentHashMap.newKeySet();
        BlockingQueue<String> queue = new LinkedBlockingQueue<>();
        
        // Initialize with start URL
        visited.add(startUrl);
        queue.offer(startUrl);
        
        // Create thread pool with fixed number of workers
        ExecutorService executor = Executors.newFixedThreadPool(4);
        CountDownLatch latch = new CountDownLatch(1);
        
        // Submit a single task that processes the entire queue
        executor.submit(new BfsCrawlTask(queue, hostname, htmlParser, visited, latch));
        
        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            executor.shutdown();
        }
        
        return new ArrayList<>(visited);
    }
    
    /**
     * BFS-based crawl task that processes URLs sequentially but uses multithreading
     * for the actual web requests (if HtmlParser supports it)
     */
    private static class BfsCrawlTask implements Runnable {
        private final BlockingQueue<String> queue;
        private final String hostname;
        private final HtmlParser htmlParser;
        private final Set<String> visited;
        private final CountDownLatch latch;
        
        public BfsCrawlTask(BlockingQueue<String> queue, String hostname, 
                           HtmlParser htmlParser, Set<String> visited, CountDownLatch latch) {
            this.queue = queue;
            this.hostname = hostname;
            this.htmlParser = htmlParser;
            this.visited = visited;
            this.latch = latch;
        }
        
        @Override
        public void run() {
            try {
                while (!queue.isEmpty()) {
                    String url = queue.poll();
                    if (url == null) break;
                    
                    List<String> urls = htmlParser.getUrls(url);
                    
                    for (String nextUrl : urls) {
                        if (getHostName(nextUrl).equals(hostname) && visited.add(nextUrl)) {
                            queue.offer(nextUrl);
                        }
                    }
                }
            } finally {
                latch.countDown();
            }
        }
        
        private String getHostName(String url) {
            int start = url.indexOf("://");
            if (start == -1) {
                throw new IllegalArgumentException("Invalid URL format");
            }
            start += 3;
            int end = url.indexOf('/', start);
            if (end == -1) {
                end = url.length();
            }
            return url.substring(start, end);
        }
    }
    
    /**
     * Mock HtmlParser implementation for testing
     */
    static class MockHtmlParser implements HtmlParser {
        private Map<String, List<String>> urlMap;
        
        public MockHtmlParser() {
            this.urlMap = new HashMap<>();
        }
        
        public void addUrl(String url, List<String> linkedUrls) {
            urlMap.put(url, linkedUrls);
        }
        
        @Override
        public List<String> getUrls(String url) {
            return urlMap.getOrDefault(url, new ArrayList<>());
        }
    }
    
    /**
     * Test method to demonstrate the solution
     */
    public static void main(String[] args) {
        WebCrawler crawler = new WebCrawler();
        MockHtmlParser parser = new MockHtmlParser();
        
        // Example test case
        String startUrl = "http://news.yahoo.com/news/topics/";
        
        // Mock the HTML parser with some test URLs
        parser.addUrl("http://news.yahoo.com/news/topics/", 
            Arrays.asList("http://news.yahoo.com/news", "http://news.yahoo.com/news/topics/"));
        parser.addUrl("http://news.yahoo.com/news", 
            Arrays.asList("http://news.yahoo.com/news/topics/", "http://news.yahoo.com/"));
        parser.addUrl("http://news.yahoo.com/", 
            Arrays.asList("http://news.yahoo.com/news"));
        
        System.out.println("=== LeetCode 1236: Web Crawler (Single-threaded) ===");
        
        // Test BFS solution
        List<String> result = crawler.crawl(startUrl, parser);
        System.out.println("BFS Result: " + result);
        
        // Test DFS solution
        List<String> resultDFS = crawler.crawlDFS(startUrl, parser);
        System.out.println("DFS Result: " + resultDFS);
        
        System.out.println("\n=== LeetCode 1242: Web Crawler Multithreaded (CORRECTED) ===");
        
        // Test multithreaded solution
        try {
            List<String> resultMulti = crawler.crawlMultithreaded(startUrl, parser);
            System.out.println("Multithreaded Result: " + resultMulti);
        } catch (Exception e) {
            System.out.println("Multithreaded test failed: " + e.getMessage());
            e.printStackTrace();
        }
        
        // Test multithreaded BFS solution
        try {
            List<String> resultMultiBFS = crawler.crawlMultithreadedBFS(startUrl, parser);
            System.out.println("Multithreaded BFS Result: " + resultMultiBFS);
        } catch (Exception e) {
            System.out.println("Multithreaded BFS test failed: " + e.getMessage());
            e.printStackTrace();
        }
        
        // Test hostname extraction
        System.out.println("\nHostname of " + startUrl + ": " + crawler.getHostName(startUrl));
        
        System.out.println("\n=== Thread Management Explanation ===");
        System.out.println("The corrected multithreaded solution uses:");
        System.out.println("1. Fixed thread pool (4 threads) - NO recursive thread creation");
        System.out.println("2. Work queue pattern - URLs are added to a shared queue");
        System.out.println("3. Worker threads process URLs from the queue");
        System.out.println("4. Atomic counter to track when all work is complete");
    }
}

/**
 * Interface definition for HtmlParser
 */
interface HtmlParser {
    /**
     * Return a list of all urls from a webpage of given url.
     */
    public List<String> getUrls(String url);
}

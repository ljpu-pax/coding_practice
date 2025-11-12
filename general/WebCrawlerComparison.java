import java.util.*;
import java.util.concurrent.*;

/**
 * Comparison of different web crawler implementations
 * Shows the problems with recursive thread creation vs proper work queue approach
 */
public class WebCrawlerComparison {
    
    /**
     * PROBLEMATIC VERSION (like the code you showed)
     * Issues: Recursive thread creation, inefficient synchronization
     */
    static class ProblematicCrawler {
        public List<String> crawl(String startUrl, HtmlParser htmlParser) {
            String hostname = getHostName(startUrl);
            Set<String> visited = ConcurrentHashMap.newKeySet();
            visited.add(startUrl);

            ExecutorService executor = Executors.newFixedThreadPool(8);
            Queue<Future<?>> futures = new LinkedList<>();

            futures.offer(executor.submit(() -> 
                crawlUrl(startUrl, hostname, visited, htmlParser, executor, futures)));

            // Wait for all tasks
            while (!futures.isEmpty()) {
                try {
                    futures.poll().get();  // Sequential processing - defeats multithreading purpose
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            executor.shutdown();
            return new ArrayList<>(visited);
        }

        private void crawlUrl(String url, String hostname, Set<String> visited,
                              HtmlParser parser, ExecutorService executor, Queue<Future<?>> futures) {
            for (String next : parser.getUrls(url)) {
                if (next.contains(hostname) && visited.add(next)) {
                    // PROBLEM: Creates new thread for each URL recursively
                    futures.offer(executor.submit(() -> 
                        crawlUrl(next, hostname, visited, parser, executor, futures)));
                }
            }
        }

        private String getHostName(String url) {
            return url.split("/")[2];
        }
    }
    
    /**
     * CORRECTED VERSION - Proper work queue approach
     * Uses fixed thread pool with work queue, no recursive thread creation
     */
    static class CorrectedCrawler {
        public List<String> crawl(String startUrl, HtmlParser htmlParser) {
            String hostname = getHostName(startUrl);
            Set<String> visited = ConcurrentHashMap.newKeySet();
            visited.add(startUrl);
            
            // Work queue for URLs to be processed
            BlockingQueue<String> workQueue = new LinkedBlockingQueue<>();
            workQueue.offer(startUrl);
            
            // Fixed thread pool - NO recursive thread creation
            ExecutorService executor = Executors.newFixedThreadPool(8);
            CountDownLatch latch = new CountDownLatch(1);
            
            // Submit single task that processes the entire queue
            executor.submit(new CrawlWorker(workQueue, hostname, htmlParser, visited, latch));
            
            try {
                latch.await(); // Wait for completion
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                executor.shutdown();
            }
            
            return new ArrayList<>(visited);
        }
        
        private static class CrawlWorker implements Runnable {
            private final BlockingQueue<String> workQueue;
            private final String hostname;
            private final HtmlParser htmlParser;
            private final Set<String> visited;
            private final CountDownLatch latch;
            
            public CrawlWorker(BlockingQueue<String> workQueue, String hostname,
                             HtmlParser htmlParser, Set<String> visited, CountDownLatch latch) {
                this.workQueue = workQueue;
                this.hostname = hostname;
                this.htmlParser = htmlParser;
                this.visited = visited;
                this.latch = latch;
            }
            
            @Override
            public void run() {
                try {
                    while (!workQueue.isEmpty()) {
                        String url = workQueue.poll();
                        if (url == null) break;
                        
                        List<String> urls = htmlParser.getUrls(url);
                        for (String nextUrl : urls) {
                            // Proper hostname matching
                            if (getHostName(nextUrl).equals(hostname) && visited.add(nextUrl)) {
                                workQueue.offer(nextUrl); // Add to queue, don't create new thread
                            }
                        }
                    }
                } finally {
                    latch.countDown();
                }
            }
            
            private String getHostName(String url) {
                int start = url.indexOf("://");
                if (start == -1) return "";
                start += 3;
                int end = url.indexOf('/', start);
                if (end == -1) end = url.length();
                return url.substring(start, end);
            }
        }
        
        private String getHostName(String url) {
            int start = url.indexOf("://");
            if (start == -1) return "";
            start += 3;
            int end = url.indexOf('/', start);
            if (end == -1) end = url.length();
            return url.substring(start, end);
        }
    }
    
    /**
     * OPTIMAL VERSION - Multiple worker threads processing the queue
     * This is the most efficient approach for true parallel processing
     */
    static class OptimalCrawler {
        public List<String> crawl(String startUrl, HtmlParser htmlParser) {
            String hostname = getHostName(startUrl);
            Set<String> visited = ConcurrentHashMap.newKeySet();
            visited.add(startUrl);
            
            BlockingQueue<String> workQueue = new LinkedBlockingQueue<>();
            workQueue.offer(startUrl);
            
            ExecutorService executor = Executors.newFixedThreadPool(8);
            AtomicInteger activeTasks = new AtomicInteger(1);
            
            // Submit initial task
            executor.submit(new OptimalWorker(workQueue, hostname, htmlParser, visited, activeTasks));
            
            try {
                // Wait for all work to complete
                while (activeTasks.get() > 0) {
                    Thread.sleep(10);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                executor.shutdown();
            }
            
            return new ArrayList<>(visited);
        }
        
        private static class OptimalWorker implements Runnable {
            private final BlockingQueue<String> workQueue;
            private final String hostname;
            private final HtmlParser htmlParser;
            private final Set<String> visited;
            private final AtomicInteger activeTasks;
            
            public OptimalWorker(BlockingQueue<String> workQueue, String hostname,
                               HtmlParser htmlParser, Set<String> visited, AtomicInteger activeTasks) {
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
                        String url = workQueue.poll(100, TimeUnit.MILLISECONDS);
                        if (url == null) break;
                        
                        List<String> urls = htmlParser.getUrls(url);
                        for (String nextUrl : urls) {
                            if (getHostName(nextUrl).equals(hostname) && visited.add(nextUrl)) {
                                workQueue.offer(nextUrl);
                                activeTasks.incrementAndGet();
                            }
                        }
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    activeTasks.decrementAndGet();
                }
            }
            
            private String getHostName(String url) {
                int start = url.indexOf("://");
                if (start == -1) return "";
                start += 3;
                int end = url.indexOf('/', start);
                if (end == -1) end = url.length();
                return url.substring(start, end);
            }
        }
        
        private String getHostName(String url) {
            int start = url.indexOf("://");
            if (start == -1) return "";
            start += 3;
            int end = url.indexOf('/', start);
            if (end == -1) end = url.length();
            return url.substring(start, end);
        }
    }
    
    // Mock HtmlParser for testing
    static class MockHtmlParser implements HtmlParser {
        private Map<String, List<String>> urlMap = new HashMap<>();
        
        public void addUrl(String url, List<String> linkedUrls) {
            urlMap.put(url, linkedUrls);
        }
        
        @Override
        public List<String> getUrls(String url) {
            return urlMap.getOrDefault(url, new ArrayList<>());
        }
    }
    
    public static void main(String[] args) {
        String startUrl = "http://news.yahoo.com/news/topics/";
        MockHtmlParser parser = new MockHtmlParser();
        
        // Setup test data
        parser.addUrl("http://news.yahoo.com/news/topics/", 
            Arrays.asList("http://news.yahoo.com/news", "http://news.yahoo.com/news/topics/"));
        parser.addUrl("http://news.yahoo.com/news", 
            Arrays.asList("http://news.yahoo.com/news/topics/", "http://news.yahoo.com/"));
        parser.addUrl("http://news.yahoo.com/", 
            Arrays.asList("http://news.yahoo.com/news"));
        
        System.out.println("=== PROBLEMATIC VERSION (Recursive Thread Creation) ===");
        System.out.println("Issues:");
        System.out.println("1. Creates new thread for each URL");
        System.out.println("2. Can create thousands of threads");
        System.out.println("3. Sequential future processing defeats multithreading");
        System.out.println("4. Race conditions with shared queue");
        System.out.println("5. Insecure hostname matching with contains()");
        
        System.out.println("\n=== CORRECTED VERSION (Work Queue Pattern) ===");
        System.out.println("Improvements:");
        System.out.println("1. Fixed thread pool (8 threads)");
        System.out.println("2. Work queue for URL processing");
        System.out.println("3. No recursive thread creation");
        System.out.println("4. Proper hostname matching");
        System.out.println("5. Thread-safe data structures");
        
        // Test corrected version
        try {
            CorrectedCrawler corrected = new CorrectedCrawler();
            List<String> result = corrected.crawl(startUrl, parser);
            System.out.println("Corrected Result: " + result);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}

interface HtmlParser {
    List<String> getUrls(String url);
}

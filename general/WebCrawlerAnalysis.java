import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.net.*;

/**
 * Analysis of different web crawler implementations
 * Shows why recursive thread creation is problematic even with CompletionService
 */
public class WebCrawlerAnalysis {
    
    /**
     * PROBLEMATIC VERSION (like the code you showed)
     * Issues: Still creates threads recursively, race conditions, inefficient task management
     */
    static class ProblematicCompletionServiceCrawler {
        public List<String> crawl(String startUrl, HtmlParser htmlParser) {
            String hostname = getHostName(startUrl);
            Set<String> visited = ConcurrentHashMap.newKeySet();
            visited.add(startUrl);

            // Fixed thread pool avoids explosion
            ExecutorService executor = Executors.newFixedThreadPool(8);
            CompletionService<Void> completion = new ExecutorCompletionService<>(executor);

            // Count tasks dynamically
            AtomicInteger taskCount = new AtomicInteger(1);
            completion.submit(() -> {
                crawlUrl(startUrl, hostname, visited, htmlParser, completion, taskCount);
                return null;
            });

            try {
                // Process tasks as they complete
                while (taskCount.get() > 0) {
                    Future<Void> f = completion.take(); // waits for next finished task
                    f.get(); // propagate exceptions if any
                    taskCount.decrementAndGet();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                executor.shutdown();
            }

            return new ArrayList<>(visited);
        }

        private void crawlUrl(String url, String hostname, Set<String> visited,
                              HtmlParser parser, CompletionService<Void> completion, AtomicInteger taskCount) {
            for (String next : parser.getUrls(url)) {
                if (getHostName(next).equals(hostname) && visited.add(next)) {
                    // PROBLEM: Still creates new thread for each URL!
                    taskCount.incrementAndGet();
                    completion.submit(() -> {
                        crawlUrl(next, hostname, visited, parser, completion, taskCount);
                        return null;
                    });
                }
            }
        }

        private String getHostName(String url) {
            try {
                return new URL(url).getHost();
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        }
    }
    
    /**
     * CORRECTED VERSION - Proper work queue approach
     * Uses fixed thread pool with work queue, no recursive thread creation
     */
    static class CorrectedWorkQueueCrawler {
        public List<String> crawl(String startUrl, HtmlParser htmlParser) {
            String hostname = getHostName(startUrl);
            Set<String> visited = ConcurrentHashMap.newKeySet();
            visited.add(startUrl);
            
            // Work queue for URLs to be processed
            BlockingQueue<String> workQueue = new LinkedBlockingQueue<>();
            workQueue.offer(startUrl);
            
            // Fixed thread pool - NO recursive thread creation
            ExecutorService executor = Executors.newFixedThreadPool(8);
            AtomicInteger activeTasks = new AtomicInteger(1);
            
            // Submit single task that processes the entire queue
            executor.submit(new CrawlWorker(workQueue, hostname, htmlParser, visited, activeTasks));
            
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
        
        private static class CrawlWorker implements Runnable {
            private final BlockingQueue<String> workQueue;
            private final String hostname;
            private final HtmlParser htmlParser;
            private final Set<String> visited;
            private final AtomicInteger activeTasks;
            
            public CrawlWorker(BlockingQueue<String> workQueue, String hostname,
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
                                workQueue.offer(nextUrl); // Add to queue, don't create new thread
                                activeTasks.incrementAndGet();
                            }
                        }
                        activeTasks.decrementAndGet();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            
            private String getHostName(String url) {
                try {
                    return new URL(url).getHost();
                } catch (MalformedURLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        
        private String getHostName(String url) {
            try {
                return new URL(url).getHost();
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        }
    }
    
    /**
     * OPTIMAL VERSION - Multiple worker threads with proper synchronization
     * This is the most efficient approach for true parallel processing
     */
    static class OptimalMultiWorkerCrawler {
        public List<String> crawl(String startUrl, HtmlParser htmlParser) {
            String hostname = getHostName(startUrl);
            Set<String> visited = ConcurrentHashMap.newKeySet();
            visited.add(startUrl);
            
            BlockingQueue<String> workQueue = new LinkedBlockingQueue<>();
            workQueue.offer(startUrl);
            
            ExecutorService executor = Executors.newFixedThreadPool(8);
            CountDownLatch latch = new CountDownLatch(1);
            
            // Submit initial task
            executor.submit(new OptimalWorker(workQueue, hostname, htmlParser, visited, latch));
            
            try {
                latch.await();
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
            private final CountDownLatch latch;
            
            public OptimalWorker(BlockingQueue<String> workQueue, String hostname,
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
                            if (getHostName(nextUrl).equals(hostname) && visited.add(nextUrl)) {
                                workQueue.offer(nextUrl);
                            }
                        }
                    }
                } finally {
                    latch.countDown();
                }
            }
            
            private String getHostName(String url) {
                try {
                    return new URL(url).getHost();
                } catch (MalformedURLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        
        private String getHostName(String url) {
            try {
                return new URL(url).getHost();
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
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
        
        System.out.println("=== PROBLEMATIC VERSION (CompletionService with Recursive Threads) ===");
        System.out.println("Issues:");
        System.out.println("1. Still creates new thread for each URL");
        System.out.println("2. Race condition with taskCount");
        System.out.println("3. Inefficient task management");
        System.out.println("4. Overhead of CompletionService");
        System.out.println("5. Can still create thousands of threads");
        
        System.out.println("\n=== CORRECTED VERSION (Work Queue Pattern) ===");
        System.out.println("Improvements:");
        System.out.println("1. Fixed thread pool (8 threads)");
        System.out.println("2. Work queue for URL processing");
        System.out.println("3. No recursive thread creation");
        System.out.println("4. Proper synchronization");
        System.out.println("5. Efficient resource usage");
        
        // Test corrected version
        try {
            CorrectedWorkQueueCrawler corrected = new CorrectedWorkQueueCrawler();
            List<String> result = corrected.crawl(startUrl, parser);
            System.out.println("Corrected Result: " + result);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
        
        System.out.println("\n=== KEY INSIGHT ===");
        System.out.println("Even with CompletionService, recursive thread creation is still problematic!");
        System.out.println("The work queue pattern is the standard approach for multithreaded web crawling.");
    }
}

interface HtmlParser {
    List<String> getUrls(String url);
}

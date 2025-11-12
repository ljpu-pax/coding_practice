import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.*;
import java.util.*;

public class CameraServer {

    private static final int PORT = 8000;
    private static final BlockingQueue<Long> requestQueue = new LinkedBlockingQueue<>();
    private static final BlockingQueue<String> logQueue = new LinkedBlockingQueue<>();

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);

        server.createContext("/poll_for_command", new PollCommandHandler());
        server.createContext("/logs", new GetLogHandler());
        server.createContext("/send_logs", new SendLogHandler());

        server.setExecutor(Executors.newCachedThreadPool());
        server.start();
        System.out.println("Server started on port " + PORT);
    }

    static class PollCommandHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendResponse(exchange, 405, "Method Not Allowed");
                return;
            }

            try {
                Long timestamp = requestQueue.poll(30, TimeUnit.SECONDS);
                String response = (timestamp != null) ? "get_logs" : "no_command";
                sendResponse(exchange, 200, response);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                sendResponse(exchange, 500, "Internal Server Error");
            }
        }
    }

    static class GetLogHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendResponse(exchange, 405, "Method Not Allowed");
                return;
            }

            URI requestURI = exchange.getRequestURI();
            String path = requestURI.getPath();
            String[] segments = path.split("/");
            if (segments.length < 3) {
                sendResponse(exchange, 400, "Bad Request: Missing timestamp");
                return;
            }

            try {
                long timestamp = Long.parseLong(segments[2]);
                requestQueue.offer(timestamp);
                String log = logQueue.poll(30, TimeUnit.SECONDS);
                if (log != null) {
                    sendResponse(exchange, 200, log);
                } else {
                    sendResponse(exchange, 204, "No Content");
                }
            } catch (NumberFormatException e) {
                sendResponse(exchange, 400, "Bad Request: Invalid timestamp");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                sendResponse(exchange, 500, "Internal Server Error");
            }
        }
    }

    static class SendLogHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendResponse(exchange, 405, "Method Not Allowed");
                return;
            }

            InputStream is = exchange.getRequestBody();
            String logMessage = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))
                    .lines()
                    .collect(Collectors.joining("\n"));
            logQueue.offer(logMessage);
            sendResponse(exchange, 200, "Log received");
        }
    }

    private static void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "text/plain; charset=UTF-8");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }
}

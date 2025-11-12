package service;

import model.TicketPrice;
import java.net.URI;
import java.net.http.*;
import java.util.*;

public class PriceService {

    private static final String API_URL = "https://sh-mockapi.azurewebsites.net/api/ticketprice";

    public Map<Integer, TicketPrice> getAllPrices() {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .GET()
                .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return parseJson(response.body());
        } catch (Exception e) {
            System.err.println("API error: " + e.getMessage());
            return new HashMap<>();
        }
    }

    // Minimal JSON parser
    private Map<Integer, TicketPrice> parseJson(String json) {
        Map<Integer, TicketPrice> map = new HashMap<>();
        for (String obj : json.replace("[{", "").replace("}]", "").split("\\},\\{")) {
            int id = 0;
            double price = 0;
            for (String kv : obj.split(",")) {
                String[] parts = kv.split(":");
                String key = parts[0].replace("\"", "");
                String val = parts[1].replace("\"", "");
                if (key.equals("Id")) id = Integer.parseInt(val);
                if (key.equals("Price")) price = Double.parseDouble(val);
            }
            map.put(id, new TicketPrice(id, "USD", price));
        }
        return map;
    }
}

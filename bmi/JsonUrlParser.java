import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.regex.*;

/**
 * Problem: Fetch JSON from URL and find max/min values with their indices
 *
 * Common operations:
 * - Read JSON from URL
 * - Parse JSON (simple parsing without external libraries)
 * - Find max/min values and their indices
 *
 * Example JSON format:
 * {
 *   "data": [10, 5, 20, 15, 8]
 * }
 *
 * Output:
 * Max: 20 at index 2
 * Min: 5 at index 1
 *
 * Note: This implementation uses simple string parsing for JSON.
 * For production, consider using external libraries like:
 * - org.json (JSONObject/JSONArray)
 * - Gson (Google)
 * - Jackson
 */
public class JsonUrlParser {

    /**
     * Fetch JSON content from URL
     */
    public static String fetchJsonFromUrl(String urlString) throws Exception {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");

        // Check response code
        if (conn.getResponseCode() != 200) {
            throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
        }

        // Read response
        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;

        while ((line = br.readLine()) != null) {
            response.append(line);
        }

        br.close();
        conn.disconnect();

        return response.toString();
    }

    /**
     * Find max value and index from JSON array
     */
    public static Result findMaxFromJson(String jsonString, String arrayKey) {
        JSONObject jsonObject = new JSONObject(jsonString);
        JSONArray dataArray = jsonObject.getJSONArray(arrayKey);

        int maxValue = Integer.MIN_VALUE;
        int maxIndex = -1;

        for (int i = 0; i < dataArray.length(); i++) {
            int value = dataArray.getInt(i);
            if (value > maxValue) {
                maxValue = value;
                maxIndex = i;
            }
        }

        return new Result(maxValue, maxIndex);
    }

    /**
     * Find min value and index from JSON array
     */
    public static Result findMinFromJson(String jsonString, String arrayKey) {
        JSONObject jsonObject = new JSONObject(jsonString);
        JSONArray dataArray = jsonObject.getJSONArray(arrayKey);

        int minValue = Integer.MAX_VALUE;
        int minIndex = -1;

        for (int i = 0; i < dataArray.length(); i++) {
            int value = dataArray.getInt(i);
            if (value < minValue) {
                minValue = value;
                minIndex = i;
            }
        }

        return new Result(minValue, minIndex);
    }

    /**
     * Find both max and min in one pass
     */
    public static MaxMinResult findMaxMinFromJson(String jsonString, String arrayKey) {
        JSONObject jsonObject = new JSONObject(jsonString);
        JSONArray dataArray = jsonObject.getJSONArray(arrayKey);

        int maxValue = Integer.MIN_VALUE;
        int maxIndex = -1;
        int minValue = Integer.MAX_VALUE;
        int minIndex = -1;

        for (int i = 0; i < dataArray.length(); i++) {
            int value = dataArray.getInt(i);

            if (value > maxValue) {
                maxValue = value;
                maxIndex = i;
            }

            if (value < minValue) {
                minValue = value;
                minIndex = i;
            }
        }

        return new MaxMinResult(maxValue, maxIndex, minValue, minIndex);
    }

    /**
     * Handle nested JSON structure
     * Example: {"response": {"values": [1,2,3]}}
     */
    public static JSONArray getNestedArray(String jsonString, String... keys) {
        JSONObject current = new JSONObject(jsonString);

        // Navigate through nested objects
        for (int i = 0; i < keys.length - 1; i++) {
            current = current.getJSONObject(keys[i]);
        }

        // Get the final array
        return current.getJSONArray(keys[keys.length - 1]);
    }

    /**
     * Parse array of objects and find max by specific field
     * Example: [{"id": 1, "value": 10}, {"id": 2, "value": 20}]
     */
    public static Result findMaxByField(String jsonString, String arrayKey, String fieldName) {
        JSONObject jsonObject = new JSONObject(jsonString);
        JSONArray dataArray = jsonObject.getJSONArray(arrayKey);

        int maxValue = Integer.MIN_VALUE;
        int maxIndex = -1;

        for (int i = 0; i < dataArray.length(); i++) {
            JSONObject obj = dataArray.getJSONObject(i);
            int value = obj.getInt(fieldName);

            if (value > maxValue) {
                maxValue = value;
                maxIndex = i;
            }
        }

        return new Result(maxValue, maxIndex);
    }

    // Result classes
    static class Result {
        int value;
        int index;

        Result(int value, int index) {
            this.value = value;
            this.index = index;
        }

        @Override
        public String toString() {
            return "Value: " + value + " at index: " + index;
        }
    }

    static class MaxMinResult {
        int maxValue;
        int maxIndex;
        int minValue;
        int minIndex;

        MaxMinResult(int maxValue, int maxIndex, int minValue, int minIndex) {
            this.maxValue = maxValue;
            this.maxIndex = maxIndex;
            this.minValue = minValue;
            this.minIndex = minIndex;
        }

        @Override
        public String toString() {
            return "Max: " + maxValue + " at index: " + maxIndex +
                   ", Min: " + minValue + " at index: " + minIndex;
        }
    }

    // Example usage
    public static void main(String[] args) {
        try {
            // Example 1: Simple JSON with array
            String jsonString = "{\"data\": [10, 5, 20, 15, 8]}";

            Result max = findMaxFromJson(jsonString, "data");
            System.out.println("Max: " + max);

            Result min = findMinFromJson(jsonString, "data");
            System.out.println("Min: " + min);

            MaxMinResult both = findMaxMinFromJson(jsonString, "data");
            System.out.println("Both: " + both);

            // Example 2: Fetch from URL (uncomment to use)
            // String url = "https://api.example.com/data";
            // String jsonFromUrl = fetchJsonFromUrl(url);
            // MaxMinResult result = findMaxMinFromJson(jsonFromUrl, "data");
            // System.out.println(result);

            // Example 3: Nested JSON
            String nestedJson = "{\"response\": {\"values\": [30, 10, 50]}}";
            JSONArray nestedArray = getNestedArray(nestedJson, "response", "values");
            System.out.println("Nested array: " + nestedArray);

            // Example 4: Array of objects
            String objectsJson = "{\"items\": [{\"id\": 1, \"score\": 85}, {\"id\": 2, \"score\": 92}]}";
            Result maxScore = findMaxByField(objectsJson, "items", "score");
            System.out.println("Max score: " + maxScore);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

/*
 * Dependencies needed (add to pom.xml if using Maven):
 * <dependency>
 *     <groupId>org.json</groupId>
 *     <artifactId>json</artifactId>
 *     <version>20231013</version>
 * </dependency>
 *
 * Or for Gradle:
 * implementation 'org.json:json:20231013'
 *
 * Key JSON operations to remember:
 * 1. JSONObject - Parse object: new JSONObject(string)
 * 2. JSONArray - Parse array: jsonObject.getJSONArray(key)
 * 3. Get values: getInt(), getString(), getDouble(), getBoolean()
 * 4. Navigate: getJSONObject(key), getJSONArray(key)
 * 5. Length: jsonArray.length()
 *
 * Common patterns:
 * - Always check response code when fetching from URL
 * - Close connections and readers
 * - Handle exceptions appropriately
 * - Use StringBuilder for efficient string concatenation
 */

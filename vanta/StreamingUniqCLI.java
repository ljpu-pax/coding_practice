package vanta;

/**
 * VANTA CODING QUESTION (90-minute practical round)
 * --------------------------------------------------
 * Build a CLI tool similar to Unix's `uniq`, which reads lines from input and prints only
 * the unique lines. The tool should support two modes:
 *
 * (1) Default mode: Only remove **adjacent** duplicate lines (like Unix `uniq`).
 *     Example:
 *     Input: 1, 1, 2, 2, 3   → Output: 1, 2, 3
 *
 * (2) --global mode: Remove **all** duplicate lines across the entire stream.
 *     Since input can be infinite or too large to fit in memory, your solution should
 *     partition seen lines into hash buckets written to disk. For each new line:
 *     - Hash it to a bucket file
 *     - Load that one bucket file into memory
 *     - If unseen, print + append to the bucket file
 *     - If already seen, skip
 *
 * The tool must read from stdin or from a specified input file.
 *
 * Example Usage:
 *     javac vanta/StreamingUniqCLI.java
 *     java -cp . vanta.StreamingUniqCLI --global input.txt
 */

 import java.io.*;
 import java.util.*;
 
 public class StreamingUniqCLI {
     public static void main(String[] args) {
         boolean globalMode = false;
         String inputFile = null;
 
         // Parse CLI args
         for (String arg : args) {
             if (arg.equals("--global")) {
                 globalMode = true;
             } else {
                 inputFile = arg;
             }
         }
 
         try {
             InputStream inputStream = (inputFile == null)
                     ? System.in
                     : new FileInputStream(inputFile);
 
             if (globalMode) {
                 runStreamingGlobalUniq(inputStream, System.out);
             } else {
                 runUniqAdjacent(inputStream, System.out);
             }
         } catch (IOException e) {
             System.err.println("Error: " + e.getMessage());
             System.exit(1);
         }
     }
 
     /**
      * Removes only adjacent duplicate lines (like Unix uniq).
      */
     public static void runUniqAdjacent(InputStream input, OutputStream output) throws IOException {
         BufferedReader reader = new BufferedReader(new InputStreamReader(input));
         BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(output));
 
         String prevLine = null;
         String line;
 
         while ((line = reader.readLine()) != null) {
             if (!line.equals(prevLine)) {
                 writer.write(line);
                 writer.newLine();
                 writer.flush();
                 prevLine = line;
             }
         }
     }
 
     /**
      * Streaming global deduplication:
      * For each input line, it hashes to a bucket file. That file is loaded into memory,
      * checked for duplicates, and if unseen, the line is emitted + stored.
      */
     public static void runStreamingGlobalUniq(InputStream input, OutputStream output) throws IOException {
         final int NUM_BUCKETS = 100;
         File[] bucketFiles = new File[NUM_BUCKETS];
         for (int i = 0; i < NUM_BUCKETS; i++) {
             bucketFiles[i] = File.createTempFile("uniq_seen_bucket_", "_" + i + ".txt");
             bucketFiles[i].deleteOnExit();
         }
 
         BufferedReader reader = new BufferedReader(new InputStreamReader(input));
         BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(output));
         String line;
 
         while ((line = reader.readLine()) != null) {
             int bucketIndex = Math.abs(line.hashCode() % NUM_BUCKETS);
             File bucket = bucketFiles[bucketIndex];
 
             // Load current bucket file into memory (Set)
             Set<String> seen = new HashSet<>();
             if (bucket.exists()) {
                 try (BufferedReader br = new BufferedReader(new FileReader(bucket))) {
                     String existing;
                     while ((existing = br.readLine()) != null) {
                         seen.add(existing);
                     }
                 }
             }
 
             // Check and write if new
             if (!seen.contains(line)) {
                 writer.write(line);
                 writer.newLine();
                 writer.flush();
 
                 try (BufferedWriter bw = new BufferedWriter(new FileWriter(bucket, true))) {
                     bw.write(line);
                     bw.newLine();
                 }
             }
         }
     }
 }
 
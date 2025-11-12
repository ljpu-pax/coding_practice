package speak;

import java.util.*;

public class TokenExtractor {

    public static void main(String[] args) {
        List<String> tokens = Arrays.asList(
            "T", "utor", ":", " Hello", " and", " welc", "ome", " to", " Speak", "\n",
            "Conver", "sation", " Finished", ":", " False"
        );
        String startTag = "Tutor";
        String endTag = "Conversation Finished";

        List<Map<String, String>> output = extractTextDeltas(tokens, startTag, endTag);

        System.out.println("📦 Input Tokens:");
        for (int i = 0; i < tokens.size(); i++) {
            System.out.println(i + ": \"" + tokens.get(i) + "\"");
        }

        System.out.println("\n🔍 Output (tokens between tags):");
        for (Map<String, String> map : output) {
            System.out.println(map);
        }
    }

    public static List<Map<String, String>> extractTextDeltas(List<String> tokens, String startTag, String endTag) {
        List<Map<String, String>> result = new ArrayList<>();

        StringBuilder combined = new StringBuilder();
        List<Integer> starts = new ArrayList<>();

        // Build the full string and track where each token starts
        for (String token : tokens) {
            starts.add(combined.length());
            combined.append(token);
        }

        String fullText = combined.toString();
        int startChar = fullText.indexOf(startTag);
        int endChar = fullText.indexOf(endTag);

        if (startChar == -1 || endChar == -1 || endChar <= startChar) return result;

        int startToken = findTokenIndexAfter(starts, startChar + startTag.length());
        int endToken = findTokenIndexAtOrAfter(starts, endChar);

        for (int i = startToken; i < endToken; i++) {
            Map<String, String> map = new HashMap<>();
            map.put("textDelta", tokens.get(i));
            result.add(map);
        }

        return result;
    }

    // Return first token index whose start is > charPos (used after startTag)
    private static int findTokenIndexAfter(List<Integer> starts, int charPos) {
        for (int i = 0; i < starts.size(); i++) {
            if (starts.get(i) > charPos) {
                return i;
            }
        }
        return starts.size();
    }

    // Return first token index whose start is >= charPos (used for endTag)
    private static int findTokenIndexAtOrAfter(List<Integer> starts, int charPos) {
        for (int i = 0; i < starts.size(); i++) {
            if (starts.get(i) >= charPos) {
                return i;
            }
        }
        return starts.size();
    }
}






/*
 * Click `Run` to execute the snippet below!
 */

 import java.io.*;
 import java.util.*;
 
 /*
  * To execute Java, please define "static void main" on a class
  * named Solution.
  *
  * If you need more classes, simply define them inline.
  */
 
 // class Solution {
 //   public static void main(String[] args) {
 //     ArrayList<String> strings = new ArrayList<String>();
 //     strings.add("Hello, World!");
 //     strings.add("Welcome to CoderPad.");
 //     strings.add("This pad is running Java " + Runtime.version().feature());
 
 //     for (String string : strings) {
 //       System.out.println(string);
 //     }
 //   }
 // }
 
 
 // from typing import Generator
 
 // # example data
 // start_tag = "Tutor"
 // end_tag = "Conversation Finished"
 
 // def llm_stream():
 //     example_text = [
 //         'T', 0
 //         'utor', 1
 //         ':', 5
 //         ' Hello', 
 //         ' and',
 //         ' welc',
 //         'ome',
 //         ' to',
 //         ' Speak',
 //         '!\n',
 //         'Conver',
 //         'sation',
 //         ' Finished',
 //         ':',
 //         ' False'
 //     ]
 //     for tok in example_text:
 //         yield tok
 
 
 // # implement this function
 // def process_llm_response(
 //     response: Generator, start_tag: str, end_tag: str
 // ) -> Generator:
 //     for token in response:
 //         yield { "textDelta": token }
 
 
 // # prints your output
 // for out in process_llm_response(llm_stream(), start_tag, end_tag):
 //     print(out)
 
//  import java.util.Iterator;
//  import java.util.List;
//  import java.util.ArrayList;
 
//  public class Solution {
 
//      // Example data
//      static String startTag = "Tutor";
//      static String endTag = "Conversation Finished";
 
//      // Function that simulates a stream of tokens
//      public static Iterator<String> llmStream() {
//          List<String> exampleText = new ArrayList<>();
//          exampleText.add("T");
//          exampleText.add("utor");
//          exampleText.add(":");
//          exampleText.add(" Hello");
//          exampleText.add(" and");
//          exampleText.add(" welc");
//          exampleText.add("ome");
//          exampleText.add(" to");
//          exampleText.add(" Speak");
//          exampleText.add("!\n");
//          exampleText.add("Conver");
//          exampleText.add("sation");
//          exampleText.add(" Finished");
//          exampleText.add(":");
//          exampleText.add(" False");
 
//          return exampleText.iterator();
//      }
 
//      // Implement this function to process the response
//      public static Iterator<String> processLLMResponse(Iterator<String> response, String startTag, String endTag) {
//          List<String> result = new ArrayList<>();
//          StringBuilder sb = new StringBuilder();
//          String combined = "";
//          List<Integer> starts = new ArrayList<>();
 
//          int index = 0;
//          int startIndex = -1;
//          int endIndex = -1;
 
//          // T, "utor"
//          while (response.hasNext()) {
//              String token = response.next();
//              sb.append(token);
//              combined = sb.toString();
//              starts.add(combined.length()); // 0, 1, 5
//              // result.add(token); // t, utor, : 
 
//              // tutor:
//              // 0
//              // end index of tag is 10
//              // we are at index 12
//              startIndex = combined.indexOf(startTag);
//              endIndex = combined.indexOf(endTag);
 
//              // && starts.get(index) > startIndex && starts.get(index) < endIndex
//              // starts 0, 1
//              // 12,
//              boolean haveSeenStartTag = starts.get(index) > startIndex + startTag.length();
//              boolean haveSeenEndTag = endIndex >= 0;
 
//              if (haveSeenStartTag && !haveSeenEndTag) {
//                result.add(token);
//              }
 
//              // int remove = starts.get();
 
//              // if (haveSeenStartTag || starts.get(index) < endIndex - endTag.length()) {
//              //   result.add(token);
//              // }
 
//              index++;
//          }
 
//          return result.iterator();
//      }
 
//      // Main function to print the output
//      public static void main(String[] args) {
//          Iterator<String> output = processLLMResponse(llmStream(), startTag, endTag);
//          while (output.hasNext()) {
//              System.out.println(output.next());
//          }
//      }
//  }
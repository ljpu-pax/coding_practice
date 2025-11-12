import java.io.*;
import java.util.*;

/*

Example 1
Input: deadends = [], target = "0202"
Output: 4

Example 2
Input: deadends = ["0201","0101","0102","1212","2002"], target = "0202"
Output: 6

"0000" -> "1000" -> "1100" -> "1200" -> "1201" -> "1202" -> "0202"

Example 3
Input: deadends = ["8887","8889","8878","8898","8788","8988","7888","9888"], target = "8888"
Output: -1

*/

// Main class should be named 'Solution' and should not be public.
class OpenLock {
    public int openLock(String[] deadends, String target) {
        Set<String> deadend = new HashSet<>(Arrays.asList(deadends));
        if (deadend.contains("0000")) return -1;
        
        Queue<String> q = new LinkedList<>();
        Set<String> visited = new HashSet<>();
        
        q.offer("0000");
        visited.add("0000");
        int steps = 0;
        while (!q.isEmpty()) {
            int size = q.size();
            for (int i = 0; i < size; i++) {
                String current = q.poll();
                // if (deadend.contains(current)) continue;
                if (current.equals(target)) return steps;
        
                for (String combo : generateCombo(current)) {
                    if (!visited.contains(combo) && !deadend.contains(combo)) {
                        q.offer(combo);
                        visited.add(combo);
                    }
                }
            }
            steps++;
        }
        
        return steps;
    }
    
    private List<String> generateCombo(String current) {
        // 0000
        // 0001, 0009
        List<String> result = new ArrayList<>();
        
        char[] charArray = current.toCharArray();
        
        
        for (int i = 0; i < 4; i++) {
            char origin = charArray[i];
            
            // 0
            // go up
            if (origin == '9') {
                charArray[i] = (char) '0';
            } else {
                charArray[i] = (char) (origin + 1);
            }
            
            System.out.println(charArray);
        
            result.add(new String(charArray)); // 1000, 
            
            // go down
            if (origin == '0') {
                charArray[i] = (char) '9';
            } else {
                charArray[i] = (char) (origin - 1);
            }
            
            result.add(new String(charArray)); // 0000
            
            charArray[i] = origin;
        }
        
        return result;
    }
    public static void main(String[] args) {
        System.out.println("Hello, World");
        
        OpenLock s = new OpenLock();
        
        // System.out.println(s.openLock(new String[]{}, "0001"));
        
        // System.out.println(s.openLock(new String[]{"0201","0101","0102","1212","2002"}, "0202"));
        System.out.println(s.generateCombo("0000"));
    }
}


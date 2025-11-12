package doordash;
/*
 * Click Run to execute the snippet below!
 */

import java.io.*;
import java.util.*;

/*
 * To execute Java, please define "static void main" on a class
 * named Solution.
 *
 * If you need more classes, simply define them inline.
 */

 /*
 
 implment basic calculator 

 1+1 2
 1-1 2


 op +1 int valid

 1+1x2 3
 2x2/2+1 3

10*8 + 9
1+1*2-1*1

10*8 + 9
string 1 
number 10
number stack 89
operator

1+1*2-1*1
number 1
number stack 2
operatorstack   

+ 


  */

class Solution {
  public int basicCalculator(String equation) {
    if (equation == null || equation.length() == 0) return 0;
    Stack<Integer> intS = new Stack<>();
    Stack<Character> opS = new Stack<>();

    char[] cArray = equation.toCharArray();
    int num = 0;
    int i = 0;
    // 1 + 1
    while (i < cArray.length) {
      while (Character.isDigit(cArray[i])) {
        num += (int) cArray[i];
        i++;
        // 1
      }

      if (!intS.isEmpty() && !opS.isEmpty() && (opS.peek() == '/' || opS.peek() == '*')) {
        int prev = intS.pop();
        char op = opS.pop();
        if (op == '/') {
            intS.push(prev / num);
        }

        if (op  == '*') {
          intS.push(prev * num);
        }
      }

      if (cArray[i] == '+' || cArray[i] == '-') {
        opS.push(cArray[i]);
        // opS + 
      }

      if (num != 0) {
        intS.push(num);
        num = 0;
        continue;
      }
      // intS 1 1
      i++;
      // 1 2
    }


    // 1 1
    // + 

    int calculation = 0;
    while (!opS.isEmpty()) {
      int first = intS.pop();
      // 1
      int second = intS.pop();
      // 1
      char op = opS.pop();
      if (op == '-') {
        calculation += second - first;
      }

      if (op == '+') {
        calculation += second + first;
        // 2
      }
    }

    return calculation;
  }
  public static void main(String[] args) {
    // ArrayList<String> strings = new ArrayList<String>();
    // strings.add("Hello, World!");
    // strings.add("Welcome to CoderPad.");
    // strings.add("This pad is running Java " + Runtime.version().feature());

    Solution s = new Solution();
    System.out.println(s.basicCalculator("1"));

    // for (String string : strings) {
    //   System.out.println(string);
    // }
  }
}

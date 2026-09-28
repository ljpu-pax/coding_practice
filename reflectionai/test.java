package reflectionai;

// define and build a library
// 1 -> 1
// x -> x
// x + 1 -> x + 1
// 1 + x + 1 -> x + 2
// 1 + x + (-1) -> x
// x + x -> 2 * x
// x + 1 + x -> 2*x + 1
// x + (-1 * x) -> 0
// (x + 1) * x -> x*x + x

// int, char, 

// data types

import java.util.*;

abstract class Expr {
        abstract Expr simplify();
        abstract public String toString();
    }

class Num extends Expr {
    int value;

    Num (int v) {
        this.value = v;
    }

    Expr simplify() {
        return this;
    }

    public String toString() {
        return Integer.toString(value);
    }
}

class Var extends Expr {
    String name;

    Var (String v) {
        this.name = v;
    }

    Expr simplify() {
        return this;
    }

    public String toString() {
        return name;
    }
}

// 1 + (x * 2)
class Operation extends Expr {
    String op;
    Expr left;
    Expr right;

    Operation(String op, Expr left, Expr right) {
        this.op = op;
        this.left = left;
        this.right = right;
    }

    Expr simplify() {
        Expr l = left.simplify();
        Expr r = right.simplify();
        
        // 2 = 2
        if (l instanceof Num && r instanceof Num) {
            int a = ((Num) l).value;
            int b = ((Num) r).value;

            if (op.equals("+")) return new Num(a + b);
            if (op.equals("*")) return new Num(a * b);
        }

        // x + x = 2 * x
        if (op.equals("+") && l instanceof Var && r instanceof Var) {
            Var v1 = (Var) l;
            Var v2 = (Var) r;

            if (v1.name.equals(v2.name)) {
                return new Operation("*", new Num(2), v1);
            }
        }

        // 1 + (x + 1) equals = x + 2
        if (op.equals("+") && l instanceof Num && r instanceof Operation) {
            Operation br = (Operation) r;
            if (br.op.equals("+") && br.left instanceof Var && br.right instanceof Num) {
                Operation newOp = new Operation("+", br.left, br.right);
                return new Operation("+", l, newOp);
            }
        }

        return null;
    }

    public String toString() {
        return left + " " + op + " " + right;
    }
}

public class test {
    public static void main(String[] args) {
        // 1 + 1 * 2
        Operation o1 = new Operation("+", new Var("x"), new Var("x"));
        System.out.println(o1.simplify().toString());
        
        Operation o2 = new Operation("+", new Num(1), new Operation("+", new Num(1), new Var("x")));
        System.out.println(o2.simplify().toString());
    }
}

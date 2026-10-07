import java.util.*;

/** TUF 424 - If ElseIf. Branching with if, else if, else, nesting, compound conditions and the ternary operator. */
public class P424_IfElseIf {

    /** A chain of else-if branches: the first true condition wins and everything below it is skipped. */
    static String grade(int marks) {
        if (marks >= 90) return "A";
        else if (marks >= 80) return "B";
        else if (marks >= 70) return "C";
        else if (marks >= 60) return "D";
        else return "F";
    }

    /** Nested rules: a year is leap if divisible by 4, except centuries, except every 400th year. */
    static boolean isLeapYear(int year) {
        if (year % 4 != 0) {
            return false;
        } else if (year % 100 != 0) {
            return true;
        } else {
            return year % 400 == 0;
        }
    }

    /** Order matters: test the most specific condition (divisible by both) first. */
    static String fizzBuzz(int n) {
        if (n % 15 == 0) return "FizzBuzz";
        if (n % 3 == 0) return "Fizz";
        if (n % 5 == 0) return "Buzz";
        return String.valueOf(n);
    }

    /** Largest of three with independent if statements and a running best. */
    static int maxOfThree(int a, int b, int c) {
        int best = a;
        if (b > best) best = b;
        if (c > best) best = c;
        return best;
    }

    /** Largest of three with an else-if chain; exactly one branch runs. */
    static int maxOfThreeChain(int a, int b, int c) {
        if (a >= b && a >= c) return a;
        else if (b >= c) return b;
        else return c;
    }

    /** The same decision as a nested ternary expression; fine for two levels, unreadable beyond that. */
    static String sign(int x) {
        return x > 0 ? "positive" : (x < 0 ? "negative" : "zero");
    }

    /** Short-circuit evaluation: a[i] is only read once both index checks have passed. */
    static boolean isPositiveAt(int[] a, int i) {
        return i >= 0 && i < a.length && a[i] > 0;
    }

    /** Compound conditions with || and &&, plus a nested if for the exception inside a branch. */
    static String ticketPrice(int age, boolean student) {
        if (age < 5 || age >= 65) {
            return "free";
        } else if (age < 18) {
            return "child";
        } else {
            if (student) return "student";
            return "adult";
        }
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    public static void main(String[] args) {
        check(grade(95).equals("A") && grade(90).equals("A"), "A, including the boundary");
        check(grade(85).equals("B") && grade(80).equals("B"), "B");
        check(grade(79).equals("C") && grade(60).equals("D"), "C and D");
        check(grade(59).equals("F") && grade(0).equals("F"), "F, including zero");

        check(isLeapYear(2024), "2024 is divisible by 4");
        check(!isLeapYear(2023), "2023 is not divisible by 4");
        check(!isLeapYear(1900), "1900: a century that is not divisible by 400");
        check(isLeapYear(2000), "2000: divisible by 400");

        check(fizzBuzz(15).equals("FizzBuzz") && fizzBuzz(30).equals("FizzBuzz"), "both");
        check(fizzBuzz(9).equals("Fizz"), "three only");
        check(fizzBuzz(10).equals("Buzz"), "five only");
        check(fizzBuzz(7).equals("7") && fizzBuzz(1).equals("1"), "neither");

        int[][] triples = { {1, 2, 3}, {3, 2, 1}, {2, 3, 2}, {-5, -9, -7}, {4, 4, 4}, {0, -1, 0} };
        int[] expectedMax = {3, 3, 3, -5, 4, 0};
        for (int k = 0; k < triples.length; k++) {
            int a = triples[k][0], b = triples[k][1], c = triples[k][2];
            check(maxOfThree(a, b, c) == expectedMax[k], "maxOfThree case " + k);
            check(maxOfThreeChain(a, b, c) == expectedMax[k], "maxOfThreeChain case " + k);
        }

        check(sign(5).equals("positive"), "positive");
        check(sign(-2).equals("negative"), "negative");
        check(sign(0).equals("zero"), "zero");
        check(sign(Integer.MIN_VALUE).equals("negative"), "smallest int");

        int[] arr = {3, -1, 0, 8};
        check(isPositiveAt(arr, 0), "index 0 is positive");
        check(!isPositiveAt(arr, 1) && !isPositiveAt(arr, 2), "negative and zero are not positive");
        check(!isPositiveAt(arr, -1), "negative index is rejected without touching the array");
        check(!isPositiveAt(arr, 4), "index == length is rejected");
        check(!isPositiveAt(new int[0], 0), "empty array");

        check(ticketPrice(3, false).equals("free") && ticketPrice(70, true).equals("free"), "free");
        check(ticketPrice(5, false).equals("child") && ticketPrice(17, true).equals("child"), "child");
        check(ticketPrice(20, true).equals("student"), "student");
        check(ticketPrice(18, false).equals("adult") && ticketPrice(64, false).equals("adult"), "adult");

        System.out.println("OK P424_IfElseIf");
    }
}

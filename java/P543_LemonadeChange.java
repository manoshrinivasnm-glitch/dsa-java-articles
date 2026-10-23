import java.util.*;

/** TUF 543 - Lemonade Change. Each lemonade costs 5; customers pay with 5, 10 or 20 in order. Can everyone get exact change? */
public class P543_LemonadeChange {

    /** Approach 1: try every legal way of giving change for each 20 (recursive search). O(2^k) time for k twenties, O(n) stack. */
    static boolean bruteForce(int[] bills) {
        return canServe(bills, 0, 0, 0);   // start at the first customer with an empty till
    }

    static boolean canServe(int[] bills, int i, int fives, int tens) {
        if (i == bills.length) return true;   // every customer got correct change
        if (bills[i] == 5) return canServe(bills, i + 1, fives + 1, tens);     // a 5 needs no change
        // a 10 needs one 5 back
        if (bills[i] == 10) return fives > 0 && canServe(bills, i + 1, fives - 1, tens + 1);
        // a 20 needs 15 back: either 10 + 5 or 5 + 5 + 5
        boolean ok = false;
        if (tens > 0 && fives > 0) ok = canServe(bills, i + 1, fives - 1, tens - 1);   // try 10 + 5
        if (!ok && fives >= 3) ok = canServe(bills, i + 1, fives - 3, tens);   // otherwise try 5 + 5 + 5
        return ok;
    }

    /** Approach 2: greedy counting; for a 20 always hand back 10 + 5 when possible. O(n) time, O(1) space. */
    static boolean optimal(int[] bills) {
        int fives = 0, tens = 0;               // bills in the till (20s are never given back, so not tracked)
        for (int bill : bills) {
            if (bill == 5) {                   // exact payment: just keep it
                fives++;
            } else if (bill == 10) {           // owe 5 back
                if (fives == 0) return false;   // no 5 to give: stuck
                fives--;
                tens++;
            } else {                           // bill == 20
                if (tens > 0 && fives > 0) {   // prefer 10 + 5: keeps 5s, which are more useful
                    tens--;
                    fives--;
                } else if (fives >= 3) {       // otherwise pay 15 as three 5s
                    fives -= 3;
                } else {                       // cannot make 15 in change
                    return false;
                }
            }
        }
        return true;                           // served everyone
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] bills, boolean expected) {
        String in = Arrays.toString(bills);
        check(bruteForce(bills) == expected, "bruteForce wrong for " + in);
        check(optimal(bills) == expected, "optimal wrong for " + in);
    }

    public static void main(String[] args) {
        verify(new int[]{5, 5, 5, 10, 20}, true);
        verify(new int[]{5, 5, 10, 10, 20}, false);
        verify(new int[]{5, 5, 5, 5, 10, 20, 10, 10}, true);    // fails if a 20 is paid with three 5s first
        verify(new int[]{5, 5, 5, 5, 20, 20}, false);           // second 20 finds only one 5
        verify(new int[]{5, 5, 10, 20, 5, 5, 5, 5, 5, 5, 5, 5, 5, 10, 5, 5, 20, 5, 20, 5}, true);
        verify(new int[]{10}, false);                           // first customer needs change from an empty drawer
        verify(new int[]{20}, false);
        verify(new int[]{5}, true);
        verify(new int[]{}, true);                              // no customers
        System.out.println("OK P543_LemonadeChange");
    }
}

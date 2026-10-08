import java.util.*;

/** TUF 319 - Minimum coins. Make amount V with the fewest Indian currency coins/notes; return them largest first. */
public class P319_MinimumCoins {

    static final int[] INDIAN = {1, 2, 5, 10, 20, 50, 100, 500, 1000};

    /** Approach 1: dynamic programming over every amount 0..V; correct for any coin system. O(V * k) time, O(V) space. */
    static List<Integer> dynamicProgramming(int[] coins, int amount) {
        int[] best = new int[amount + 1];               // best[v] = fewest coins that make exactly v
        Arrays.fill(best, Integer.MAX_VALUE);
        best[0] = 0;
        for (int v = 1; v <= amount; v++) {
            for (int c : coins) {
                if (c <= v && best[v - c] != Integer.MAX_VALUE) best[v] = Math.min(best[v], best[v - c] + 1);
            }
        }
        if (best[amount] == Integer.MAX_VALUE) return null;    // amount cannot be formed at all
        List<Integer> used = new ArrayList<>();
        int v = amount;
        while (v > 0) {
            for (int i = coins.length - 1; i >= 0; i--) {      // largest coin that keeps us on an optimal path
                int c = coins[i];
                if (c <= v && best[v - c] == best[v] - 1) {
                    used.add(c);
                    v -= c;
                    break;
                }
            }
        }
        return used;
    }

    /** Approach 2: greedy, always take the largest coin that still fits. O(k + answer) time, O(1) extra space. */
    static List<Integer> greedy(int[] coins, int amount) {
        List<Integer> used = new ArrayList<>();
        for (int i = coins.length - 1; i >= 0; i--) {  // coins are sorted ascending, so walk from the end
            while (amount >= coins[i]) {
                amount -= coins[i];
                used.add(coins[i]);
            }
        }
        return used;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int amount, Integer... expected) {
        List<Integer> want = Arrays.asList(expected);
        check(dynamicProgramming(INDIAN, amount).equals(want), "dynamicProgramming V=" + amount);
        check(greedy(INDIAN, amount).equals(want), "greedy V=" + amount);
    }

    public static void main(String[] args) {
        verify(70, 50, 20);
        verify(121, 100, 20, 1);
        verify(49, 20, 20, 5, 2, 2);
        verify(1999, 1000, 500, 100, 100, 100, 100, 50, 20, 20, 5, 2, 2);
        verify(3, 2, 1);
        verify(1, 1);
        verify(0);                                      // edge: nothing to pay
        verify(1000, 1000);                             // exactly one note

        for (int v = 0; v <= 2000; v++) {               // greedy is optimal for every amount in this coin system
            check(greedy(INDIAN, v).equals(dynamicProgramming(INDIAN, v)), "exhaustive V=" + v);
        }

        int[] tricky = {1, 3, 4};                       // not a canonical system: greedy is no longer safe
        check(dynamicProgramming(tricky, 6).equals(List.of(3, 3)), "dp on {1,3,4}");
        check(greedy(tricky, 6).equals(List.of(4, 1, 1)), "greedy on {1,3,4} uses one coin too many");
        check(dynamicProgramming(new int[]{5, 10}, 3) == null, "dp reports unreachable amount");
        System.out.println("OK P319_MinimumCoins");
    }
}

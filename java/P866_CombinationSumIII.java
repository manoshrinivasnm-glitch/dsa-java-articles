import java.util.*;

/** TUF 866 - Combination Sum III. All sets of k distinct digits from 1..9 that add up to n. */
public class P866_CombinationSumIII {

    /** Approach 1: every subset of 1..9 is a 9-bit mask; keep the ones with k bits whose digits sum to n. 512 masks. */
    static List<List<Integer>> bruteForce(int k, int n) {
        List<List<Integer>> out = new ArrayList<>();
        for (int mask = 0; mask < (1 << 9); mask++) {
            if (Integer.bitCount(mask) != k) continue;
            int sum = 0;
            List<Integer> combo = new ArrayList<>();
            for (int d = 1; d <= 9; d++) {
                if ((mask & (1 << (d - 1))) != 0) {
                    sum += d;
                    combo.add(d);
                }
            }
            if (sum == n) out.add(combo);
        }
        return out;
    }

    /** Approach 2: pick / not-pick over the digits 1..9, carrying how many digits and how much sum are still needed. */
    static List<List<Integer>> pickNotPick(int k, int n) {
        List<List<Integer>> out = new ArrayList<>();
        pickRec(1, k, n, new ArrayList<>(), out);
        return out;
    }

    static void pickRec(int d, int k, int n, List<Integer> cur, List<List<Integer>> out) {
        if (k == 0) {
            if (n == 0) out.add(new ArrayList<>(cur));
            return;
        }
        if (d > 9 || n <= 0) return;                       // digits ran out, or the remaining sum cannot be reached
        cur.add(d);                                        // pick d
        pickRec(d + 1, k - 1, n - d, cur, out);
        cur.remove(cur.size() - 1);
        pickRec(d + 1, k, n, cur, out);                    // skip d
    }

    /** Approach 3: loop recursion that prunes on both the remaining sum and the number of digits left. */
    static List<List<Integer>> optimal(int k, int n) {
        List<List<Integer>> out = new ArrayList<>();
        loopRec(1, k, n, new ArrayList<>(), out);
        return out;
    }

    static void loopRec(int start, int k, int n, List<Integer> cur, List<List<Integer>> out) {
        if (k == 0) {
            if (n == 0) out.add(new ArrayList<>(cur));
            return;
        }
        for (int d = start; d <= 9; d++) {
            if (d > n) break;                              // d, and every larger digit, overshoots the sum
            if (10 - d < k) break;                         // fewer than k digits remain in d..9
            cur.add(d);
            loopRec(d + 1, k - 1, n - d, cur, out);
            cur.remove(cur.size() - 1);
        }
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    /** Order-independent fingerprint: each combination sorted and printed, then the list of prints sorted. */
    static List<String> canon(List<List<Integer>> lists) {
        List<String> keys = new ArrayList<>();
        for (List<Integer> l : lists) {
            List<Integer> sorted = new ArrayList<>(l);
            Collections.sort(sorted);
            keys.add(sorted.toString());
        }
        Collections.sort(keys);
        return keys;
    }

    static void verify(int k, int n, String... expected) {
        List<String> exp = new ArrayList<>(Arrays.asList(expected));
        Collections.sort(exp);
        String label = "k=" + k + " n=" + n;
        check(canon(bruteForce(k, n)).equals(exp), "bruteForce " + label);
        check(canon(pickNotPick(k, n)).equals(exp), "pickNotPick " + label);
        check(canon(optimal(k, n)).equals(exp), "optimal " + label);
    }

    public static void main(String[] args) {
        verify(3, 7, "[1, 2, 4]");
        verify(3, 9, "[1, 2, 6]", "[1, 3, 5]", "[2, 3, 4]");
        verify(4, 1);                                              // four distinct digits sum to at least 10
        verify(2, 18);                                             // 9 + 8 = 17 is the largest pair
        verify(1, 9, "[9]");
        verify(9, 45, "[1, 2, 3, 4, 5, 6, 7, 8, 9]");               // every digit exactly once
        verify(9, 44);                                             // all nine digits are forced and they sum to 45
        verify(3, 15, "[1, 5, 9]", "[1, 6, 8]", "[2, 4, 9]", "[2, 5, 8]", "[2, 6, 7]", "[3, 4, 8]", "[3, 5, 7]", "[4, 5, 6]");
        check(optimal(4, 20).size() == 12 && canon(optimal(4, 20)).equals(canon(bruteForce(4, 20)))
                && canon(optimal(4, 20)).equals(canon(pickNotPick(4, 20))), "k=4 n=20");
        System.out.println("OK P866_CombinationSumIII");
    }
}

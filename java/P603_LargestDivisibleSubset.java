import java.util.*;

/** TUF 603 - Largest Divisible Subset. From distinct positive integers, return a largest subset in which every pair (a, b) has a % b == 0 or b % a == 0. */
public class P603_LargestDivisibleSubset {

    /** Approach 1: try every subset of the sorted values; a sorted set is divisible iff each element divides the next. O(2^n * n) time. */
    static List<Integer> bruteForce(int[] nums) {
        int[] a = nums.clone();
        Arrays.sort(a);
        int n = a.length, bestMask = 0;
        for (int mask = 1; mask < (1 << n); mask++) {
            if (Integer.bitCount(mask) <= Integer.bitCount(bestMask)) continue;
            int prev = -1;
            boolean ok = true;
            for (int i = 0; i < n && ok; i++) {
                if ((mask >> i & 1) == 0) continue;
                if (prev != -1 && a[i] % a[prev] != 0) ok = false;      // consecutive chosen values must divide
                prev = i;
            }
            if (ok) bestMask = mask;
        }
        List<Integer> res = new ArrayList<>();
        for (int i = 0; i < n; i++) if ((bestMask >> i & 1) == 1) res.add(a[i]);
        return res;
    }

    /** Approach 2: sort, then LIS-style DP where "increasing" becomes "divides", with parent pointers to print the chain. O(n^2) time, O(n) space. */
    static List<Integer> optimal(int[] nums) {
        int n = nums.length;
        List<Integer> res = new ArrayList<>();
        if (n == 0) return res;
        int[] a = nums.clone();
        Arrays.sort(a);
        int[] dp = new int[n], parent = new int[n];                     // dp[i] = largest chain ending at a[i]
        int last = 0;
        for (int i = 0; i < n; i++) {
            dp[i] = 1;
            parent[i] = i;
            for (int j = 0; j < i; j++) {
                if (a[i] % a[j] == 0 && dp[j] + 1 > dp[i]) {
                    dp[i] = dp[j] + 1;
                    parent[i] = j;
                }
            }
            if (dp[i] > dp[last]) last = i;
        }
        res.add(a[last]);
        while (parent[last] != last) {
            last = parent[last];
            res.add(a[last]);
        }
        Collections.reverse(res);
        return res;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    /** A valid answer has the expected size, uses distinct input values only, and is pairwise divisible. */
    static void checkAnswer(int[] nums, List<Integer> got, int expectedSize, String who) {
        String in = Arrays.toString(nums) + " -> " + got;
        check(got.size() == expectedSize, who + ": wrong size for " + in);
        Set<Integer> input = new HashSet<>();
        for (int x : nums) input.add(x);
        check(new HashSet<>(got).size() == got.size(), who + ": repeated value in " + in);
        for (int x : got) check(input.contains(x), who + ": value not in input for " + in);
        for (int x : got) for (int y : got) check(x % y == 0 || y % x == 0, who + ": " + x + " and " + y + " do not divide in " + in);
    }

    static void verify(int[] nums, int expectedSize) {
        checkAnswer(nums, bruteForce(nums), expectedSize, "bruteForce");
        checkAnswer(nums, optimal(nums), expectedSize, "optimal");
    }

    public static void main(String[] args) {
        verify(new int[]{1, 2, 3}, 2);                          // {1, 2} or {1, 3}
        verify(new int[]{1, 2, 4, 8}, 4);                       // the whole array
        verify(new int[]{3, 4, 16, 8}, 3);                      // {4, 8, 16}; input is unsorted
        verify(new int[]{2, 3, 5, 7}, 1);                       // primes: any single value
        verify(new int[]{1, 16, 7, 8, 4}, 4);                   // {1, 4, 8, 16}
        verify(new int[]{5, 9, 18, 54, 108, 540, 90, 180, 360, 720}, 6);   // 9, 18, 90, 180, 360, 720
        verify(new int[]{13}, 1);                               // edge: one element
        verify(new int[]{}, 0);                                 // edge: empty
        check(optimal(new int[]{3, 4, 16, 8}).equals(List.of(4, 8, 16)), "unique answer, sorted order");

        Random rnd = new Random(603);
        for (int t = 0; t < 300; t++) {
            Set<Integer> s = new TreeSet<>();
            int size = rnd.nextInt(12);
            while (s.size() < size) s.add(1 + rnd.nextInt(60));
            int[] nums = s.stream().mapToInt(Integer::intValue).toArray();
            verify(nums, bruteForce(nums).size());
        }
        int[] big = new int[1000];
        for (int i = 0; i < big.length; i++) big[i] = i + 1;   // 1..1000: best is the powers of two, size 10
        checkAnswer(big, optimal(big), 10, "optimal");
        System.out.println("OK P603_LargestDivisibleSubset");
    }
}

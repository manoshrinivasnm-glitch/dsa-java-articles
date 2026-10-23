import java.util.*;

/** TUF 780 - Number of Longest Increasing Subsequences. Count the strictly increasing subsequences of maximum length. */
public class P780_NumberOfLongestIncreasingSubsequences {

    /** Approach 1: enumerate every subsequence. O(2^n * n) time, O(1) space. */
    static int bruteForce(int[] nums) {
        int n = nums.length, bestLen = 0, count = 0;            // longest length seen, and how many have it
        for (int mask = 1; mask < (1 << n); mask++) {           // each mask is one non-empty subsequence
            int len = 0, prev = Integer.MIN_VALUE;              // picked length so far, last picked value
            boolean ok = true;
            for (int i = 0; i < n && ok; i++) {
                if ((mask & (1 << i)) == 0) continue;           // index i is not picked
                if (len > 0 && nums[i] <= prev) ok = false;     // not strictly increasing: reject this pick
                prev = nums[i];
                len++;
            }
            if (!ok) continue;                                  // skip subsequences that are not increasing
            if (len > bestLen) { bestLen = len; count = 1; }    // new longest length: restart the count
            else if (len == bestLen) count++;                   // ties the longest: one more
        }
        return count;
    }

    /** Approach 2: LIS DP that also counts how many chains reach each length. O(n^2) time, O(n) space. */
    static int better(int[] nums) {
        int n = nums.length, maxLen = 0, total = 0;
        int[] len = new int[n], cnt = new int[n];           // len[i] = LIS ending at i, cnt[i] = how many
        for (int i = 0; i < n; i++) {
            len[i] = 1;                                     // nums[i] alone is one chain of length 1
            cnt[i] = 1;
            for (int j = 0; j < i; j++) {
                if (nums[j] >= nums[i]) continue;           // nums[j] cannot come before nums[i]
                if (len[j] + 1 > len[i]) { len[i] = len[j] + 1; cnt[i] = cnt[j]; }  // longer chain: inherit its count
                else if (len[j] + 1 == len[i]) cnt[i] += cnt[j];                    // same length: add its count
            }
            maxLen = Math.max(maxLen, len[i]);              // longest length found anywhere so far
        }
        for (int i = 0; i < n; i++) if (len[i] == maxLen) total += cnt[i];   // count every chain of max length
        return total;
    }

    /** Approach 3: Fenwick tree over value ranks storing (best length, count). O(n log n) time, O(n) space. */
    static int optimal(int[] nums) {
        int[] sorted = Arrays.stream(nums).distinct().sorted().toArray();   // distinct values, sorted, for ranks
        int m = sorted.length, bestLen = 0, bestCnt = 0;        // overall best length and its count
        int[] treeLen = new int[m + 1], treeCnt = new int[m + 1];   // Fenwick: best length and count per range
        for (int x : nums) {
            int r = Arrays.binarySearch(sorted, x) + 1;         // 1-based rank of x
            int len = 0, cnt = 0;
            for (int i = r - 1; i > 0; i -= i & -i) {           // best chain ending in a strictly smaller value
                if (treeLen[i] > len) { len = treeLen[i]; cnt = treeCnt[i]; }   // longer chain: take its count
                else if (treeLen[i] == len) cnt += treeCnt[i];   // an equal chain adds its count
            }
            if (len == 0) cnt = 1;                              // x starts a new chain on its own
            len++;                                              // x extends the chain by one
            for (int i = r; i <= m; i += i & -i) {              // record the chain ending at x
                if (len > treeLen[i]) { treeLen[i] = len; treeCnt[i] = cnt; }   // x makes a longer chain here
                else if (len == treeLen[i]) treeCnt[i] += cnt;   // same length: add the ways
            }
            if (len > bestLen) { bestLen = len; bestCnt = cnt; }   // new overall best length
            else if (len == bestLen) bestCnt += cnt;            // ties the best: add its ways
        }
        return bestCnt;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] nums, int expected) {
        String in = Arrays.toString(nums);
        check(bruteForce(nums) == expected, "bruteForce failed for " + in);
        check(better(nums) == expected, "better failed for " + in);
        check(optimal(nums) == expected, "optimal failed for " + in);
    }

    public static void main(String[] args) {
        verify(new int[]{1, 3, 5, 4, 7}, 2);                    // 1 3 5 7 and 1 3 4 7
        verify(new int[]{2, 2, 2, 2, 2}, 5);                    // every single element is an LIS of length 1
        verify(new int[]{1, 2, 4, 3, 5, 4, 7, 2}, 3);           // 1 2 4 5 7, 1 2 3 5 7, 1 2 3 4 7
        verify(new int[]{3, 2, 1}, 3);                          // strictly decreasing
        verify(new int[]{1, 2, 3}, 1);                          // the whole array
        verify(new int[]{-1, -3, 0, -2, 2}, 3);                 // negatives: -1 0 2, -3 0 2, -3 -2 2
        verify(new int[]{5}, 1);                                // edge: single element
        verify(new int[]{}, 0);                                 // edge: empty array

        Random rnd = new Random(780);
        for (int t = 0; t < 300; t++) {
            int[] nums = new int[rnd.nextInt(13)];
            for (int i = 0; i < nums.length; i++) nums[i] = rnd.nextInt(7) - 3;
            verify(nums, bruteForce(nums));
        }
        int[] big = new int[2000];                              // 0..999 twice: switch copies at any of 1001 points
        for (int i = 0; i < big.length; i++) big[i] = i % 1000;
        check(better(big) == 1001 && optimal(big) == 1001, "two copies of 0..999");
        System.out.println("OK P780_NumberOfLongestIncreasingSubsequences");
    }
}

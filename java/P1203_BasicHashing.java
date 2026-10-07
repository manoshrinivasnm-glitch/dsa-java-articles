import java.util.*;

/** TUF 1203 - Basic Hashing. Precompute once, then answer "how many times does x occur?" in O(1) per query. */
public class P1203_BasicHashing {

    /** Approach 1: no hashing, rescan the array for every query. O(n * q) time, O(1) extra space. */
    static int[] countQueriesBrute(int[] arr, int[] queries) {
        int[] answers = new int[queries.length];
        for (int i = 0; i < queries.length; i++) {
            int count = 0;
            for (int v : arr) if (v == queries[i]) count++;
            answers[i] = count;
        }
        return answers;
    }

    /** Approach 2: array hashing. Needs non-negative, bounded values; hash[v] is how often v occurs. O(n + q + maxValue) time, O(maxValue) space. */
    static int[] countQueriesArrayHash(int[] arr, int[] queries) {
        int maxValue = 0;
        for (int v : arr) maxValue = Math.max(maxValue, v);
        int[] hash = new int[maxValue + 1];
        for (int v : arr) hash[v]++;                                    // pre-storing step
        int[] answers = new int[queries.length];
        for (int i = 0; i < queries.length; i++) {
            int q = queries[i];
            answers[i] = (q >= 0 && q <= maxValue) ? hash[q] : 0;      // fetching step
        }
        return answers;
    }

    /** Approach 3: HashMap hashing for arbitrary keys (negative, huge, sparse). O(n + q) average time, O(distinct values) space. */
    static int[] countQueriesMapHash(int[] arr, int[] queries) {
        Map<Integer, Integer> hash = new HashMap<>();
        for (int v : arr) hash.merge(v, 1, Integer::sum);               // pre-storing step
        int[] answers = new int[queries.length];
        for (int i = 0; i < queries.length; i++) answers[i] = hash.getOrDefault(queries[i], 0);   // fetching step
        return answers;
    }

    /** Approach 4: the same idea with a hand-made table, to see what HashMap does internally. O(n + q) average time. */
    static int[] countQueriesChained(int[] arr, int[] queries) {
        ChainedHashTable table = new ChainedHashTable(16);              // tiny on purpose, so collisions really happen
        for (int v : arr) table.increment(v);
        int[] answers = new int[queries.length];
        for (int i = 0; i < queries.length; i++) answers[i] = table.count(queries[i]);
        return answers;
    }

    /** Character hashing for lowercase strings: the slot of c is c - 'a'. O(n) time, O(26) space. */
    static int[] charFrequencyLowercase(String s) {
        int[] hash = new int[26];
        for (int i = 0; i < s.length(); i++) hash[s.charAt(i) - 'a']++;
        return hash;
    }

    /** Character hashing for any 8-bit character: the char code itself is the slot. O(n) time, O(256) space. */
    static int[] charFrequencyAscii(String s) {
        int[] hash = new int[256];
        for (int i = 0; i < s.length(); i++) hash[s.charAt(i)]++;
        return hash;
    }

    /** Division-method hash table with separate chaining: key -> bucket key mod capacity, each bucket is a linked list. */
    static class ChainedHashTable {
        private static class Node {
            final int key;
            int count;
            Node next;
            Node(int key, Node next) { this.key = key; this.count = 1; this.next = next; }
        }

        private final Node[] buckets;

        ChainedHashTable(int capacity) { buckets = new Node[capacity]; }

        /** The hash function. floorMod keeps negative keys inside [0, capacity). */
        int bucketOf(int key) { return Math.floorMod(key, buckets.length); }

        void increment(int key) {
            int b = bucketOf(key);
            for (Node cur = buckets[b]; cur != null; cur = cur.next) {
                if (cur.key == key) { cur.count++; return; }            // key already in this chain
            }
            buckets[b] = new Node(key, buckets[b]);                     // new key: push onto the chain
        }

        int count(int key) {
            for (Node cur = buckets[bucketOf(key)]; cur != null; cur = cur.next) {
                if (cur.key == key) return cur.count;
            }
            return 0;
        }
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verifyCounts(int[] arr, int[] queries, int[] expected, boolean smallNonNegative) {
        check(Arrays.equals(countQueriesBrute(arr, queries), expected), "brute failed for " + Arrays.toString(arr));
        check(Arrays.equals(countQueriesMapHash(arr, queries), expected), "map failed for " + Arrays.toString(arr));
        check(Arrays.equals(countQueriesChained(arr, queries), expected), "chained failed for " + Arrays.toString(arr));
        if (smallNonNegative) {
            check(Arrays.equals(countQueriesArrayHash(arr, queries), expected), "array failed for " + Arrays.toString(arr));
        }
    }

    public static void main(String[] args) {
        verifyCounts(new int[]{1, 3, 2, 1, 3}, new int[]{1, 4, 2, 3, 12}, new int[]{2, 0, 1, 2, 0}, true);
        verifyCounts(new int[]{}, new int[]{0, 5}, new int[]{0, 0}, true);                  // edge: empty array
        verifyCounts(new int[]{7, 7, 7, 7}, new int[]{7, 0}, new int[]{4, 0}, true);        // one repeated value
        verifyCounts(new int[]{0, 16, 32, 48, 1}, new int[]{0, 16, 32, 48, 64, 1},
                new int[]{1, 1, 1, 1, 0, 1}, true);                                         // 0, 16, 32, 48 all land in bucket 0
        verifyCounts(new int[]{-5, 1_000_000_000, -5, 42}, new int[]{-5, 1_000_000_000, 42, -42, 0},
                new int[]{2, 1, 1, 0, 0}, false);                                           // negative and huge keys: only maps can do this

        int[] lower = charFrequencyLowercase("hello");
        check(lower['l' - 'a'] == 2 && lower['h' - 'a'] == 1 && lower['o' - 'a'] == 1 && lower['z' - 'a'] == 0, "lowercase hello");
        check(Arrays.stream(charFrequencyLowercase("")).sum() == 0, "empty string has no characters");
        int[] ascii = charFrequencyAscii("Hello, World!");
        check(ascii['l'] == 3 && ascii['o'] == 2 && ascii['H'] == 1 && ascii['h'] == 0 && ascii[' '] == 1 && ascii['!'] == 1, "ascii Hello, World!");
        int[] lowerAgain = charFrequencyLowercase("abcabcz");
        int[] asciiAgain = charFrequencyAscii("abcabcz");
        for (char c = 'a'; c <= 'z'; c++) {
            check(lowerAgain[c - 'a'] == asciiAgain[c], "the two character tables disagree on " + c);
        }
        System.out.println("OK P1203_BasicHashing");
    }
}

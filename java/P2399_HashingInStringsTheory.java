import java.util.*;

/**
 * TUF 2399 - Hashing In Strings (theory).
 * Polynomial rolling hash, prefix hashes for O(1) substring hashes, a sliding window hash,
 * collisions, double hashing, and counting distinct substrings with hashes.
 * Strings are lowercase letters; 'a' maps to 1, ..., 'z' maps to 26.
 */
public class P2399_HashingInStringsTheory {

    static final long MOD = 1_000_000_007L;     // large prime: keeps hashes small and collisions rare
    static final long BASE = 31;                // prime just above the alphabet size (26)

    /** Hash of the whole string by Horner's rule: s0*B^(n-1) + s1*B^(n-2) + ... + s(n-1), mod M. O(n). */
    static long polyHash(String s) {
        long h = 0;                                      // hash of the prefix read so far
        for (int i = 0; i < s.length(); i++) {
            h = (h * BASE + (s.charAt(i) - 'a' + 1)) % MOD;   // shift old digits up a power, add the new one
        }
        return h;
    }

    /** Same formula with any base and modulus, used to show collisions with a tiny modulus. */
    static long polyHash(String s, long base, long mod) {
        long h = 0;                                      // hash of the prefix read so far
        for (int i = 0; i < s.length(); i++) {
            h = (h * base + (s.charAt(i) - 'a' + 1)) % mod;   // Horner step with the given base and modulus
        }
        return h;
    }

    /** pre[i] = hash of the first i characters, so pre[0] = 0 and pre[n] = polyHash(s). O(n). */
    static long[] prefixHashes(String s) {
        long[] pre = new long[s.length() + 1];           // pre[0] = 0 is the hash of the empty prefix
        for (int i = 0; i < s.length(); i++) {
            pre[i + 1] = (pre[i] * BASE + (s.charAt(i) - 'a' + 1)) % MOD;   // extend prefix i by char i
        }
        return pre;
    }

    /** pw[k] = BASE^k mod M for k = 0..n. O(n). */
    static long[] powers(int n) {
        long[] pw = new long[n + 1];
        pw[0] = 1;                                       // BASE^0
        for (int k = 1; k <= n; k++) pw[k] = pw[k - 1] * BASE % MOD;   // previous power times BASE
        return pw;
    }

    /** Hash of s[l..r) (r exclusive) in O(1): pre[r] - pre[l] * BASE^(r-l). */
    static long substringHash(long[] pre, long[] pw, int l, int r) {
        // strip the first l chars (shifted up by r - l places) from pre[r]; + MOD keeps it non-negative
        return ((pre[r] - pre[l] * pw[r - l]) % MOD + MOD) % MOD;
    }

    /** Hashes of every window of length k, computed by sliding: drop the left char, shift, add the right char. O(n). */
    static long[] windowHashes(String s, int k) {
        int n = s.length();
        if (k <= 0 || k > n) return new long[0];         // no window of that length exists
        long[] out = new long[n - k + 1];                // one hash per window start
        long high = 1;                                   // BASE^(k-1): weight of the window's first char
        for (int i = 1; i < k; i++) high = high * BASE % MOD;
        long h = 0;
        for (int i = 0; i < k; i++) h = (h * BASE + (s.charAt(i) - 'a' + 1)) % MOD;   // hash the first window
        out[0] = h;
        for (int i = k; i < n; i++) {                    // char i enters, char i - k leaves
            h = (h - (s.charAt(i - k) - 'a' + 1) * high % MOD + MOD) % MOD;   // remove the leftmost char
            h = (h * BASE + (s.charAt(i) - 'a' + 1)) % MOD;                   // shift and append the new char
            out[i - k + 1] = h;                          // hash of the window s[i-k+1..i]
        }
        return out;
    }

    /** Count distinct substrings by storing every substring itself. O(n^3) time in the worst case. */
    static int countDistinctBrute(String s) {
        Set<String> seen = new HashSet<>();              // every distinct substring, stored in full
        for (int i = 0; i < s.length(); i++) {           // i = start of the substring
            for (int j = i + 1; j <= s.length(); j++) seen.add(s.substring(i, j));   // s[i..j) for each end j
        }
        return seen.size();                              // duplicates were absorbed by the set
    }

    /** Count distinct substrings with a double hash per substring. O(n^2) time and space. */
    static int countDistinctHashing(String s) {
        final long M1 = 1_000_000_007L, B1 = 31, M2 = 1_000_000_009L, B2 = 37;   // two independent hashes
        int n = s.length();
        Set<Long> seen = new HashSet<>();                // one combined key per distinct substring
        for (int i = 0; i < n; i++) {                    // i = start of the substring
            long h1 = 0, h2 = 0;                         // both hashes of the empty string s[i..i)
            for (int j = i; j < n; j++) {                // extend s[i..j] one char at a time
                int v = s.charAt(j) - 'a' + 1;
                h1 = (h1 * B1 + v) % M1;                 // append char j to each hash
                h2 = (h2 * B2 + v) % M2;
                long key = h1 * M2 + h2;                 // one long that is unique per pair (h1, h2)
                seen.add(key);
            }
        }
        return seen.size();
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    public static void main(String[] args) {
        // worked examples from the article
        check(polyHash("a") == 1, "hash a");
        check(polyHash("ab") == 33, "hash ab");
        check(polyHash("abc") == 1026, "hash abc");
        check(polyHash("") == 0, "hash of empty string");
        check(polyHash("abc", BASE, MOD) == polyHash("abc"), "overload agrees");

        // prefix hashes and O(1) substring hashes
        String s = "abcab";
        long[] pre = prefixHashes(s), pw = powers(s.length());
        check(pre[s.length()] == polyHash(s), "pre[n] is the full hash");
        check(substringHash(pre, pw, 0, 2) == substringHash(pre, pw, 3, 5), "ab at 0 and ab at 3");
        check(substringHash(pre, pw, 1, 3) == polyHash("bc"), "bc");
        check(substringHash(pre, pw, 0, 2) != substringHash(pre, pw, 1, 3), "ab vs bc");
        for (int l = 0; l <= s.length(); l++) {
            for (int r = l; r <= s.length(); r++) {
                check(substringHash(pre, pw, l, r) == polyHash(s.substring(l, r)), "substring " + l + "," + r);
            }
        }

        // sliding window hashes agree with prefix-hash substrings
        String t = "abracadabra";
        long[] tp = prefixHashes(t), tw = powers(t.length());
        for (int k = 1; k <= t.length(); k++) {
            long[] w = windowHashes(t, k);
            check(w.length == t.length() - k + 1, "window count");
            for (int i = 0; i < w.length; i++) check(w[i] == substringHash(tp, tw, i, i + k), "window " + k + "," + i);
        }
        check(windowHashes("abc", 4).length == 0, "window longer than string");

        // collisions: with modulus 101, "ab" and "dj" both hash to 33
        check(polyHash("ab", 31, 101) == 33 && polyHash("dj", 31, 101) == 33, "tiny modulus collision");
        check(polyHash("ab") != polyHash("dj"), "large modulus separates them");
        // pigeonhole: 676 two-letter strings into 101 buckets must collide
        Map<Long, String> bucket = new HashMap<>();
        boolean collided = false;
        for (char x = 'a'; x <= 'z'; x++) {
            for (char y = 'a'; y <= 'z'; y++) {
                String w = "" + x + y;
                if (bucket.putIfAbsent(polyHash(w, 31, 101), w) != null) collided = true;
            }
        }
        check(collided, "pigeonhole guarantees a collision");

        // counting distinct substrings: brute force vs hashing
        String[] inputs = {"", "a", "aaa", "abab", "abcab", "banana", "mississippi"};
        int[] expected = {0, 1, 3, 7, 12, 15, 53};
        for (int i = 0; i < inputs.length; i++) {
            check(countDistinctBrute(inputs[i]) == expected[i], "brute distinct " + inputs[i]);
            check(countDistinctHashing(inputs[i]) == expected[i], "hash distinct " + inputs[i]);
        }
        Random rnd = new Random(2399);
        for (int trial = 0; trial < 200; trial++) {
            int n = rnd.nextInt(40);
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < n; i++) sb.append((char) ('a' + rnd.nextInt(3)));
            String r = sb.toString();
            check(countDistinctBrute(r) == countDistinctHashing(r), "random distinct " + r);
        }
        System.out.println("OK P2399_HashingInStringsTheory");
    }
}

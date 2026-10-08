import java.util.*;

/** TUF 198 - Compare version numbers (LeetCode 165). Return -1, 0 or 1; revisions compare as integers and missing ones count as 0. */
public class P198_CompareVersionNumbers {

    /** Approach 1: split on '.', parse each revision, pad the shorter list with zeros. O(n + m) time and space. */
    static int splitAndParse(String version1, String version2) {
        String[] a = version1.split("\\."), b = version2.split("\\.");
        int len = Math.max(a.length, b.length);
        for (int i = 0; i < len; i++) {
            int x = i < a.length ? Integer.parseInt(a[i]) : 0;   // parseInt drops leading zeros: "001" -> 1
            int y = i < b.length ? Integer.parseInt(b[i]) : 0;
            if (x != y) return x < y ? -1 : 1;
        }
        return 0;
    }

    /** Approach 2: walk both strings with two pointers, building one revision at a time. O(n + m) time, O(1) space. */
    static int optimal(String version1, String version2) {
        int i = 0, j = 0, n = version1.length(), m = version2.length();
        while (i < n || j < m) {
            int x = 0, y = 0;                                    // an exhausted string contributes 0
            while (i < n && version1.charAt(i) != '.') x = x * 10 + (version1.charAt(i++) - '0');
            while (j < m && version2.charAt(j) != '.') y = y * 10 + (version2.charAt(j++) - '0');
            if (x != y) return x < y ? -1 : 1;
            i++;                                                 // step over the '.' (or past the end)
            j++;
        }
        return 0;
    }

    /** Approach 3: never convert. Strip leading zeros, then more digits wins, else compare digit by digit. O(n + m) time, O(1) space. */
    static int digitStrings(String version1, String version2) {
        int i = 0, j = 0, n = version1.length(), m = version2.length();
        while (i < n || j < m) {
            while (i < n && version1.charAt(i) == '0') i++;      // leading zeros carry no value
            while (j < m && version2.charAt(j) == '0') j++;
            int si = i, sj = j;
            while (i < n && version1.charAt(i) != '.') i++;
            while (j < m && version2.charAt(j) != '.') j++;
            int lenX = i - si, lenY = j - sj;                    // number of significant digits (0 means the value 0)
            if (lenX != lenY) return lenX < lenY ? -1 : 1;
            for (int k = 0; k < lenX; k++) {                     // same length: the first differing digit decides
                char cx = version1.charAt(si + k), cy = version2.charAt(sj + k);
                if (cx != cy) return cx < cy ? -1 : 1;
            }
            i++;
            j++;
        }
        return 0;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(String v1, String v2, int expected) {
        String in = "\"" + v1 + "\" vs \"" + v2 + "\"";
        check(splitAndParse(v1, v2) == expected, "splitAndParse failed for " + in);
        check(optimal(v1, v2) == expected, "optimal failed for " + in);
        check(digitStrings(v1, v2) == expected, "digitStrings failed for " + in);
    }

    public static void main(String[] args) {
        verify("1.2", "1.10", -1);                  // 2 < 10 as integers, although "2" > "10" as text
        verify("1.01", "1.001", 0);                 // leading zeros are ignored
        verify("1.0", "1.0.0.0", 0);                // missing revisions count as 0
        verify("0.1", "1.1", -1);
        verify("1.0.1", "1", 1);                    // the longer one has a non-zero extra revision
        verify("7.5.2.4", "7.5.3", -1);
        verify("1.0.0.1", "1", 1);
        verify("2147483647", "2147483646", 1);      // largest int revision
        verify("01", "1", 0);
        verify("1", "1", 0);                        // edge: identical single revisions
        verify("0", "0.0.0", 0);                    // edge: all zeros

        // Revisions far beyond the int range: only the digit-string comparison can handle these.
        check(digitStrings("1.00000000000000000000000000001", "1.1") == 0, "huge leading-zero revision");
        check(digitStrings("1.99999999999999999999", "1.100000000000000000000") == -1, "20 nines < 1 followed by 20 zeros");
        check(digitStrings("123456789012345678901234567891", "123456789012345678901234567890") == 1, "last digit decides");

        // Seeded random versions with small revisions and random leading zeros, cross-checked against splitAndParse.
        Random rnd = new Random(198);
        for (int t = 0; t < 2000; t++) {
            String v1 = randomVersion(rnd), v2 = randomVersion(rnd);
            verify(v1, v2, splitAndParse(v1, v2));
        }
        System.out.println("OK P198_CompareVersionNumbers");
    }

    static String randomVersion(Random rnd) {
        StringBuilder sb = new StringBuilder();
        int parts = 1 + rnd.nextInt(4);
        for (int p = 0; p < parts; p++) {
            if (p > 0) sb.append('.');
            int zeros = rnd.nextInt(3);
            for (int z = 0; z < zeros; z++) sb.append('0');
            sb.append(rnd.nextInt(4) == 0 ? 0 : rnd.nextInt(25));
        }
        return sb.toString();
    }
}

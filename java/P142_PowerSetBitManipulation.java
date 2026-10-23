import java.util.*;

/** TUF 142 - Power Set via bit manipulation. Return every subset of an array of distinct integers. */
public class P142_PowerSetBitManipulation {

    /** Approach 1: recursion; at every index either skip the element or take it. O(2^n * n) time, O(n) recursion depth. */
    static List<List<Integer>> recursion(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();    // every subset found so far
        buildSubsets(nums, 0, new ArrayList<>(), result);  // start at index 0 with an empty subset
        return result;
    }

    private static void buildSubsets(int[] nums, int index, List<Integer> current, List<List<Integer>> result) {
        if (index == nums.length) {                      // every element decided: one complete subset
            result.add(new ArrayList<>(current));        // store a copy, since current keeps changing
            return;
        }
        buildSubsets(nums, index + 1, current, result);  // skip nums[index]
        current.add(nums[index]);                        // take nums[index]
        buildSubsets(nums, index + 1, current, result);  // explore every subset that includes it
        current.remove(current.size() - 1);              // undo the choice before returning
    }

    /** Approach 2: every mask in [0, 2^n) names one subset; bit i set means nums[i] is in it. O(2^n * n) time, O(1) extra space. */
    static List<List<Integer>> optimal(int[] nums) {
        int n = nums.length;
        int total = 1 << n;                                       // 2^n subsets
        List<List<Integer>> result = new ArrayList<>(total);
        for (int mask = 0; mask < total; mask++) {                // each mask encodes exactly one subset
            List<Integer> subset = new ArrayList<>();
            for (int i = 0; i < n; i++) {                         // test every bit position
                if ((mask & (1 << i)) != 0) subset.add(nums[i]);  // bit i set -> nums[i] is in this subset
            }
            result.add(subset);
        }
        return result;
    }

    /** Same idea on a string: every subsequence of s, one per mask (the TUF statement of this problem). O(2^n * n) time. */
    static List<String> subsequences(String s) {
        int n = s.length();
        List<String> result = new ArrayList<>();
        for (int mask = 0; mask < (1 << n); mask++) {  // each of the 2^n masks picks one subsequence
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < n; i++) {
                if (((mask >> i) & 1) == 1) sb.append(s.charAt(i));   // bit i set -> keep s.charAt(i)
            }
            result.add(sb.toString());                 // characters keep their original order
        }
        return result;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    /** Order-independent view: sort each subset, render it, sort the renderings. */
    static List<String> normalize(List<List<Integer>> subsets) {
        List<String> out = new ArrayList<>();
        for (List<Integer> s : subsets) {
            List<Integer> copy = new ArrayList<>(s);
            Collections.sort(copy);
            out.add(copy.toString());
        }
        Collections.sort(out);
        return out;
    }

    static void verify(int[] nums, String... expectedSubsets) {
        List<String> expected = new ArrayList<>(Arrays.asList(expectedSubsets));
        Collections.sort(expected);
        String label = Arrays.toString(nums);
        check(normalize(recursion(nums)).equals(expected), "recursion " + label);
        check(normalize(optimal(nums)).equals(expected), "optimal " + label);
    }

    static void verifyString(String s, String... expected) {
        List<String> got = new ArrayList<>(subsequences(s));
        Collections.sort(got);
        List<String> exp = new ArrayList<>(Arrays.asList(expected));
        Collections.sort(exp);
        check(got.equals(exp), "subsequences of \"" + s + "\": " + got);
    }

    public static void main(String[] args) {
        verify(new int[]{}, "[]");                                               // empty set has exactly one subset
        verify(new int[]{5}, "[]", "[5]");
        verify(new int[]{1, 2}, "[]", "[1]", "[2]", "[1, 2]");
        verify(new int[]{1, 2, 3}, "[]", "[1]", "[2]", "[3]", "[1, 2]", "[1, 3]", "[2, 3]", "[1, 2, 3]");
        verify(new int[]{-1, 0}, "[]", "[-1]", "[0]", "[-1, 0]");                // negatives and zero are just values

        // the bitmask version has a fixed, predictable order: mask 0, 1, 2, ... 7
        check(optimal(new int[]{1, 2, 3}).toString().equals("[[], [1], [2], [1, 2], [3], [1, 3], [2, 3], [1, 2, 3]]"),
                "mask order for [1, 2, 3]");

        int[] ten = new int[10];
        for (int i = 0; i < 10; i++) ten[i] = i + 1;
        List<String> a = normalize(recursion(ten)), b = normalize(optimal(ten));
        check(a.size() == 1024 && a.equals(b) && new HashSet<>(a).size() == 1024, "n = 10 gives 1024 distinct subsets");

        verifyString("abc", "", "a", "b", "c", "ab", "ac", "bc", "abc");
        verifyString("", "");
        verifyString("xy", "", "x", "y", "xy");
        verifyString("aa", "", "a", "a", "aa");                                  // positions differ, so both "a" are kept
        System.out.println("OK P142_PowerSetBitManipulation");
    }
}

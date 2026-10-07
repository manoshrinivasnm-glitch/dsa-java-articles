import java.util.*;

/** TUF 875 - Letter Combinations of a Phone Number. All strings the digits 2-9 could spell on a phone keypad. */
public class P875_LetterCombinationsOfAPhoneNumber {

    /** Letters printed on each key; index = digit. Keys 0 and 1 carry no letters. */
    static final String[] KEYS = {"", "", "abc", "def", "ghi", "jkl", "mno", "pqrs", "tuv", "wxyz"};

    /** Approach 1: iterative cascade; extend every partial string by each letter of the next digit. O(4^n * n) time. */
    static List<String> iterative(String digits) {
        List<String> out = new ArrayList<>();
        if (digits.isEmpty()) return out;
        out.add("");                                        // one empty prefix to start from
        for (char ch : digits.toCharArray()) {
            String letters = KEYS[ch - '0'];
            List<String> next = new ArrayList<>(out.size() * letters.length());
            for (String prefix : out) {
                for (char letter : letters.toCharArray()) next.add(prefix + letter);
            }
            out = next;
        }
        return out;
    }

    /** Approach 2: backtracking with one shared StringBuilder; level i chooses the letter for digit i. O(4^n * n) time, O(n) depth. */
    static List<String> optimal(String digits) {
        List<String> out = new ArrayList<>();
        if (digits.isEmpty()) return out;
        backtrack(digits, 0, new StringBuilder(), out);
        return out;
    }

    static void backtrack(String digits, int i, StringBuilder cur, List<String> out) {
        if (i == digits.length()) {
            out.add(cur.toString());
            return;
        }
        String letters = KEYS[digits.charAt(i) - '0'];
        for (int j = 0; j < letters.length(); j++) {
            cur.append(letters.charAt(j));
            backtrack(digits, i + 1, cur, out);
            cur.deleteCharAt(cur.length() - 1);             // undo before trying the next letter
        }
    }

    /** Approach 3: number the combinations 0..total-1 and decode each like a mixed-radix odometer. O(4^n * n) time, O(n) extra. */
    static List<String> mixedRadix(String digits) {
        List<String> out = new ArrayList<>();
        if (digits.isEmpty()) return out;
        int n = digits.length();
        int total = 1;
        for (int i = 0; i < n; i++) total *= KEYS[digits.charAt(i) - '0'].length();
        char[] buf = new char[n];
        for (int t = 0; t < total; t++) {
            int rest = t;
            for (int i = n - 1; i >= 0; i--) {              // the last digit is the least significant position
                String letters = KEYS[digits.charAt(i) - '0'];
                buf[i] = letters.charAt(rest % letters.length());
                rest /= letters.length();
            }
            out.add(new String(buf));
        }
        return out;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(String digits, List<String> expected) {
        check(iterative(digits).equals(expected), "iterative \"" + digits + "\"");
        check(optimal(digits).equals(expected), "optimal \"" + digits + "\"");
        check(mixedRadix(digits).equals(expected), "mixedRadix \"" + digits + "\"");
    }

    public static void main(String[] args) {
        verify("23", List.of("ad", "ae", "af", "bd", "be", "bf", "cd", "ce", "cf"));
        verify("", List.of());                                                  // no digits: no combinations, not [""]
        verify("2", List.of("a", "b", "c"));
        verify("9", List.of("w", "x", "y", "z"));                               // a four-letter key
        verify("79", List.of("pw", "px", "py", "pz", "qw", "qx", "qy", "qz", "rw", "rx", "ry", "rz", "sw", "sx", "sy", "sz"));
        List<String> big = optimal("7799");
        check(big.size() == 256 && big.get(0).equals("ppww") && big.get(255).equals("sszz") && big.get(100).equals("qrxw"), "7799");
        check(iterative("7799").equals(big) && mixedRadix("7799").equals(big), "7799 agreement");
        check(new HashSet<>(big).size() == 256, "7799 duplicates");
        List<String> three = mixedRadix("234");
        check(three.size() == 27 && three.get(0).equals("adg") && three.get(26).equals("cfi"), "234");
        check(iterative("234").equals(three) && optimal("234").equals(three), "234 agreement");
        System.out.println("OK P875_LetterCombinationsOfAPhoneNumber");
    }
}

import java.util.*;

/** TUF 331 - Encode and Decode Strings (LeetCode 271). Turn a list of arbitrary strings into one string and back, losslessly. */
public class P331_EncodeAndDecodeStrings {

    /** Approach 1 (encode): double every '#' inside a word, then end the word with "#;". O(total length) time. */
    static String encodeEscaped(List<String> strs) {
        StringBuilder sb = new StringBuilder();
        for (String s : strs) {
            for (int i = 0; i < s.length(); i++) {
                char c = s.charAt(i);
                if (c == '#') sb.append("##");          // escaped '#'
                else sb.append(c);
            }
            sb.append("#;");                            // '#' followed by anything except '#' ends a word
        }
        return sb.toString();
    }

    /** Approach 1 (decode): read left to right; "##" is a literal '#', "#;" closes the current word. O(total length) time. */
    static List<String> decodeEscaped(String s) {
        List<String> out = new ArrayList<>();
        StringBuilder cur = new StringBuilder();
        int i = 0;
        while (i < s.length()) {
            char c = s.charAt(i);
            if (c != '#') {
                cur.append(c);
                i++;
            } else if (s.charAt(i + 1) == '#') {        // a '#' is never the last character of a valid encoding
                cur.append('#');
                i += 2;
            } else {
                out.add(cur.toString());
                cur.setLength(0);
                i += 2;
            }
        }
        return out;
    }

    /** Approach 2 (encode): write each word as length + '#' + word. O(total length) time, no escaping needed. */
    static String encode(List<String> strs) {
        StringBuilder sb = new StringBuilder();
        for (String s : strs) sb.append(s.length()).append('#').append(s);
        return sb.toString();
    }

    /** Approach 2 (decode): read digits up to the first '#', then copy exactly that many characters blindly. O(total length) time. */
    static List<String> decode(String s) {
        List<String> out = new ArrayList<>();
        int i = 0;
        while (i < s.length()) {
            int len = 0;
            while (s.charAt(i) != '#') {                // the header contains only digits
                len = len * 10 + (s.charAt(i) - '0');
                i++;
            }
            i++;                                        // skip the '#' that ends the header
            out.add(s.substring(i, i + len));           // the payload may contain '#' or digits; we never look at it
            i += len;
        }
        return out;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(List<String> strs) {
        check(decodeEscaped(encodeEscaped(strs)).equals(strs), "escaped round trip failed for " + strs);
        check(decode(encode(strs)).equals(strs), "length-prefix round trip failed for " + strs);
    }

    public static void main(String[] args) {
        verify(List.of("lint", "code", "love", "you"));
        verify(List.of("we", "say", ":", "yes"));
        verify(List.of("#", "##", "a#", "#;", "3#abc", "12#"));   // delimiters and headers inside the data
        verify(List.of("hello world", "tab\there", "line\nbreak", "caf\u00E9"));
        verify(List.of("x".repeat(1234), "", "y"));             // multi-digit length header, empty word in the middle
        verify(List.of(""));                                    // edge: one empty string
        verify(List.of("", ""));                                // edge: two empty strings
        verify(List.of());                                      // edge: no strings at all

        // The exact formats, and an empty list must not look like a list holding one empty string.
        check(encodeEscaped(List.of("a#b", "")).equals("a##b#;#;"), "escaped format");
        check(encode(List.of("a#b", "")).equals("3#a#b0#"), "length-prefix format");
        check(!encode(List.of()).equals(encode(List.of(""))), "[] and [\"\"] must differ");
        check(!encodeEscaped(List.of()).equals(encodeEscaped(List.of(""))), "[] and [\"\"] must differ (escaped)");

        // Seeded random lists over an alphabet full of troublemakers: '#', ';' and digits.
        Random rnd = new Random(331);
        String alphabet = "#;0123a";
        for (int t = 0; t < 2000; t++) {
            List<String> strs = new ArrayList<>();
            int count = rnd.nextInt(6);
            for (int k = 0; k < count; k++) {
                StringBuilder sb = new StringBuilder();
                int len = rnd.nextInt(14);
                for (int i = 0; i < len; i++) sb.append(alphabet.charAt(rnd.nextInt(alphabet.length())));
                strs.add(sb.toString());
            }
            verify(strs);
        }
        System.out.println("OK P331_EncodeAndDecodeStrings");
    }
}

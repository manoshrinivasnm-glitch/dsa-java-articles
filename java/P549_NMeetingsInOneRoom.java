import java.util.*;

/** TUF 549 - N meetings in one room. Pick the most meetings with no overlap; a meeting must start strictly after the previous one ends. */
public class P549_NMeetingsInOneRoom {

    /** Approach 1: try every subset of meetings and keep the largest clash-free one. O(2^n * n log n) time, O(n) space. */
    static int bruteForce(int[] start, int[] end) {
        int n = start.length, best = 0;
        for (int mask = 0; mask < (1 << n); mask++) {
            List<Integer> chosen = new ArrayList<>();
            for (int i = 0; i < n; i++) if ((mask & (1 << i)) != 0) chosen.add(i);
            if (chosen.size() <= best) continue;          // cannot improve the answer
            chosen.sort((a, b) -> Integer.compare(start[a], start[b]));
            boolean ok = true;
            for (int k = 1; k < chosen.size() && ok; k++) {
                if (start[chosen.get(k)] <= end[chosen.get(k - 1)]) ok = false;   // clash with the previous meeting
            }
            if (ok) best = chosen.size();
        }
        return best;
    }

    /** Approach 2: sort by end time and take every meeting that starts after the last chosen one ends. O(n log n) time, O(n) space. */
    static List<Integer> optimal(int[] start, int[] end) {
        int n = start.length;
        Integer[] order = new Integer[n];
        for (int i = 0; i < n; i++) order[i] = i;
        // earliest finish first; on equal end times the lower meeting number goes first
        Arrays.sort(order, (a, b) -> end[a] != end[b] ? Integer.compare(end[a], end[b]) : Integer.compare(a, b));
        List<Integer> picked = new ArrayList<>();
        long lastEnd = Long.MIN_VALUE;
        for (int i : order) {
            if (start[i] > lastEnd) {
                picked.add(i + 1);                       // meetings are numbered from 1
                lastEnd = end[i];
            }
        }
        return picked;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] start, int[] end, int expected, List<Integer> expectedOrder) {
        String in = Arrays.toString(start) + " / " + Arrays.toString(end);
        int b = bruteForce(start, end);
        List<Integer> picked = optimal(start, end);
        check(b == expected, "bruteForce gave " + b + ", expected " + expected + " for " + in);
        check(picked.size() == expected, "optimal picked " + picked + ", expected " + expected + " meetings for " + in);
        for (int k = 1; k < picked.size(); k++) {
            int prev = picked.get(k - 1) - 1, cur = picked.get(k) - 1;
            check(start[cur] > end[prev], "optimal picked clashing meetings " + picked + " for " + in);
        }
        check(picked.equals(expectedOrder), "optimal order " + picked + ", expected " + expectedOrder + " for " + in);
    }

    public static void main(String[] args) {
        verify(new int[]{1, 3, 0, 5, 8, 5}, new int[]{2, 4, 5, 7, 9, 9}, 4, List.of(1, 2, 4, 5));
        verify(new int[]{10, 12, 20}, new int[]{20, 25, 30}, 1, List.of(1));               // 20 does not start after 20
        verify(new int[]{1, 2, 3}, new int[]{2, 3, 4}, 2, List.of(1, 3));                  // touching endpoints clash
        verify(new int[]{1, 3, 5, 7}, new int[]{2, 4, 6, 8}, 4, List.of(1, 2, 3, 4));      // no clashes at all
        verify(new int[]{1, 1, 1}, new int[]{4, 4, 4}, 1, List.of(1));                     // identical meetings
        verify(new int[]{75250, 50074, 43659, 8931, 11273, 27545, 50879, 77924},
               new int[]{112960, 114515, 81825, 93424, 54316, 35533, 73383, 160252}, 3, List.of(6, 7, 1));
        verify(new int[]{5}, new int[]{5}, 1, List.of(1));                                 // single zero-length meeting
        verify(new int[]{}, new int[]{}, 0, List.of());                                    // no meetings
        System.out.println("OK P549_NMeetingsInOneRoom");
    }
}

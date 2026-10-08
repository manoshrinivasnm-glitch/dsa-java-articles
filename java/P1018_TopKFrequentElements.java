import java.util.*;

/** TUF 1018 - Top K Frequent Elements. Return the k values that occur most often (the answer is unique; any order). */
public class P1018_TopKFrequentElements {

    /** Approach 1: count, sort all distinct values by count, take the first k. O(n + d log d) time, O(d) space. */
    static int[] bruteForce(int[] nums, int k) {
        Map<Integer, Integer> freq = new HashMap<>();
        for (int x : nums) freq.merge(x, 1, Integer::sum);
        List<Map.Entry<Integer, Integer>> entries = new ArrayList<>(freq.entrySet());
        entries.sort((p, q) -> Integer.compare(q.getValue(), p.getValue()));   // most frequent first
        int[] res = new int[k];
        for (int i = 0; i < k; i++) res[i] = entries.get(i).getKey();
        return res;
    }

    /** Approach 2: count, then keep the k most frequent in a size-k min-heap. O(n + d log k) time, O(d) space. */
    static int[] better(int[] nums, int k) {
        Map<Integer, Integer> freq = new HashMap<>();
        for (int x : nums) freq.merge(x, 1, Integer::sum);
        PriorityQueue<Integer> pq = new PriorityQueue<>((p, q) -> Integer.compare(freq.get(p), freq.get(q)));   // least frequent on top
        for (int x : freq.keySet()) {
            pq.add(x);
            if (pq.size() > k) pq.poll();                      // evict the weakest candidate
        }
        int[] res = new int[k];
        for (int i = k - 1; i >= 0; i--) res[i] = pq.poll();
        return res;
    }

    /** Approach 3: count, then bucket values by their count (counts are at most n). O(n) time, O(n) space. */
    static int[] optimal(int[] nums, int k) {
        Map<Integer, Integer> freq = new HashMap<>();
        for (int x : nums) freq.merge(x, 1, Integer::sum);
        List<List<Integer>> buckets = new ArrayList<>();       // buckets.get(f) = values seen exactly f times
        for (int f = 0; f <= nums.length; f++) buckets.add(new ArrayList<>());
        for (Map.Entry<Integer, Integer> e : freq.entrySet()) buckets.get(e.getValue()).add(e.getKey());
        int[] res = new int[k];
        int idx = 0;
        for (int f = nums.length; f >= 1 && idx < k; f--) {
            for (int x : buckets.get(f)) {
                if (idx == k) break;
                res[idx++] = x;
            }
        }
        return res;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] nums, int k, int[] expected) {
        int[] want = expected.clone();
        Arrays.sort(want);                                     // any order is accepted, so compare sorted
        String tag = Arrays.toString(nums) + " k=" + k;
        int[][] results = {bruteForce(nums, k), better(nums, k), optimal(nums, k)};
        String[] names = {"bruteForce", "better", "optimal"};
        for (int r = 0; r < results.length; r++) {
            int[] got = results[r].clone();
            Arrays.sort(got);
            check(Arrays.equals(got, want), names[r] + " " + tag + " got " + Arrays.toString(results[r]));
        }
    }

    public static void main(String[] args) {
        verify(new int[]{1, 1, 1, 2, 2, 3}, 2, new int[]{1, 2});
        verify(new int[]{4, 4, -1, -1, -1, 2}, 1, new int[]{-1});                // negatives
        verify(new int[]{1, 2, 1, 2, 1, 2, 3, 1, 3, 2}, 2, new int[]{1, 2});      // 1 and 2 tie at 4, both needed
        verify(new int[]{5, 3, 1, 1, 1, 3, 73, 1}, 2, new int[]{1, 3});
        verify(new int[]{7, 8, 9}, 3, new int[]{7, 8, 9});                        // k = number of distinct values
        verify(new int[]{2, 2, 2, 2}, 1, new int[]{2});                           // one value fills the top bucket
        verify(new int[]{1}, 1, new int[]{1});                                    // edge: single element
        // deterministic random cross-check, using inputs whose answer is unique
        Random rnd = new Random(1018);
        int tested = 0;
        while (tested < 200) {
            int[] nums = new int[1 + rnd.nextInt(25)];
            for (int i = 0; i < nums.length; i++) nums[i] = rnd.nextInt(8) - 3;
            Map<Integer, Integer> freq = new HashMap<>();
            for (int x : nums) freq.merge(x, 1, Integer::sum);
            List<Integer> counts = new ArrayList<>(freq.values());
            counts.sort(Collections.reverseOrder());
            int k = 1 + rnd.nextInt(counts.size());
            if (k < counts.size() && counts.get(k - 1).equals(counts.get(k))) continue;   // tie at the cut: answer not unique
            verify(nums, k, bruteForce(nums, k));
            tested++;
        }
        System.out.println("OK P1018_TopKFrequentElements");
    }
}

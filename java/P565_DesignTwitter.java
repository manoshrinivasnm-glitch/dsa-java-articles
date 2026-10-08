import java.util.*;

/** TUF 565 - Design Twitter. postTweet, follow, unfollow and getNewsFeed (10 most recent tweets from the user and followees). */
public class P565_DesignTwitter {

    /** Common contract so every implementation can be driven by the same test script. */
    interface Twitter {
        void postTweet(int userId, int tweetId);
        List<Integer> getNewsFeed(int userId);
        void follow(int followerId, int followeeId);
        void unfollow(int followerId, int followeeId);
    }

    static final int FEED_SIZE = 10;

    /** Approach 1: one global log of every tweet; the feed scans it from newest to oldest. Feed O(T) time. */
    static class GlobalLogTwitter implements Twitter {
        private final List<int[]> log = new ArrayList<>();                      // {userId, tweetId}, oldest first
        private final Map<Integer, Set<Integer>> following = new HashMap<>();

        public void postTweet(int userId, int tweetId) {
            log.add(new int[]{userId, tweetId});
        }

        public List<Integer> getNewsFeed(int userId) {
            Set<Integer> followees = following.getOrDefault(userId, Set.of());
            List<Integer> feed = new ArrayList<>();
            for (int i = log.size() - 1; i >= 0 && feed.size() < FEED_SIZE; i--) {
                int[] t = log.get(i);
                if (t[0] == userId || followees.contains(t[0])) feed.add(t[1]);
            }
            return feed;
        }

        public void follow(int followerId, int followeeId) {
            if (followerId != followeeId) following.computeIfAbsent(followerId, k -> new HashSet<>()).add(followeeId);
        }

        public void unfollow(int followerId, int followeeId) {
            Set<Integer> s = following.get(followerId);
            if (s != null) s.remove(followeeId);
        }
    }

    /** Approach 2: per-user tweet lists; gather the last 10 of each source, sort by time, keep 10. Feed O(F log F) time. */
    static class SortFeedTwitter implements Twitter {
        private int clock = 0;
        private final Map<Integer, List<int[]>> tweets = new HashMap<>();      // userId -> {time, tweetId}, oldest first
        private final Map<Integer, Set<Integer>> following = new HashMap<>();

        public void postTweet(int userId, int tweetId) {
            tweets.computeIfAbsent(userId, k -> new ArrayList<>()).add(new int[]{clock++, tweetId});
        }

        public List<Integer> getNewsFeed(int userId) {
            Set<Integer> sources = new HashSet<>(following.getOrDefault(userId, Set.of()));
            sources.add(userId);
            List<int[]> candidates = new ArrayList<>();
            for (int u : sources) {
                List<int[]> list = tweets.getOrDefault(u, List.of());
                for (int i = Math.max(0, list.size() - FEED_SIZE); i < list.size(); i++) candidates.add(list.get(i));
            }
            candidates.sort((a, b) -> Integer.compare(b[0], a[0]));            // newest first
            List<Integer> feed = new ArrayList<>();
            for (int i = 0; i < candidates.size() && i < FEED_SIZE; i++) feed.add(candidates.get(i)[1]);
            return feed;
        }

        public void follow(int followerId, int followeeId) {
            if (followerId != followeeId) following.computeIfAbsent(followerId, k -> new HashSet<>()).add(followeeId);
        }

        public void unfollow(int followerId, int followeeId) {
            Set<Integer> s = following.get(followerId);
            if (s != null) s.remove(followeeId);
        }
    }

    /** Approach 3: per-user tweet lists merged lazily with a max-heap (k-way merge). Feed O(F + 10 log F) time. */
    static class HeapMergeTwitter implements Twitter {
        private int clock = 0;
        private final Map<Integer, List<int[]>> tweets = new HashMap<>();      // userId -> {time, tweetId}, oldest first
        private final Map<Integer, Set<Integer>> following = new HashMap<>();

        public void postTweet(int userId, int tweetId) {
            tweets.computeIfAbsent(userId, k -> new ArrayList<>()).add(new int[]{clock++, tweetId});
        }

        public List<Integer> getNewsFeed(int userId) {
            Set<Integer> sources = new HashSet<>(following.getOrDefault(userId, Set.of()));
            sources.add(userId);
            // heap entry {time, tweetId, userId, indexInThatUsersList}; newest tweet on top
            PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> Integer.compare(b[0], a[0]));
            for (int u : sources) {
                List<int[]> list = tweets.get(u);
                if (list == null || list.isEmpty()) continue;
                int last = list.size() - 1;
                pq.add(new int[]{list.get(last)[0], list.get(last)[1], u, last});
            }
            List<Integer> feed = new ArrayList<>();
            while (!pq.isEmpty() && feed.size() < FEED_SIZE) {
                int[] top = pq.poll();
                feed.add(top[1]);
                int prev = top[3] - 1;                                         // next older tweet of the same user
                if (prev >= 0) {
                    int[] older = tweets.get(top[2]).get(prev);
                    pq.add(new int[]{older[0], older[1], top[2], prev});
                }
            }
            return feed;
        }

        public void follow(int followerId, int followeeId) {
            if (followerId != followeeId) following.computeIfAbsent(followerId, k -> new HashSet<>()).add(followeeId);
        }

        public void unfollow(int followerId, int followeeId) {
            Set<Integer> s = following.get(followerId);
            if (s != null) s.remove(followeeId);
        }
    }

    /** Runs a script: ops[i] with args[i] = {a, b}; only getNewsFeed produces an output. */
    static List<List<Integer>> simulate(Twitter tw, String[] ops, int[][] args) {
        List<List<Integer>> out = new ArrayList<>();
        for (int i = 0; i < ops.length; i++) {
            int a = args[i][0], b = args[i].length > 1 ? args[i][1] : 0;
            switch (ops[i]) {
                case "post" -> tw.postTweet(a, b);
                case "feed" -> out.add(tw.getNewsFeed(a));
                case "follow" -> tw.follow(a, b);
                case "unfollow" -> tw.unfollow(a, b);
                default -> throw new IllegalArgumentException("unknown op " + ops[i]);
            }
        }
        return out;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static List<Twitter> fresh() {
        return List.<Twitter>of(new GlobalLogTwitter(), new SortFeedTwitter(), new HeapMergeTwitter());
    }

    static void verify(String[] ops, int[][] args, List<List<Integer>> expected) {
        for (Twitter tw : fresh()) {
            List<List<Integer>> got = simulate(tw, ops, args);
            check(got.equals(expected), tw.getClass().getSimpleName() + " got " + got + " expected " + expected);
        }
    }

    public static void main(String[] args) {
        // 1. the classic example
        verify(new String[]{"post", "feed", "follow", "post", "feed", "unfollow", "feed"},
               new int[][]{{1, 5}, {1}, {1, 2}, {2, 6}, {1}, {1, 2}, {1}},
               List.of(List.of(5), List.of(6, 5), List.of(5)));
        // 2. more than 10 tweets: only the 10 newest survive, interleaved across two users
        String[] ops2 = new String[14];
        int[][] args2 = new int[14][];
        ops2[0] = "follow";
        args2[0] = new int[]{1, 2};
        for (int t = 1; t <= 12; t++) {
            ops2[t] = "post";
            args2[t] = new int[]{t % 2 == 0 ? 2 : 1, 100 + t};             // odd t by user 1, even t by user 2
        }
        ops2[13] = "feed";
        args2[13] = new int[]{1};
        verify(ops2, args2, List.of(List.of(112, 111, 110, 109, 108, 107, 106, 105, 104, 103)));
        // 3. follow is one-directional, and a user who follows nobody sees only their own tweets
        verify(new String[]{"post", "post", "follow", "feed", "feed", "post", "feed"},
               new int[][]{{1, 10}, {2, 20}, {1, 2}, {1}, {2}, {3, 30}, {3}},
               List.of(List.of(20, 10), List.of(20), List.of(30)));
        // 4. edge: empty feed, self-follow and self-unfollow are ignored, unfollowing a stranger is a no-op
        verify(new String[]{"feed", "follow", "post", "unfollow", "unfollow", "feed"},
               new int[][]{{7}, {7, 7}, {7, 70}, {7, 7}, {7, 9}, {7}},
               List.of(List.<Integer>of(), List.of(70)));
        // 5. re-following brings the old tweets back, in time order with the user's own
        verify(new String[]{"post", "post", "post", "follow", "unfollow", "feed", "follow", "feed"},
               new int[][]{{2, 1}, {1, 2}, {2, 3}, {1, 2}, {1, 2}, {1}, {1, 2}, {1}},
               List.of(List.of(2), List.of(3, 2, 1)));
        // 6. deterministic random workload: every implementation must agree with the global log
        Random rnd = new Random(565);
        List<Twitter> impls = fresh();
        int nextTweet = 1;
        for (int step = 0; step < 3000; step++) {
            int op = rnd.nextInt(10), a = rnd.nextInt(6), b = rnd.nextInt(6);
            List<Integer> first = null;
            for (Twitter tw : impls) {
                if (op < 4) tw.postTweet(a, nextTweet);
                else if (op < 6) tw.follow(a, b);
                else if (op < 7) tw.unfollow(a, b);
                else {
                    List<Integer> feed = tw.getNewsFeed(a);
                    if (first == null) first = feed;
                    else check(feed.equals(first), "random feed mismatch at step " + step);
                }
            }
            if (op < 4) nextTweet++;
        }
        System.out.println("OK P565_DesignTwitter");
    }
}

import java.util.*;

/** TUF 960 - LFU Cache. get and put in O(1); when full, evict the least frequently used key, ties broken by least recent use. */
public class P960_LFUCache {

    /** Common contract so every implementation can be driven by the same test script. */
    interface Cache {
        int get(int key);
        void put(int key, int value);
    }

    /** Approach 1: list of {key, value, freq, lastUsed}; eviction scans for the smallest (freq, lastUsed). O(capacity) per operation. */
    static class BruteLFU implements Cache {
        private final int capacity;
        private final List<int[]> entries = new ArrayList<>();   // {key, value, freq, lastUsed}
        private int tick = 0;

        BruteLFU(int capacity) { this.capacity = capacity; }

        private int find(int key) {
            for (int i = 0; i < entries.size(); i++) if (entries.get(i)[0] == key) return i;
            return -1;
        }

        public int get(int key) {
            int i = find(key);
            if (i == -1) return -1;
            int[] e = entries.get(i);
            e[2]++;
            e[3] = ++tick;
            return e[1];
        }

        public void put(int key, int value) {
            if (capacity == 0) return;
            int i = find(key);
            if (i != -1) {
                int[] e = entries.get(i);
                e[1] = value;
                e[2]++;
                e[3] = ++tick;
                return;
            }
            if (entries.size() == capacity) {
                int victim = 0;
                for (int j = 1; j < entries.size(); j++) {
                    int[] a = entries.get(j), b = entries.get(victim);
                    if (a[2] < b[2] || (a[2] == b[2] && a[3] < b[3])) victim = j;
                }
                entries.remove(victim);
            }
            entries.add(new int[]{key, value, 1, ++tick});
        }
    }

    /** Approach 2: HashMap plus a TreeSet ordered by (freq, lastUsed); the first element is the victim. O(log capacity) per operation. */
    static class TreeSetLFU implements Cache {
        private final int capacity;
        private final Map<Integer, int[]> map = new HashMap<>();       // key -> {key, value, freq, lastUsed}
        private final TreeSet<int[]> order = new TreeSet<>((a, b) ->
                a[2] != b[2] ? Integer.compare(a[2], b[2]) : Integer.compare(a[3], b[3]));
        private int tick = 0;

        TreeSetLFU(int capacity) { this.capacity = capacity; }

        private void touch(int[] e) {
            order.remove(e);                  // remove before changing the fields the comparator reads
            e[2]++;
            e[3] = ++tick;
            order.add(e);
        }

        public int get(int key) {
            int[] e = map.get(key);
            if (e == null) return -1;
            touch(e);
            return e[1];
        }

        public void put(int key, int value) {
            if (capacity == 0) return;
            int[] e = map.get(key);
            if (e != null) {
                e[1] = value;
                touch(e);
                return;
            }
            if (map.size() == capacity) {
                int[] victim = order.pollFirst();
                map.remove(victim[0]);
            }
            e = new int[]{key, value, 1, ++tick};
            map.put(key, e);
            order.add(e);
        }
    }

    /** Approach 3: one doubly linked list per frequency plus minFreq. O(1) per operation. */
    static class LFUCache implements Cache {
        private static class Node {
            int key, value, freq = 1;
            Node prev, next;
            Node(int key, int value) { this.key = key; this.value = value; }
        }

        /** Doubly linked list with sentinels; the front is the most recently used. */
        private static class DList {
            final Node head = new Node(0, 0), tail = new Node(0, 0);
            int size = 0;

            DList() { head.next = tail; tail.prev = head; }

            void addFront(Node node) {
                node.next = head.next;
                node.prev = head;
                head.next.prev = node;
                head.next = node;
                size++;
            }

            void unlink(Node node) {
                node.prev.next = node.next;
                node.next.prev = node.prev;
                size--;
            }

            Node removeLast() {
                Node node = tail.prev;
                unlink(node);
                return node;
            }
        }

        private final int capacity;
        private final Map<Integer, Node> nodes = new HashMap<>();
        private final Map<Integer, DList> buckets = new HashMap<>();   // freq -> nodes used exactly freq times
        private int minFreq = 0;

        LFUCache(int capacity) { this.capacity = capacity; }

        private void touch(Node node) {
            DList list = buckets.get(node.freq);
            list.unlink(node);
            if (list.size == 0 && minFreq == node.freq) minFreq++;
            node.freq++;
            buckets.computeIfAbsent(node.freq, f -> new DList()).addFront(node);
        }

        public int get(int key) {
            Node node = nodes.get(key);
            if (node == null) return -1;
            touch(node);
            return node.value;
        }

        public void put(int key, int value) {
            if (capacity == 0) return;
            Node node = nodes.get(key);
            if (node != null) {
                node.value = value;
                touch(node);
                return;
            }
            if (nodes.size() == capacity) {
                Node victim = buckets.get(minFreq).removeLast();
                nodes.remove(victim.key);
            }
            node = new Node(key, value);
            nodes.put(key, node);
            buckets.computeIfAbsent(1, f -> new DList()).addFront(node);
            minFreq = 1;
        }
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    /** ops: {key} means get(key), {key, value} means put(key, value). Returns the results of the gets in order. */
    static int[] run(Cache cache, int[][] ops) {
        List<Integer> out = new ArrayList<>();
        for (int[] op : ops) {
            if (op.length == 1) out.add(cache.get(op[0]));
            else cache.put(op[0], op[1]);
        }
        int[] res = new int[out.size()];
        for (int i = 0; i < res.length; i++) res[i] = out.get(i);
        return res;
    }

    static void verify(int capacity, int[][] ops, int[] expected) {
        Cache[] caches = {new BruteLFU(capacity), new TreeSetLFU(capacity), new LFUCache(capacity)};
        for (Cache c : caches) {
            int[] got = run(c, ops);
            check(Arrays.equals(got, expected), c.getClass().getSimpleName() + " cap=" + capacity + " -> " + Arrays.toString(got));
        }
    }

    public static void main(String[] args) {
        // LeetCode example: put(1,1) put(2,2) get(1) put(3,3) get(2) get(3) put(4,4) get(1) get(3) get(4)
        verify(2, new int[][]{{1, 1}, {2, 2}, {1}, {3, 3}, {2}, {3}, {4, 4}, {1}, {3}, {4}}, new int[]{1, -1, 3, -1, 3, 4});
        verify(0, new int[][]{{0, 0}, {0}}, new int[]{-1});                                   // capacity 0 stores nothing
        verify(1, new int[][]{{1, 1}, {1}, {2, 2}, {1}, {2}}, new int[]{1, -1, 2});            // capacity 1
        verify(2, new int[][]{{1, 1}, {2, 2}, {1, 10}, {3, 3}, {2}, {1}, {3}}, new int[]{-1, 10, 3}); // put on existing key counts as a use
        verify(2, new int[][]{{1, 1}, {2, 2}, {3, 3}, {1}, {2}, {3}}, new int[]{-1, 2, 3});    // equal freq: evict least recent
        verify(3, new int[][]{{1, 1}, {2, 2}, {3, 3}, {1}, {1}, {2}, {4, 4}, {3}, {4}, {5, 5}, {2}, {1}, {4}, {5}},
                new int[]{1, 1, 2, -1, 4, -1, 1, 4, 5});
        System.out.println("OK P960_LFUCache");
    }
}

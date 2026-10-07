import java.util.*;

/** TUF 611 - Clone a LL with random and next pointer. Build a deep copy in which every next and random pointer refers to copied nodes. */
public class P611_CloneALLWithRandomAndNextPointer {

    static class Node {
        int val;
        Node next, random;
        Node(int val) { this.val = val; }
    }

    /** Approach 1: copy values and next pointers, then resolve each random by position: find the index of the target in the original list and walk to the same index in the copy. O(n^2) time, O(1) extra space beyond the copy. */
    static Node bruteForce(Node head) {
        if (head == null) return null;
        Node copyHead = new Node(head.val), copyTail = copyHead;          // pass 1: values and next pointers
        for (Node t = head.next; t != null; t = t.next) {
            copyTail.next = new Node(t.val);
            copyTail = copyTail.next;
        }
        Node orig = head, copy = copyHead;                                 // pass 2: random pointers by position
        while (orig != null) {
            if (orig.random != null) {
                int pos = 0;
                for (Node t = head; t != orig.random; t = t.next) pos++;   // index of the random target in the original list
                Node target = copyHead;
                for (int i = 0; i < pos; i++) target = target.next;        // the node at the same index in the copy
                copy.random = target;
            }
            orig = orig.next;
            copy = copy.next;
        }
        return copyHead;
    }

    /** Approach 2: a hash map from original node to its copy. Pass 1 creates the copies, pass 2 wires next and random through the map. O(n) time, O(n) space. */
    static Node better(Node head) {
        Map<Node, Node> copyOf = new HashMap<>();
        for (Node t = head; t != null; t = t.next) copyOf.put(t, new Node(t.val));
        for (Node t = head; t != null; t = t.next) {
            Node copy = copyOf.get(t);
            copy.next = copyOf.get(t.next);          // get(null) is null, so the tail needs no special case
            copy.random = copyOf.get(t.random);
        }
        return copyOf.get(head);
    }

    /** Approach 3: interleave each copy right after its original (A, A', B, B', ...), set every copy's random to original.random.next, then unzip the two lists. O(n) time, O(1) extra space. */
    static Node optimal(Node head) {
        if (head == null) return null;
        for (Node t = head; t != null; t = t.next.next) {        // step 1: insert a copy after every original
            Node copy = new Node(t.val);
            copy.next = t.next;
            t.next = copy;
        }
        for (Node t = head; t != null; t = t.next.next) {        // step 2: the copy's random is the copy of the original's random
            if (t.random != null) t.next.random = t.random.next;
        }
        Node copyHead = head.next;
        for (Node t = head; t != null; t = t.next) {             // step 3: separate the two lists, restoring the original
            Node copy = t.next;
            t.next = copy.next;
            copy.next = (copy.next != null) ? copy.next.next : null;
        }
        return copyHead;
    }

    // ---------------------------------------------------------------- helpers
    /** Builds a list from values and, for each node, the index of its random target (-1 for null). */
    static Node build(int[] vals, int[] randomIdx) {
        Node[] nodes = new Node[vals.length];
        for (int i = 0; i < vals.length; i++) nodes[i] = new Node(vals[i]);
        for (int i = 0; i < vals.length; i++) {
            if (i + 1 < vals.length) nodes[i].next = nodes[i + 1];
            if (randomIdx[i] >= 0) nodes[i].random = nodes[randomIdx[i]];
        }
        return vals.length == 0 ? null : nodes[0];
    }

    /** "val:randomIndex" for every node; two lists with the same signature have the same shape. */
    static String signature(Node head) {
        Map<Node, Integer> index = new HashMap<>();
        int i = 0;
        for (Node t = head; t != null; t = t.next) index.put(t, i++);
        StringBuilder sb = new StringBuilder();
        for (Node t = head; t != null; t = t.next) {
            int r = (t.random == null) ? -1 : index.get(t.random);
            sb.append(t.val).append(':').append(r).append(' ');
        }
        return sb.toString();
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] vals, int[] randomIdx) {
        String in = Arrays.toString(vals) + " random " + Arrays.toString(randomIdx);
        Node original = build(vals, randomIdx);
        String want = signature(original);
        Set<Node> originalNodes = new HashSet<>();
        for (Node t = original; t != null; t = t.next) originalNodes.add(t);
        Node[] clones = {bruteForce(original), better(original), optimal(original)};
        String[] names = {"bruteForce", "better", "optimal"};
        for (int a = 0; a < clones.length; a++) {
            check(signature(clones[a]).equals(want), names[a] + " shape differs for " + in);
            for (Node t = clones[a]; t != null; t = t.next) {
                check(!originalNodes.contains(t), names[a] + " shares a node with the original for " + in);
                check(t.random == null || !originalNodes.contains(t.random), names[a] + " random points into the original for " + in);
            }
        }
        check(signature(original).equals(want), "the original list was modified for " + in);
    }

    public static void main(String[] args) {
        verify(new int[]{7, 13, 11, 10, 1}, new int[]{-1, 0, 4, 2, 0});    // the classic example
        verify(new int[]{1, 2}, new int[]{1, 0});                          // randoms cross
        verify(new int[]{3, 3, 3}, new int[]{-1, 0, -1});                  // equal values: identity matters, not value
        verify(new int[]{}, new int[]{});                                  // empty list
        verify(new int[]{5}, new int[]{0});                                // single node pointing at itself
        verify(new int[]{1, 2, 3, 4}, new int[]{3, 3, 3, 3});              // everyone points at the tail
        verify(new int[]{1, 2, 3}, new int[]{2, 1, 0});                    // randoms run backwards
        verify(new int[]{1, 2, 3}, new int[]{-1, -1, -1});                 // no random pointers at all
        System.out.println("OK P611_CloneALLWithRandomAndNextPointer");
    }
}

import java.util.*;

/**
 * Confluent onsite / phone: Search word in documents.
 *
 * Given documents (id, text). Build a structure so that:
 * Part 1: search(word) -> ids of documents containing the word.
 * Follow-up A: searchPhrase("a b c") -> docs containing the words consecutively,
 *              in that order ("词组的位置不变").
 * Follow-up B: boolean queries like  word1 AND word2 OR word3.
 *              Represent as a binary tree: AND/OR internal nodes, words as leaves.
 *              No need to parse the query into a tree; define the node and evaluate it.
 *
 * Design: positional inverted index  word -> (docId -> sorted positions).
 * - search: key lookup, O(1) + output.
 * - phrase: start with docs of the first word, keep positions p where word_k is at p+k.
 *   Iterate the rarest-word-first optimization if asked.
 * - boolean: post-order evaluation; AND = intersection, OR = union (sorted sets).
 */
public class DocumentSearch {

    private final Map<String, Map<Integer, List<Integer>>> index = new HashMap<>();

    public DocumentSearch(Map<Integer, String> docs) {
        for (Map.Entry<Integer, String> e : docs.entrySet()) addDocument(e.getKey(), e.getValue());
    }

    public void addDocument(int id, String text) {
        List<String> words = tokenize(text);
        for (int pos = 0; pos < words.size(); pos++) {
            index.computeIfAbsent(words.get(pos), k -> new HashMap<>())
                 .computeIfAbsent(id, k -> new ArrayList<>())
                 .add(pos);
        }
    }

    static List<String> tokenize(String text) {
        List<String> res = new ArrayList<>();
        for (String w : text.toLowerCase().split("[^a-z0-9']+")) if (!w.isEmpty()) res.add(w);
        return res;
    }

    // ---------------- Part 1 ----------------
    public Set<Integer> search(String word) {
        Map<Integer, List<Integer>> postings = index.get(word.toLowerCase());
        return postings == null ? new TreeSet<>() : new TreeSet<>(postings.keySet());
    }

    // ---------------- Follow-up A: phrase ----------------
    public Set<Integer> searchPhrase(String phrase) {
        List<String> words = tokenize(phrase);
        Set<Integer> res = new TreeSet<>();
        if (words.isEmpty()) return res;
        Map<Integer, List<Integer>> first = index.get(words.get(0));
        if (first == null) return res;

        for (Map.Entry<Integer, List<Integer>> e : first.entrySet()) {
            int doc = e.getKey();
            Set<Integer> candidates = new HashSet<>(e.getValue()); // start positions
            for (int k = 1; k < words.size() && !candidates.isEmpty(); k++) {
                Map<Integer, List<Integer>> p = index.get(words.get(k));
                List<Integer> positions = p == null ? null : p.get(doc);
                if (positions == null) { candidates.clear(); break; }
                Set<Integer> posSet = new HashSet<>(positions);
                final int offset = k;
                candidates.removeIf(start -> !posSet.contains(start + offset));
            }
            if (!candidates.isEmpty()) res.add(doc);
        }
        return res;
    }

    // ---------------- Follow-up B: boolean expression tree ----------------
    enum Op { AND, OR, WORD }

    static class Node {
        final Op op;
        final String word;
        final Node left, right;

        private Node(Op op, String word, Node left, Node right) {
            this.op = op; this.word = word; this.left = left; this.right = right;
        }
        static Node word(String w) { return new Node(Op.WORD, w, null, null); }
        static Node and(Node l, Node r) { return new Node(Op.AND, null, l, r); }
        static Node or(Node l, Node r) { return new Node(Op.OR, null, l, r); }
    }

    public Set<Integer> evaluate(Node node) {
        if (node == null) return new TreeSet<>();
        if (node.op == Op.WORD) {
            // a leaf with spaces is treated as a phrase
            return node.word.trim().contains(" ") ? searchPhrase(node.word) : search(node.word);
        }
        Set<Integer> l = evaluate(node.left);
        if (node.op == Op.AND && l.isEmpty()) return l; // short-circuit
        Set<Integer> r = evaluate(node.right);
        if (node.op == Op.AND) l.retainAll(r);
        else l.addAll(r);
        return l;
    }

    public static void main(String[] args) {
        Map<Integer, String> docs = new LinkedHashMap<>();
        docs.put(1, "Kafka is a distributed event streaming platform");
        docs.put(2, "Confluent builds a streaming platform on Kafka");
        docs.put(3, "The platform is streaming events");
        docs.put(4, "Cloud native data streaming");
        DocumentSearch ds = new DocumentSearch(docs);

        System.out.println(ds.search("kafka"));                       // [1, 2]
        System.out.println(ds.search("streaming"));                   // [1, 2, 3, 4]
        System.out.println(ds.search("missing"));                     // []
        System.out.println(ds.searchPhrase("streaming platform"));    // [1, 2]
        System.out.println(ds.searchPhrase("platform streaming"));    // []
        System.out.println(ds.searchPhrase("platform is streaming")); // [3]

        // kafka AND platform OR cloud  ==  (kafka AND platform) OR cloud
        Node q = Node.or(Node.and(Node.word("kafka"), Node.word("platform")), Node.word("cloud"));
        System.out.println(ds.evaluate(q));                           // [1, 2, 4]
        Node q2 = Node.and(Node.word("streaming platform"), Node.word("confluent"));
        System.out.println(ds.evaluate(q2));                          // [2]
    }
}

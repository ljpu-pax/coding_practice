import java.util.*;

/**
 * Confluent onsite / phone: Search word in documents (1M+ docs).
 *
 * Given documents (id, text). Build a structure so that:
 * Part 1: search(word) -> ids of documents containing the word.
 * Follow-up A: searchPhrase("a b c") -> docs containing the words consecutively,
 *              in that order ("词组的位置不变").
 * Follow-up B: boolean queries like  word1 AND word2 OR word3.
 *              Represent as a binary tree: AND/OR internal nodes, words as leaves.
 *              No need to parse the query into a tree; define the node and evaluate it.
 *
 * Main design (simple):
 * - index:    word -> set of doc ids   (inverted index; search is one lookup)
 * - docWords: doc id -> its tokenized words
 * - phrase: intersect the doc sets of all phrase words to get candidates, then look for
 *   the phrase as a consecutive sublist in each candidate (Collections.indexOfSubList).
 *   Cost per candidate: O(doc length * phrase length).
 * - boolean: post-order evaluation; AND = intersection, OR = union.
 *
 * Optimization if docs are long ("don't rescan the text"): positional index
 * word -> (doc id -> positions). A phrase matches at start p if word k is at p + k.
 * Only positions are compared, never the text. See searchPhraseByPosition.
 */
public class DocumentSearch {

    private final Map<String, Set<Integer>> index = new HashMap<>();
    private final Map<Integer, List<String>> docWords = new HashMap<>();

    public DocumentSearch(Map<Integer, String> docs) {
        for (Map.Entry<Integer, String> e : docs.entrySet()) addDocument(e.getKey(), e.getValue());
    }

    public void addDocument(int id, String text) {
        List<String> words = tokenize(text);
        docWords.put(id, words);
        for (int pos = 0; pos < words.size(); pos++) {
            index.computeIfAbsent(words.get(pos), k -> new HashSet<>()).add(id);
            posIndex.computeIfAbsent(words.get(pos), k -> new HashMap<>())
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
        return new TreeSet<>(index.getOrDefault(word.toLowerCase(), Set.of()));
    }

    // ---------------- Follow-up A: phrase ----------------
    public Set<Integer> searchPhrase(String phrase) {
        List<String> words = tokenize(phrase);
        Set<Integer> res = new TreeSet<>();
        if (words.isEmpty()) return res;
        // 1. Candidates: docs that contain every word of the phrase
        Set<Integer> cand = new TreeSet<>(index.getOrDefault(words.get(0), Set.of()));
        for (String w : words) cand.retainAll(index.getOrDefault(w, Set.of()));
        // 2. Keep the ones where the words appear next to each other, in order
        for (int id : cand) {
            if (Collections.indexOfSubList(docWords.get(id), words) != -1) res.add(id);
        }
        return res;
    }

    // ---------------- Optimization: positional index ----------------
    // word -> (doc id -> positions of the word in that doc)
    private final Map<String, Map<Integer, List<Integer>>> posIndex = new HashMap<>();

    public Set<Integer> searchPhraseByPosition(String phrase) {
        List<String> words = tokenize(phrase);
        Set<Integer> res = new TreeSet<>();
        if (words.isEmpty()) return res;
        Map<Integer, List<Integer>> first = posIndex.get(words.get(0));
        if (first == null) return res;

        for (Map.Entry<Integer, List<Integer>> e : first.entrySet()) {
            int doc = e.getKey();
            Set<Integer> starts = new HashSet<>(e.getValue()); // possible start positions
            for (int k = 1; k < words.size() && !starts.isEmpty(); k++) {
                Map<Integer, List<Integer>> p = posIndex.get(words.get(k));
                List<Integer> positions = p == null ? null : p.get(doc);
                if (positions == null) { starts.clear(); break; }
                Set<Integer> posSet = new HashSet<>(positions);
                final int offset = k;
                starts.removeIf(start -> !posSet.contains(start + offset)); // word k must be at start + k
            }
            if (!starts.isEmpty()) res.add(doc);
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

        @Override public String toString() {
            return op == Op.WORD ? word : "(" + left + " " + op + " " + right + ")";
        }
    }

    // ---------------- Follow-up B': build the tree from a string ----------------
    // "Hello AND World OR is": words and operators alternate. Ask the interviewer
    // which rule they want; the example gives the same answer under both.

    /** Left to right, no precedence: a OR b AND c == (a OR b) AND c. */
    static Node parse(String query) {
        String[] t = query.trim().split("\\s+");
        Node root = Node.word(t[0]);
        for (int i = 1; i + 1 < t.length; i += 2) {
            Node right = Node.word(t[i + 1]);
            root = t[i].equals("AND") ? Node.and(root, right) : Node.or(root, right);
        }
        return root;
    }

    /** AND before OR, like * before +: a OR b AND c == a OR (b AND c). */
    static Node parseAndFirst(String query) {
        String[] t = query.trim().split("\\s+");
        Node orRoot = null;               // OR of the finished AND-groups
        Node group = Node.word(t[0]);     // current AND-group
        for (int i = 1; i + 1 < t.length; i += 2) {
            Node w = Node.word(t[i + 1]);
            if (t[i].equals("AND")) {
                group = Node.and(group, w);
            } else {                      // OR closes the current group
                orRoot = orRoot == null ? group : Node.or(orRoot, group);
                group = w;
            }
        }
        return orRoot == null ? group : Node.or(orRoot, group);
    }

    /** The interview API: parse the string, then evaluate the tree. */
    public Set<Integer> searchQuery(String query) {
        return evaluate(parse(query));
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

        // The interview example from the post
        Map<Integer, String> cloud = new LinkedHashMap<>();
        cloud.put(1, "Cloud computing is the on-demand availability of computer system resources.");
        cloud.put(2, "One integrated service for metrics uptime cloud monitoring dashboards and alerts reduces time spent navigating between systems.");
        cloud.put(3, "Monitor entire cloud infrastructure, whether in the cloud computing is or in virtualized data centers.");
        DocumentSearch cs = new DocumentSearch(cloud);
        System.out.println(cs.search("cloud"));                       // [1, 2, 3]
        System.out.println(cs.searchPhrase("cloud monitoring"));      // [2]
        System.out.println(cs.searchPhrase("Cloud computing is"));    // [1, 3]

        // Follow-up B' from the post: build the tree from "Hello AND World OR is"
        Map<Integer, String> hw = new LinkedHashMap<>();
        hw.put(1, "hello world");
        hw.put(2, "hello there");
        hw.put(3, "this is it");
        hw.put(4, "world peace");
        DocumentSearch hs = new DocumentSearch(hw);
        System.out.println(parse("Hello AND World OR is"));                    // ((Hello AND World) OR is)
        System.out.println(hs.searchQuery("Hello AND World OR is"));           // [1, 3]
        System.out.println(hs.searchQuery("hello"));                           // [1, 2]
        // Precedence matters here: hello AND world = {1}, is = {3}, is OR hello = {1, 2, 3}
        System.out.println(parse("is OR hello AND world") + " -> "
            + hs.evaluate(parse("is OR hello AND world")));                    // ((is OR hello) AND world) -> [1]
        System.out.println(parseAndFirst("is OR hello AND world") + " -> "
            + hs.evaluate(parseAndFirst("is OR hello AND world")));            // (is OR (hello AND world)) -> [1, 3]
        System.out.println(parseAndFirst("a AND b OR c AND d OR e"));         // (((a AND b) OR (c AND d)) OR e)

        // Both phrase versions must agree
        boolean same = true;
        for (DocumentSearch d : List.of(ds, cs))
            for (String p : List.of("streaming platform", "platform streaming", "platform is streaming",
                    "kafka", "cloud monitoring", "cloud computing is", "computing is the", "missing word", ""))
                same &= d.searchPhrase(p).equals(d.searchPhraseByPosition(p));
        System.out.println("simple == positional: " + same);         // true
    }
}

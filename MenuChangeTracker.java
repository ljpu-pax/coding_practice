import java.util.*;

class Node {
    String key;
    int value;
    List<Node> children;

    public Node(String key, int value) {
        this.key = key;
        this.value = value;
        this.children = new ArrayList<>();
    }
}

public class MenuChangeTracker {

    static class Changes {
        List<String> added = new ArrayList<>();
        List<String> deleted = new ArrayList<>();
        List<String> updated = new ArrayList<>();

        void printChanges() {
            System.out.println("Added Nodes: " + added);
            System.out.println("Deleted Nodes: " + deleted);
            System.out.println("Updated Nodes: " + updated);
        }
    }

    public static Changes compareTrees(Node oldTree, Node newTree) {
        Changes changes = new Changes();
        dfs(oldTree, newTree, changes);
        return changes;
    }

    private static void dfs(Node oldNode, Node newNode, Changes changes) {
        if (oldNode == null && newNode == null) {
            return;
        } else if (oldNode == null) {
            changes.added.add(newNode.key);
            for (Node child : newNode.children) {
                dfs(null, child, changes);
            }
        } else if (newNode == null) {
            changes.deleted.add(oldNode.key);
            for (Node child : oldNode.children) {
                dfs(child, null, changes);
            }
        } else {
            if (!oldNode.key.equals(newNode.key)) {
                changes.deleted.add(oldNode.key);
                changes.added.add(newNode.key);
            } else if (oldNode.value != newNode.value) {
                changes.updated.add(oldNode.key);
            }

            Map<String, Node> oldChildrenMap = new HashMap<>();
            for (Node child : oldNode.children) {
                oldChildrenMap.put(child.key, child);
            }

            Map<String, Node> newChildrenMap = new HashMap<>();
            for (Node child : newNode.children) {
                newChildrenMap.put(child.key, child);
            }

            Set<String> allKeys = new HashSet<>();
            allKeys.addAll(oldChildrenMap.keySet());
            allKeys.addAll(newChildrenMap.keySet());

            for (String key : allKeys) {
                dfs(oldChildrenMap.get(key), newChildrenMap.get(key), changes);
            }
        }
    }

    public static void main(String[] args) {
        // Example 1
        Node oldTree1 = new Node("a", 1);
        Node b1 = new Node("b", 2);
        Node c1 = new Node("c", 3);
        Node d1 = new Node("d", 4);
        Node e1 = new Node("e", 5);
        Node f1 = new Node("f", 6);

        oldTree1.children.add(b1);
        oldTree1.children.add(c1);
        b1.children.add(d1);
        b1.children.add(e1);
        c1.children.add(f1);

        Node newTree1 = new Node("a", 1);
        Node c1New = new Node("c", 3);
        Node f1New = new Node("f", 66);

        newTree1.children.add(c1New);
        c1New.children.add(f1New);

        Changes changes1 = compareTrees(oldTree1, newTree1);
        System.out.println("Example 1:");
        changes1.printChanges();

        // Example 2
        Node oldTree2 = new Node("a", 1);
        Node b2 = new Node("b", 2);
        Node c2 = new Node("c", 3);
        Node d2 = new Node("d", 4);
        Node e2 = new Node("e", 5);
        Node g2 = new Node("g", 7);

        oldTree2.children.add(b2);
        oldTree2.children.add(c2);
        b2.children.add(d2);
        b2.children.add(e2);
        c2.children.add(g2);

        Node newTree2 = new Node("a", 1);
        Node b2New = new Node("b", 2);
        Node h2New = new Node("h", 8);
        Node e2New = new Node("e", 5);
        Node d2New = new Node("d", 4);
        Node f2New = new Node("f", 6);
        Node g2New = new Node("g", 7);

        newTree2.children.add(b2New);
        newTree2.children.add(h2New);
        b2New.children.add(e2New);
        b2New.children.add(d2New);
        b2New.children.add(f2New);
        h2New.children.add(g2New);

        Changes changes2 = compareTrees(oldTree2, newTree2);
        System.out.println("Example 2:");
        changes2.printChanges();
    }
}

import java.util.*;

public class HierarchicalKVStore {
    private static class Node {
        String value;
        Map<String, Node> children = new HashMap<>();

        Node(String value) {
            this.value = value;
        }
    }

    private final Node root;

    public HierarchicalKVStore() {
        root = new Node("root");
    }

    // Helper method to traverse the path
    private Node traverse(String path) {
        if (path.equals("/")) return root;
        String[] parts = path.split("/");
        Node current = root;
        for (int i = 1; i < parts.length; i++) {
            current = current.children.get(parts[i]);
            if (current == null) return null;
        }
        return current;
    }

    // Create a new path under an existing parent
    public boolean createPath(String path, String value) {
        if (path == null || path.isEmpty() || path.equals("/")) return false;
        String[] parts = path.split("/");
        Node current = root;
        for (int i = 1; i < parts.length - 1; i++) {
            current = current.children.get(parts[i]);
            if (current == null) return false; // Parent path doesn't exist
        }
        String lastPart = parts[parts.length - 1];
        if (current.children.containsKey(lastPart)) return false; // Path already exists
        current.children.put(lastPart, new Node(value));
        return true;
    }

    // Set the value of an existing path
    public boolean setValue(String path, String value) {
        Node node = traverse(path);
        if (node == null) return false;
        node.value = value;
        return true;
    }

    // Get the value of an existing path
    public String getValue(String path) {
        Node node = traverse(path);
        return node != null ? node.value : null;
    }

    // Delete a leaf path (excluding the root)
    public boolean deletePath(String path) {
        if (path == null || path.isEmpty() || path.equals("/")) return false;
        String[] parts = path.split("/");
        Node current = root;
        for (int i = 1; i < parts.length - 1; i++) {
            current = current.children.get(parts[i]);
            if (current == null) return false; // Path doesn't exist
        }
        String lastPart = parts[parts.length - 1];
        Node target = current.children.get(lastPart);
        if (target == null || !target.children.isEmpty()) return false; // Not a leaf
        current.children.remove(lastPart);
        return true;
    }

    // For demonstration purposes
    public static void main(String[] args) {
        HierarchicalKVStore store = new HierarchicalKVStore();
        System.out.println(store.createPath("/a", "value_a"));      // true
        System.out.println(store.createPath("/a/b", "value_b"));    // true
        System.out.println(store.getValue("/a"));                   // value_a
        System.out.println(store.getValue("/a/b"));                 // value_b
        System.out.println(store.setValue("/a/b", "new_value_b"));  // true
        System.out.println(store.getValue("/a/b"));                 // new_value_b
        System.out.println(store.deletePath("/a/b"));               // true
        System.out.println(store.getValue("/a/b"));                 // null
        System.out.println(store.deletePath("/a"));                 // true
        System.out.println(store.getValue("/a"));                   // null
    }
}
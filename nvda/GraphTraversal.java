package nvda;

import java.util.*;

public class GraphTraversal {
    
    // LeetCode 207: Course Schedule (already exists, but adding for completeness)
    // DFS cycle detection - O(V + E) time, O(V + E) space
    public boolean canFinish(int numCourses, int[][] prerequisites) {
        List<List<Integer>> graph = new ArrayList<>();
        for (int i = 0; i < numCourses; i++) {
            graph.add(new ArrayList<>());
        }
        
        for (int[] edge : prerequisites) {
            graph.get(edge[1]).add(edge[0]);
        }
        
        boolean[] visited = new boolean[numCourses];
        boolean[] onPath = new boolean[numCourses];
        
        for (int i = 0; i < numCourses; i++) {
            if (!visited[i] && hasCycle(graph, i, visited, onPath)) {
                return false;
            }
        }
        
        return true;
    }
    
    private boolean hasCycle(List<List<Integer>> graph, int node, boolean[] visited, boolean[] onPath) {
        visited[node] = true;
        onPath[node] = true;
        
        for (int neighbor : graph.get(node)) {
            if (!visited[neighbor]) {
                if (hasCycle(graph, neighbor, visited, onPath)) return true;
            } else if (onPath[neighbor]) {
                return true;
            }
        }
        
        onPath[node] = false;
        return false;
    }

    // LeetCode 210: Course Schedule II
    // Topological sort - O(V + E) time, O(V + E) space
    public int[] findOrder(int numCourses, int[][] prerequisites) {
        List<List<Integer>> graph = new ArrayList<>();
        int[] indegree = new int[numCourses];
        
        for (int i = 0; i < numCourses; i++) {
            graph.add(new ArrayList<>());
        }
        
        for (int[] edge : prerequisites) {
            graph.get(edge[1]).add(edge[0]);
            indegree[edge[0]]++;
        }
        
        Queue<Integer> queue = new LinkedList<>();
        for (int i = 0; i < numCourses; i++) {
            if (indegree[i] == 0) {
                queue.offer(i);
            }
        }
        
        int[] result = new int[numCourses];
        int index = 0;
        
        while (!queue.isEmpty()) {
            int course = queue.poll();
            result[index++] = course;
            
            for (int neighbor : graph.get(course)) {
                indegree[neighbor]--;
                if (indegree[neighbor] == 0) {
                    queue.offer(neighbor);
                }
            }
        }
        
        return index == numCourses ? result : new int[0];
    }

    // LeetCode 133: Clone Graph
    // DFS with HashMap - O(V + E) time, O(V) space
    static class Node {
        public int val;
        public List<Node> neighbors;
        
        public Node() {
            val = 0;
            neighbors = new ArrayList<>();
        }
        
        public Node(int _val) {
            val = _val;
            neighbors = new ArrayList<>();
        }
        
        public Node(int _val, ArrayList<Node> _neighbors) {
            val = _val;
            neighbors = _neighbors;
        }
    }
    
    public Node cloneGraph(Node node) {
        if (node == null) return null;
        
        Map<Node, Node> visited = new HashMap<>();
        return dfsClone(node, visited);
    }
    
    private Node dfsClone(Node node, Map<Node, Node> visited) {
        if (visited.containsKey(node)) {
            return visited.get(node);
        }
        
        Node clone = new Node(node.val);
        visited.put(node, clone);
        
        for (Node neighbor : node.neighbors) {
            clone.neighbors.add(dfsClone(neighbor, visited));
        }
        
        return clone;
    }

    // LeetCode 261: Graph Valid Tree
    // Union-Find approach - O(V + E) time, O(V) space
    public boolean validTree(int n, int[][] edges) {
        if (edges.length != n - 1) return false; // Tree must have exactly n-1 edges
        
        UnionFind uf = new UnionFind(n);
        
        for (int[] edge : edges) {
            if (!uf.union(edge[0], edge[1])) {
                return false; // Cycle detected
            }
        }
        
        return uf.getComponents() == 1;
    }
    
    static class UnionFind {
        private int[] parent;
        private int[] rank;
        private int components;
        
        public UnionFind(int n) {
            parent = new int[n];
            rank = new int[n];
            components = n;
            
            for (int i = 0; i < n; i++) {
                parent[i] = i;
            }
        }
        
        public int find(int x) {
            if (parent[x] != x) {
                parent[x] = find(parent[x]);
            }
            return parent[x];
        }
        
        public boolean union(int x, int y) {
            int rootX = find(x);
            int rootY = find(y);
            
            if (rootX == rootY) return false; // Already connected
            
            if (rank[rootX] < rank[rootY]) {
                parent[rootX] = rootY;
            } else if (rank[rootX] > rank[rootY]) {
                parent[rootY] = rootX;
            } else {
                parent[rootY] = rootX;
                rank[rootX]++;
            }
            
            components--;
            return true;
        }
        
        public int getComponents() {
            return components;
        }
    }

    // LeetCode 323: Number of Connected Components in an Undirected Graph
    // Union-Find approach - O(V + E) time, O(V) space
    public int countComponents(int n, int[][] edges) {
        UnionFind uf = new UnionFind(n);
        
        for (int[] edge : edges) {
            uf.union(edge[0], edge[1]);
        }
        
        return uf.getComponents();
    }

    // LeetCode 547: Number of Provinces
    // DFS approach - O(n²) time, O(n) space
    public int findCircleNum(int[][] isConnected) {
        int n = isConnected.length;
        boolean[] visited = new boolean[n];
        int provinces = 0;
        
        for (int i = 0; i < n; i++) {
            if (!visited[i]) {
                dfs(isConnected, i, visited);
                provinces++;
            }
        }
        
        return provinces;
    }
    
    private void dfs(int[][] isConnected, int city, boolean[] visited) {
        visited[city] = true;
        
        for (int neighbor = 0; neighbor < isConnected.length; neighbor++) {
            if (isConnected[city][neighbor] == 1 && !visited[neighbor]) {
                dfs(isConnected, neighbor, visited);
            }
        }
    }

    // LeetCode 684: Redundant Connection
    // Union-Find approach - O(n) time, O(n) space
    public int[] findRedundantConnection(int[][] edges) {
        UnionFind uf = new UnionFind(edges.length + 1);
        
        for (int[] edge : edges) {
            if (!uf.union(edge[0], edge[1])) {
                return edge; // This edge creates a cycle
            }
        }
        
        return new int[0];
    }

    public static void main(String[] args) {
        GraphTraversal solver = new GraphTraversal();
        
        // Test Course Schedule
        System.out.println("=== Course Schedule (LeetCode 207) ===");
        int numCourses1 = 2;
        int[][] prerequisites1 = {{1, 0}};
        System.out.println("Can finish courses: " + solver.canFinish(numCourses1, prerequisites1));
        
        int numCourses2 = 2;
        int[][] prerequisites2 = {{1, 0}, {0, 1}};
        System.out.println("Can finish courses (with cycle): " + solver.canFinish(numCourses2, prerequisites2));
        System.out.println();
        
        // Test Course Schedule II
        System.out.println("=== Course Schedule II (LeetCode 210) ===");
        int numCourses3 = 4;
        int[][] prerequisites3 = {{1, 0}, {2, 0}, {3, 1}, {3, 2}};
        int[] order = solver.findOrder(numCourses3, prerequisites3);
        System.out.println("Course order: " + Arrays.toString(order));
        System.out.println();
        
        // Test Graph Valid Tree
        System.out.println("=== Graph Valid Tree (LeetCode 261) ===");
        int n1 = 5;
        int[][] edges1 = {{0, 1}, {0, 2}, {0, 3}, {1, 4}};
        System.out.println("Valid tree: " + solver.validTree(n1, edges1));
        
        int n2 = 5;
        int[][] edges2 = {{0, 1}, {1, 2}, {2, 3}, {1, 3}, {1, 4}};
        System.out.println("Valid tree (with cycle): " + solver.validTree(n2, edges2));
        System.out.println();
        
        // Test Number of Connected Components
        System.out.println("=== Number of Connected Components (LeetCode 323) ===");
        int n3 = 5;
        int[][] edges3 = {{0, 1}, {1, 2}, {3, 4}};
        System.out.println("Number of components: " + solver.countComponents(n3, edges3));
        System.out.println();
        
        // Test Number of Provinces
        System.out.println("=== Number of Provinces (LeetCode 547) ===");
        int[][] isConnected = {{1, 1, 0}, {1, 1, 0}, {0, 0, 1}};
        System.out.println("Number of provinces: " + solver.findCircleNum(isConnected));
        System.out.println();
        
        // Test Redundant Connection
        System.out.println("=== Redundant Connection (LeetCode 684) ===");
        int[][] edges4 = {{1, 2}, {1, 3}, {2, 3}};
        int[] redundant = solver.findRedundantConnection(edges4);
        System.out.println("Redundant edge: " + Arrays.toString(redundant));
    }
}

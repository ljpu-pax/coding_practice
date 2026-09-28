package openai;

import java.util.*;

/**
 * Distributed Tree Node Counting - Simplified Version
 *
 * Problem:
 * You are given a tree where each node represents a machine in a cluster. Only parent and child nodes
 * can communicate with each other. Communication happens via sendAsyncMessage() and receiveMessage().
 *
 * Design a method to count the total number of machines in the tree.
 * - Root receives initial message: receiveMessage(null, "0")
 * - Each node can only communicate with direct parent/child
 * - Use message passing to aggregate counts from children
 *
 * Part 1 - Count Nodes:
 * Message Protocol:
 * - "COUNT" means: please count your subtree
 * - "COUNT:<number>" means: response with count of subtree
 *
 * Part 2 - Get Topology:
 * Message Protocol:
 * - "TOPOLOGY" means: please return your subtree topology
 * - "TOPOLOGY:<json>" means: response with subtree structure
 *
 * Algorithm:
 * 1. Root receives message from external (fromNodeId = null)
 * 2. If leaf, print "1" or send to parent
 * 3. If internal, forward "0" to all children
 * 4. Collect responses from all children
 * 5. Sum children counts + 1 (self), send to parent
 * 6. Root prints final count
 *
 * Follow-up: Handle network failures and retries
 * - Use records[] to track which children have responded
 * - Check if child already in records to avoid duplicate counting
 *
 * Time Complexity: O(n) where n is number of nodes
 * Space Complexity: O(h) where h is height (for recursion stack)
 */
public class ClusterNodeCountSimple {

    /**
     * Simple tree structure for topology
     */
    static class TreeNode {
        String nodeId;
        List<TreeNode> children;

        TreeNode(String nodeId) {
            this.nodeId = nodeId;
            this.children = new ArrayList<>();
        }

        String toJson() {
            if (children.isEmpty()) {
                return "{\"id\":\"" + nodeId + "\"}";
            }
            StringBuilder sb = new StringBuilder();
            sb.append("{\"id\":\"").append(nodeId).append("\",\"children\":[");
            for (int i = 0; i < children.size(); i++) {
                sb.append(children.get(i).toJson());
                if (i < children.size() - 1) sb.append(",");
            }
            sb.append("]}");
            return sb.toString();
        }

        @Override
        public String toString() {
            return toString(0);
        }

        private String toString(int level) {
            StringBuilder sb = new StringBuilder();
            sb.append("  ".repeat(level)).append(nodeId).append("\n");
            for (TreeNode child : children) {
                sb.append(child.toString(level + 1));
            }
            return sb.toString();
        }
    }

    /**
     * Node class representing a machine in the cluster
     */
    static class Node {
        String nodeId;
        List<String> children;
        String parent;

        // For counting
        int totalCount;
        List<String> countRecords;  // Track which children have responded for count

        // For topology
        Map<String, TreeNode> topologyResponses;  // Track topology responses from children

        // Reference to simulator for testing
        ClusterSimulator simulator;

        Node(String nodeId, List<String> children, String parent) {
            this.nodeId = nodeId;
            this.children = children != null ? children : new ArrayList<>();
            this.parent = parent;
            this.totalCount = 0;
            this.countRecords = new ArrayList<>();
            this.topologyResponses = new HashMap<>();
        }

        /**
         * Handle incoming messages
         * @param fromNodeId The node that sent the message (null for external messages to root)
         * @param message The message content
         */
        public void receiveMessage(String fromNodeId, String message) {
            // Determine message type
            if (message.startsWith("COUNT")) {
                handleCountMessage(fromNodeId, message);
            } else if (message.startsWith("TOPOLOGY")) {
                handleTopologyMessage(fromNodeId, message);
            }
        }

        /**
         * Handle COUNT messages
         */
        private void handleCountMessage(String fromNodeId, String message) {
            // Case 1: Root node receiving initial COUNT request
            if (fromNodeId == null) {
                if (children.isEmpty()) {
                    // Leaf node that is also root (single node tree)
                    System.out.println("Total node count: 1");
                } else {
                    // Root with children: forward count request
                    for (String child : children) {
                        sendAsyncMessage(child, "COUNT");
                    }
                }
                return;
            }

            // Case 2: COUNT request from parent
            if (fromNodeId.equals(parent)) {
                if (children.isEmpty()) {
                    // Leaf node: respond with count = 1
                    sendAsyncMessage(parent, "COUNT:1");
                } else {
                    // Internal node: forward to all children
                    for (String child : children) {
                        sendAsyncMessage(child, "COUNT");
                    }
                }
                return;
            }

            // Case 3: COUNT response from child (format: "COUNT:<number>")
            if (children.contains(fromNodeId)) {
                // Idempotency check: avoid duplicate counting
                if (countRecords.contains(fromNodeId)) {
                    System.out.println(nodeId + ": Ignoring duplicate COUNT response from " + fromNodeId);
                    return;
                }

                // Parse count from message
                int subTreeCount = Integer.parseInt(message.substring(6)); // Skip "COUNT:"
                totalCount += subTreeCount;
                countRecords.add(fromNodeId);

                // Check if received responses from all children
                if (countRecords.size() == children.size()) {
                    // Add self to count
                    totalCount += 1;

                    if (parent == null) {
                        // Root node: print result
                        System.out.println("Total node count: " + totalCount);
                    } else {
                        // Send to parent
                        sendAsyncMessage(parent, "COUNT:" + totalCount);
                    }

                    // Reset for potential future counts
                    totalCount = 0;
                    countRecords.clear();
                }
            }
        }

        /**
         * Handle TOPOLOGY messages
         */
        private void handleTopologyMessage(String fromNodeId, String message) {
            // Case 1: Root node receiving initial TOPOLOGY request
            if (fromNodeId == null) {
                if (children.isEmpty()) {
                    // Leaf node that is also root
                    TreeNode topology = new TreeNode(nodeId);
                    System.out.println("Cluster Topology:");
                    System.out.println(topology);
                } else {
                    // Root with children: forward topology request
                    topologyResponses.clear();
                    for (String child : children) {
                        sendAsyncMessage(child, "TOPOLOGY");
                    }
                }
                return;
            }

            // Case 2: TOPOLOGY request from parent
            if (fromNodeId.equals(parent)) {
                if (children.isEmpty()) {
                    // Leaf node: respond with own topology
                    TreeNode topology = new TreeNode(nodeId);
                    sendAsyncMessage(parent, "TOPOLOGY:" + topology.toJson());
                } else {
                    // Internal node: forward to all children
                    topologyResponses.clear();
                    for (String child : children) {
                        sendAsyncMessage(child, "TOPOLOGY");
                    }
                }
                return;
            }

            // Case 3: TOPOLOGY response from child (format: "TOPOLOGY:<json>")
            if (children.contains(fromNodeId)) {
                // Idempotency check
                if (topologyResponses.containsKey(fromNodeId)) {
                    System.out.println(nodeId + ": Ignoring duplicate TOPOLOGY response from " + fromNodeId);
                    return;
                }

                // Parse topology from message (simplified - in real system would parse JSON)
                String jsonData = message.substring(9); // Skip "TOPOLOGY:"
                TreeNode childTopology = parseTopology(jsonData);
                topologyResponses.put(fromNodeId, childTopology);

                // Check if received responses from all children
                if (topologyResponses.size() == children.size()) {
                    // Build own topology with children
                    TreeNode topology = new TreeNode(nodeId);
                    for (TreeNode childTopo : topologyResponses.values()) {
                        topology.children.add(childTopo);
                    }

                    if (parent == null) {
                        // Root node: print topology
                        System.out.println("Cluster Topology:");
                        System.out.println(topology);
                    } else {
                        // Send to parent
                        sendAsyncMessage(parent, "TOPOLOGY:" + topology.toJson());
                    }

                    // Reset for potential future requests
                    topologyResponses.clear();
                }
            }
        }

        /**
         * Simple JSON parser for topology (simplified version)
         */
        private TreeNode parseTopology(String json) {
            // Extract node ID
            int idStart = json.indexOf("\"id\":\"") + 6;
            int idEnd = json.indexOf("\"", idStart);
            String id = json.substring(idStart, idEnd);

            TreeNode node = new TreeNode(id);

            // Check if has children
            int childrenStart = json.indexOf("\"children\":[");
            if (childrenStart != -1) {
                // Has children - simplified parsing (for demo)
                // In real implementation, use proper JSON parser
            }

            return node;
        }

        /**
         * Send async message to another node
         * This is provided by the system - we simulate it here
         */
        void sendAsyncMessage(String toNodeId, String message) {
            System.out.println("  " + nodeId + " -> " + toNodeId + ": " + message);

            // In simulation, route through simulator
            if (simulator != null) {
                simulator.routeMessage(toNodeId, nodeId, message);
            }
        }
    }

    /**
     * Simulator for testing the distributed system
     */
    static class ClusterSimulator {
        Map<String, Node> nodes;

        ClusterSimulator() {
            this.nodes = new HashMap<>();
        }

        void addNode(Node node) {
            nodes.put(node.nodeId, node);
            node.simulator = this;
        }

        void routeMessage(String toNodeId, String fromNodeId, String message) {
            Node node = nodes.get(toNodeId);
            if (node != null) {
                node.receiveMessage(fromNodeId, message);
            }
        }

        void startCount(String rootId) {
            Node root = nodes.get(rootId);
            if (root != null) {
                root.receiveMessage(null, "COUNT");
            }
        }

        void startTopology(String rootId) {
            Node root = nodes.get(rootId);
            if (root != null) {
                root.receiveMessage(null, "TOPOLOGY");
            }
        }
    }

    /**
     * Test the implementation
     */
    public static void main(String[] args) {
        System.out.println("=== Test Case 1: Simple Tree ===");
        /*
         * Tree structure:
         *       root
         *       / \
         *      A   B
         *     / \
         *    C   D
         *
         * Total nodes: 5
         */
        ClusterSimulator sim1 = new ClusterSimulator();

        Node root = new Node("root", Arrays.asList("A", "B"), null);
        Node nodeA = new Node("A", Arrays.asList("C", "D"), "root");
        Node nodeB = new Node("B", new ArrayList<>(), "root");
        Node nodeC = new Node("C", new ArrayList<>(), "A");
        Node nodeD = new Node("D", new ArrayList<>(), "A");

        sim1.addNode(root);
        sim1.addNode(nodeA);
        sim1.addNode(nodeB);
        sim1.addNode(nodeC);
        sim1.addNode(nodeD);

        sim1.startCount("root");
        System.out.println();

        System.out.println("=== Test Case 2: Linear Tree ===");
        /*
         * Tree structure:
         *   root -> A -> B -> C
         *
         * Total nodes: 4
         */
        ClusterSimulator sim2 = new ClusterSimulator();

        Node root2 = new Node("root", Arrays.asList("A"), null);
        Node nodeA2 = new Node("A", Arrays.asList("B"), "root");
        Node nodeB2 = new Node("B", Arrays.asList("C"), "A");
        Node nodeC2 = new Node("C", new ArrayList<>(), "B");

        sim2.addNode(root2);
        sim2.addNode(nodeA2);
        sim2.addNode(nodeB2);
        sim2.addNode(nodeC2);

        sim2.startCount("root");
        System.out.println();

        System.out.println("=== Test Case 3: Single Node ===");
        ClusterSimulator sim3 = new ClusterSimulator();

        Node singleNode = new Node("root", new ArrayList<>(), null);
        sim3.addNode(singleNode);

        sim3.startCount("root");
        System.out.println();

        System.out.println("=== Test Case 4: Wide Tree ===");
        /*
         * Tree structure:
         *       root
         *      / | \  \
         *     A  B  C  D
         *
         * Total nodes: 5
         */
        ClusterSimulator sim4 = new ClusterSimulator();

        Node root4 = new Node("root", Arrays.asList("A", "B", "C", "D"), null);
        Node nodeA4 = new Node("A", new ArrayList<>(), "root");
        Node nodeB4 = new Node("B", new ArrayList<>(), "root");
        Node nodeC4 = new Node("C", new ArrayList<>(), "root");
        Node nodeD4 = new Node("D", new ArrayList<>(), "root");

        sim4.addNode(root4);
        sim4.addNode(nodeA4);
        sim4.addNode(nodeB4);
        sim4.addNode(nodeC4);
        sim4.addNode(nodeD4);

        sim4.startCount("root");
        System.out.println();

        System.out.println("=== Test Case 5: Idempotency Test (Duplicate Response) ===");
        ClusterSimulator sim5 = new ClusterSimulator();

        Node root5 = new Node("root", Arrays.asList("A"), null);
        Node nodeA5 = new Node("A", new ArrayList<>(), "root");

        sim5.addNode(root5);
        sim5.addNode(nodeA5);

        // Start count
        root5.receiveMessage(null, "COUNT");

        // Simulate duplicate response from child A (network retry)
        root5.receiveMessage("A", "COUNT:1"); // Second response should be ignored
        System.out.println();

        System.out.println("=== Test Case 6: Balanced Binary Tree ===");
        /*
         * Tree structure:
         *          root
         *         /    \
         *        A      B
         *       / \    / \
         *      C   D  E   F
         *
         * Total nodes: 7
         */
        ClusterSimulator sim6 = new ClusterSimulator();

        Node root6 = new Node("root", Arrays.asList("A", "B"), null);
        Node nodeA6 = new Node("A", Arrays.asList("C", "D"), "root");
        Node nodeB6 = new Node("B", Arrays.asList("E", "F"), "root");
        Node nodeC6 = new Node("C", new ArrayList<>(), "A");
        Node nodeD6 = new Node("D", new ArrayList<>(), "A");
        Node nodeE6 = new Node("E", new ArrayList<>(), "B");
        Node nodeF6 = new Node("F", new ArrayList<>(), "B");

        sim6.addNode(root6);
        sim6.addNode(nodeA6);
        sim6.addNode(nodeB6);
        sim6.addNode(nodeC6);
        sim6.addNode(nodeD6);
        sim6.addNode(nodeE6);
        sim6.addNode(nodeF6);

        sim6.startCount("root");
        System.out.println();

        System.out.println("=== Test Case 7: Deep Tree ===");
        /*
         * Tree structure:
         *   root -> A -> B -> C -> D -> E
         *
         * Total nodes: 6
         */
        ClusterSimulator sim7 = new ClusterSimulator();

        Node root7 = new Node("root", Arrays.asList("A"), null);
        Node nodeA7 = new Node("A", Arrays.asList("B"), "root");
        Node nodeB7 = new Node("B", Arrays.asList("C"), "A");
        Node nodeC7 = new Node("C", Arrays.asList("D"), "B");
        Node nodeD7 = new Node("D", Arrays.asList("E"), "C");
        Node nodeE7 = new Node("E", new ArrayList<>(), "D");

        sim7.addNode(root7);
        sim7.addNode(nodeA7);
        sim7.addNode(nodeB7);
        sim7.addNode(nodeC7);
        sim7.addNode(nodeD7);
        sim7.addNode(nodeE7);

        sim7.startCount("root");
        System.out.println();

        System.out.println("\n" + "=".repeat(60));
        System.out.println("PART 2: TOPOLOGY DISCOVERY");
        System.out.println("=".repeat(60) + "\n");

        System.out.println("=== Test Case 8: Simple Tree Topology ===");
        /*
         * Tree structure:
         *       root
         *       / \
         *      A   B
         *     / \
         *    C   D
         */
        ClusterSimulator sim8 = new ClusterSimulator();

        Node root8 = new Node("root", Arrays.asList("A", "B"), null);
        Node nodeA8 = new Node("A", Arrays.asList("C", "D"), "root");
        Node nodeB8 = new Node("B", new ArrayList<>(), "root");
        Node nodeC8 = new Node("C", new ArrayList<>(), "A");
        Node nodeD8 = new Node("D", new ArrayList<>(), "A");

        sim8.addNode(root8);
        sim8.addNode(nodeA8);
        sim8.addNode(nodeB8);
        sim8.addNode(nodeC8);
        sim8.addNode(nodeD8);

        sim8.startTopology("root");
        System.out.println();

        System.out.println("=== Test Case 9: Balanced Binary Tree Topology ===");
        /*
         * Tree structure:
         *          root
         *         /    \
         *        A      B
         *       / \    / \
         *      C   D  E   F
         */
        ClusterSimulator sim9 = new ClusterSimulator();

        Node root9 = new Node("root", Arrays.asList("A", "B"), null);
        Node nodeA9 = new Node("A", Arrays.asList("C", "D"), "root");
        Node nodeB9 = new Node("B", Arrays.asList("E", "F"), "root");
        Node nodeC9 = new Node("C", new ArrayList<>(), "A");
        Node nodeD9 = new Node("D", new ArrayList<>(), "A");
        Node nodeE9 = new Node("E", new ArrayList<>(), "B");
        Node nodeF9 = new Node("F", new ArrayList<>(), "B");

        sim9.addNode(root9);
        sim9.addNode(nodeA9);
        sim9.addNode(nodeB9);
        sim9.addNode(nodeC9);
        sim9.addNode(nodeD9);
        sim9.addNode(nodeE9);
        sim9.addNode(nodeF9);

        sim9.startTopology("root");
        System.out.println();

        System.out.println("=== Test Case 10: Linear Tree Topology ===");
        /*
         * Tree structure:
         *   root -> A -> B -> C
         */
        ClusterSimulator sim10 = new ClusterSimulator();

        Node root10 = new Node("root", Arrays.asList("A"), null);
        Node nodeA10 = new Node("A", Arrays.asList("B"), "root");
        Node nodeB10 = new Node("B", Arrays.asList("C"), "A");
        Node nodeC10 = new Node("C", new ArrayList<>(), "B");

        sim10.addNode(root10);
        sim10.addNode(nodeA10);
        sim10.addNode(nodeB10);
        sim10.addNode(nodeC10);

        sim10.startTopology("root");
        System.out.println();

        System.out.println("=== Test Case 11: Single Node Topology ===");
        ClusterSimulator sim11 = new ClusterSimulator();

        Node singleNode11 = new Node("root", new ArrayList<>(), null);
        sim11.addNode(singleNode11);

        sim11.startTopology("root");
    }
}

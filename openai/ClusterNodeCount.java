package openai;

import java.util.*;

/**
 * Distributed Tree Node Counting and Topology Discovery
 *
 * Problem:
 * You are given a tree where each node represents a machine in a cluster. Only parent and child nodes
 * can communicate with each other. Communication happens via sendAsyncMessage() and receiveMessage().
 *
 * The sendAsyncMessage() is provided as an interface and can be directly used. When a node receives
 * a message via sendAsyncMessage(), it automatically triggers receiveMessage() on that node.
 *
 * Part 1: Count total number of machines
 * - Design a method to count the total number of machines in the tree
 * - Root receives initial message: receiveMessage(null, message)
 * - Each node can only communicate with direct parent/child
 * - Use message passing to aggregate counts from children
 *
 * Part 2: Return the entire tree topology
 * - Instead of just counting, return the structure of the entire tree
 * - Each node should collect topology information from children and aggregate
 *
 * Follow-up: Handle network failures and retries
 * - Ensure idempotency (no duplicate counting due to retries)
 * - Solution: Use a Set to track processed message IDs
 *
 * Message Types:
 * - COUNT: Request to count nodes
 * - COUNT_RESPONSE: Response with count from subtree
 * - TOPOLOGY: Request to get tree structure
 * - TOPOLOGY_RESPONSE: Response with subtree topology
 *
 * Algorithm:
 * 1. Root receives COUNT message from external source
 * 2. Root sends COUNT to all children
 * 3. Each child recursively sends COUNT to their children
 * 4. Leaf nodes respond with COUNT_RESPONSE = 1
 * 5. Internal nodes wait for all children responses, sum them, add 1 (self), send to parent
 * 6. Root aggregates all responses and prints result
 *
 * Time Complexity: O(n) where n is number of nodes
 * Space Complexity: O(n) for storing topology and message tracking
 */
public class ClusterNodeCount {

    /**
     * Message types for communication
     */
    enum MessageType {
        COUNT,
        COUNT_RESPONSE,
        TOPOLOGY,
        TOPOLOGY_RESPONSE
    }

    /**
     * Message class for inter-node communication
     */
    static class Message {
        MessageType type;
        int count;              // For COUNT_RESPONSE
        TreeNode topology;      // For TOPOLOGY_RESPONSE
        String messageId;       // For idempotency tracking

        Message(MessageType type) {
            this.type = type;
            this.messageId = UUID.randomUUID().toString();
        }

        Message(MessageType type, int count) {
            this.type = type;
            this.count = count;
            this.messageId = UUID.randomUUID().toString();
        }

        Message(MessageType type, TreeNode topology) {
            this.type = type;
            this.topology = topology;
            this.messageId = UUID.randomUUID().toString();
        }

        Message(MessageType type, String messageId) {
            this.type = type;
            this.messageId = messageId;
        }
    }

    /**
     * Tree structure for topology response
     */
    static class TreeNode {
        String nodeId;
        List<TreeNode> children;

        TreeNode(String nodeId) {
            this.nodeId = nodeId;
            this.children = new ArrayList<>();
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
        String parentId;
        List<String> childIds;

        // For tracking responses from children
        Map<String, Integer> childCountResponses;
        Map<String, TreeNode> childTopologyResponses;

        // For idempotency - track processed messages
        Set<String> processedMessages;

        Node(String nodeId, String parentId, List<String> childIds) {
            this.nodeId = nodeId;
            this.parentId = parentId;
            this.childIds = childIds != null ? childIds : new ArrayList<>();
            this.childCountResponses = new HashMap<>();
            this.childTopologyResponses = new HashMap<>();
            this.processedMessages = new HashSet<>();
        }

        /**
         * Handle incoming messages
         * @param fromNodeId The node that sent the message (null for external messages to root)
         * @param message The message content
         */
        public void receiveMessage(String fromNodeId, Message message) {
            // Idempotency check - avoid processing duplicate messages
            if (processedMessages.contains(message.messageId)) {
                System.out.println(nodeId + ": Ignoring duplicate message " + message.messageId);
                return;
            }
            processedMessages.add(message.messageId);

            switch (message.type) {
                case COUNT:
                    handleCountRequest(message);
                    break;
                case COUNT_RESPONSE:
                    handleCountResponse(fromNodeId, message);
                    break;
                case TOPOLOGY:
                    handleTopologyRequest(message);
                    break;
                case TOPOLOGY_RESPONSE:
                    handleTopologyResponse(fromNodeId, message);
                    break;
            }
        }

        /**
         * Handle COUNT request
         */
        private void handleCountRequest(Message message) {
            if (childIds.isEmpty()) {
                // Leaf node: respond immediately with count = 1
                if (parentId != null) {
                    Message response = new Message(MessageType.COUNT_RESPONSE, 1);
                    sendAsyncMessage(parentId, response);
                }
            } else {
                // Internal node: forward to all children
                childCountResponses.clear();
                for (String childId : childIds) {
                    Message forwardMsg = new Message(MessageType.COUNT, message.messageId);
                    sendAsyncMessage(childId, forwardMsg);
                }
            }
        }

        /**
         * Handle COUNT_RESPONSE from child
         */
        private void handleCountResponse(String fromNodeId, Message message) {
            childCountResponses.put(fromNodeId, message.count);

            // Check if we've received responses from all children
            if (childCountResponses.size() == childIds.size()) {
                // Calculate total count: sum of children + self
                int totalCount = 1; // Count self
                for (int count : childCountResponses.values()) {
                    totalCount += count;
                }

                if (parentId == null) {
                    // Root node: print result
                    System.out.println("Total node count: " + totalCount);
                } else {
                    // Send to parent
                    Message response = new Message(MessageType.COUNT_RESPONSE, totalCount);
                    sendAsyncMessage(parentId, response);
                }
            }
        }

        /**
         * Handle TOPOLOGY request
         */
        private void handleTopologyRequest(Message message) {
            if (childIds.isEmpty()) {
                // Leaf node: respond with own topology
                TreeNode topology = new TreeNode(nodeId);
                if (parentId != null) {
                    Message response = new Message(MessageType.TOPOLOGY_RESPONSE, topology);
                    sendAsyncMessage(parentId, response);
                }
            } else {
                // Internal node: forward to all children
                childTopologyResponses.clear();
                for (String childId : childIds) {
                    Message forwardMsg = new Message(MessageType.TOPOLOGY, message.messageId);
                    sendAsyncMessage(childId, forwardMsg);
                }
            }
        }

        /**
         * Handle TOPOLOGY_RESPONSE from child
         */
        private void handleTopologyResponse(String fromNodeId, Message message) {
            childTopologyResponses.put(fromNodeId, message.topology);

            // Check if we've received responses from all children
            if (childTopologyResponses.size() == childIds.size()) {
                // Build topology with self as root and children's topologies
                TreeNode topology = new TreeNode(nodeId);
                for (TreeNode childTopology : childTopologyResponses.values()) {
                    topology.children.add(childTopology);
                }

                if (parentId == null) {
                    // Root node: print topology
                    System.out.println("Cluster Topology:");
                    System.out.println(topology);
                } else {
                    // Send to parent
                    Message response = new Message(MessageType.TOPOLOGY_RESPONSE, topology);
                    sendAsyncMessage(parentId, response);
                }
            }
        }

        /**
         * Send async message to another node
         * This is provided by the system - we simulate it here
         */
        private void sendAsyncMessage(String toNodeId, Message message) {
            // In real system, this would send message to the target node
            // For simulation, we'll track this in the test
            System.out.println(nodeId + " -> " + toNodeId + ": " + message.type);
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
        }

        void sendMessage(String toNodeId, String fromNodeId, Message message) {
            Node node = nodes.get(toNodeId);
            if (node != null) {
                node.receiveMessage(fromNodeId, message);
            }
        }

        // Simulate sendAsyncMessage by routing to target node
        void simulateAsyncMessages(Node node) {
            // Override the sendAsyncMessage to use simulator
            // In practice, this would be handled by the messaging infrastructure
        }
    }

    /**
     * Test the implementation
     */
    public static void main(String[] args) {
        System.out.println("=== Test Case 1: Simple Tree Count ===");
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

        Node root = new Node("root", null, Arrays.asList("A", "B"));
        Node nodeA = new Node("A", "root", Arrays.asList("C", "D"));
        Node nodeB = new Node("B", "root", new ArrayList<>());
        Node nodeC = new Node("C", "A", new ArrayList<>());
        Node nodeD = new Node("D", "A", new ArrayList<>());

        // Manually simulate message flow for counting
        simulateCountTest(root, nodeA, nodeB, nodeC, nodeD);
        System.out.println();

        System.out.println("=== Test Case 2: Linear Tree ===");
        /*
         * Tree structure:
         *   root -> A -> B -> C
         *
         * Total nodes: 4
         */
        Node root2 = new Node("root", null, Arrays.asList("A"));
        Node nodeA2 = new Node("A", "root", Arrays.asList("B"));
        Node nodeB2 = new Node("B", "A", Arrays.asList("C"));
        Node nodeC2 = new Node("C", "B", new ArrayList<>());

        simulateCountLinear(root2, nodeA2, nodeB2, nodeC2);
        System.out.println();

        System.out.println("=== Test Case 3: Idempotency Test ===");
        Node root3 = new Node("root", null, Arrays.asList("A"));
        Node nodeA3 = new Node("A", "root", new ArrayList<>());

        Message msg = new Message(MessageType.COUNT);
        root3.receiveMessage(null, msg);
        // Simulate duplicate message (retry due to network failure)
        root3.receiveMessage(null, msg); // Should be ignored
        System.out.println();

        System.out.println("=== Topology Discovery Example ===");
        System.out.println("Tree structure:");
        System.out.println("       root");
        System.out.println("       / \\");
        System.out.println("      A   B");
        System.out.println("     / \\");
        System.out.println("    C   D");
        System.out.println();
        simulateTopologyTest(
            new Node("root", null, Arrays.asList("A", "B")),
            new Node("A", "root", Arrays.asList("C", "D")),
            new Node("B", "root", new ArrayList<>()),
            new Node("C", "A", new ArrayList<>()),
            new Node("D", "A", new ArrayList<>())
        );
    }

    /**
     * Simulate COUNT operation for tree
     */
    private static void simulateCountTest(Node root, Node a, Node b, Node c, Node d) {
        // 1. Root receives COUNT from external
        Message countMsg = new Message(MessageType.COUNT);
        root.receiveMessage(null, countMsg);

        // 2. Children propagate COUNT
        a.receiveMessage("root", new Message(MessageType.COUNT, countMsg.messageId));
        b.receiveMessage("root", new Message(MessageType.COUNT, countMsg.messageId));

        // 3. Leaf C responds
        c.receiveMessage("A", new Message(MessageType.COUNT, countMsg.messageId));

        // 4. Leaf D responds
        d.receiveMessage("A", new Message(MessageType.COUNT, countMsg.messageId));

        // 5. A receives responses from C and D, then responds to root
        a.receiveMessage("C", new Message(MessageType.COUNT_RESPONSE, 1));
        a.receiveMessage("D", new Message(MessageType.COUNT_RESPONSE, 1));

        // 6. B (leaf) responds to root
        Message bResponse = new Message(MessageType.COUNT_RESPONSE, 1);

        // 7. Root receives responses and calculates total
        root.receiveMessage("A", new Message(MessageType.COUNT_RESPONSE, 3));
        root.receiveMessage("B", bResponse);
    }

    /**
     * Simulate COUNT for linear tree
     */
    private static void simulateCountLinear(Node root, Node a, Node b, Node c) {
        Message countMsg = new Message(MessageType.COUNT);
        root.receiveMessage(null, countMsg);
        a.receiveMessage("root", new Message(MessageType.COUNT, countMsg.messageId));
        b.receiveMessage("A", new Message(MessageType.COUNT, countMsg.messageId));
        c.receiveMessage("B", new Message(MessageType.COUNT, countMsg.messageId));

        // Responses back up
        b.receiveMessage("C", new Message(MessageType.COUNT_RESPONSE, 1));
        a.receiveMessage("B", new Message(MessageType.COUNT_RESPONSE, 2));
        root.receiveMessage("A", new Message(MessageType.COUNT_RESPONSE, 3));
    }

    /**
     * Simulate TOPOLOGY discovery
     */
    private static void simulateTopologyTest(Node root, Node a, Node b, Node c, Node d) {
        Message topoMsg = new Message(MessageType.TOPOLOGY);
        root.receiveMessage(null, topoMsg);

        a.receiveMessage("root", new Message(MessageType.TOPOLOGY, topoMsg.messageId));
        b.receiveMessage("root", new Message(MessageType.TOPOLOGY, topoMsg.messageId));

        c.receiveMessage("A", new Message(MessageType.TOPOLOGY, topoMsg.messageId));
        d.receiveMessage("A", new Message(MessageType.TOPOLOGY, topoMsg.messageId));

        // Responses with topology
        TreeNode cTopo = new TreeNode("C");
        TreeNode dTopo = new TreeNode("D");

        a.receiveMessage("C", new Message(MessageType.TOPOLOGY_RESPONSE, cTopo));
        a.receiveMessage("D", new Message(MessageType.TOPOLOGY_RESPONSE, dTopo));

        TreeNode bTopo = new TreeNode("B");
        TreeNode aTopo = new TreeNode("A");
        aTopo.children.add(cTopo);
        aTopo.children.add(dTopo);

        root.receiveMessage("A", new Message(MessageType.TOPOLOGY_RESPONSE, aTopo));
        root.receiveMessage("B", new Message(MessageType.TOPOLOGY_RESPONSE, bTopo));
    }
}

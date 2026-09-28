package openai;

import java.util.*;

public class MachineTree {

    // Global registry so sendAsyncMessage can find other nodes
    static Map<String, Node> nodes = new HashMap<>();

    public static void main(String[] args) {
        // Build a test tree:
        //        A
        //      /   \
        //     B     C
        //          / \
        //         D   E

        nodes.put("A", new Node("A", List.of("B", "C"), null));
        nodes.put("B", new Node("B", List.of(), "A"));
        nodes.put("C", new Node("C", List.of("D", "E"), "A"));
        nodes.put("D", new Node("D", List.of(), "C"));
        nodes.put("E", new Node("E", List.of(), "C"));

        System.out.println("===== TEST 1: COUNT =====");
        nodes.get("A").receiveMessage(null, "COUNT");

        System.out.println("\n===== TEST 2: TOPOLOGY =====");
        nodes.get("A").receiveMessage(null, "TOPO");
    }

    // ========================== Node Class ==========================
    static class Node {
        String id;
        List<String> children;
        String parent;

        // For idempotency
        Set<String> seen = new HashSet<>();

        // For COUNT
        Map<String, Integer> countResp = new HashMap<>();

        // For TOPO
        Map<String, String> topoResp = new HashMap<>();

        Node(String id, List<String> children, String parent) {
            this.id = id;
            this.children = children;
            this.parent = parent;
        }

        // Provided API — system will call receiveMessage on destination
        void sendAsyncMessage(String to, String msg) {
            Node target = MachineTree.nodes.get(to);
            target.receiveMessage(this.id, msg);
        }

        synchronized void receiveMessage(String from, String msg) {
            // Clear state for new operations
            if (msg.equals("COUNT")) {
                countResp.clear();
                seen.clear();
            } else if (msg.equals("TOPO")) {
                topoResp.clear();
                seen.clear();
            }

            // Ensure idempotency
            String key = from + "|" + msg;
            if (from != null && !seen.add(key)) return;

            if (msg.equals("COUNT")) {
                handleCount();
            } else if (msg.startsWith("RESP:")) {
                int val = Integer.parseInt(msg.substring(5));
                handleCountResp(from, val);

            } else if (msg.equals("TOPO")) {
                handleTopo();
            } else if (msg.startsWith("TOPO_RESP:")) {
                handleTopoResp(from, msg.substring(10));  // "TOPO_RESP:" is 10 chars
            }
        }

        // ========================= COUNT =========================

        private void handleCount() {
            if (children.isEmpty()) {
                sendAsyncMessage(parent, "RESP:1");
                return;
            }
            for (String c : children) sendAsyncMessage(c, "COUNT");
        }

        private void handleCountResp(String child, int val) {
            countResp.put(child, val);
            if (countResp.size() == children.size()) {
                int sum = 1; // include itself
                for (int v : countResp.values()) sum += v;

                if (parent == null) {
                    System.out.println("[RESULT] TOTAL COUNT = " + sum);
                } else {
                    sendAsyncMessage(parent, "RESP:" + sum);
                }
            }
        }

        // ========================= TOPOLOGY =========================

        private void handleTopo() {
            if (children.isEmpty()) {
                sendAsyncMessage(parent, "TOPO_RESP:" + id);
                return;
            }
            for (String c : children) sendAsyncMessage(c, "TOPO");
        }

        private void handleTopoResp(String child, String topo) {
            topoResp.put(child, topo);

            if (topoResp.size() == children.size()) {
                StringBuilder sb = new StringBuilder();
                sb.append(id).append("(");

                for (int i = 0; i < children.size(); i++) {
                    if (i > 0) sb.append(",");
                    sb.append(topoResp.get(children.get(i)));
                }

                sb.append(")");

                if (parent == null) {
                    System.out.println("[RESULT] TOPOLOGY = " + sb);
                } else {
                    sendAsyncMessage(parent, "TOPO_RESP:" + sb);
                }
            }
        }

    }
}


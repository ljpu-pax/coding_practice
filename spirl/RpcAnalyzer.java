package spirl;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

// Bool shouldRateLimit(String customerId)
// 5 times in 2 secs
// 1, 0s, 0s, 0s, 
// 1, 2, 1s, 1s,
// 1, 2, 2s, 2s
// 1, 2, 3s, 3s,



public class RpcAnalyzer {
    int limit;
    int window;
    Deque<Events> dq;

    public class Events {
        String id;
        int currentTime;
        public Events(String eventId, int second) {
            this.id = eventId;
            this.currentTime = second;
        }
    }

    public RpcAnalyzer(int windowSize, int windowLimit) {
        this.window = windowSize;
        this.limit = windowLimit;
        this.dq = new ArrayDeque<>();
    }
    // sliding window
    // deque moving
    public boolean shouldRateLimit(String customerId, int currentTime) {
        int count = 0;
        while (!dq.isEmpty() && dq.peekFirst().id == customerId && dq.peekFirst().currentTime < currentTime - limit ) {
            System.out.println("remove " + dq.peekFirst().id);
            dq.removeFirst();
        }
        // 7
        Iterator<Events> iterator = dq.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().id == customerId) {
                count++;
                if (count > limit) return true;
                // System.out.println(count);
            }
        }
        return false;
    }

    public void addEvents(String customerId, int currentTime) {
        dq.add(new Events(customerId, currentTime));
    }

    public static void main(String[] args) {
        RpcAnalyzer rpc = new RpcAnalyzer(2, 5);
        // rpc.addEvents("0", 0);
        // rpc.addEvents("0", 0);
        // rpc.addEvents("0", 0);
        // rpc.addEvents("0", 0);
        // rpc.addEvents("101", 1);
        // rpc.addEvents("101", 1);
        rpc.addEvents("0", 1);
        rpc.addEvents("1", 1);
        rpc.addEvents("1", 1);
        rpc.addEvents("1", 1);
        rpc.addEvents("1", 1);
        rpc.addEvents("1", 1);
        rpc.addEvents("1", 1);
        // rpc.addEvents("0", 1);

        System.out.println(rpc.shouldRateLimit("1", 2));
        // System.out.println(rpc.shouldRateLimit("1", 2));
        // System.out.println(rpc.shouldRateLimit("1", 2));
        // System.out.println(rpc.shouldRateLimit("2", 2));
    }

}

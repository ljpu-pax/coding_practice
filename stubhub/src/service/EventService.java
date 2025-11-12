package service;

import model.Event;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;

public class EventService {

    // 模拟外部API调用: 异步获取所有事件数据
    public CompletableFuture<List<Event>> fetchAllEventsAsync() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // 模拟网络延迟
                Thread.sleep(200);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return mockEvents();
        });
    }

    // 模拟数据
    private List<Event> mockEvents() {
        return List.of(
                new Event("1", "Jazz Concert", "San Francisco", 37.77, -122.42, 120,
                        LocalDateTime.now().plusDays(2)),
                new Event("2", "Rock Show", "San Jose", 37.33, -121.88, 90,
                        LocalDateTime.now().plusDays(5)),
                new Event("3", "Tech Expo", "San Francisco", 37.78, -122.41, 60,
                        LocalDateTime.now().plusDays(10)),
                new Event("4", "Art Fair", "Oakland", 37.80, -122.27, 30,
                        LocalDateTime.now().plusDays(3))
        );
    }
}

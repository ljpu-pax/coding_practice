import model.*;
import service.*;
import java.time.LocalDate;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class Main {
    public static void main(String[] args) throws Exception {
        User user = new User("u1", "John", "San Francisco",
                37.7749, -122.4194, LocalDate.of(1990, 5, 10));

        EventService eventService = new EventService();
        OutreachEngine engine = new OutreachEngine(eventService);

        // 在面试中，逐步实现并调用这些方法
        System.out.println("---- Same City Events ----");
        System.out.println(engine.getEventsInSameCity(user).get());

        System.out.println("---- Closest To Birthday ----");
        System.out.println(engine.getClosestToBirthday(user).get());

        System.out.println("---- Nearest Events ----");
        System.out.println(engine.getNearestEvents(user, 3).get());

         // 4️⃣ 综合运行所有 Campaigns（异步并发）
        System.out.println("---- Running All Campaigns ----");
        CompletableFuture<Map<String, Object>> campaignsFuture = engine.runCampaigns(user);
        Map<String, Object> results = campaignsFuture.get();

        // 5️⃣ 输出结果
        results.forEach((k, v) -> {
            System.out.println("Campaign: " + k);
            System.out.println("Result: " + v);
            System.out.println("------------------------------");
        });
    }
}

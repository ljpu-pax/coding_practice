// 说是小镇上有统计所有在小镇当天举办party的开始时间和结束时间，输入的参数里有一项包括了小镇信息（名字）以及party ID，
// 另外一项输入信息是每个party window信息，包括party开始和结束时间以及ID等等，时间用的是string的形式(2am, 10pm)
// 第一问是打印出所有小镇party的开始时间和结束时间，例如小镇A有两个party，一个是1pm-3pm，
// 另一个是8-10pm，那么最后输出应该是{A: [13-22]}这里需要转换am pm到时间整数 in java


import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class PartyTimeAggregator {

    // 定义时间格式解析器
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("h[:mm]a", Locale.ENGLISH);

    // 派对信息类
    static class Party {
        String town;
        String id;
        String startTimeStr;
        String endTimeStr;

        Party(String town, String id, String startTimeStr, String endTimeStr) {
            this.town = town;
            this.id = id;
            this.startTimeStr = startTimeStr;
            this.endTimeStr = endTimeStr;
        }
    }

    public static void main(String[] args) {
        // 示例输入数据
        List<Party> parties = Arrays.asList(
            new Party("A", "p1", "1pm", "3pm"),
            new Party("A", "p2", "8pm", "10pm"),
            new Party("B", "p3", "2am", "4am"),
            new Party("B", "p4", "6pm", "9pm")
        );

        // 汇总每个小镇的最早开始时间和最晚结束时间
        Map<String, int[]> townTimeRanges = new HashMap<>();

        for (Party party : parties) {
            int startHour = parseHour(party.startTimeStr);
            int endHour = parseHour(party.endTimeStr);

            townTimeRanges.compute(party.town, (town, range) -> {
                if (range == null) {
                    return new int[]{startHour, endHour};
                } else {
                    range[0] = Math.min(range[0], startHour);
                    range[1] = Math.max(range[1], endHour);
                    return range;
                }
            });
        }

        // 打印结果
        for (Map.Entry<String, int[]> entry : townTimeRanges.entrySet()) {
            String town = entry.getKey();
            int[] range = entry.getValue();
            System.out.println(town + ": [" + range[0] + "-" + range[1] + "]");
        }
    }

    // 解析时间字符串为小时整数
    private static int parseHour(String timeStr) {
        // 添加 ":00" 分钟部分以匹配解析器
        if (!timeStr.matches(".*\\d{1,2}:\\d{2}.*")) {
            timeStr = timeStr.replaceAll("(?i)(\\d+)(am|pm)", "$1:00$2");
        }
        LocalTime time = LocalTime.parse(timeStr.toUpperCase(), TIME_FORMATTER);
        return time.getHour();
    }
}

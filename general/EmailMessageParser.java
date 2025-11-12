import java.util.*;

public class EmailMessageParser {

    public static List<String> parseMessage(String message) {
        List<String> output = new ArrayList<>();
        String[] lines = message.split("\n");
        StringBuilder buffer = new StringBuilder();
        boolean inQuoteBlock = false;

        for (int i = 0; i <= lines.length; i++) {
            String line = (i < lines.length) ? lines[i] : ""; // sentinel
            boolean isQuote = line.startsWith(">");
            boolean isEmpty = line.trim().isEmpty();

            // Case 1: Handle end of quote block
            if (!isQuote && inQuoteBlock) {
                output.add(formatQuote(buffer.toString()));
                buffer.setLength(0);
                inQuoteBlock = false;
            }

            // Case 2: Handle end of paragraph block
            if (!inQuoteBlock && isEmpty && buffer.length() > 0) {
                output.add(formatParagraph(buffer.toString()));
                buffer.setLength(0);
                continue;
            }

            // Case 3: Accumulate quote
            if (isQuote) {
                inQuoteBlock = true;
                buffer.append(line.substring(1).trim()).append("<Break />");
            }

            // Case 4: Accumulate paragraph
            else if (!isEmpty) {
                if (buffer.length() > 0) buffer.append("<Break />");
                buffer.append(line.trim());
            }
        }

        // Add remaining buffer
        if (buffer.length() > 0) {
            if (inQuoteBlock) output.add(formatQuote(buffer.toString()));
            else output.add(formatParagraph(buffer.toString()));
        }

        return output;
    }

    private static String formatParagraph(String text) {
        return "<Paragraph>" + text + "</Paragraph>";
    }

    private static String formatQuote(String text) {
        // remove trailing <Break /> if present
        if (text.endsWith("<Break />")) {
            text = text.substring(0, text.length() - "<Break />".length());
        }
        return "<Quote>" + text + "</Quote>";
    }

    // For testing
    public static void main(String[] args) {
        String message = "Please tag @Bob Jones for approval!\n\n"
                + "Here's the max amount we were allotted:\n\n"
                + ">Max spend: $5000\n"
                + ">Max spend per user: $12\n"
                + ">Target spend: $3000\n\n"
                + "Do you think it will be ok?\nOtherwise let's consider a new vendor.";

        List<String> htmlOutput = parseMessage(message);
        htmlOutput.forEach(System.out::println);
    }
}


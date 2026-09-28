/**
 * Problem: Implement a function that takes a data buffer as input and
 * prints a 3-column "hexdump" output. The columns are: offset from the
 * start of the buffer, hex representation, and "best-effort" ASCII.
 *
 * Example output format:
 * 00000000: 2550 4446 2d31 2e33 0a25 ffff ffff 0a36  %PDF-1.3.%.....6
 * 00000010: 2030 206f 626a 0a3c 3c0a 2f54 7970 6520   0 obj.<<./Type
 * 00000020: 2f50 6167 6572 656e 7420 3120 0a2f 5061  /Pagerent 1 ./Pa
 * ...
 */
public class HexDump {

    /**
     * Prints hexdump output for the given data buffer
     *
     * @param data byte array to convert to hexdump format
     */
    public static void hexDump(byte[] data) {
        if (data == null || data.length == 0) {
            return;
        }

        int bytesPerLine = 16;

        for (int offset = 0; offset < data.length; offset += bytesPerLine) {
            // Column 1: Print offset (8 hex digits)
            System.out.printf("%08x: ", offset);

            // Column 2: Print hex representation (16 bytes, grouped by 2)
            int bytesInLine = Math.min(bytesPerLine, data.length - offset);
            for (int i = 0; i < bytesPerLine; i++) {
                if (i < bytesInLine) {
                    System.out.printf("%02x", data[offset + i]);
                } else {
                    System.out.print("  "); // padding for incomplete lines
                }

                // Add space after every 2 bytes
                if (i % 2 == 1) {
                    System.out.print(" ");
                }
            }

            // Column 3: Print ASCII representation
            System.out.print(" ");
            for (int i = 0; i < bytesInLine; i++) {
                byte b = data[offset + i];
                // Print printable ASCII (32-126), otherwise print '.'
                char c = (b >= 32 && b <= 126) ? (char) b : '.';
                System.out.print(c);
            }

            System.out.println();
        }
    }

    /**
     * Alternative implementation with StringBuilder for better performance
     */
    public static String hexDumpToString(byte[] data) {
        if (data == null || data.length == 0) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        int bytesPerLine = 16;

        for (int offset = 0; offset < data.length; offset += bytesPerLine) {
            // Column 1: Offset
            sb.append(String.format("%08x: ", offset));

            // Column 2: Hex representation
            int bytesInLine = Math.min(bytesPerLine, data.length - offset);
            for (int i = 0; i < bytesPerLine; i++) {
                if (i < bytesInLine) {
                    sb.append(String.format("%02x", data[offset + i]));
                } else {
                    sb.append("  ");
                }

                if (i % 2 == 1) {
                    sb.append(" ");
                }
            }

            // Column 3: ASCII representation
            sb.append(" ");
            for (int i = 0; i < bytesInLine; i++) {
                byte b = data[offset + i];
                char c = (b >= 32 && b <= 126) ? (char) b : '.';
                sb.append(c);
            }

            sb.append("\n");
        }

        return sb.toString();
    }

    // Test cases
    public static void main(String[] args) {
        System.out.println("=== Test 1: PDF Header ===");
        String pdfHeader = "%PDF-1.3\n%\uffff\uffff\uffff\uffff\n6 0 obj\n<<\n/Type /Pagerent 1 \n/Pages 4 0 R\n/Resources 5 0 R\n";
        byte[] data1 = pdfHeader.getBytes();
        hexDump(data1);

        System.out.println("\n=== Test 2: Simple Text ===");
        String simpleText = "Hello, World!";
        byte[] data2 = simpleText.getBytes();
        hexDump(data2);

        System.out.println("\n=== Test 3: Binary Data ===");
        byte[] data3 = new byte[50];
        for (int i = 0; i < data3.length; i++) {
            data3[i] = (byte) i;
        }
        hexDump(data3);

        System.out.println("\n=== Test 4: Empty Array ===");
        byte[] data4 = new byte[0];
        hexDump(data4);

        System.out.println("\n=== Test 5: Partial Line (7 bytes) ===");
        byte[] data5 = "Testing".getBytes();
        hexDump(data5);

        System.out.println("\n=== Test 6: Using StringBuilder version ===");
        System.out.print(hexDumpToString(simpleText.getBytes()));
    }
}

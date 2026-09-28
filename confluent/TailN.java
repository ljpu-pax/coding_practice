import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Confluent onsite (most frequent): implement `tail -n`.
 *
 * Print the last N lines of a file.
 *
 * Interview notes:
 * - Interviewer saves a .txt file and wants you to read from it -> be fluent with
 *   BufferedWriter / BufferedReader (see writeTestFile / tailBuffer).
 * - Tradeoff discussion:
 *   1) Buffer approach: read forward, keep a ring buffer of the last N lines.
 *      O(file size) time, O(N lines) memory. Works on streams/pipes (no seek).
 *   2) File pointer approach: seek to end, scan backwards in chunks counting '\n'
 *      until N+1 newlines found, then stream forward to stdout.
 *      O(size of last N lines) time, O(chunk) memory. Needs a seekable file.
 * - Follow-up: given only an abstract file API (read n bytes, move pointer +/-n,
 *   get size), write it and STREAM the result to stdout (don't hold all N lines).
 *   See tailWithFileApi.
 */
public class TailN {

    // ---------------- Approach 1: ring buffer of lines ----------------
    public static void tailBuffer(String path, int n, PrintStream out) throws IOException {
        if (n <= 0) return;
        Deque<String> last = new ArrayDeque<>();
        try (BufferedReader br = new BufferedReader(new FileReader(path, StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                last.addLast(line);
                if (last.size() > n) last.pollFirst();
            }
        }
        for (String s : last) out.println(s);
    }


    // ---------------- Approach 2: seek backwards with RandomAccessFile ----------------
    public static void tailSeek(String path, int n, PrintStream out) throws IOException {
        if (n <= 0) return;
        try (RandomAccessFile raf = new RandomAccessFile(path, "r")) {
            long start = findStartOffset(raf, n, 4096);
            // stream forward from start to EOF
            raf.seek(start);
            byte[] buf = new byte[4096];
            int r;
            while ((r = raf.read(buf)) != -1) out.write(buf, 0, r);
            out.flush();
        }
    }

    // ---------------- Approach 2 (simple): same idea, one byte at a time ----------------
    // Easiest to write in an interview. One read() per byte is slower than chunks,
    // but only the last n lines are scanned. Upgrade to chunks if asked.
    public static void tailSimple(String path, int n, PrintStream out) throws IOException {
        if (n <= 0) return;
        try (RandomAccessFile raf = new RandomAccessFile(path, "r")) {
            long pos = raf.length() - 1;
            // A trailing '\n' ends the last line; skip it.
            if (pos >= 0) {
                raf.seek(pos);
                if (raf.read() == '\n') pos--;
            }
            // Walk backwards until the n-th '\n' (the one before the last n lines).
            int count = 0;
            for (; pos >= 0; pos--) {
                raf.seek(pos);
                if (raf.read() == '\n' && ++count == n) break;
            }
            // pos is that '\n' (or -1 if fewer than n lines). Print everything after it.
            raf.seek(pos + 1);
            byte[] buf = new byte[4096];
            int r;
            while ((r = raf.read(buf)) != -1) out.write(buf, 0, r);
            out.flush();
        }
    }

    /** Returns the byte offset where the last n lines begin. */
    static long findStartOffset(RandomAccessFile raf, int n, int chunk) throws IOException {
        long size = raf.length();
        if (size == 0) return 0;
        long pos = size;
        int newlines = 0;
        // A trailing '\n' terminates the last line; it does not start a new one.
        raf.seek(size - 1);
        boolean endsWithNewline = raf.read() == '\n';
        int target = endsWithNewline ? n + 1 : n;
        byte[] buf = new byte[chunk];
        while (pos > 0) {
            int len = (int) Math.min(chunk, pos);
            pos -= len;
            raf.seek(pos);
            raf.readFully(buf, 0, len);
            for (int i = len - 1; i >= 0; i--) {
                if (buf[i] == '\n' && ++newlines == target) {
                    return pos + i + 1;
                }
            }
        }
        return 0; // fewer than n lines -> whole file
    }

    // ---------------- Follow-up: abstract file API (pseudocode made runnable) ----------------
    /** The API the interviewer gives you. */
    interface SimpleFile {
        long size();
        void move(long delta);        // move pointer by +/- delta
        int read(byte[] buf, int n);  // read up to n bytes at pointer, advances pointer
        long position();
    }

    static void tailWithFileApi(SimpleFile f, int n, OutputStream out, int chunk) throws IOException {
        if (n <= 0 || f.size() == 0) return;
        long size = f.size();
        byte[] one = new byte[1];
        f.move(size - 1 - f.position());
        f.read(one, 1);
        int target = one[0] == '\n' ? n + 1 : n;

        // Phase 1: walk backwards chunk by chunk, counting newlines.
        long pos = size, start = 0;
        int newlines = 0;
        byte[] buf = new byte[chunk];
        outer:
        while (pos > 0) {
            int len = (int) Math.min(chunk, pos);
            pos -= len;
            f.move(pos - f.position());
            f.read(buf, len);
            for (int i = len - 1; i >= 0; i--) {
                if (buf[i] == '\n' && ++newlines == target) {
                    start = pos + i + 1;
                    break outer;
                }
            }
        }
        // Phase 2: stream forward to stdout, only `chunk` bytes in memory at a time.
        f.move(start - f.position());
        int r;
        while ((r = f.read(buf, chunk)) > 0) out.write(buf, 0, r);
        out.flush();
    }

    /** In-memory implementation of SimpleFile for testing. */
    static class ByteArrayFile implements SimpleFile {
        private final byte[] data;
        private long p = 0;
        ByteArrayFile(byte[] data) { this.data = data; }
        public long size() { return data.length; }
        public void move(long d) { p = Math.max(0, Math.min(data.length, p + d)); }
        public long position() { return p; }
        public int read(byte[] buf, int n) {
            int r = (int) Math.min(n, data.length - p);
            System.arraycopy(data, (int) p, buf, 0, r);
            p += r;
            return r;
        }
    }

    // ---------------- Test helpers ----------------
    static void writeTestFile(String path, String content) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(path, StandardCharsets.UTF_8))) {
            bw.write(content);
        }
    }

    static String capture(IOAction a) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(bos, true, StandardCharsets.UTF_8);
        a.run(ps);
        return bos.toString(StandardCharsets.UTF_8);
    }

    interface IOAction { void run(PrintStream ps) throws IOException; }

    public static void main(String[] args) throws IOException {
        File tmp = File.createTempFile("tail", ".txt");
        tmp.deleteOnExit();
        String path = tmp.getPath();

        String[][] cases = {
            {"a\nb\nc\nd\ne\n", "3", "c\nd\ne\n"},
            {"a\nb\nc\nd\ne", "2", "d\ne"},       // no trailing newline
            {"a\nb\n", "10", "a\nb\n"},            // n > line count
            {"", "3", ""},
            {"only\n", "1", "only\n"},
            {"\n\n\n", "2", "\n\n"},              // empty lines
        };
        for (String[] c : cases) {
            writeTestFile(path, c[0]);
            int n = Integer.parseInt(c[1]);
            String seek = capture(ps -> tailSeek(path, n, ps));
            String simple = capture(ps -> tailSimple(path, n, ps));
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            tailWithFileApi(new ByteArrayFile(c[0].getBytes(StandardCharsets.UTF_8)), n, bos, 2);
            String api = bos.toString(StandardCharsets.UTF_8);
            System.out.printf("n=%s seek=%s simple=%s api=%s%n",
                c[1], seek.equals(c[2]), simple.equals(c[2]), api.equals(c[2]));
        }

        // Buffer approach normalizes every line with println
        writeTestFile(path, "l1\nl2\nl3\nl4\n");
        System.out.print("buffer tail 2:\n" + capture(ps -> tailBuffer(path, 2, ps)));
    }
}

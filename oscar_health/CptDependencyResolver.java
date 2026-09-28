import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * CPT Dependency Resolution
 *
 * Problem summary:
 * You receive two lists:
 * 1. `lines`: ordered CPT codes that could run today. Each entry carries a line number and the CPT string.
 * 2. `primaryToAddon`: dependency pairs (primary, addon) indicating that `addon` cannot execute until `primary` has executed.
 *
 * Goal: return every CPT from `lines` that can be executed given the dependency table. Treat dependencies like
 * prerequisites: a CPT becomes executable only after all of its prereqs have already executed (or when it has none).
 * If a CPT depends on a code that never appears in `lines`, it will remain blocked.
 */
public class CptDependencyResolver {

    public static class Line {
        private final String lineNumber;
        private final String cpt;

        public Line(String lineNumber, String cpt) {
            this.lineNumber = lineNumber;
            this.cpt = cpt;
        }

        public String lineNumber() {
            return lineNumber;
        }

        public String cpt() {
            return cpt;
        }
    }

    public static class Dependency {
        private final String primary;
        private final String addon;

        public Dependency(String primary, String addon) {
            this.primary = primary;
            this.addon = addon;
        }

        public String primary() {
            return primary;
        }

        public String addon() {
            return addon;
        }
    }

    /**
     * Returns the CPTs that can run, respecting dependency ordering. The returned list preserves the original
     * `lines` ordering for any CPT that becomes executable as soon as its prerequisites are satisfied.
     */
    public static List<String> findExecutableCpts(List<Line> lines, List<Dependency> dependencies) {
        Map<String, Set<String>> prereqs = buildPrereqMap(dependencies);
        List<String> executionOrder = new ArrayList<>();
        Set<String> completed = new HashSet<>();

        boolean madeProgress;
        do {
            madeProgress = false;
            for (Line line : lines) {
                String cpt = line.cpt();
                if (completed.contains(cpt)) {
                    continue;
                }

                if (canRun(cpt, prereqs, completed)) {
                    completed.add(cpt);
                    executionOrder.add(cpt);
                    madeProgress = true;
                }
            }
        } while (madeProgress);

        return executionOrder;
    }

    private static Map<String, Set<String>> buildPrereqMap(List<Dependency> dependencies) {
        Map<String, Set<String>> prereqs = new HashMap<>();
        for (Dependency dep : dependencies) {
            prereqs.computeIfAbsent(dep.addon(), key -> new HashSet<>()).add(dep.primary());
        }
        return prereqs;
    }

    private static boolean canRun(String cpt, Map<String, Set<String>> prereqs, Set<String> completed) {
        Set<String> needed = prereqs.getOrDefault(cpt, Collections.emptySet());
        for (String prerequisite : needed) {
            if (!completed.contains(prerequisite)) {
                return false;
            }
        }
        return true;
    }

    public static void main(String[] args) {
        List<Line> lines = Arrays.asList(
            new Line("1", "A1"),
            new Line("2", "A2"),
            new Line("3", "A3"),
            new Line("4", "A4"),
            new Line("5", "P1")
        );

        List<Dependency> primaryToAddon = Arrays.asList(
            new Dependency("P1", "A1"),
            new Dependency("P2", "A2"),
            new Dependency("P1", "A3"),
            new Dependency("P1", "A4"),
            new Dependency("A2", "A4")
        );

        List<String> executable = findExecutableCpts(lines, primaryToAddon);
        System.out.println(executable); // prints [P1, A1, A3]
    }
}

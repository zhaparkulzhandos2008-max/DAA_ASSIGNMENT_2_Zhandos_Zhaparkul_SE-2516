package benchmark;

import metrics.OperationMetrics;
import structures.DynamicArray;
import structures.IntList;
import structures.MinHeap;
import structures.MyLinkedList;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Random;

/** Runs workloads W1-W4 and writes reproducible results to results/results.csv. */
public final class BenchmarkRunner {
    private static final int[] SIZES = {100, 1_000, 10_000, 100_000};
    private static final int W1_GETS = 10_000;
    private static final int W2_QUERIES = 1_000;
    private static final int W3_OPERATIONS = 1_000;
    private static final int MEASURED_RUNS = 5;

    private static volatile long blackHole;

    private BenchmarkRunner() {
    }

    public static void main(String[] args) throws IOException {
        Path output = Path.of("results", "results.csv");
        Files.createDirectories(output.getParent());

        try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(output))) {
            writer.println("workload,variant,structure,n,time_ms,steps,moves,comparisons");

            for (int n : SIZES) {
                int[] data = generateData(n);
                int[] indices = generateIndices(n, W1_GETS);
                int[] searchQueries = generateSearchQueries(data);

                write(writer, measureW1("DynamicArray", n, data, indices));
                write(writer, measureW1("MyLinkedList", n, data, indices));

                write(writer, measureW2("DynamicArray", n, data, searchQueries));
                write(writer, measureW2("MyLinkedList", n, data, searchQueries));

                write(writer, measureW3("DynamicArray", "head", n, data));
                write(writer, measureW3("MyLinkedList", "head", n, data));
                write(writer, measureW3("DynamicArray", "middle", n, data));
                write(writer, measureW3("MyLinkedList", "middle", n, data));

                write(writer, measureW4(n, data));
                writer.flush();
                System.out.println("Completed n=" + n);
            }
        }

        System.out.println("Benchmark complete: " + output.toAbsolutePath());
        System.out.println("Black-hole checksum: " + blackHole);
    }

    private static BenchmarkResult measureW1(String structure, int n, int[] data, int[] indices) {
        runW1Once(structure, data, indices); // warm-up run, discarded
        CaseResult[] runs = new CaseResult[MEASURED_RUNS];
        for (int i = 0; i < MEASURED_RUNS; i++) {
            runs[i] = runW1Once(structure, data, indices);
        }
        return combine("W1", "-", structure, n, runs);
    }

    private static CaseResult runW1Once(String structure, int[] data, int[] indices) {
        IntList list = newList(structure);
        fill(list, data);
        list.getMetrics().reset();

        long checksum = 0;
        long start = System.nanoTime();
        for (int index : indices) {
            checksum += list.get(index);
        }
        long elapsed = System.nanoTime() - start;
        blackHole ^= checksum;

        return caseResult(elapsed, list.getMetrics());
    }

    private static BenchmarkResult measureW2(String structure, int n, int[] data, int[] queries) {
        runW2Once(structure, data, queries); // warm-up run, discarded
        CaseResult[] runs = new CaseResult[MEASURED_RUNS];
        for (int i = 0; i < MEASURED_RUNS; i++) {
            runs[i] = runW2Once(structure, data, queries);
        }
        return combine("W2", "-", structure, n, runs);
    }

    private static CaseResult runW2Once(String structure, int[] data, int[] queries) {
        IntList list = newList(structure);
        fill(list, data);
        list.getMetrics().reset();

        int found = 0;
        long start = System.nanoTime();
        for (int query : queries) {
            if (list.contains(query)) {
                found++;
            }
        }
        long elapsed = System.nanoTime() - start;
        blackHole ^= found;

        if (found != W2_QUERIES / 2) {
            throw new IllegalStateException("W2 must contain exactly half present queries, got " + found);
        }
        return caseResult(elapsed, list.getMetrics());
    }

    private static BenchmarkResult measureW3(String structure, String variant, int n, int[] data) {
        runW3Once(structure, variant, n, data); // warm-up run, discarded
        CaseResult[] runs = new CaseResult[MEASURED_RUNS];
        for (int i = 0; i < MEASURED_RUNS; i++) {
            runs[i] = runW3Once(structure, variant, n, data);
        }
        return combine("W3", variant, structure, n, runs);
    }

    private static CaseResult runW3Once(String structure, String variant, int n, int[] data) {
        IntList list = newList(structure);
        fill(list, data);
        list.getMetrics().reset();

        int index = variant.equals("head") ? 0 : n / 2;
        long start = System.nanoTime();
        for (int i = 0; i < W3_OPERATIONS; i++) {
            list.add(index, -1_000_000 - i);
        }
        for (int i = 0; i < W3_OPERATIONS; i++) {
            list.remove(index);
        }
        long elapsed = System.nanoTime() - start;

        if (list.size() != n) {
            throw new IllegalStateException("W3 size mismatch");
        }
        blackHole ^= list.size();
        return caseResult(elapsed, list.getMetrics());
    }

    private static BenchmarkResult measureW4(int n, int[] data) {
        runW4Once(data); // warm-up run, discarded
        CaseResult[] runs = new CaseResult[MEASURED_RUNS];
        for (int i = 0; i < MEASURED_RUNS; i++) {
            runs[i] = runW4Once(data);
        }
        return combine("W4", "-", "MinHeap", n, runs);
    }

    private static CaseResult runW4Once(int[] data) {
        MinHeap heap = new MinHeap();
        heap.getMetrics().reset();

        long start = System.nanoTime();
        for (int value : data) {
            heap.insert(value);
        }

        int previous = Integer.MIN_VALUE;
        long checksum = 0;
        while (heap.size() > 0) {
            int current = heap.extractMin();
            if (current < previous) {
                throw new IllegalStateException("Heap output is not non-decreasing");
            }
            previous = current;
            checksum += current;
        }
        long elapsed = System.nanoTime() - start;
        blackHole ^= checksum;

        return caseResult(elapsed, heap.getMetrics());
    }

    private static IntList newList(String structure) {
        if (structure.equals("DynamicArray")) {
            return new DynamicArray();
        }
        if (structure.equals("MyLinkedList")) {
            return new MyLinkedList();
        }
        throw new IllegalArgumentException("Unknown structure: " + structure);
    }

    private static void fill(IntList list, int[] data) {
        for (int value : data) {
            list.add(value);
        }
    }

    private static int[] generateData(int n) {
        Random random = new Random(42);
        int[] data = new int[n];
        for (int i = 0; i < n; i++) {
            data[i] = random.nextInt();
        }
        return data;
    }

    private static int[] generateIndices(int n, int count) {
        Random random = new Random(43);
        int[] indices = new int[count];
        for (int i = 0; i < count; i++) {
            indices[i] = random.nextInt(n);
        }
        return indices;
    }

    private static int[] generateSearchQueries(int[] data) {
        int[] queries = new int[W2_QUERIES];
        Random random = new Random(44);
        int half = W2_QUERIES / 2;

        for (int i = 0; i < half; i++) {
            queries[i] = data[random.nextInt(data.length)];
        }

        for (int i = half; i < W2_QUERIES; i++) {
            int candidate = 0x70000000 + i;
            while (containsRaw(data, candidate)) {
                candidate++;
            }
            queries[i] = candidate;
        }
        return queries;
    }

    private static boolean containsRaw(int[] data, int x) {
        for (int value : data) {
            if (value == x) {
                return true;
            }
        }
        return false;
    }

    private static CaseResult caseResult(long elapsedNanos, OperationMetrics metrics) {
        return new CaseResult(
                elapsedNanos / 1_000_000.0,
                metrics.getSteps(),
                metrics.getMoves(),
                metrics.getComparisons()
        );
    }

    private static BenchmarkResult combine(
            String workload,
            String variant,
            String structure,
            int n,
            CaseResult[] runs
    ) {
        double[] times = new double[runs.length];
        for (int i = 0; i < runs.length; i++) {
            times[i] = runs[i].timeMs;
        }
        Arrays.sort(times);
        double median = times[times.length / 2];

        // Operation counts are deterministic for these fixed inputs, so one measured run is sufficient.
        CaseResult counts = runs[0];
        return new BenchmarkResult(
                workload,
                variant,
                structure,
                n,
                median,
                counts.steps,
                counts.moves,
                counts.comparisons
        );
    }

    private static void write(PrintWriter writer, BenchmarkResult result) {
        writer.printf(
                "%s,%s,%s,%d,%.6f,%d,%d,%d%n",
                result.workload,
                result.variant,
                result.structure,
                result.n,
                result.timeMs,
                result.steps,
                result.moves,
                result.comparisons
        );
    }

    private static final class CaseResult {
        private final double timeMs;
        private final long steps;
        private final long moves;
        private final long comparisons;

        private CaseResult(double timeMs, long steps, long moves, long comparisons) {
            this.timeMs = timeMs;
            this.steps = steps;
            this.moves = moves;
            this.comparisons = comparisons;
        }
    }

    private static final class BenchmarkResult {
        private final String workload;
        private final String variant;
        private final String structure;
        private final int n;
        private final double timeMs;
        private final long steps;
        private final long moves;
        private final long comparisons;

        private BenchmarkResult(
                String workload,
                String variant,
                String structure,
                int n,
                double timeMs,
                long steps,
                long moves,
                long comparisons
        ) {
            this.workload = workload;
            this.variant = variant;
            this.structure = structure;
            this.n = n;
            this.timeMs = timeMs;
            this.steps = steps;
            this.moves = moves;
            this.comparisons = comparisons;
        }
    }
}

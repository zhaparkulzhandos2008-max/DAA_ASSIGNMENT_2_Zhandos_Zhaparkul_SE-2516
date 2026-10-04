# DAA Assignment 2 — Data Structures

Implementation of `DynamicArray`, `MyLinkedList`, and `MinHeap` for the **Design and Analysis of Algorithms** Assignment 2.

## Requirements

- JDK 17 or newer
- Maven 3.9+ (for build and JUnit 5 tests)
- Python 3 + Matplotlib only if you want to regenerate the provided plots

## Project layout

```text
src/main/java/
  structures/
    IntList.java
    DynamicArray.java
    MyLinkedList.java
    MinHeap.java
  metrics/
    OperationMetrics.java
  benchmark/
    BenchmarkRunner.java
src/test/java/structures/
  DynamicArrayTest.java
  MyLinkedListTest.java
  MinHeapTest.java
results/
  results.csv
  plots/
tools/
  plot_results.py
REPORT.md
pom.xml
```

## Build and run tests

```bash
mvn clean test
```

JUnit 5 tests cover empty structures, one element, duplicates, first/last indexes, invalid indexes, random-data comparison with Java reference collections, heap property after every insertion/extraction, and sorted heap output.

## Run benchmark

```bash
mvn -q -DskipTests compile exec:java
```

This runs W1-W4 for `n = 100, 1_000, 10_000, 100_000`, uses `new Random(42)` for input data, discards one warm-up run per case, measures five runs, saves the median, and writes:

```text
results/results.csv
```

CSV columns:

```text
workload,variant,structure,n,time_ms,steps,moves,comparisons
```

## Regenerate plots

The repository already contains the required PNG plots. To regenerate them after a new benchmark run:

```bash
python -m pip install matplotlib
python tools/plot_results.py
```

Plots are written to `results/plots/`.

## Metrics definition

- `steps`: one array-cell read or one move to the next linked-list node
- `moves`: one shifted/moved array element or one link/pointer update
- `comparisons`: one comparison of two stored values

Counters are updated inside the data-structure methods rather than estimated after the operation.

## Git workflow required by the assignment

The intended branches are:

```text
main
feature/array
feature/list
feature/heap
feature/metrics
```

The prepared repository history uses small feature commits and is tagged `v1.0`. After adding your own GitHub repository as `origin`, push the required branches and tag:

```bash
git remote add origin YOUR_GITHUB_REPOSITORY_URL
git push -u origin main
git push origin feature/array feature/list feature/heap feature/metrics
git push origin v1.0
```

Before submission, open the GitHub page and confirm that **main** contains the working project and tag **v1.0** exists.

## Submission

1. Rename the final archive to `DAA_Assignment2_name_surname_group.zip` using your real details.
2. Put the GitHub repository link into Moodle together with the ZIP submission if Moodle provides a text/link field.
3. Be ready to run `mvn clean test` and the benchmark during the defense.
4. Be ready to explain both loop invariants, the operation counters, and why cache locality makes the array faster in several workloads.

See `REPORT.md` for the complexity table, loop-invariant proofs, plots, and discussion.

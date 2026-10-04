package structures;

import org.junit.jupiter.api.Test;

import java.util.PriorityQueue;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class MinHeapTest {

    @Test
    void emptyHeapThrows() {
        MinHeap heap = new MinHeap();
        assertThrows(IllegalStateException.class, heap::peekMin);
        assertThrows(IllegalStateException.class, heap::extractMin);
    }

    @Test
    void oneElementAndDuplicates() {
        MinHeap heap = new MinHeap();
        heap.insert(5);
        assertEquals(5, heap.peekMin());
        heap.insert(5);
        heap.insert(2);
        assertTrue(heap.isHeapPropertyValid());
        assertEquals(2, heap.extractMin());
        assertEquals(5, heap.extractMin());
        assertEquals(5, heap.extractMin());
    }

    @Test
    void heapPropertyHoldsAfterEveryInsertAndExtract() {
        MinHeap heap = new MinHeap();
        Random random = new Random(42);

        for (int i = 0; i < 2_000; i++) {
            heap.insert(random.nextInt());
            assertTrue(heap.isHeapPropertyValid());
        }

        while (heap.size() > 0) {
            heap.extractMin();
            assertTrue(heap.isHeapPropertyValid());
        }
    }

    @Test
    void randomDataMatchesPriorityQueueAndOutputIsSorted() {
        Random random = new Random(42);
        MinHeap actual = new MinHeap();
        PriorityQueue<Integer> expected = new PriorityQueue<>();

        for (int i = 0; i < 5_000; i++) {
            int value = random.nextInt();
            actual.insert(value);
            expected.add(value);
        }

        int previous = Integer.MIN_VALUE;
        while (!expected.isEmpty()) {
            int expectedValue = expected.remove();
            int actualValue = actual.extractMin();
            assertEquals(expectedValue, actualValue);
            assertTrue(actualValue >= previous);
            previous = actualValue;
        }
        assertEquals(0, actual.size());
    }
}

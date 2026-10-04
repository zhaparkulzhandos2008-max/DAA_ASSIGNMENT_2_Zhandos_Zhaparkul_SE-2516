package structures;

import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class MyLinkedListTest {

    @Test
    void emptyAndInvalidIndexes() {
        MyLinkedList list = new MyLinkedList();
        assertEquals(0, list.size());
        assertFalse(list.contains(10));
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(0));
        assertThrows(IndexOutOfBoundsException.class, () -> list.remove(0));
        assertThrows(IndexOutOfBoundsException.class, () -> list.add(-1, 5));
        assertThrows(IndexOutOfBoundsException.class, () -> list.add(1, 5));
    }

    @Test
    void oneElementDuplicatesFirstAndLastIndex() {
        MyLinkedList list = new MyLinkedList();
        list.add(7);
        assertEquals(7, list.get(0));

        list.add(0, 7);
        list.add(9);
        assertEquals(3, list.size());
        assertEquals(7, list.get(0));
        assertEquals(9, list.get(list.size() - 1));
        assertTrue(list.contains(7));

        assertEquals(7, list.remove(0));
        assertEquals(9, list.remove(list.size() - 1));
        assertEquals(7, list.get(0));
    }

    @Test
    void matchesLinkedListOnRandomData() {
        Random random = new Random(42);
        MyLinkedList actual = new MyLinkedList();
        LinkedList<Integer> expected = new LinkedList<>();

        for (int i = 0; i < 2_000; i++) {
            int value = random.nextInt(100);
            if (expected.isEmpty() || random.nextBoolean()) {
                int index = random.nextInt(expected.size() + 1);
                actual.add(index, value);
                expected.add(index, value);
            } else {
                int index = random.nextInt(expected.size());
                assertEquals(expected.remove(index).intValue(), actual.remove(index));
            }

            assertEquals(expected.size(), actual.size());
            for (int j = 0; j < expected.size(); j++) {
                assertEquals(expected.get(j).intValue(), actual.get(j));
            }
        }
    }
}

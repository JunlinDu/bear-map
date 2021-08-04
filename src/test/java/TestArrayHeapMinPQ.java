import utils.dataStructures.priorityQueue.ArrayHeapMinPQ;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.TreeMap;

import static org.junit.Assert.assertEquals;

/**
 * Created by Junlin Du, Apr 4, 2021
 *
 * fancyHeapDrawingHelper() and printFancyHeapDrawing() by Josh Hug
 *
 * Sanity checks for ArrayHeapMinPQ, mainly focuses on sink()
 * */
public class TestArrayHeapMinPQ {
    private static ArrayHeapMinPQ<String> arrayHeapMinPQ;

    @Before
    public void setUp() {
        arrayHeapMinPQ = new ArrayHeapMinPQ<String>();
    }

    @Test
    public void testAdd() {
        addingLetterItem();
        TreeMap<String, Integer> actual = arrayHeapMinPQ.getKeySet();
        TreeMap<String, Integer> expected = new TreeMap<>();
        expected.put("a", 1);
        expected.put("b", 2);
        expected.put("c", 3);
        expected.put("d", 5);
        expected.put("e", 7);
        expected.put("f", 4);
        expected.put("g", 6);
        expected.put("h", 8);
        assertEquals(expected,actual);
    }

    @Test
    public void testRemoveSmallest() {
        addingNumberItem();
        ArrayList<String> actual = new ArrayList<>();

        for (int i = 0; i < 8; i++) {
            actual.add(arrayHeapMinPQ.removeSmallest());
        }

        System.out.println(actual);
        ArrayList<String> expected = new ArrayList<>();
        for (int i = 1; i <=8; i++) {
            expected.add(String.valueOf(i));
        }
        assertEquals(expected, actual);

    }

    @Test
    public void testRemoveSmallestTwo() {
        for (int i = 1; i <=10; i++) {
            arrayHeapMinPQ.add(String.valueOf(i), i);
        }
        ArrayList<String> actual = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            actual.add(arrayHeapMinPQ.removeSmallest());

        }
        System.out.println(actual);

        ArrayList<String> expected = new ArrayList<>();
        for (int i = 1; i <=10; i++) {
            expected.add(String.valueOf(i));
        }
        assertEquals(expected, actual);
    }

    private void addingLetterItem() {
        arrayHeapMinPQ.add("h", 8);
        arrayHeapMinPQ.add("a", 1);
        arrayHeapMinPQ.add("c", 3);
        arrayHeapMinPQ.add("b", 2);
        arrayHeapMinPQ.add("d", 4);
        arrayHeapMinPQ.add("g", 7);
        arrayHeapMinPQ.add("e", 5);
        arrayHeapMinPQ.add("f", 6);
    }

    private void addingNumberItem() {
        arrayHeapMinPQ.add("8", 8);
        arrayHeapMinPQ.add("1", 1);
        arrayHeapMinPQ.add("3", 3);
        arrayHeapMinPQ.add("2", 2);
        arrayHeapMinPQ.add("4", 4);
        arrayHeapMinPQ.add("7", 7);
        arrayHeapMinPQ.add("5", 5);
        arrayHeapMinPQ.add("6", 6);
    }

    /** Prints out a drawing of the given array of Objects assuming it
     *  is a heap starting at index 1. You're welcome to copy and paste
     *  code from this method into your code, just make sure to cite
     *  this with the @source tag. */
    public static void printFancyHeapDrawing(Object[] items) {
        String drawing = fancyHeapDrawingHelper(items, 1, "");
        System.out.println(drawing);
    }

    /* Recursive helper method for toString. */
    private static String fancyHeapDrawingHelper(Object[] items, int index, String soFar) {
        if (index >= items.length || items[index] == null) {
            return "";
        } else {
            String toReturn = "";
            int rightIndex = 2 * index + 1;
            toReturn += fancyHeapDrawingHelper(items, rightIndex, "        " + soFar);
            if (rightIndex < items.length && items[rightIndex] != null) {
                toReturn += soFar + "    /";
            }
            toReturn += "\n" + soFar + items[index] + "\n";
            int leftIndex = 2 * index;
            if (leftIndex < items.length && items[leftIndex] != null) {
                toReturn += soFar + "    \\";
            }
            toReturn += fancyHeapDrawingHelper(items, leftIndex, "        " + soFar);
            return toReturn;
        }
    }

}

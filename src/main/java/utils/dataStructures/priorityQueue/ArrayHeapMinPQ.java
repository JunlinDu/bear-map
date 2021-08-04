package utils.dataStructures.priorityQueue;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.TreeMap;

/* Implementation of the Priority Queue, implemented on Jul 24, 2020
* @author Junlin Du
* */
public class ArrayHeapMinPQ<I> implements ExtrinsicMinPQ<I> {

    private class Node implements Comparable<Node>{

        /* The item being Stored in the object */
        I item;

        /* The Priority Value of the object */
        double priority;

        Node(I item, double priority) {
            this.item = item;
            this.priority = priority;
        }

        /* Item Getter */
        public I getItem() {
            return item;
        }

        /* Priority Value Getter */
        public double getPriority() {
            return priority;
        }

        /* Priority Value Setter */
        public void setPriority(double priority) {
            this.priority = priority;
        }

        /**
         * Compares this Node with the specified Node for order.  Returns a
         * negative integer, zero, or a positive integer as this Node is less
         * than, equal to, or greater than the specified Node. */
        @Override
        public int compareTo(Node o) {
            int a = 0;
            if (this.getPriority() > o.getPriority()) a++;
            if (this.getPriority() < o.getPriority()) a--;
            return a;
        }
    }

    /* The Arraylist that represents the Min Heap */
    private ArrayList<Node> minHeap;

    /* The TreeMap that maps a Node item to the position in the heap
    * serves for constant time lookup */
    private TreeMap<I, Integer> keySet;

    public ArrayHeapMinPQ() {
        minHeap = new ArrayList<>();
        minHeap.add(new Node(null, 0.0));
        keySet = new TreeMap<>();
    }

    /* Adding keySet getter to resolve access issue from test class
    * TODO to be deleted later */
    public TreeMap<I, Integer> getKeySet() {
        return keySet;
    }

    /* return the index of the parent node of the specified node index */
    private int getParent(int index) {
        return index / 2;
    }

    /* return the index of the left child of the specified node index */
    private int leftChild(int index) {
        return index * 2;
    }

    /* return the index of the right child of the specified node index */
    private int rightChild(int index) {
        return index *2 + 1;
    }

    /* return true if the first argument is less than the second argument, false otherwise*/
    private boolean lessThan(int i, int j) {
        return this.minHeap.get(i).compareTo(this.minHeap.get(j)) < 0;
    }

    /* Swap the child node at the provided index with its parentNode */
    private void swap(int childIndex, int parentIndex) {
        Node temp = this.minHeap.get(parentIndex);
        this.minHeap.set(parentIndex, this.minHeap.get(childIndex));
        this.minHeap.set(childIndex, temp);
        this.keySet.put(this.minHeap.get(childIndex).item, childIndex);
        this.keySet.put(this.minHeap.get(parentIndex).item, parentIndex);
    }

    /* Promote a node up the hierarchy to the position that it belongs to */
    private void swim(int index) {
        int parentIndex = getParent(index);
        if (index != 0 && lessThan(index, parentIndex)) {
            swap(index, parentIndex);
            swim(parentIndex);
        }
        return;
    }

    /* Demote a node down the hierarchy to the position that it belongs to */
    private void sink(int index) {
        if (size() == 2 && !lessThan(index, leftChild(index)))
            swap(leftChild(index), index);

        if (leftChild(index) > size() || rightChild(index) > size()) return;

        if (lessThan(leftChild(index), index) || lessThan(rightChild(index), index)) {
            if (lessThan(leftChild(index), rightChild(index))) {
                swap(leftChild(index), index);
                sink(leftChild(index));
            } else {
                swap(rightChild(index), index);
                sink(rightChild(index));
            }
        }
    }

    /* Add a node */
    @Override
    public void add(I item, double priority) {
        if (contains(item))
            throw new IllegalArgumentException("Duplicate items are not allowed to be added");

        /* Adding a new node to end of the heap */
        this.minHeap.add(new Node(item, priority));

        /* Adding the item reference to the end of the key set */
        this.keySet.put(item, size());

        /* Promote the node to where it supposed to belong in the heap */
        swim(this.minHeap.size() - 1);
    }

    /* Checks to see if the PQ contains a given item, return true
     *  if it does, false otherwise */
    @Override
    public boolean contains(I item) {
        if (item == null) throw new IllegalArgumentException();
        return keySet.containsKey(item);
    }

    /* returns the the smallest item in the PQ */
    @Override
    public I getSmallest() {
        if (this.minHeap.size() == 1) {
            throw new NoSuchElementException();
        }
        return this.minHeap.get(1).getItem();
    }

    /* remove the smallest item from the PQ */
    @Override
    public I removeSmallest() {
        if (this.minHeap.size() == 1) throw new NoSuchElementException();

        I smallest = this.getSmallest();

        /* Remove item with lowest priority from key set */
        this.keySet.remove(smallest);
        /* Change the index of the last item in the heap to 1 */
        this.keySet.put(this.minHeap.get(size()).item,1);

        /* Move the last item to the top of the heap  */
        this.minHeap.set(1, this.minHeap.get(size()));
        /* Remove item with lowest priority from heap */
        this.minHeap.remove(size());

        /* demote the node to where it supposed to be */
        sink(1);

        return smallest;
    }

    /* return the size of the PQ */
    @Override
    public int size() {
        return this.minHeap.size() - 1;
    }

    /* Change the priority of an Item */
    @Override
    public void changePriority(I item, double priority) {
        if (!keySet.containsKey(item)) throw new NoSuchElementException();

        /* getting the node/item index in the heap */
        int index = this.keySet.get(item);

        /* getting the actual node/item */
        Node node = this.minHeap.get(index);

        /* setting the priority of the node */
        node.setPriority(priority);

        /* if the priority value of the current node/item is less than its parent node/item */
        if (lessThan(index, getParent(index))) {
            swim(index);
        } else {
            if (size() != 2) sink(index);
        }

    }

    /* clear the min Priority Queue */
    public void clearMinPQ() {
        this.minHeap.subList(1, minHeap.size()).clear();
        this.keySet.clear();
    }

}


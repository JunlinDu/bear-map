package utils.dataStructures.trie;

import java.util.*;

/* This class represents the Retrieval Tree data structure
* @author Junlin Du, implemented Jul 23, 2020
* */
public class Trie implements TrieSet {

    private static class Node {

        /* The letter that the node is representing */
        Character letter;

        /* A boolean value that determines whether the
        *  node is the Last Character of each key */
        boolean endNode;

        /* the map to children nodes*/
        TreeMap<Character, Node> mapToChildren;

        Node(Character letter, boolean endNode) {
            this.letter = letter;
            this.endNode = endNode;
            this.mapToChildren = new TreeMap<>();
        }

        /* map getter */
        public TreeMap<Character, Node> getMapToChildren() {
            return mapToChildren;
        }

        public void setEndNode(boolean endNode) {
            this.endNode = endNode;
        }

        public Character getLetter() {
            return letter;
        }
    }

    /* the sentinel node that is the first node of the trie.*/
    private Node sentinel;

    public Trie() {
        sentinel = new Node(null, false);
    }

    /* Clears all items out of Trie */
    @Override
    public void clear() {
        sentinel = new Node(null, false);
    }

    /* find the last node in the trie that correspond to the key */
    private Node findNode(String key) {
        Node curr = sentinel;
        for (int i = 0; i < key.length(); i++) {
            char a = key.charAt(i);
            if (!curr.mapToChildren.containsKey(a)) return null;
            curr = curr.mapToChildren.get(a);
        }
        return curr;
    }

    /* Returns true if the Trie contains KEY, false otherwise */
    @Override
    public boolean contains(String key) {
        if (key == null || key.length() < 1)
            throw new IllegalArgumentException();

        Node node = findNode(key);
        return node != null && node.endNode;
    }

    /** Inserts string KEY into Trie */
    @Override
    public void add(String key) {
        if (key == null || key.length() < 1) return;

        Node curr = sentinel;
        for (int i = 0, n = key.length(); i < n; i++) {
            char c = key.charAt(i);
            if (!curr.mapToChildren.containsKey(c)) {
                curr.mapToChildren.put(c, new Node(c, false));
            }
            curr = curr.mapToChildren.get(c);
        }
        curr.endNode = true;
    }

    /* Returns a list of all words that start with PREFIX */
    @Override
    public List<String> keysWithPrefix(String prefix) {
        List<String> keys = new ArrayList<>();
        Node curr = findNode(prefix);
        return keysWithPrefixRecursive(curr, keys, prefix.substring(0, prefix.length() - 1));
    }

    private List<String> keysWithPrefixRecursive (Node node, List<String> list, String prefix) {
        if (node == null) return null;
        if (node.mapToChildren.isEmpty() && node.endNode) {
            list.add(prefix + node.getLetter());
            return list;
        } else if (node.endNode) {
            list.add(prefix + node.getLetter());
        }

        for (Map.Entry<Character, Node> entry : node.mapToChildren.entrySet()) {
            list = keysWithPrefixRecursive(entry.getValue(), list, prefix + node.getLetter());
        }
        
        return list;
    }

    /** Returns the longest prefix of KEY that exists in the Trie */
    @Override
    public String longestPrefixOf(String key) {
        throw new UnsupportedOperationException();
    }
}

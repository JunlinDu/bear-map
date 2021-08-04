package utils.dataStructures.trie;

import java.util.List;

public interface TrieSet {

    /** Clears all items out of utils.dataStructures.Trie */
    void clear();

    /** Returns true if the utils.dataStructures.Trie contains KEY, false otherwise */
    boolean contains(String key);

    /** Inserts string KEY into utils.dataStructures.Trie */
    void add(String key);

    /** Returns a list of all words that start with PREFIX */
    List<String> keysWithPrefix(String prefix);

    /** Returns the longest prefix of KEY that exists in the utils.dataStructures.Trie
     */
    String longestPrefixOf(String key);

}

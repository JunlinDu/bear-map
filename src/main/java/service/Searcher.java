package service;

import utils.dataStructures.trie.Trie;
import utils.dataStructures.trie.TrieSet;

import java.util.*;

public class Searcher {
    // hashmap for name - node lookup
    private Map<String, ArrayList<Long>> namesDict = new HashMap<>();
    // map for lowercase - original mapping
    private Map<String, String> loToOrigin = new HashMap<>();
    // Retrieval tree for storing node name
    private TrieSet nodeNamesTrie = new Trie();

    /**
     * map node names (string) to node id(s)
     * @param name name of the node
     * @param id node id*/
    public void addToNamesDict(String name, String id) {
        Long nodeId = Long.parseLong(id);
        if (!this.namesDict.containsKey(name)) {
            ArrayList<Long> nodes = new ArrayList<>();
            nodes.add(nodeId);
            namesDict.put(name, nodes);
        }  else {
            this.namesDict.get(name).add(nodeId);
        }
    }

    /**
     * adding names to the trie set
     * @param name name of a node
     * */
    public void addToTrie(String name) {
        this.nodeNamesTrie.add(name);
    }

    /**
     * adding lower-cased version to original name mapping
     * @param original original string*/
    public void addLowerToOriginalMapping(String original) {
        this.loToOrigin.put(original.toLowerCase(), original);
    }

    /**
     * getting original cased node names by providing prefix
     * @param prefix the string prefix to match
     * @return A list of node names matched by provided prefix */
    public List<String> getKeysByPrefix(String prefix) {
        ArrayList<String> originalNameList = new ArrayList<>();

        // Search in the retrieval tree for matches
        ArrayList<String> lowercaseNameList = (ArrayList<String>) this.nodeNamesTrie.keysWithPrefix(prefix);

        if(lowercaseNameList == null) return originalNameList;

        for (String lrStr : lowercaseNameList) {
            originalNameList.add(this.loToOrigin.get(lrStr));
        }
        return originalNameList;
    }

    public List<Map<String, Object>> getLocations(String locationName) {
        // TODO To Be Implemented
        return new LinkedList<>();
    }
}

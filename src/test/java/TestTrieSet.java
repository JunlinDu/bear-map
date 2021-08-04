import utils.dataStructures.trie.Trie;
import utils.dataStructures.trie.TrieSet;
import service.GraphDB;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class TestTrieSet {
    private static final String OSM_DB_PATH = "../library-sp18/data/berkeley-2018.osm.xml";
    private static final String WORDS_LIST_PATH = "../library-sp18/data/words.txt";
    private static final String WORDS_LIST_PATH_SMALL = "../library-sp18/data/word1.txt";

    GraphDB graph;

    TrieSet wordTrieSet = new Trie();

    @Before
    public void setUp() throws Exception {
        graph = new GraphDB(OSM_DB_PATH);
    }

    public void buildTrieFromText(String filenName) {
        String str = null;
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(filenName));

            while ((str = br.readLine()) != null) {
                wordTrieSet.add(str.toLowerCase());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testPrefixSmall() {
        buildTrieFromText(WORDS_LIST_PATH_SMALL);
        assertEquals(2, wordTrieSet.keysWithPrefix("ca").size());
    }

    @Test
    public void testPrefixLarge() {
        buildTrieFromText(WORDS_LIST_PATH);
        assertEquals(13, wordTrieSet.keysWithPrefix("aa").size());
    }

    @Test
    public void testGetKeysByPrefix() {
        System.out.println(graph.getSearcher().getKeysByPrefix("univer"));
        assertEquals(25, graph.getSearcher().getKeysByPrefix("univer").size());
    }

    @Test
    public void testNoMatch() {
        assertEquals(0, graph.getSearcher().getKeysByPrefix("zzzzz").size());
    }

}

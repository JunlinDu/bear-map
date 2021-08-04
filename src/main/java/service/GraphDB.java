package service;

import utils.GraphBuildingHandler;
import utils.dataStructures.trie.Trie;
import utils.dataStructures.trie.TrieSet;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.util.*;

/**
 * Graph for storing all of the intersection (vertex) and road (edge) information.
 * @author Alan Yao, Josh Hug, Junlin Du
 */
public class GraphDB {
    // An adjacency list(map) that represents the graph
    private Map<Long, ArrayList<Long>> graph = new HashMap<>();
    // HashMap, serves for fast lookup operation, that maps node ids to corresponding nodes
    private Map<Long, Node> nodesDict = new HashMap<>();
    // way
    private Map<Long, Way> waysDict = new HashMap<>();

    private Searcher searcher = new Searcher();

    /**
     * Inner class that represents a node on the map.
     * Nodes is one of the elements in the OSM XML that represents a single point
     * defined by latitude, longitude and an ID.
     * <a href="https://wiki.openstreetmap.org/wiki/Node">Documentation</a>
     * */
    public static class Node {
        private long id;
        private double lon;
        private double lat;

        /* The way to which the node belongs */
        private Set<Long> wayIds = new HashSet<>();

        public Node(String id, String lon, String lat) {
            this.id = Long.parseLong(id);
            this.lon = Double.parseDouble(lon);
            this.lat = Double.parseDouble(lat);
        }

        public long getId() {
            return id;
        }

        public double getLon() {
            return lon;
        }

        public double getLat() {
            return lat;
        }

        private void addWayId(String wayId) {
            this.wayIds.add(Long.parseLong(wayId));
        }
    }

    /**
     * Inner class representing a way
     * */
    public static class Way {
        private long id;
        private String name;
        private ArrayList<Long> nodes = new ArrayList<>();

        public Way(String id, String name, ArrayList<String> nodes) {
            this.id = Long.parseLong(id);
            this.name = name;
            for (String nd : nodes) {
                this.nodes.add(Long.parseLong(nd));
            }
        }

        public long getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public ArrayList<Long> getNodes() {
            return nodes;
        }
    }

    /**
     * Example constructor shows how to create and start an XML parser.
     * You do not need to modify this constructor, but you're welcome to do so.
     * @param dbPath Path to the XML file to be parsed.
     */
    public GraphDB(String dbPath) {
        try {
            File inputFile = new File(dbPath);
            FileInputStream inputStream = new FileInputStream(inputFile);
            // GZIPInputStream stream = new GZIPInputStream(inputStream);

            // Setting up the parser
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();


            GraphBuildingHandler gbh = new GraphBuildingHandler(this);
            saxParser.parse(inputStream, gbh);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
        clean();
    }

    public Searcher getSearcher() {
        return searcher;
    }

    /**
     * Helper to process strings into their "cleaned" form, ignoring punctuation and capitalization.
     * @param s Input string.
     * @return Cleaned string.
     */
    static String cleanString(String s) {
        return s.replaceAll("[^a-zA-Z ]", "").toLowerCase();
    }

    /**
     *  Remove nodes with no connections from the graph.
     *  While this does not guarantee that any two nodes in the remaining graph are connected,
     *  we can reasonably assume this since typically roads are connected.
     */
    private void clean() {
        Iterator<Map.Entry<Long, Node>> it = this.nodesDict.entrySet().iterator();
        while (it.hasNext()) {
            if (!this.graph.containsKey(it.next().getKey())) {
                it.remove();
            }
        }
    }

    /**
     * Returns an iterable of all vertex IDs in the graph.
     * @return An iterable of id's of all vertices in the graph.
     */
    public Iterable<Long> vertices() {
        return new ArrayList<Long>(nodesDict.keySet());
    }

    /**
     * Returns ids of all vertices adjacent to v.
     * @param v The id of the vertex we are looking adjacent to.
     * @return An iterable of the ids of the neighbors of v.
     */
    public Iterable<Long> adjacent(long v) {
        return new ArrayList<>(graph.get(v));
    }

    /**
     * Returns the great-circle distance between vertices v and w in miles.
     * Assumes the lon/lat methods are implemented properly.
     * <a href="https://www.movable-type.co.uk/scripts/latlong.html">Source</a>.
     * @param v The id of the first vertex.
     * @param w The id of the second vertex.
     * @return The great-circle distance between the two locations from the graph.
     */
    public double distance(long v, long w) {
        return distance(lon(v), lat(v), lon(w), lat(w));
    }

    public static double distance(double lonV, double latV, double lonW, double latW) {
        double phi1 = Math.toRadians(latV);
        double phi2 = Math.toRadians(latW);
        double dphi = Math.toRadians(latW - latV);
        double dlambda = Math.toRadians(lonW - lonV);

        double a = Math.sin(dphi / 2.0) * Math.sin(dphi / 2.0);
        a += Math.cos(phi1) * Math.cos(phi2) * Math.sin(dlambda / 2.0) * Math.sin(dlambda / 2.0);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return 3963 * c;
    }

    /**
     * Returns the initial bearing (angle) between vertices v and w in degrees.
     * The initial bearing is the angle that, if followed in a straight line
     * along a great-circle arc from the starting point, would take you to the
     * end point.
     * Assumes the lon/lat methods are implemented properly.
     * <a href="https://www.movable-type.co.uk/scripts/latlong.html">Source</a>.
     * @param v The id of the first vertex.
     * @param w The id of the second vertex.
     * @return The initial bearing between the vertices.
     */
    public double bearing(long v, long w) {
        return bearing(lon(v), lat(v), lon(w), lat(w));
    }

    public static double bearing(double lonV, double latV, double lonW, double latW) {
        double phi1 = Math.toRadians(latV);
        double phi2 = Math.toRadians(latW);
        double lambda1 = Math.toRadians(lonV);
        double lambda2 = Math.toRadians(lonW);

        double y = Math.sin(lambda2 - lambda1) * Math.cos(phi2);
        double x = Math.cos(phi1) * Math.sin(phi2);
        x -= Math.sin(phi1) * Math.cos(phi2) * Math.cos(lambda2 - lambda1);
        return Math.toDegrees(Math.atan2(y, x));
    }

    /**
     * Returns the vertex closest to the given longitude and latitude.
     * @param lon The target longitude.
     * @param lat The target latitude.
     * @return The id of the node in the graph closest to the target.
     */
    public long closest(double lon, double lat) {
        long node = 0L;
        double dist, closestDist = Double.POSITIVE_INFINITY;

        for (Node nd : this.nodesDict.values()) {
            dist = distance(lon, lat, nd.getLon(), nd.getLat());
            if (dist < closestDist) {
                closestDist = dist;
                node = nd.getId();
            }
        }

        return node;
    }

    /**
     * Gets the longitude of a vertex.
     * @param v The id of the vertex.
     * @return The longitude of the vertex.
     */
    public double lon(long v) {
        if (!nodesDict.containsKey(v)) return 0;
        return this.nodesDict.get(v).getLon();
    }

    /**
     * Gets the latitude of a vertex.
     * @param v The id of the vertex.
     * @return The latitude of the vertex.
     */
    public double lat(long v) {
        if (!nodesDict.containsKey(v)) return 0;
        return this.nodesDict.get(v).getLat();
    }

    /**
     * Adding a Node to the Node Map
     * @param node the Node to be added
     * */
    public void addNode(Node node) {
        this.nodesDict.put(node.getId(), node);
    }

    /**
     * Remove a Node from the Node Map
     * @param id the id of the Node to be removed
     * */
    public void removeNode(long id) {
        this.nodesDict.remove(id);
    }

    /**
     * Adding an edge to the adjacency list
     * @param originNode the id of the Node from which the edge extends
     * @param destNode the id of the Node to which the edge extends
     * */
    public void addAdjacency(String originNode, String destNode) {
        Long originNd = Long.parseLong(originNode);
        Long destNd = Long.parseLong(destNode);
        
        if (!this.graph.containsKey(originNd)) {
            ArrayList<Long> adjacencies = new ArrayList<>();
            adjacencies.add(destNd);
            this.graph.put(originNd, adjacencies);
        } else {
            this.graph.get(originNd).add(destNd);
        }

    }

    /**
     * Setting the way property of a node
     * @param nodeId the Id of the Node
     * @param wayId the Id of the Road
     * */
    public void setNodeToWay(String nodeId, String wayId) {
        nodesDict.get(Long.parseLong(nodeId)).addWayId(wayId);
    }

    /**
     * Adding a way to waysDict
     * @param way a way object
     * */
    public void addWay(Way way) {
        this.waysDict.put(way.id, way);
    }

    /**
     * Find the Id of the way that a node belongs to
     * @param nodeId the Id of the node
     *
     * @return the Id of the way that the node belongs to
     * */
    public Set<Long> getWayIdSetByNode(Long nodeId) {
        return this.nodesDict.get(nodeId).wayIds;
    }

    /**
     * Find the Name of the way that a node belongs to
     * @param nodeId the Id of the node
     *
     * @return the Name of the way that the node belongs to
     * */
    public Set<String> getWayNameListByNode(Long nodeId) {
        Set<String> wayNameList = new HashSet<>();
        Set<Long> wayIdList = this.getWayIdSetByNode(nodeId);
        for (Long id : wayIdList) {
            wayNameList.add(this.waysDict.get(id).name);
        }
        return wayNameList;
    }

    /**
     * check if a way contains a node
     * @param wayId the Id of a way
     * @param nodeId the Id of the Node
     *
     * @return a boolean value that indicates whether the way contains the node */
    public boolean containsNode(Long wayId, Long nodeId) {
        ArrayList<Long> nodes = this.waysDict.get(wayId).nodes;
        for (Long nd : nodes) if (nd.equals(nodeId)) return true;
        return false;
    }


}

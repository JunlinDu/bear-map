package utils;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import service.GraphDB;
import service.Searcher;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 *  Parses OSM XML files using an XML SAX parser. Used to construct the graph of roads for
 *  pathfinding, under some constraints.
 *
 *  @author Alan Yao, Maurice Lee, Junlin Du
 */
public class GraphBuildingHandler extends DefaultHandler {
    /**
     * Only allow for non-service roads; this prevents going on pedestrian streets as much as
     * possible.
     */
    private static final Set<String> ALLOWED_HIGHWAY_TYPES = new HashSet<>(Arrays.asList
            ("motorway", "trunk", "primary", "secondary", "tertiary", "unclassified",
                    "residential", "living_street", "motorway_link", "trunk_link", "primary_link",
                    "secondary_link", "tertiary_link"));
    private String activeState = "";
    private final GraphDB db;
    private String wayId;
    private String wayName;
    private String currentNodeId;
    private ArrayList<String> way = new ArrayList<>();
    private boolean valid = false;

    /**
     * Create a new GraphBuilding.GraphBuildingHandler.
     * @param db The graph to populate with the XML data.
     */
    public GraphBuildingHandler(GraphDB db) {
        this.db = db;
    }

    /**
     * Called at the beginning of an element.
     * @param uri The Namespace URI, or the empty string if the element has no Namespace URI or
     *            if Namespace processing is not being performed.
     * @param localName The local name (without prefix), or the empty string if Namespace
     *                  processing is not being performed.
     * @param qName The qualified name (with prefix), or the empty string if qualified names are
     *              not available. This tells us which element we're looking at.
     * @param attributes The attributes attached to the element. If there are no attributes, it
     *                   shall be an empty Attributes object.
     * @throws SAXException Any SAX exception, possibly wrapping another exception.
     * @see Attributes
     */
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes)
            throws SAXException {
        if (qName.equals("node")) {
            /* A <node .../> is encountered */
            activeState = "node";
            currentNodeId = attributes.getValue("id");

            GraphDB.Node newNode = new GraphDB.Node
                    (currentNodeId, attributes.getValue("lon"), attributes.getValue("lat"));
            db.addNode(newNode);

        } else if (qName.equals("way")) {
            /* A <way> is encountered */
            activeState = "way";
            this.wayId = attributes.getValue("id");
        } else if (activeState.equals("way") && qName.equals("nd")) {
            /* <nd ... /> is encountered as a child element of <way> ... </way> */
            way.add(attributes.getValue("ref"));

        } else if (activeState.equals("way") && qName.equals("tag")) {
            /* <tag ... /> is encountered as a child element of <way> ... </way> */
            String k = attributes.getValue("k");
            String v = attributes.getValue("v");
            if (k.equals("highway")) {
                if (ALLOWED_HIGHWAY_TYPES.contains(v)) valid = true;
            } else if (k.equals("name")) {
                this.wayName = attributes.getValue("v");
            }

        } else if (activeState.equals("node") && qName.equals("tag") && attributes.getValue("k")
                .equals("name")) {
            /* <tag ... /> with k="name" is encountered as a child element of <node> ... </node> . */
            String nodeName = attributes.getValue("v");

            Searcher searcher = db.getSearcher();

            searcher.addLowerToOriginalMapping(nodeName);
            searcher.addToNamesDict(nodeName.toLowerCase(), currentNodeId);
            searcher.addToTrie(nodeName.toLowerCase());
        }
    }

    /**
     * Receive notification of the end of an element.
     * @param uri The Namespace URI, or the empty string if the element has no Namespace URI or
     *            if Namespace processing is not being performed.
     * @param localName The local name (without prefix), or the empty string if Namespace
     *                  processing is not being performed.
     * @param qName The qualified name (with prefix), or the empty string if qualified names are
     *              not available.
     * @throws SAXException  Any SAX exception, possibly wrapping another exception.
     */
    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equals("way")) {
            /* </way> is encountered. */
            if (valid) {
                db.addWay(new GraphDB.Way(wayId, wayName, way));

                for (int i = 0; i < way.size() - 1; i++) {
                    db.addAdjacency(way.get(i), way.get(i + 1));
                    db.addAdjacency(way.get(i + 1), way.get(i));
                    db.setNodeToWay(way.get(i), wayId);
                    if (i == way.size() - 2) db.setNodeToWay(way.get(i + 1), wayId);
                }
            }

            valid = false;
            way.clear();
        }
    }

}

package GraphBuilder;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

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
    private ArrayList<Long> way = new ArrayList<>();
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
            GraphDB.Node newNode = new GraphDB.Node
                    (attributes.getValue("id"), attributes.getValue("lon"), attributes.getValue("lat"));
            db.addNode(newNode);

        } else if (qName.equals("way")) {
            /* A <way> is encountered */
            activeState = "way";

            // For testing purposes
            System.out.println("Beginning a way...");
            // TODO Add way ID later

        } else if (activeState.equals("way") && qName.equals("nd")) {
            /* <nd ... /> is encountered as a child element of <way> ... </way> */
            way.add(Long.parseLong(attributes.getValue("ref")));

        } else if (activeState.equals("way") && qName.equals("tag")) {
            /* <tag ... /> is encountered as a child element of <way> ... </way> */
            String k = attributes.getValue("k");
            String v = attributes.getValue("v");
            if (k.equals("maxspeed")) {
                /* TODO set the max speed of the "current way" here. */

            } else if (k.equals("highway")) {
                if (ALLOWED_HIGHWAY_TYPES.contains(v)) valid = true;

            } else if (k.equals("name")) {
                // TODO To be added later for driving direction purposes.

            }

        } else if (activeState.equals("node") && qName.equals("tag") && attributes.getValue("k")
                .equals("name")) {
            /* <tag ... /> with k="name" is encountered as a child element of <node> ... </node> . */
            /* TODO Create a location. */
            /* Hint: Since we found this <tag...> INSIDE a node, we should probably remember which
            node this tag belongs to. Remember XML is parsed top-to-bottom, so probably it's the
            last node that you looked at (check the first if-case). */
//            System.out.println("Node's name: " + attributes.getValue("v"));
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
                for (int i = 0; i < way.size() - 1; i++) {
                    double weight = db.distance(way.get(i), way.get(i + 1));
                    db.addEdge(way.get(i), way.get(i + 1), weight);
                }
            }

            System.out.println("Finishing a way..."); // Testing Purposes
            valid = false;
            way.clear();
        }
    }

}

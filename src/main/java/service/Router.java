package service;

import utils.dataStructures.priorityQueue.ArrayHeapMinPQ;
import utils.dataStructures.priorityQueue.ExtrinsicMinPQ;


import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class provides a shortestPath method for finding routes between two points
 * on the map.
 */
public class Router {
    /* A Min Priority Queue/Min Heap used for performing path searching */
    private static ExtrinsicMinPQ<Long> fringe = new ArrayHeapMinPQ<Long>();

    /* Map representing the shortest distance from start node to the target node */
    private static Map<Long, Double> distTo = new HashMap<>();

    /* Map representing the edge via which constructs the shortest path from
    *  start node to the target node */
    private static Map<Long, Long> edgeTo = new HashMap<>();

    /**
     * Return a List of longs representing the shortest path from the node
     * closest to a start location and the node closest to the destination
     * location.
     * @param db The graph to use.
     * @param stlon The longitude of the start location.
     * @param stlat The latitude of the start location.
     * @param destlon The longitude of the destination location.
     * @param destlat The latitude of the destination location.
     * @return A list of node id's in the order visited on the shortest path.
     */
    public static List<Long> shortestPath(GraphDB db, double stlon, double stlat,
                                          double destlon, double destlat) {
        clean();
        Long startNode = db.closest(stlon, stlat);
        Long destNode = db.closest(destlon, destlat);

        AStar(db, startNode, destNode);

        return constructPath(destNode);
    }

    /**
     * Performs A* Algorithm (directionally optimized Dijkstra using heuristics) on a graph
     *
     * @param db the database representing the graph
     * @param startNode the node where the path searching starts
     * @param destNode to node where the path searching leads to
     * */
    public static void AStar (GraphDB db, Long startNode, Long destNode) {
        fringe.add(startNode, 0);
        edgeTo.put(startNode, null);
        distTo.put(startNode, 0.0);

        Long currExamNode;
        while (fringe.size() != 0 && !fringe.getSmallest().equals(destNode)) {
            currExamNode = fringe.removeSmallest();
            AStarRelaxEdgeFrom(currExamNode, destNode, db);
        }
    }

    /**
     * Performs edge relaxation operation for A*
     *
     * @param currExamNode the node from which an edge is extended
     * @param destNode the destination node
     * @param db the database representing the graph
     * */
    private static void AStarRelaxEdgeFrom(Long currExamNode, Long destNode, GraphDB db) {
        Iterable<Long> it = db.adjacent(currExamNode);
        for (Long adjNode: it) {
            if (!fringe.contains(adjNode)) fringe.add(adjNode, Double.POSITIVE_INFINITY);
            if (!distTo.containsKey(adjNode)) distTo.put(adjNode, Double.POSITIVE_INFINITY);

            // the distance/priority associated with an adjacent node is the distance from the
            // start node to the current adjacent node plus the great circle distance from the
            // current adjacent node to the destination node as heuristics.
            double weight = db.distance(currExamNode, adjNode) + db.distance(adjNode, destNode);

            if (distTo.get(currExamNode) + weight < distTo.get(adjNode)) {
                distTo.put(adjNode, distTo.get(currExamNode) + weight);
                edgeTo.put(adjNode, currExamNode);
                fringe.changePriority(adjNode, distTo.get(adjNode));
            }
        }
    }

    /**
     *  Constructing the shortest path return query
     *
     *  @param targetNode the target to which the shortest path is heading
     *  @return a list containing nodes to be traverse through that constructs a
     *          shortest path to the target node in the order of start -> target
     *  */
    private static ArrayList<Long> constructPath(Long targetNode) {
        ArrayList<Long> path = new ArrayList<>();
        while (path.add(targetNode) && edgeTo.get(targetNode) != null)
            targetNode = edgeTo.get(targetNode);

        Collections.reverse(path);
        return path;
    }

    /**
     * Clean the shortest path route */
    private static void clean() {
        fringe.clearMinPQ();
        distTo.clear();
        edgeTo.clear();
    }


    /**
     * Create the list of directions corresponding to a route on the graph.
     * @param db The graph to use.
     * @param route The route to translate into directions. Each element
     *              corresponds to a node from the graph in the route.
     * @return A list of NavigatiionDirection objects corresponding to the input
     * route.
     */
    public static List<NavigationDirection> routeDirections(GraphDB db, List<Long> route) {
        ArrayList<NavigationDirection> navigationDirections = new ArrayList<>();
        double dist = 0.0;

        Long currNode = route.get(0);
        Long nextNode = route.get(1);

        String wayName = identifyWayName(currNode, nextNode, db);

        int dir = NavigationDirection.START;

        for (int i = 0; i < route.size() - 1; i++) {
            currNode = route.get(i);
            nextNode = route.get(i + 1);

            /* For Testing Purposes */
//            System.out.println("Node ID: " + currNode + "  |  Lat: " + db.lat(currNode) + "  |  Lon: " + db.lon(currNode));
//            System.out.println(Arrays.toString(db.getWayNameListByNode(currNode).toArray()));
//            System.out.println("Name Identification: " + identifyWayName(currNode, nextNode, db) + "\n");

            if (wayName.equals(identifyWayName(currNode, nextNode, db))) {
                dist += db.distance(currNode, nextNode);
            } else {
                navigationDirections.add(createNavigationDirection(dir, wayName, dist));

                dir = headingDirection(route.get(i - 1), currNode, nextNode, db);
                dist = db.distance(currNode, nextNode);
                wayName = identifyWayName(currNode, nextNode, db);
            }
        }

        navigationDirections.add(createNavigationDirection(dir, wayName, dist));

        return navigationDirections;
    }

    /** Create and set a new NavigationDirection Object
     * @param direction an integer value that represents the direction of the NavigationDirection
     * @param way a String which is the name of the way
     * @param distance The distance of the way
     *
     * @return a NavigationDirection Object
     * */
    private static NavigationDirection createNavigationDirection(int direction, String way, double distance) {
        NavigationDirection nav = new NavigationDirection();
        nav.direction = direction;
        nav.way = way;
        nav.distance = distance;
        return nav;
    }

    /**
     * identify the name of way that two nodes are on
     * (suppose that the two nodes are on the same way)
     *  @param nodeOne Node Id of a node
     *  @param nodeTwo Node Id of a node
     *  @param db the database that represents the graph
     *
     * @return the name of the way
     *  */
    private static String identifyWayName(Long nodeOne, Long nodeTwo, GraphDB db) {
        Set<String> waySetOne = db.getWayNameListByNode(nodeOne);
        Set<String> waySetTwo = db.getWayNameListByNode(nodeTwo);

        for (String wayNameOne : waySetOne) {
            if (waySetTwo.contains(wayNameOne)) return wayNameOne;
        }

        /* Handles the corner case when the second node is the turning point in the route */
        Iterator<String> it = waySetOne.iterator();
        return it.next();
    }

    /**
     * return the heading direction based on the relative bearing of the headed direction
     *
     * @param prevNode the node Id of the node that comes before the current Node in the path
     * @param currNode the node Id of the current node in the path
     * @param targetNode the node Id the the node that the path is leading toward
     * @param db the database that represents the graph
     *
     * @return an integer value which indicates a Navigation Direction
     * FIXME: Relative Bearing does not work properly
     * */
    private static int headingDirection(Long prevNode, Long currNode, Long targetNode, GraphDB db) {
        int direction = -1;
        double initialBearing = db.bearing(prevNode, currNode);
        if (initialBearing < 0) initialBearing = 360 + initialBearing;
        double secondBearing = db.bearing(currNode, targetNode);
        if (secondBearing < 0) secondBearing = 360 + secondBearing;

        double relativeBearing = initialBearing - secondBearing;
        // FIXME

        /* For Testing Purposes */
//        System.out.println("***** Turning Point *****");
//        System.out.println(" **Prev -> Curr** initial bearing: " + initialBearing);
//        System.out.println(" **Curr -> Tar ** second bearing: " + secondBearing);
//        System.out.println("Relative Bearing: " + relativeBearing);

        if (relativeBearing >= 0) {
            if (relativeBearing < 15) {
                direction = NavigationDirection.STRAIGHT;
            } else if (relativeBearing < 30) {
                direction = NavigationDirection.SLIGHT_LEFT;
            } else if (relativeBearing < 100) {
                direction = NavigationDirection.LEFT;
            } else {
                direction = NavigationDirection.SHARP_LEFT;
            }
        } else if (relativeBearing < 0) {
            if (relativeBearing > -15) {
                direction = NavigationDirection.STRAIGHT;
            } else if (relativeBearing > -30) {
                direction = NavigationDirection.SLIGHT_RIGHT;
            } else if (relativeBearing > -100) {
                direction = NavigationDirection.RIGHT;
            } else {
                direction = NavigationDirection.SHARP_RIGHT;
            }
        }

        /* For Testing Purposes */
        System.out.println("Direction: " + NavigationDirection.DIRECTIONS[direction] + "\n\n");

        return direction;
    }

    /**
     * Class to represent a navigation direction, which consists of 3 attributes:
     * a direction to go, a way, and the distance to travel for.
     */
    public static class NavigationDirection {

        /** Integer constants representing directions. */
        public static final int START = 0;
        public static final int STRAIGHT = 1;
        public static final int SLIGHT_LEFT = 2;
        public static final int SLIGHT_RIGHT = 3;
        public static final int RIGHT = 4;
        public static final int LEFT = 5;
        public static final int SHARP_LEFT = 6;
        public static final int SHARP_RIGHT = 7;

        /** Number of directions supported. */
        public static final int NUM_DIRECTIONS = 8;

        /** A mapping of integer values to directions.*/
        public static final String[] DIRECTIONS = new String[NUM_DIRECTIONS];

        /** Default name for an unknown way. */
        public static final String UNKNOWN_ROAD = "unknown road";
        
        static {
            DIRECTIONS[START] = "Start";
            DIRECTIONS[STRAIGHT] = "Go straight";
            DIRECTIONS[SLIGHT_LEFT] = "Slight left";
            DIRECTIONS[SLIGHT_RIGHT] = "Slight right";
            DIRECTIONS[LEFT] = "Turn left";
            DIRECTIONS[RIGHT] = "Turn right";
            DIRECTIONS[SHARP_LEFT] = "Sharp left";
            DIRECTIONS[SHARP_RIGHT] = "Sharp right";
        }

        /** The direction a given NavigationDirection represents.*/
        int direction;
        /** The name of the way I represent. */
        String way;
        /** The distance along this way I represent. */
        double distance;

        /**
         * Create a default, anonymous NavigationDirection.
         */
        public NavigationDirection() {
            this.direction = STRAIGHT;
            this.way = UNKNOWN_ROAD;
            this.distance = 0.0;
        }

        public String toString() {
            return String.format("%s on %s and continue for %.3f miles.",
                    DIRECTIONS[direction], way, distance);
        }

        /**
         * Takes the string representation of a navigation direction and converts it into
         * a Navigation Direction object.
         * @param dirAsString The string representation of the NavigationDirection.
         * @return A NavigationDirection object representing the input string.
         */
        public static NavigationDirection fromString(String dirAsString) {
            String regex = "([a-zA-Z\\s]+) on ([\\w\\s]*) and continue for ([0-9\\.]+) miles\\.";
            Pattern p = Pattern.compile(regex);
            Matcher m = p.matcher(dirAsString);
            NavigationDirection nd = new NavigationDirection();
            if (m.matches()) {
                String direction = m.group(1);
                if (direction.equals("Start")) {
                    nd.direction = NavigationDirection.START;
                } else if (direction.equals("Go straight")) {
                    nd.direction = NavigationDirection.STRAIGHT;
                } else if (direction.equals("Slight left")) {
                    nd.direction = NavigationDirection.SLIGHT_LEFT;
                } else if (direction.equals("Slight right")) {
                    nd.direction = NavigationDirection.SLIGHT_RIGHT;
                } else if (direction.equals("Turn right")) {
                    nd.direction = NavigationDirection.RIGHT;
                } else if (direction.equals("Turn left")) {
                    nd.direction = NavigationDirection.LEFT;
                } else if (direction.equals("Sharp left")) {
                    nd.direction = NavigationDirection.SHARP_LEFT;
                } else if (direction.equals("Sharp right")) {
                    nd.direction = NavigationDirection.SHARP_RIGHT;
                } else {
                    return null;
                }

                nd.way = m.group(2);
                try {
                    nd.distance = Double.parseDouble(m.group(3));
                } catch (NumberFormatException e) {
                    return null;
                }
                return nd;
            } else {
                // not a valid nd
                return null;
            }
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof NavigationDirection) {
                return direction == ((NavigationDirection) o).direction
                    && way.equals(((NavigationDirection) o).way)
                    && distance == ((NavigationDirection) o).distance;
            }
            return false;
        }

        @Override
        public int hashCode() {
            return Objects.hash(direction, way, distance);
        }
    }
}

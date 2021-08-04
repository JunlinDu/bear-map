package utils;

import service.GraphDB;
import service.Router;

import java.util.List;

public class TextFormatter {
    /**
     * Takes the route of the server and converts it into an HTML friendly
     * String to be passed to the frontend.
     */
    public static String getDirectionsText(GraphDB graph, List<Long> route) {

        List<Router.NavigationDirection> directions = Router.routeDirections(graph, route);

        if (directions == null || directions.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        int step = 1;
        for (Router.NavigationDirection d: directions) {
            sb.append(String.format("%d. %s <br>", step, d));
            step += 1;
        }
        return sb.toString();
    }
}

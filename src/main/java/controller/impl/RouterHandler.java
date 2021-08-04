package controller.impl;

import service.Router;
import controller.RouteHandler;
import spark.Request;
import spark.Response;
import utils.Constants;

import java.util.HashMap;
import java.util.Map;

import static utils.TextFormatter.getDirectionsText;
import static utils.Constants.graph;
import static utils.Constants.ROUTES;

public class RouterHandler extends RouteHandler<Map<String, Double>, Map<String, Object>> {
    @Override
    protected Map<String, Double> parseRequestParams(Request req) {
        return getRequestParams(req, Constants.REQUIRED_ROUTE_REQUEST_PARAMS);
    }

    @Override
    protected Map<String, Object> processRequest(Map<String, Double> params, Response res) {
        ROUTES.clear();

        ROUTES.addAll(Router.shortestPath(graph,
                        params.get("start_lon"),
                        params.get("start_lat"),
                        params.get("end_lon"),
                        params.get("end_lat")));

        String directions = getDirectionsText(graph, ROUTES);

        Map<String, Object> routeParams = new HashMap<>();
        routeParams.put("routing_success", !ROUTES.isEmpty());
        routeParams.put("directions_success", directions.length() > 0);
        routeParams.put("directions", directions);

        return routeParams;
    }
}

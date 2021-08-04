package controller.impl;

import controller.RouteHandler;
import spark.Request;
import spark.Response;

import utils.dataStructures.Tuple;

import java.util.Set;

import static utils.Constants.graph;

public class SearchHandler extends RouteHandler<Tuple<Set<String>, String>, Object>
{
    @Override
    protected Tuple<Set<String>, String> parseRequestParams(Request req) {
        return new Tuple<>(req.queryParams(), req.queryParams("term"));
    }

    @Override
    protected Object processRequest(Tuple<Set<String>, String> params, Response res) {
        Set<String> reqParams = params.first;
        String term = params.second;

        /* Search for actual location data. */
        if (reqParams.contains("full")) return graph.getSearcher().getLocations(term);

        /* Search for prefix matching strings. */
        return graph.getSearcher().getKeysByPrefix(term);
    }
}

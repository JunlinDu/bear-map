package controller.impl;

import controller.RouteHandler;
import spark.Request;
import spark.Response;

import static utils.Constants.ROUTES;

public class ClearRouteHandler extends RouteHandler {
    @Override
    protected Object parseRequestParams(Request req) {
        return null;
    }

    @Override
    protected Object processRequest(Object params, Response res) {
        ROUTES.clear();
        return true;
    }
}

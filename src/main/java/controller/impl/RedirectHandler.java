package controller.impl;

import controller.RouteHandler;
import spark.Request;
import spark.Response;

public class RedirectHandler extends RouteHandler {
    @Override
    protected Object parseRequestParams(Request req) {
        return null;
    }

    @Override
    protected Object processRequest(Object params, Response res) {
        res.redirect("/map.html", 301);
        return true;
    }

    @Override
    protected Object buildJsonResponse(Object o) {
        return true;
    }
}

package controller;

import controller.impl.*;

import java.util.HashMap;
import java.util.Map;

public class RouteHandlerFactory {

    public static final Map<String, RouteHandler> handlerMap;

    static {
        handlerMap = new HashMap<>();
        handlerMap.put("raster", new RasterHandler());
        handlerMap.put("route", new RouterHandler());
        handlerMap.put("clear_route", new ClearRouteHandler());
        handlerMap.put("search", new SearchHandler());
        handlerMap.put("", new RedirectHandler());
    }
}

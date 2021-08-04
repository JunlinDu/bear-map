import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import controller.RouteHandler;
import service.GraphDB;
import service.Rasterer;
import utils.Constants;

import static spark.Spark.*;

public class ServerInitializer {

    public static void initializeServer (Map<String, RouteHandler> handlers) {
        /* Generate a in-memory representation of the graph */
        Constants.graph = new GraphDB(Constants.OSM_DB_PATH);

        /* Create a new image rasterer */
        Constants.rasterer = new Rasterer();

        /* File location for static contents */
        staticFileLocation("/page");

        /* Allow for cors requests */
        before((request, response) -> {
            response.header("Access-Control-Allow-Origin", "*");
            response.header("Access-Control-Request-Method", "*");
            response.header("Access-Control-Allow-Headers", "*");
        });

        for(Map.Entry<String, RouteHandler> apiRoute: handlers.entrySet()){
            get("/"+apiRoute.getKey(), apiRoute.getValue());
        }
    }
}

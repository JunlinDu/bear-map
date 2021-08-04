package controller;

import com.google.gson.Gson;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.HashMap;
import java.util.Set;

import static spark.Spark.halt;

/**
 * The base class that defines the handling of a http request
 * API Docs for Route can be found
 * <a href="https://javadoc.io/doc/com.sparkjava/spark-core/latest/spark/Route.html">here</a>
 *
 * @author Rahul, Junlin Du
 */
public abstract class RouteHandler<Req, Res> implements Route {

    /**
     * HTTP invalid request response.
     */
    private static final int HALT_RESPONSE = 400;

    private Gson gson;

    public RouteHandler() {
        gson = new Gson();
    }

    @Override
    public Object handle(Request request, Response response) throws Exception {
        Req requestParams = parseRequestParams(request);
        Res result = processRequest(requestParams, response);
        return buildJsonResponse(result);
    }

    /**
     * Defines how to parse and extract the request parameters from request
     *
     * @param req the request object
     * @return extracted request parameters
     */
    protected abstract Req parseRequestParams(Request req);

    /**
     * Process the request using the given parameters
     *
     * @param params request parameters
     * @param res    response object
     * @return the result computed after processing request
     */
    protected abstract Res processRequest(Req params, Response res);

    /**
     * Builds a JSON response to return from the result object
     */
    protected Object buildJsonResponse(Res res) {
        return gson.toJson(res);
    }

    /**
     * Validate & return a parameter map of the required request parameters.
     * Requires that all input parameters are doubles.
     *
     * @param req            HTTP Request.
     * @param requiredParams TestParams to validate.
     * @return A populated map of input parameter to it's numerical value.
     */
    protected HashMap<String, Double> getRequestParams(spark.Request req, String[] requiredParams) {
        Set<String> reqParams = req.queryParams();
        HashMap<String, Double> params = new HashMap<>();

        for (String param : requiredParams) {
            if (!reqParams.contains(param)) halt(HALT_RESPONSE, "Invalid Request - parameters missing.");

            else {
                try {
                    params.put(param, Double.parseDouble(req.queryParams(param)));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    halt(HALT_RESPONSE, "Incorrect parameters - provide numbers.");
                }
            }
        }
        return params;
    }

}

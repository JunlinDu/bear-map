package controller.impl;

import controller.RouteHandler;
import utils.Constants;
import spark.Request;
import spark.Response;

import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.Map;

import static utils.Constants.rasterer;
import static utils.ImageToOutputStreamWriter.writeImagesToOutputStream;

public class RasterHandler extends RouteHandler<Map<String, Double>, Map<String, Object>> {

    @Override
    protected Map<String, Double> parseRequestParams(Request req) {
        return getRequestParams(req, Constants.REQUIRED_RASTER_REQUEST_PARAMS);
    }

    @Override
    protected Map<String, Object> processRequest(Map<String, Double> params, Response res) {
        /* getMapRaster() does almost all the work for this API call */
        return rasterer.getMapRaster(params);
    }

    @Override
    protected Object buildJsonResponse(Map<String, Object> stringObjectMap) {
        /* The png image is written to the ByteArrayOutputStream */
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        writeImagesToOutputStream(stringObjectMap, os);
        String encodedImage = Base64.getEncoder().encodeToString(os.toByteArray());
        stringObjectMap.put("b64_encoded_image_data", encodedImage);

        return super.buildJsonResponse(stringObjectMap);
    }
}

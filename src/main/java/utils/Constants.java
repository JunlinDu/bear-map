package utils;

import service.GraphDB;
import service.Rasterer;

import java.awt.*;
import java.util.LinkedList;
import java.util.List;

public class Constants {
    /**
     * The root upper left/lower right longitudes and latitudes represent the bounding box of
     * the root tile, as the images in the img/ folder are scraped.
     * Longitude == x-axis; latitude == y-axis.
     */
    public static final double
            ROOT_ULLAT = 37.892195547244356,
            ROOT_ULLON = -122.2998046875,
            ROOT_LRLAT = 37.82280243352756,
            ROOT_LRLON = -122.2119140625;

    /** The tile images are in the IMG_ROOT folder. */
    public static final String IMG_ROOT = "../library/data/imgs/";

    /**
     * The OSM XML file path. Downloaded from <a href="http://download.bbbike.org/osm/">here</a>
     * using custom region selection.
     **/
    public static final String OSM_DB_PATH = "../library/data/graph/berkeley-2018.osm.xml";

    /** Each tile is 256x256 pixels. */
    public static final int TILE_SIZE = 256;

    /** HTTP failed response. */
    public static final int HALT_RESPONSE = 403;

    /** Route stroke information: typically roads are not more than 5px wide. */
    public static final float ROUTE_STROKE_WIDTH_PX = 5.0f;

    /** Route stroke information: Cyan with half transparency. */
    public static final Color ROUTE_STROKE_COLOR = new Color(108, 181, 230, 200);

    /**
     * Each raster request to the server will have the following parameters
     * as keys in the params map accessible by,
     * i.e., params.get("ullat") inside getMapRaster(). <br>
     * ullat : upper left corner latitude, <br> ullon : upper left corner longitude, <br>
     * lrlat : lower right corner latitude,<br> lrlon : lower right corner longitude <br>
     * w : user viewport window width in pixels,<br> h : user viewport height in pixels.
     **/
    public static final String[] REQUIRED_RASTER_REQUEST_PARAMS = {"ullat", "ullon", "lrlat",
            "lrlon", "w", "h"};

    /**
     * Each route request to the server will have the following parameters
     * as keys in the params map.<br>
     * start_lat : start point latitude,<br> start_lon : start point longitude,<br>
     * end_lat : end point latitude, <br>end_lon : end point longitude.
     **/
    public static final String[] REQUIRED_ROUTE_REQUEST_PARAMS = {"start_lat", "start_lon",
            "end_lat", "end_lon"};

    /**
     * The result of rastering must be a map containing all of the
     * fields listed in the comments for getMapRaster in service.Rasterer.java.
     **/
     public static final String[] REQUIRED_RASTER_RESULT_PARAMS = {"render_grid", "raster_ul_lon",
            "raster_ul_lat", "raster_lr_lon", "raster_lr_lat", "depth", "query_success"};

    public static final List<Long> ROUTES = new LinkedList<>();

    public static GraphDB graph;

    public static Rasterer rasterer;
}

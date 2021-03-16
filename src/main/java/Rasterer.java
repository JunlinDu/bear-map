import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;

/**
 * This class provides all code necessary to take a query box and produce
 * a query result. The getMapRaster method must return a Map containing all
 * seven of the required fields, otherwise the front end code will probably
 * not draw the output correctly.
 */
public class Rasterer {

    private double lonCoverage;
    private double latCoverage;
    private ArrayList<Double>  zoomLevelLonDPPs;

    public Rasterer() {
        lonCoverage = MapServer.ROOT_LRLON - MapServer.ROOT_ULLON;
        latCoverage = MapServer.ROOT_ULLAT - MapServer.ROOT_LRLAT;
        zoomLevelLonDPPs = new ArrayList<Double>();
        // [3.4332275390625E-4, 1.71661376953125E-4, 8.58306884765625E-5,
        // 4.291534423828125E-5, 2.1457672119140625E-5, 1.0728836059570312E-5,
        // 5.364418029785156E-6, 2.682209014892578E-6]

        for (int i = 0, l = 1; i < 8; i++, l*=2) {
            // calculates and initiates an array list for the LonDPP of 8 zoom levels
            zoomLevelLonDPPs.add(lonCoverage / (l * MapServer.TILE_SIZE));
        }
        System.out.println(zoomLevelLonDPPs);
    }

    /**
     * Takes a user query and finds the grid of images that best matches the query. These
     * images will be combined into one big image (rastered) by the front end. <br>
     *
     *     The grid of images must obey the following properties, where image in the
     *     grid is referred to as a "tile".
     *     <ul>
     *         <li>The tiles collected must cover the most longitudinal distance per pixel
     *         (LonDPP) possible, while still covering less than or equal to the amount of
     *         longitudinal distance per pixel in the query box for the user viewport size. </li>
     *         <li>Contains all tiles that intersect the query bounding box that fulfill the
     *         above condition.</li>
     *         <li>The tiles must be arranged in-order to reconstruct the full image.</li>
     *     </ul>
     *
     * @param params Map of the HTTP GET request's query parameters - the query box and
     *               the user viewport width and height.
     *
     * @return A map of results for the front end as specified: <br>
     * "render_grid"   : String[][], the files to display. <br>
     * "raster_ul_lon" : Number, the bounding upper left longitude of the rastered image. <br>
     * "raster_ul_lat" : Number, the bounding upper left latitude of the rastered image. <br>
     * "raster_lr_lon" : Number, the bounding lower right longitude of the rastered image. <br>
     * "raster_lr_lat" : Number, the bounding lower right latitude of the rastered image. <br>
     * "depth"         : Number, the depth of the nodes of the rastered image <br>
     * "query_success" : Boolean, whether the query was able to successfully complete; don't
     *                    forget to set this to true on success! <br>
     */
    public Map<String, Object> getMapRaster(Map<String, Double> params) {

        // prints out the HTTP GET request's query parameters
        System.out.println(params);
        // TODO print out the LonDPP of the query Box
        System.out.println(params.get("lrlon"));
        System.out.println(params.get("ullon"));
        System.out.println(params.get("w"));
        System.out.println((params.get("lrlon") - params.get("ullon"))/ params.get("w"));

        Map<String, Object> results = new HashMap<>();
        return results;
    }

}

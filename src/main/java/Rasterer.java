import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;

/**
 * This class provides all code necessary to take a query box and produce
 * a query result.
 * @author Junlin Du
 */
public class Rasterer {

    private double lonCoverage;
    private double latCoverage;
    private ArrayList<Double>  zoomLevelLonDPPs;

    public Rasterer() {
        lonCoverage = MapServer.ROOT_LRLON - MapServer.ROOT_ULLON;
        latCoverage = MapServer.ROOT_ULLAT - MapServer.ROOT_LRLAT;
        zoomLevelLonDPPs = new ArrayList<Double>();
        // LonDPP of 8 levels of tile:
        // [3.4332275390625E-4, 1.71661376953125E-4, 8.58306884765625E-5, 4.291534423828125E-5,
        // 2.1457672119140625E-5, 1.0728836059570312E-5, 5.364418029785156E-6, 2.682209014892578E-6]

        for (int i = 0, l = 1; i < 8; i++, l*=2) {
            // calculates and initiates an array list for the LonDPP of 8 levels of zoom
            zoomLevelLonDPPs.add(lonCoverage / (l * MapServer.TILE_SIZE));
        }
    }

    /** Takes the query LonDPP and returns the level of depth corresponding to the query
     *
     * @param queryLonDPP The LonDPP of the query box
     * @return the corresponding level of depth
     */
    private int calcDepth(double queryLonDPP) {
        int i = 0;
        for (double ldpp : zoomLevelLonDPPs) {
            if (ldpp < queryLonDPP || i == zoomLevelLonDPPs.size() - 1) break;
            i++;
        }
        return i;
    }

    /**
     * This function search for the longitude or latitude of the appropriate
     * tile based on the current zoom level and a given coordinate.
     * This is a recursive function utilizes the principles of binary
     * search.
     *
     * @param depth the current level of zoom (depth)
     * @param coordinate the coordinate of the query box
     * @param lowerBound the coordinate of the lower bound of the bounding box for a single dimension
     * @param upperBound the coordinate of the upper bound of the bounding box for a single dimension
     *
     * @return the coordinate of a rastered tile
     *
    * */
    private double searchSingleDimCoord(int depth, double coordinate, double lowerBound, double upperBound) {

        if (depth != 0) {
            double mid = lowerBound + (upperBound - lowerBound) / 2;
            if (coordinate == mid) return mid;

            if (coordinate < mid)
                return searchSingleDimCoord(depth - 1, coordinate, lowerBound, mid);

            return searchSingleDimCoord(depth - 1, coordinate, mid, upperBound);
        }

        return lowerBound;
    }


    private int calcULTileCoord(int depth) {

        return 0;
    }


    private int calcLRTileCoord() {

        return 0;
    }


    private Map<String, Double> calcRasterCoord(double ulLon, double ulLat, double lrLon, double lrLat) {
        return null;
    }

    private String[][] constructTile() {
        return null;
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
        double lonDPP  = (params.get("lrlon") - params.get("ullon"))/ params.get("w");
        System.out.println("LonDPP: " + lonDPP);
        System.out.println("Depth: " + String.valueOf(calcDepth(lonDPP)));
        Map<String, Object> results = new HashMap<>();
        return results;
    }


    /** The Main function is used for testing purposes.*/
    public static void main (String[] args) {
        Rasterer rasterer = new Rasterer();
        double result = rasterer.searchSingleDimCoord(3, 4.2, 0.0, 16.0);
        System.out.println(result); // 4
        result = rasterer.searchSingleDimCoord(3, 5.2, 0.0, 16.0);
        System.out.println(result);
        result = rasterer.searchSingleDimCoord
                (7, -122.24163047377972,
                        MapServer.ROOT_ULLON, MapServer.ROOT_LRLON);
        System.out.println(result); // expected: -122.24212646484375
        result = rasterer.searchSingleDimCoord
                (1, -122.3027284165759,
                        MapServer.ROOT_ULLON, MapServer.ROOT_LRLON);
        System.out.println(result); // expected: -122.2998046875
    }

}

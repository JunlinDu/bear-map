package service;

import utils.Constants;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.lang.Math;

/**
 * This class provides all code necessary to take a query box and produce a query result.
 * @author Junlin Du
 */
public class Rasterer {
    private static final double LONCOVERAGE = Constants.ROOT_LRLON - Constants.ROOT_ULLON;
    private static final double LATCOVERAGE = Constants.ROOT_ULLAT - Constants.ROOT_LRLAT;
    private double[] tileLonCoverage;
    private double[] tileLatCoverage;

    /*
     * For reference and testing purposes. LonDPP of 8 levels of tile:
     * [3.4332275390625E-4, 1.71661376953125E-4, 8.58306884765625E-5, 4.291534423828125E-5,
     * 2.1457672119140625E-5, 1.0728836059570312E-5, 5.364418029785156E-6, 2.682209014892578E-6]
     */
    private ArrayList<Double>  zoomLevelLonDPPs;

    public Rasterer() {
        zoomLevelLonDPPs = new ArrayList<Double>();
        tileLonCoverage = new double[8];
        tileLatCoverage = new double[8];

        // calculates and initiates an array list for the LonDPP of 8 levels of zoom
        for (int i = 0, l = 1; i < 8; i++, l*=2) {
            zoomLevelLonDPPs.add(LONCOVERAGE / (l * Constants.TILE_SIZE));
            tileLonCoverage[i] = LONCOVERAGE / l;
            tileLatCoverage[i] = LATCOVERAGE / l;
        }
    }


    /**
     * Takes a user query and finds the grid of images that best matches the query.
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
     * "query_success" : Boolean, whether the query was able to successfully complete <br>
     */
    public Map<String, Object> getMapRaster(Map<String, Double> params) {
        Map<String, Object> results = new HashMap<>();

        int depth = calcDepth((params.get("lrlon") - params.get("ullon"))/ params.get("w"));

        double[] uLTileULCoor = calcTileULCoor(depth, params.get("ullon"), params.get("ullat"));
        double[] lRTileULCoor = calcTileULCoor(depth, params.get("lrlon"), params.get("lrlat"));
        double[] lRTileLRCoor = new double[]
                {lRTileULCoor[0] + this.tileLonCoverage[depth],
                        lRTileULCoor[1] - this.tileLatCoverage[depth]};

        results.put("raster_ul_lon", uLTileULCoor[0]);
        results.put("depth", depth);
        results.put("raster_lr_lon", lRTileLRCoor[0]);
        results.put("raster_lr_lat", lRTileLRCoor[1]);
        results.put("render_grid", constructTile(depth, uLTileULCoor, lRTileULCoor));
        results.put("raster_ul_lat", uLTileULCoor[1]);
        results.put("query_success", true);

        return results;
    }

    /** This function takes the current depth/level of zoom and two edge tile coordinates and
     * returns a two-dimensional array of Strings representing the tile files to be retrieved
     * and displayed to the user based on the user's request.
     *
     * @param depth an int value representing the current depth/level of zoom
     * @param uLTileULCoor a double array containing the upper left coordinate of the tile
     *                     located on the upper left corner of the rastered image
     * @param lRTileULCoor a double array containing the lower right coordinate of the tile
     *                     located on the lower right corner of the rastered image
     *
     * @return result: String[][], files to be retrieved from the database and displayed to
     *                 the user.
     * */
    private String[][] constructTile(int depth, double[] uLTileULCoor, double[] lRTileULCoor) {
        int[] uLTile = identifyFileNum(depth, uLTileULCoor[0], uLTileULCoor[1]),
                lRTile = identifyFileNum(depth, lRTileULCoor[0], lRTileULCoor[1]);
        int numOfCol = lRTile[0] - uLTile[0] + 1,
                numOfRow = lRTile[1] - uLTile[1] + 1;

        String[][] result = new String[numOfRow][numOfCol];

        for (int y = 0; y < numOfRow; y++) {
            for (int x = 0; x < numOfCol; x++)
                result[y][x] = "d" + depth + "_x" + (uLTile[0] + x) + "_y" + (uLTile[1] + y) + ".png";
        }

        return result;
    }


    /** Takes the query LonDPP and returns the level of depth corresponding to the query
     *
     * @param queryLonDPP The LonDPP of the query box
     * @return the corresponding level of depth
     */
    private int calcDepth(double queryLonDPP) {
        int i = 0;
        for (double ldpp : zoomLevelLonDPPs) {
            if (ldpp <= queryLonDPP || i == zoomLevelLonDPPs.size() - 1) break;
            i++;
        }
        return i;
    }


    /**
     * This function search for the longitude or latitude of the appropriate
     * tile based on the current zoom level and a given coordinate.
     * This is a recursive function utilizes the principles of binary
     * search. The upperBound and lowerBound can be thought of as two ends
     * of a sorted array, where the depth determines the number of elements
     * in the array.
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

            // If the coordinate happens to coincide with the mid point. This may happen very rarely.
            if (coordinate == mid) return mid;

            // If the coordinate is smaller than the mid point, i.e. is on the left side of the imaginary array.
            if (coordinate < mid)
                return searchSingleDimCoord(depth - 1, coordinate, lowerBound, mid);

            // The only possibility left is that the coordinate is greater that the mid point.
            return searchSingleDimCoord(depth - 1, coordinate, mid, upperBound);
        }

        // This will be the coordinate of the rastered tile. The coordinate is the greatest of an array (imaginary)
        // of tiles that is smaller than the given coordinate, thus have the requested region covered.
        return lowerBound;
    }


    /**
    * This function returns the upper left longitudinal and latitudinal
     * coordinates of a tile that covers the requested coordinate in the
     * current zoom level.
     *
     * @param depth current level of zoom
     * @param longitude requested longitude
     * @param latitude requested latitude
     *
     * @return a double array: [tileUlLon, tileUlLat]
    * */
    private double[] calcTileULCoor(int depth, double longitude, double latitude) {

        return new double[]{
                searchSingleDimCoord
                        (depth, longitude, Constants.ROOT_ULLON, Constants.ROOT_LRLON),
                searchSingleDimCoord
                        (depth, latitude, Constants.ROOT_LRLAT, Constants.ROOT_ULLAT)
                        + this.tileLatCoverage[depth]};
    }


    /**
     * This function identifies the x and y axis of the tile image based on
     * a given longitude and latitude.
     *
     * @param depth current zoom level
     * @param tileUlLon the longitude of a tile's upper left
     * @param tileUlLat the latitude of a tile's upper left
     *
     * @return [x, y]
     * */
    private int[] identifyFileNum(int depth, double tileUlLon, double tileUlLat) {
        int x = 0, y = 0;
        double numOfTiles = Math.pow(2, depth);
        double longDisFromBound = tileUlLon - Constants.ROOT_ULLON;
        double latDisFromBound = Constants.ROOT_ULLAT - tileUlLat;

        if (longDisFromBound != 0) x = (int) Math.round((longDisFromBound / LONCOVERAGE) * numOfTiles);
        if (latDisFromBound != 0) y = (int) Math.round((latDisFromBound / LATCOVERAGE) * numOfTiles);

        return new int[] {x, y};
    }


    /** The Main function is used for testing purposes.*/
    public static void main (String[] args) {
        Rasterer rasterer = new Rasterer();
    }

}

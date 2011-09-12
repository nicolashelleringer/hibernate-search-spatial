package net.novacodex.GridManager;

import java.util.HashMap;
import java.util.Map;

public class GridManager {
	public static String getCellIndex(double coordinate, double range, int tileLevel) {
		return Integer.toString( (int) Math.floor( Math.pow( 2, tileLevel ) * coordinate / range ) );
	}

	public static String getGridCellId(Point point, int tileLevel) {
		double[] indexablesCoordinates= projectToIndexSpace(point);
		return getCellIndex(indexablesCoordinates[0],GeometricConstants.PROJECTED_LATITUDE_RANGE,tileLevel)+"|"+getCellIndex(indexablesCoordinates[1],GeometricConstants.PROJECTED_LONGITUDE_RANGE,tileLevel);
	}

	public static Map<Integer,String> getGridCellIds(Point point, int minTileLevel, int maxTileLevel) {
		if(minTileLevel<0
		|| maxTileLevel<minTileLevel)
			return null;

		Map<Integer,String> gridCellIds= new HashMap<Integer,String>();

		for(int i= minTileLevel; i<= maxTileLevel; i++) {
			gridCellIds.put( i, getGridCellId(point,i));
		}

		return gridCellIds;
	}

	public static int findBestTileLevelForSearchRange(double range) {

	    double iterations= GeometricConstants.EARTH_EQUATOR_CIRCUMFERENCE_KM / (2.0d * range);

	    int bestTileLevel =  (int)Math.ceil(Math.log(iterations) / Math.log(2));

	    return bestTileLevel;
	}

	public static double[] projectToIndexSpace(Point point) {
		double[] projectedCoordinates= new double[2];

		projectedCoordinates[0]= Math.toRadians(point.getLongitude())* Math.cos(Math.toRadians(point.getLatitude()));
		projectedCoordinates[1]= Math.toRadians(point.getLatitude());

		return projectedCoordinates;
	}
}

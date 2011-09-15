package net.novacodex.hibernate.search.spatial;

import org.apache.lucene.search.ConstantScoreQuery;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.QueryWrapperFilter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GridManager {
	public static int getCellIndex( double coordinate, double range, int gridLevel ) {
		return (int) Math.floor( Math.pow( 2, gridLevel ) * coordinate / range );
	}

	public static String getGridCellId( Point point, int gridLevel ) {
		double[] indexablesCoordinates = projectToIndexSpace( point );
		return Integer.toString( getCellIndex( indexablesCoordinates[0], GeometricConstants.PROJECTED_LONGITUDE_RANGE, gridLevel ) ) + "|" + Integer.toString( getCellIndex( indexablesCoordinates[1], GeometricConstants.PROJECTED_LATITUDE_RANGE, gridLevel ) );
	}

	public static Map<Integer, String> getGridCellsIds( Point point, int minGridLevel, int maxGridLevel ) {
		if ( minGridLevel < 0 || maxGridLevel < minGridLevel )
			return null;

		Map<Integer, String> gridCellIds = new HashMap<Integer, String>();

		for ( int i = minGridLevel; i <= maxGridLevel; i++ ) {
			gridCellIds.put( i, getGridCellId( point, i ) );
		}

		return gridCellIds;
	}

	public static List<String> getGridCellsIds( Point lowerLeft, Point upperRight, int gridLevel ) {
		double[] projectedLowerLeft = projectToIndexSpace( lowerLeft );
		int lowerLeftXIndex = getCellIndex( projectedLowerLeft[0], GeometricConstants.PROJECTED_LONGITUDE_RANGE, gridLevel );
		int lowerLeftYIndex = getCellIndex( projectedLowerLeft[1], GeometricConstants.PROJECTED_LATITUDE_RANGE, gridLevel );

		double[] projectedUpperRight = projectToIndexSpace( upperRight );
		int upperRightXIndex = getCellIndex( projectedUpperRight[0], GeometricConstants.PROJECTED_LONGITUDE_RANGE, gridLevel );
		int upperRightYIndex = getCellIndex( projectedUpperRight[1], GeometricConstants.PROJECTED_LATITUDE_RANGE, gridLevel );

		int startX, endX;
		if ( lowerLeftXIndex > upperRightXIndex ) {
			startX = upperRightXIndex;
			endX = lowerLeftXIndex;
		} else {
			startX = lowerLeftXIndex;
			endX = upperRightXIndex;
		}

		int startY, endY;
		if ( lowerLeftYIndex > upperRightYIndex ) {
			startY = upperRightYIndex;
			endY = lowerLeftYIndex;
		} else {
			startY = lowerLeftYIndex;
			endY = upperRightYIndex;
		}

		List<String> gridCellsIds = new ArrayList<String>();
		int xIndex, yIndex;
		for ( xIndex = startX; xIndex <= endX; xIndex++ ) {
			for ( yIndex = startY; yIndex <= endY; yIndex++ ) {
				gridCellsIds.add( Integer.toString( xIndex ) + "|" + Integer.toString( yIndex ) );
			}
		}

		return gridCellsIds;
	}

	public static List<String> getGridCellsIds( Point center, double radius, int gridLevel ) {

		Rectangle boundingBox = new Rectangle( center, radius );

		double lowerLeftLatitude, lowerLeftLongitude;
		double upperRightLatitude, upperRightLongitude;

		lowerLeftLatitude = boundingBox.getLowerLeft().getLatitude();
		lowerLeftLongitude = boundingBox.getLowerLeft().getLongitude();
		upperRightLatitude = boundingBox.getUpperRight().getLatitude();
		upperRightLongitude = boundingBox.getUpperRight().getLongitude();

		if ( upperRightLongitude < lowerLeftLongitude ) { // Box cross the 180 meridian
			List<String> gridCellsIds;
			gridCellsIds = getGridCellsIds( new Point( lowerLeftLatitude, lowerLeftLongitude ), new Point( upperRightLatitude, GeometricConstants.LONGITUDE_DEGREE_RANGE / 2 ), gridLevel );
			gridCellsIds.addAll( getGridCellsIds( new Point( lowerLeftLatitude, -GeometricConstants.LONGITUDE_DEGREE_RANGE / 2 ), new Point( upperRightLatitude, upperRightLongitude ), gridLevel ) );
			return gridCellsIds;
		} else {
			return getGridCellsIds( new Point( lowerLeftLatitude, lowerLeftLongitude ), new Point( upperRightLatitude, upperRightLongitude ), gridLevel );
		}
	}

	public static int findBestGridLevelForSearchRange( double searchRange ) {

		double iterations = GeometricConstants.EARTH_EQUATOR_CIRCUMFERENCE_KM / ( 2.0d * searchRange );

		return (int) Math.ceil( Math.log( iterations ) / Math.log( 2 ) );
	}

	public static double[] projectToIndexSpace( Point point ) {
		double[] projectedCoordinates = new double[2];

		projectedCoordinates[0] = Math.toRadians( point.getLongitude() ) * Math.cos( Math.toRadians( point.getLatitude() ) );
		projectedCoordinates[1] = Math.toRadians( point.getLatitude() );

		return projectedCoordinates;
	}


}

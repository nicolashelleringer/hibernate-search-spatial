package net.novacodex.hibernate.search.spatial;

import org.apache.lucene.search.ConstantScoreQuery;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.QueryWrapperFilter;

import java.util.List;

public abstract class SpatialQueryBuilder {

	public static Filter createGridFilter( Point center, double radius, String fieldName ) {
		int bestGridLevel = GridManager.findBestGridLevelForSearchRange( 2.0d * radius );
		if ( bestGridLevel > SpatialFieldBridge.MAX_GRID_LEVEL ) {
			bestGridLevel = SpatialFieldBridge.MAX_GRID_LEVEL;
		}
		List<String> gridCellsIds = GridManager.getGridCellsIds( center, radius, bestGridLevel );
		return new GridFilter( gridCellsIds, FieldUtils.formatFieldName( bestGridLevel, fieldName ) );
	}

	public static Filter createDistanceFilter( Filter previousFilter, Point center, double radius, String fieldName ) {
		return new DistanceFilter( previousFilter, center, radius, fieldName );
	}

	public static Query buildGridQuery( Point center, double radius, String fieldName ) {
		return new ConstantScoreQuery( createGridFilter( center, radius, fieldName ) );
	}

	public static Query buildDistanceQuery( Point center, double radius, String fieldName ) {
		Filter allFilter = new QueryWrapperFilter( new MatchAllDocsQuery() );
		return new ConstantScoreQuery( createDistanceFilter( allFilter, center, radius, fieldName ) );
	}

	public static Query buildSpatialQuery( Point center, double radius, String fieldName ) {
		return new ConstantScoreQuery( createDistanceFilter( createGridFilter( center, radius, fieldName ), center, radius, fieldName ) );
	}
}

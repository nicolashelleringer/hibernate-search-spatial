package net.novacodex.hibernate.search.spatial;

import org.apache.lucene.search.ConstantScoreQuery;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.QueryWrapperFilter;

import java.util.List;

/**
 * @author Nicolas Helleringer
 *
 * The SpatialQueryBuilder hold builder methods for Grid, Distance and Spatial (Grid+Distance) filters and queries
 *
 */
public abstract class SpatialQueryBuilder {
	/**
	 * Returns a Lucene filter which rely on Hibernate Search Spatial
	 * grid indexation to filter document at radius
	 *
	 * @param center center of the search discus
	 * @param radius distance max to center
	 * @param fieldName name of the Lucene Field implementing SpatialIndexable
	 *
	 * @return Lucene filter to be used in a Query
	 *
	 * @see	Query
	 * @see	SpatialIndexable
	 * @see Filter
	 */
	public static Filter buildGridFilter(Point center, double radius, String fieldName) {
		int bestGridLevel = GridHelper.findBestGridLevelForSearchRange( 2.0d * radius );
		if ( bestGridLevel > SpatialFieldBridge.MAX_GRID_LEVEL ) {
			bestGridLevel = SpatialFieldBridge.MAX_GRID_LEVEL;
		}
		List<String> gridCellsIds = GridHelper.getGridCellsIds( center, radius, bestGridLevel );
		return new GridFilter( gridCellsIds, GridHelper.formatFieldName( bestGridLevel, fieldName ) );
	}

	/**
	 * Returns a Lucene filter to fine filter document by distance
	 *
	 * @param center center of the search discus
	 * @param radius distance max to center
	 * @param fieldName name of the Lucene Field implementing SpatialIndexable
	 *
	 * @return Lucene filter to be used in a Query
	 *
	 * @param	previousFilter	preceding filter in filter chain
	 * Warning if passed null DistanceFilter constructor use a
	 * filter wrapped match all query (time/ressource consuming !)
	 * @see	Query
	 * @see	SpatialIndexable
	 * @see	DistanceFilter
	 * @see Filter
	 */
	public static Filter buildDistanceFilter(Filter previousFilter, Point center, double radius, String fieldName) {
		return new DistanceFilter( previousFilter, center, radius, fieldName );
	}

	/**
	 * Returns a Lucene Query which rely on Hibernate Search Spatial
	 * grid indexation to filter document at radius by wrapping a
	 * GridFilter into a ConstantScoreQuery
	 *
	 * @param center center of the search discus
	 * @param radius distance max to center
	 * @param fieldName name of the Lucene Field implementing SpatialIndexable
	 *
	 * @return Lucene Query to be used in a search
	 *
	 * @see	Query
	 * @see	SpatialIndexable
	 * @see ConstantScoreQuery
	 */
	public static Query buildGridQuery(Point center, double radius, String fieldName) {
		return new ConstantScoreQuery( buildGridFilter( center, radius, fieldName ) );
	}

	/**
	 * Returns a Lucene Query searching directly by conmputing distance against
	 * all docs in the index (costly !)
	 *
	 * @param center center of the search discus
	 * @param radius distance max to center
	 * @param fieldName name of the Lucene Field implementing SpatialIndexable
	 *
	 * @return Lucene Query to be used in a search
	 *
	 * @see	Query
	 * @see	SpatialIndexable
	 */
	public static Query buildDistanceQuery(Point center, double radius, String fieldName) {
		Filter allFilter = new QueryWrapperFilter( new MatchAllDocsQuery() );
		return new ConstantScoreQuery( buildDistanceFilter( allFilter, center, radius, fieldName ) );
	}

	/**
	 * Returns a Lucene Query which rely on Hibernate Search Spatial
	 * grid indexation to filter document at radius and filter its results
	 * by a fine DistanceFilter
	 *
	 * @param center center of the search discus
	 * @param radius distance max to center
	 * @param fieldName name of the Lucene Field implementing SpatialIndexable
	 *
	 * @return Lucene Query to be used in a search
	 *
	 * @see	Query
	 * @see	SpatialIndexable
	 */
	public static Query buildSpatialQuery(Point center, double radius, String fieldName) {
		return new ConstantScoreQuery(
				buildDistanceFilter(
						buildGridFilter( center, radius, fieldName ),
						center,
						radius,
						fieldName
				)
		);
	}
}

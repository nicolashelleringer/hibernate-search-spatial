package net.novacodex.hibernate.search.spatial;

import java.io.IOException;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.DocIdSet;
import org.apache.lucene.search.FieldCache;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.FilteredDocIdSet;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.QueryWrapperFilter;

/**
 * @author Nicolas Helleringer
 *         <p/>
 *         Lucene Filter for filtering documents which have been indexed with Hibernate Search Spatial SpatialFieldBridge
 *         Use double lat,long field ine the index from a SpatialIndexable field declaration
 * @see SpatialFieldBridge
 * @see SpatialIndexable
 */
final class DistanceFilter extends Filter {

	private Filter previousFilter;
	private Point center;
	private double radius;
	private String fieldName;

	/**
	 * Construct a Distance Filter to match document distant at most of radius from center Point
	 *
	 * @param previousFilter previous Filter in the chain. As Distance is costly by retrieving the lat and long field
	 * it is better to use it last
	 * @param center center of the search perimeter
	 * @param radius radius of the search perimeter
	 * @param fieldName name of the field implementing SpatialIndexable
	 *
	 * @see SpatialIndexable
	 */
	public DistanceFilter(Filter previousFilter, Point center, double radius, String fieldName) {
		if ( previousFilter != null ) {
			this.previousFilter = previousFilter;
		}
		else {
			this.previousFilter = new QueryWrapperFilter( new MatchAllDocsQuery() );
		}
		this.center = center;
		this.radius = radius;
		this.fieldName = fieldName;
	}

	/**
	 * Returns Doc Ids by retrieving their lat,long and checking if within distance(radius) of the center of the search
	 *
	 * @param reader reader to the index
	 */
	@Override
	public DocIdSet getDocIdSet(IndexReader reader) throws IOException {

		final double[] latitudeValues = FieldCache.DEFAULT.getDoubles( reader, GridHelper.formatLatitude( fieldName ) );
		final double[] longitudeValues = FieldCache.DEFAULT
				.getDoubles( reader, GridHelper.formatLongitude( fieldName ) );

		DocIdSet docs = previousFilter.getDocIdSet( reader );

		if ( ( docs == null ) || ( docs.iterator() == null ) ) {
			return null;
		}

		return new FilteredDocIdSet( docs ) {
			@Override
			protected boolean match(int documentIndex) {

				Double documentDistance;
				Point documentPosition = Point.fromDegrees(
						latitudeValues[documentIndex],
						longitudeValues[documentIndex]
				);
				documentDistance = documentPosition.getDistanceTo( center );
				if ( documentDistance <= radius ) {
					return true;
				}
				else {
					return false;
				}
			}
		};
	}
}
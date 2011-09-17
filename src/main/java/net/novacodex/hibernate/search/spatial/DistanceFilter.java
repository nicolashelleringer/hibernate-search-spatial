package net.novacodex.hibernate.search.spatial;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.DocIdSet;
import org.apache.lucene.search.FieldCache;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.FilteredDocIdSet;

import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.QueryWrapperFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

final class DistanceFilter extends Filter {

	private Filter previousFilter;
	private Point center;
	private double radius;
	private String fieldName;
	private Map<Integer, Double> distanceCache;

	public DistanceFilter( Filter previousFilter, Point center, double radius, String fieldName ) {
		if(previousFilter!=null) {
			this.previousFilter = previousFilter;
		} else {
			this.previousFilter= new QueryWrapperFilter( new MatchAllDocsQuery() );
		}
		this.center = center;
		this.radius = radius;
		this.fieldName = fieldName;
		this.distanceCache = new HashMap<Integer, Double>();
	}

	@Override
	public DocIdSet getDocIdSet( IndexReader reader ) throws IOException {

		final double[] latitudeValues = FieldCache.DEFAULT.getDoubles( reader, FieldUtils.formatLatitude( fieldName ) );
		final double[] longitudeValues = FieldCache.DEFAULT.getDoubles( reader, FieldUtils.formatLongitude( fieldName ) );

		return new FilteredDocIdSet( previousFilter.getDocIdSet( reader ) ) {
			@Override
			protected boolean match( int documentIndex ) {

				Double documentDistance;
				if ( ( documentDistance = distanceCache.get( documentIndex ) ) == null ) {
					Point documentPosition = Point.fromDegrees( latitudeValues[documentIndex], longitudeValues[documentIndex] );
					documentDistance = documentPosition.getDistanceTo( center );
				}
				if ( documentDistance <= radius ) {
					//System.out.println( documentDistance );
					return true;
				} else {
					return false;
				}
			}
		};
	}
}
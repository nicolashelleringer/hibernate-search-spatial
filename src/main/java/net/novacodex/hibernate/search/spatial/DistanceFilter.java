package net.novacodex.hibernate.search.spatial;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.DocIdSet;
import org.apache.lucene.search.FieldCache;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.FilteredDocIdSet;

import java.io.IOException;

public class DistanceFilter extends Filter {

	private Filter previousFilter;
	private Point center;
	private double radius;
	private String fieldName;

	public DistanceFilter( Filter previousFilter, Point center, double radius, String fieldName ) {
		this.previousFilter = previousFilter;
		this.center = center;
		this.radius = radius;
		this.fieldName = fieldName;
	}

	@Override
	public DocIdSet getDocIdSet( IndexReader reader ) throws IOException {

		final double[] latitudeValues = FieldCache.DEFAULT.getDoubles( reader, FieldUtils.formatLatitude( fieldName ) );
		final double[] longitudeValues = FieldCache.DEFAULT.getDoubles( reader, FieldUtils.formatLongitude( fieldName ) );

		return new FilteredDocIdSet( previousFilter.getDocIdSet( reader ) ) {
			@Override
			protected boolean match( int documentIndex ) {
				Point documentPosition = new Point( latitudeValues[documentIndex], longitudeValues[documentIndex] );
				return documentPosition.getDistanceTo( center ) <= radius;
			}
		};
	}
}

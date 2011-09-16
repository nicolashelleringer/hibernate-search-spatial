package net.novacodex.hibernate.search.spatial;

import org.apache.solr.util.NumberUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.search.FullTextQuery;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.hibernate.search.bridge.util.NumericFieldUtils;
import org.hibernate.search.query.dsl.QueryBuilder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.List;

public class LoadGeonames {
	public static void main( String args[] ) {
		try {
			String geonamesFile = "C:\\Dev\\hibernate-search-spatial\\geonames\\FR.txt";
			File geonames = new File( geonamesFile );
			BufferedReader buffRead = new BufferedReader( new FileReader( geonames ) );
			String line = null;

			SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
			Session session = sessionFactory.openSession();
			session.beginTransaction();
			FullTextSession fullTextSession = Search.getFullTextSession( session );
			/*
			 int i = 0;
			 while ( ( line = buffRead.readLine() ) != null ) {
				 String[] data = line.split( "\t" );
				 POI current = new POI( Integer.parseInt( data[0] ), data[1], Double.parseDouble( data[4] ), Double.parseDouble( data[5] ) );
				 session.save( current );
				 fullTextSession.index( current );
				 if ( ( i % 10000 ) == 0 ) {
					 fullTextSession.flushToIndexes();
					 session.flush();
				 }
				 i++;
			 }
			 session.getTransaction().commit();
*/
			Point center = new Point( 45, 4 );

			System.out.println( "With Spatial :" );
			org.apache.lucene.search.Query luceneQuery = SpatialQueryBuilder.buildSpatialQuery( center, 15, "location" );
			FullTextQuery hibQuery = fullTextSession.createFullTextQuery( luceneQuery, POI.class );
			hibQuery.setProjection( "id", "name" );
			final long startTime = System.nanoTime();
			final long endTime;
			List results;
			try {
				results = hibQuery.list();
			} finally {
				endTime = System.nanoTime();
			}
			final long duration = endTime - startTime;
			System.out.println( "Found " + Integer.toString( results.size() ) );
			System.out.print( "Duration : " );
			System.out.println( duration * Math.pow( 10, -9 ) );
			System.out.println();

			System.out.println( "With Distance :" );
			org.apache.lucene.search.Query luceneQuery2 = SpatialQueryBuilder.buildGridQuery( center, 15, "location" );
			org.hibernate.Query hibQuery2 = fullTextSession.createFullTextQuery( luceneQuery2, POI.class );
			final long startTime2 = System.nanoTime();
			final long endTime2;
			List results2;
			try {
				results2 = hibQuery2.list();
			} finally {
				endTime2 = System.nanoTime();
			}
			final long duration2 = endTime2 - startTime2;
			System.out.println( "Found " + Integer.toString( results2.size() ) );
			System.out.print( "Duration : " );
			System.out.println( duration2 * Math.pow( 10, -9 ) );
			System.out.println();

			System.out.println( "With Grid :" );
			org.apache.lucene.search.Query luceneQuery3 = SpatialQueryBuilder.buildDistanceQuery( center, 15, "location" );
			org.hibernate.Query hibQuery3 = fullTextSession.createFullTextQuery( luceneQuery3, POI.class );
			final long startTime3 = System.nanoTime();
			final long endTime3;
			List results3;
			try {
				results3 = hibQuery3.list();
			} finally {
				endTime3 = System.nanoTime();
			}
			final long duration3 = endTime3 - startTime3;
			System.out.println( "Found " + Integer.toString( results3.size() ) );
			System.out.print( "Duration : " );
			System.out.println( duration3 * Math.pow( 10, -9 ) );
			System.out.println();

			System.out.println( "With Double Range :" );
			final QueryBuilder b = fullTextSession.getSearchFactory().buildQueryBuilder().forEntity( POI.class ).get();

			org.apache.lucene.search.Query q = b.bool().must( b.range().onField( "latitude" ).from( 44.86510175911219 ).to( 45.1348982408878 ).createQuery() ).must( b.range().onField( "longitude" ).from( 3.808774680021486 ).to( 4.191225319978514 ).createQuery() ).createQuery();
			//org.apache.lucene.search.Query q = b.range().onField( "latitude" ).above( 0 ).createQuery();
			//org.apache.lucene.search.Query q= NumericFieldUtils.createNumericRangeQuery("latitude",44.86510175911219,45.1348982408878,true,true);
			//org.apache.lucene.search.Query q= NumericFieldUtils.createNumericRangeQuery("longitude",44.86510175911219,45.1348982408878,true,true);
			Query hq = fullTextSession.createFullTextQuery( q, POI.class );
			final long startTime4 = System.nanoTime();
			final long endTime4;
			List results4;
			try {
				results4 = hq.list();
			} finally {
				endTime4 = System.nanoTime();
			}
			final long duration4 = endTime4 - startTime4;
			System.out.println( "Found " + Integer.toString( results4.size() ) );
			System.out.print( "Duration : " );
			System.out.println( duration4 * Math.pow( 10, -9 ) );
			System.out.println();

		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}
}

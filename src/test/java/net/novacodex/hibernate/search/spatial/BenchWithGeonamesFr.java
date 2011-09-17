package net.novacodex.hibernate.search.spatial;

import org.apache.lucene.search.ConstantScoreQuery;
import org.apache.lucene.search.QueryWrapperFilter;
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
import org.hibernate.search.query.hibernate.impl.FullTextQueryImpl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.List;

public class BenchWithGeonamesFr {
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
			long gridTotalDuration= 0;
			long spatialTotalDuration= 0;
			long doubleRangeTotalDuration= 0;
			long distanceDoubleRangeTotalDuration= 0;

			for ( int i = 0; i < 100; i++ ) {
				//System.out.println( "With Spatial :" );
				org.apache.lucene.search.Query luceneQuery = SpatialQueryBuilder.buildSpatialQuery( center, 15, "location" );
				FullTextQuery hibQuery = fullTextSession.createFullTextQuery( luceneQuery, POI.class );
				hibQuery.setProjection( "id", "name" );
				long startTime = System.nanoTime();
				long endTime;
				List results;
				try {
					results = hibQuery.list();
				} finally {
					endTime = System.nanoTime();
				}
				long duration = endTime - startTime;
				//System.out.println( "Found " + Integer.toString( results.size() ) );
				//System.out.print( "Duration : " );
				//System.out.println( duration * Math.pow( 10, -9 ) );
				//System.out.println();
				spatialTotalDuration+= duration;

				//System.out.println( "With Grid :" );
				luceneQuery = SpatialQueryBuilder.buildGridQuery( center, 15, "location" );
				hibQuery = fullTextSession.createFullTextQuery( luceneQuery, POI.class );
				hibQuery.setProjection( "id", "name" );
				startTime = System.nanoTime();

				try {
					results = hibQuery.list();
				} finally {
					endTime = System.nanoTime();
				}
				duration = endTime - startTime;
				//System.out.println( "Found " + Integer.toString( results.size() ) );
				//System.out.print( "Duration : " );
				//System.out.println( duration * Math.pow( 10, -9 ) );
				//System.out.println();
				gridTotalDuration+=duration;

				/*
				System.out.println( "With Distance :" );
				luceneQuery = SpatialQueryBuilder.buildDistanceQuery( center, 15, "location" );
				hibQuery = fullTextSession.createFullTextQuery( luceneQuery, POI.class );
				hibQuery.setProjection( "id", "name" );
				startTime = System.nanoTime();
				try {
					results = hibQuery.list();
				} finally {
					endTime = System.nanoTime();
				}
				duration = endTime - startTime;
				System.out.println( "Found " + Integer.toString( results.size() ) );
				System.out.print( "Duration : " );
				System.out.println( duration * Math.pow( 10, -9 ) );
				System.out.println();
				*/
				
				//System.out.println( "With Double Range :" );
				final QueryBuilder b = fullTextSession.getSearchFactory().buildQueryBuilder().forEntity( POI.class ).get();
				org.apache.lucene.search.Query q = b.bool().must( b.range().onField( "latitude" ).from( 44.86510175911219 ).to( 45.1348982408878 ).createQuery() ).must( b.range().onField( "longitude" ).from( 3.808774680021486 ).to( 4.191225319978514 ).createQuery() ).createQuery();
				FullTextQuery hq = fullTextSession.createFullTextQuery( q, POI.class );
				hq.setProjection( "id", "name" );
				startTime = System.nanoTime();
				try {
					results = hq.list();
				} finally {
					endTime = System.nanoTime();
				}
				duration = endTime - startTime;
				//System.out.println( "Found " + Integer.toString( results.size() ) );
				//System.out.print( "Duration : " );
				//System.out.println( duration * Math.pow( 10, -9 ) );
				//System.out.println();
				doubleRangeTotalDuration+=duration;

				//System.out.println( "With Double Range + Distance :" );
				q = b.bool().must( b.range().onField( "latitude" ).from( 44.86510175911219 ).to( 45.1348982408878 ).createQuery() ).must( b.range().onField( "longitude" ).from( 3.808774680021486 ).to( 4.191225319978514 ).createQuery() ).createQuery();
				org.apache.lucene.search.Query filteredQuery = new ConstantScoreQuery( SpatialQueryBuilder.createDistanceFilter( new QueryWrapperFilter( q ), center, 15, "location" ) );
				hq = fullTextSession.createFullTextQuery( filteredQuery, POI.class );
				hq.setProjection( "id", "name" );
				startTime = System.nanoTime();
				try {
					results = hq.list();
				} finally {
					endTime = System.nanoTime();
				}
				duration = endTime - startTime;
				//System.out.println( "Found " + Integer.toString( results.size() ) );
				//System.out.print( "Duration : " );
				//System.out.println( duration * Math.pow( 10, -9 ) );
				//System.out.println();
				distanceDoubleRangeTotalDuration+=duration;
			}

			System.out.println(" Mean time with Grid : "+Double.toString((double)gridTotalDuration/100.0d*Math.pow( 10, -9 )));
			System.out.println(" Mean time with Grid + Distance filter : "+Double.toString((double)spatialTotalDuration/100.0d*Math.pow( 10, -9 )));
			System.out.println(" Mean time with DoubleRange : "+Double.toString((double)doubleRangeTotalDuration/100.0d*Math.pow( 10, -9 )));
			System.out.println( " Mean time with DoubleRange + Distance filter : " + Double.toString( (double) distanceDoubleRangeTotalDuration / 100.0d * Math.pow( 10, -9 ) ) );

		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}
}

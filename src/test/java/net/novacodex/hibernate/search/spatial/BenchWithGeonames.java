package net.novacodex.hibernate.search.spatial;

import org.apache.lucene.search.ConstantScoreQuery;
import org.apache.lucene.search.QueryWrapperFilter;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.search.FullTextQuery;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.hibernate.search.query.dsl.QueryBuilder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.List;

public class BenchWithGeonames {
	public static void main(String args[]) {
		Session session = null;
		FullTextSession fullTextSession = null;
		try {
			SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();

			session = sessionFactory.openSession();
			session.beginTransaction();
			fullTextSession = Search.getFullTextSession( session );

			long gridTotalDuration = 0;
			long spatialTotalDuration = 0;
			long doubleRangeTotalDuration = 0;
			long distanceDoubleRangeTotalDuration = 0;

			org.apache.lucene.search.Query luceneQuery;
			long startTime, endTime, duration;
			FullTextQuery hibQuery;
			List results;
			QueryBuilder b;
			org.apache.lucene.search.Query q;
			final Integer iterations = 200;
			final Integer warmUp = 25;

			for ( int i = 0; i < iterations; i++ ) {
				Point center = Point.fromDegrees( Math.random() * 180 - 90, Math.random() * 360 - 180 );
				double radius = 25.0d;
				Rectangle boundingBox = Rectangle.fromBoundingCircle( center, radius );

				session = sessionFactory.openSession();
				session.beginTransaction();
				fullTextSession = Search.getFullTextSession( session );
				b = fullTextSession.getSearchFactory().buildQueryBuilder().forEntity( POI.class ).get();
				q = b.bool()
						.must(
								b.range()
										.onField( "latitude" )
										.from( boundingBox.getLowerLeft().getLatitude() )
										.to( boundingBox.getUpperRight().getLatitude() )
										.createQuery()
						)
						.must(
								b.range()
										.onField( "longitude" )
										.from( boundingBox.getLowerLeft().getLongitude() )
										.to( boundingBox.getUpperRight().getLongitude() )
										.createQuery()
						)
						.createQuery();
				hibQuery = fullTextSession.createFullTextQuery( q, POI.class );
				hibQuery.setProjection( "id", "name" );
				startTime = System.nanoTime();
				try {
					hibQuery.getResultSize();
				}
				finally {
					endTime = System.nanoTime();
				}
				duration = endTime - startTime;
				if ( i > ( iterations - warmUp ) ) {
					doubleRangeTotalDuration += duration;
				}
				session.close();

				session = sessionFactory.openSession();
				session.beginTransaction();
				fullTextSession = Search.getFullTextSession( session );
				b = fullTextSession.getSearchFactory().buildQueryBuilder().forEntity( POI.class ).get();
				q = b.bool()
						.must(
								b.range()
										.onField( "latitude" )
										.from( boundingBox.getLowerLeft().getLatitude() )
										.to( boundingBox.getUpperRight().getLatitude() )
										.createQuery()
						)
						.must(
								b.range()
										.onField( "longitude" )
										.from( boundingBox.getLowerLeft().getLongitude() )
										.to( boundingBox.getUpperRight().getLongitude() )
										.createQuery()
						)
						.createQuery();
				org.apache.lucene.search.Query filteredQuery = new ConstantScoreQuery(
						SpatialQueryBuilder.buildDistanceFilter(
								new QueryWrapperFilter( q ),
								center,
								radius,
								"location"
						)
				);
				hibQuery = fullTextSession.createFullTextQuery( filteredQuery, POI.class );
				hibQuery.setProjection( "id", "name" );
				startTime = System.nanoTime();
				try {
					hibQuery.getResultSize();
				}
				finally {
					endTime = System.nanoTime();
				}
				duration = endTime - startTime;
				if ( i > ( iterations - warmUp ) ) {
					distanceDoubleRangeTotalDuration += duration;
				}
				session.close();

				session = sessionFactory.openSession();
				session.beginTransaction();
				fullTextSession = Search.getFullTextSession( session );
				luceneQuery = SpatialQueryBuilder.buildGridQuery( center, radius, "location" );
				hibQuery = fullTextSession.createFullTextQuery( luceneQuery, POI.class );
				hibQuery.setProjection( "id", "name" );
				startTime = System.nanoTime();

				try {
					hibQuery.getResultSize();
				}
				finally {
					endTime = System.nanoTime();
				}
				duration = endTime - startTime;
				if ( i > ( iterations - warmUp ) ) {
					gridTotalDuration += duration;
				}
				session.close();

				session = sessionFactory.openSession();
				session.beginTransaction();
				fullTextSession = Search.getFullTextSession( session );
				luceneQuery = SpatialQueryBuilder.buildSpatialQuery( center, radius, "location" );
				hibQuery = fullTextSession.createFullTextQuery( luceneQuery, POI.class );
				hibQuery.setProjection( "id", "name" );
				startTime = System.nanoTime();

				try {
					hibQuery.getResultSize();
				}
				finally {
					endTime = System.nanoTime();
				}
				duration = endTime - startTime;
				if ( i > ( iterations - warmUp ) ) {
					spatialTotalDuration += duration;
				}
				session.close();
			}

			System.out
					.println(
							" Mean time with Grid : " + Double.toString(
									( double ) gridTotalDuration / 100.0d * Math.pow(
											10,
											-9
									)
							)
					);
			System.out
					.println(
							" Mean time with Grid + Distance filter : " + Double.toString(
									( double ) spatialTotalDuration / 100.0d * Math.pow( 10, -9 )
							)
					);
			System.out
					.println(
							" Mean time with DoubleRange : " + Double.toString(
									( double ) doubleRangeTotalDuration / 100.0d * Math.pow( 10, -9 )
							)
					);
			System.out
					.println(
							" Mean time with DoubleRange + Distance filter : " + Double.toString(
									( double ) distanceDoubleRangeTotalDuration / 100.0d * Math.pow( 10, -9 )
							)
					);

		}
		catch ( Exception e ) {
			e.printStackTrace();
		}
		finally {
			if ( session != null && session.isOpen() ) {
				session.close();
			}
		}
	}

	public void LoadGeonames() {
		Session session = null;
		FullTextSession fullTextSession = null;
		try {
			SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();

			session = sessionFactory.openSession();
			session.beginTransaction();
			fullTextSession = Search.getFullTextSession( session );

			String geonamesFileName = "geonames\\allcountries.txt";
			File geonamesFiles = new File( geonamesFileName );
			BufferedReader buffRead = new BufferedReader( new FileReader( geonamesFiles ) );
			String line = null;

			int line_number = 0;
			while ( ( line = buffRead.readLine() ) != null ) {
				String[] data = line.split( "\t" );
				POI current = new POI(
						Integer.parseInt( data[0] ),
						data[1],
						Double.parseDouble( data[4] ),
						Double.parseDouble( data[5] )
				);
				session.save( current );
				if ( ( line_number % 10000 ) == 0 ) {
					fullTextSession.flushToIndexes();
					session.getTransaction().commit();
					session.close();
					session = sessionFactory.openSession();
					session.beginTransaction();
					fullTextSession = Search.getFullTextSession( session );
					session.beginTransaction();
					System.out.println( Integer.toString( line_number ) );
				}
				line_number++;
			}
			session.getTransaction().commit();
			session.close();
		}
		catch ( Exception e ) {
			e.printStackTrace();
		}
		finally {
			if ( session != null && session.isOpen() ) {
				session.close();
			}
		}
	}
}

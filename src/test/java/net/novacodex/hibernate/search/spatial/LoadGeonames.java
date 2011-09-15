package net.novacodex.hibernate.search.spatial;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

/**
 * Created by IntelliJ IDEA.
 * User: Nicolas
 * Date: 15/09/11
 * Time: 16:49
 * To change this template use File | Settings | File Templates.
 */
public class LoadGeonames {
	public static void main( String args[] ) {
		try {
			String geonamesFile = "C:\\Dev\\hibernate-search-spatial\\geonames\\allCountries.txt";
			File geonames = new File( geonamesFile );
			BufferedReader buffRead = new BufferedReader( new FileReader( geonames ) );
			String line = null;

			SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
			Session session = sessionFactory.openSession();
			FullTextSession fullTextSession = Search.getFullTextSession( session );

			int i = 0;
			while ( ( line = buffRead.readLine() ) != null ) {
				String[] data = line.split( "\t" );
				POI current = new POI( Integer.parseInt( data[0] ), data[1], Double.parseDouble( data[4] ), Double.parseDouble( data[5] ) );
				session.save( current );
				fullTextSession.index( current );
				if ( ( i % 100000 ) == 0 ) {
					fullTextSession.flushToIndexes();
					session.flush();
				}
				i++;
			}
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}
}

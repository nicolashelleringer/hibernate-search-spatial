package net.novacodex.hibernate.search.spatial;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.Search;
import org.junit.Test;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class IndexingTest {
	@Test
	public void IndexingTest() {
		Point point= new Point(24,32);

		POI poi= new POI(1,"Test",point);

		SessionFactory sessionFactory= new Configuration().configure().buildSessionFactory();
		Session session= sessionFactory.openSession();
		FullTextSession fullTextSession = Search.getFullTextSession(session);

		try {
			session.save(poi);
			fullTextSession.index(poi);
			fullTextSession.flushToIndexes();
		} catch(Exception e) {
			e.printStackTrace();
		}

		try {
			Point center= new Point(24,31.5);
			org.apache.lucene.search.Query luceneQuery= GridManager.buildSpatialQuery(center,1,"position");

			org.hibernate.Query hibQuery = fullTextSession.createFullTextQuery(luceneQuery, POI.class);

			List result= hibQuery.list();

			int size= result.size();

		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}

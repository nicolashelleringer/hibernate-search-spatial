package net.novacodex.GridManager;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.Search;
import org.junit.Test;

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
	}
}

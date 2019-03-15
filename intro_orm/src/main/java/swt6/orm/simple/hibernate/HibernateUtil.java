package swt6.orm.simple.hibernate;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {
	private final static String HIBERNATE_CONFIGURATION_FILE_PATH = "hibernate.cfg.xml";

	private static SessionFactory sessionFactory;

	// creating a SessionFactory is time consuming
	// this should be done only once per database
	// SessionFactory is thread-safe
	public static SessionFactory getSessionFactory() {
		if (sessionFactory == null) {
			sessionFactory =
					new Configuration()
							.configure(HIBERNATE_CONFIGURATION_FILE_PATH)
							.buildSessionFactory();
		}
		return sessionFactory;
	}

	public static void closeSessionFactory() {
		if (sessionFactory != null) {
			sessionFactory.close();
			sessionFactory = null;
		}
	}

	// Session is a lightweight component, but isn' thread-safe
	// therefore, a separate connection has to be opened for each thread
	public static Session getCurrentSession() {
		// SessionFactory::getCurrentSession requires settings the property "hibernate.current_session_context_class" to "thread"
		return getSessionFactory().getCurrentSession();
	}
}

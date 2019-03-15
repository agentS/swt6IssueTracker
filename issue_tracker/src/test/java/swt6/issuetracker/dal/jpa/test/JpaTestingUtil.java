package swt6.issuetracker.dal.jpa.test;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public final class JpaTestingUtil {
	private static final String PERSISTENCE_UNIT_NAME = "IssueTrackerTestingPU";

	private static EntityManagerFactory entityManagerFactory;

	private JpaTestingUtil() {
	}

	public static synchronized EntityManagerFactory getEntityManagerFactory() {
		if (entityManagerFactory == null) {
			entityManagerFactory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
		}
		return entityManagerFactory;
	}
}

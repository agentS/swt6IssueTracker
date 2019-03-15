package swt6.util;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class JpaUtil {
	private static final String PERSISTENCE_UNIT_NAME = "WorklogPU";

	private static EntityManagerFactory entityManagerFactory;
	private static ThreadLocal<EntityManager> entityManagerThread = new ThreadLocal<>();

	public static synchronized EntityManagerFactory getEntityManagerFactory() {
		if (entityManagerFactory == null) {
			entityManagerFactory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
		}
		return entityManagerFactory;
	}

	public static synchronized EntityManager getEntityManager() {
		if (entityManagerThread.get() == null) {
			entityManagerThread.set(getEntityManagerFactory().createEntityManager());
		}

		return entityManagerThread.get();
	}

	public static synchronized void closeEntityManager() {
		if (entityManagerThread.get() != null) {
			entityManagerThread.get().close();
			entityManagerThread.set(null);
		}
	}

	public static synchronized EntityManager getTransactedEntityManager() {
		EntityManager entityManager = getEntityManager();
		EntityTransaction transaction = entityManager.getTransaction();

		if (!transaction.isActive()) {
			transaction.begin();
		}
		return entityManager;
	}

	public static synchronized void commit() {
		EntityManager entityManager = getEntityManager();
		EntityTransaction transaction = entityManager.getTransaction();

		if (transaction.isActive()) {
			transaction.commit();
		}
		closeEntityManager();
	}

	public static synchronized void rollback() {
		EntityManager entityManager = getEntityManager();
		EntityTransaction transaction = entityManager.getTransaction();

		if (transaction.isActive()) {
			transaction.rollback();
		}
		closeEntityManager();
	}

	public static synchronized void closeEntityManagerFactory() {
		if (entityManagerFactory != null) {
			entityManagerFactory.close();
			entityManagerFactory = null;
		}
	}
}

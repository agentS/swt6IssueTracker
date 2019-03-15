package swt6.issuetracker.dal.jpa;

import swt6.issuetracker.dal.DalTransaction;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

public class DalTransactionJpa implements DalTransaction {
	private final ThreadLocal<EntityManager> entityManager = new ThreadLocal<>();

	public DalTransactionJpa(EntityManagerFactory entityManagerFactory) {
		this(entityManagerFactory.createEntityManager());
	}

	public DalTransactionJpa(EntityManager entityManager) {
		this.entityManager.set(entityManager);
	}

	EntityManager getEntityManager() {
		return this.entityManager.get();
	}

	@Override
	public void begin() {
		EntityManager entityManager = this.getEntityManager();
		EntityTransaction transaction = entityManager.getTransaction();

		if (!transaction.isActive()) {
			transaction.begin();
		}
	}

	@Override
	public void commit() {
		EntityManager entityManager = this.getEntityManager();
		EntityTransaction transaction = entityManager.getTransaction();

		if (transaction.isActive()) {
			transaction.commit();
		}
		this.closeEntityManager();

	}

	@Override
	public void rollback() {
		EntityManager entityManager = this.getEntityManager();
		EntityTransaction transaction = entityManager.getTransaction();

		if (transaction.isActive()) {
			transaction.rollback();
		}
		this.closeEntityManager();
	}

	private void closeEntityManager() {
		if (this.entityManager.get() != null) {
			this.entityManager.get().close();
			this.entityManager.set(null);
		}
	}

	@Override
	public boolean isOpen() {
		EntityManager entityManager = this.getEntityManager();
		if (entityManager != null) {
			EntityTransaction transaction = entityManager.getTransaction();
			return transaction.isActive();
		}
		return false;
	}
}

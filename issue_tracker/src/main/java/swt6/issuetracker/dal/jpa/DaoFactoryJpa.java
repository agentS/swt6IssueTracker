package swt6.issuetracker.dal.jpa;

import swt6.issuetracker.dal.*;
import swt6.util.JpaUtil;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

public class DaoFactoryJpa implements DaoFactory {
	private final EntityManagerFactory entityManagerFactory;

	public DaoFactoryJpa(EntityManagerFactory entityManagerFactory) {
		this.entityManagerFactory = entityManagerFactory;
	}

	@Override
	public DalTransaction createTransaction() {
		return new DalTransactionJpa(this.entityManagerFactory);
	}

	@Override
	public EmployeeDao createEmployeeDao() {
		return new EmployeeDaoJpa();
	}

	@Override
	public ProjectDao createProjectDao() {
		return new ProjectDaoJpa();
	}

	@Override
	public IssueDao createIssueDao() {
		return new IssueDaoJpa();
	}
}

package swt6.orm.jpa;

import swt6.orm.domain.*;
import swt6.util.JpaUtil;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class WorkLogManager {
	private static final String PERSISTENCE_UNIT_NAME = "WorklogPU";

	private static void insertEmployeeVersion0(Employee empl) {
		EntityManagerFactory entityManagerFactory =
				Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
		EntityManager entityManager = null;
		EntityTransaction transaction = null;

		try {
			entityManager = entityManagerFactory.createEntityManager();
			transaction = entityManager.getTransaction();
			transaction.begin();

			entityManager.persist(empl);

			transaction.commit();
		} catch (Exception exception) {
			if ((transaction != null) && (transaction.isActive())) {
				transaction.rollback();
			}
			throw exception;
		} finally {
			if (entityManager != null) {
				entityManager.close();
			}
		}
		entityManagerFactory.close();
	}

	private static void insertEmployee(Employee employee) {
		try {
			EntityManager entityManager = JpaUtil.getTransactedEntityManager();
			entityManager.persist(employee);
			JpaUtil.commit();
		} catch (Exception exception) {
			JpaUtil.rollback();
			throw exception;
		}
	}

	private static <T> void insertEntity(T entity) {
		try {
			EntityManager entityManager = JpaUtil.getTransactedEntityManager();
			entityManager.persist(entity);
			JpaUtil.commit();
		} catch (Exception exception) {
			JpaUtil.rollback();
			throw exception;
		}
	}

	private static <T> T saveEntity(T entity) {
		try {
			EntityManager entityManager = JpaUtil.getTransactedEntityManager();
			entity = entityManager.merge(entity);
			JpaUtil.commit();
		} catch (Exception exception) {
			JpaUtil.rollback();
			throw exception;
		}
		return entity;
	}

	private static Employee addLogbookEntries(
			Employee employee,
			LogbookEntry... entries
	) {
		try {
			EntityManager entityManager = JpaUtil.getTransactedEntityManager();
			employee = entityManager.merge(employee);

			for (LogbookEntry entry : entries) {
				employee.addLogbookEntry(entry);
			}

			JpaUtil.commit();

			if ((employee.getLogbookEntries() != null) && (employee.getLogbookEntries().size() > 0)) {
				System.out.println("    logbookEntries:\n");
				for (LogbookEntry logbookEntry : employee.getLogbookEntries()) {
					System.out.println("    " + logbookEntry.toString());
				}
			}
		} catch (Exception exception) {
			JpaUtil.rollback();
		}

		return employee;
	}

	private static void listEmployees() {
		try {
			EntityManager entityManager = JpaUtil.getTransactedEntityManager();
			List<Employee> employees = entityManager
					.createQuery("SELECT E FROM Employee AS E ORDER BY lastName DESC", Employee.class)
					.getResultList();
			JpaUtil.commit();

			employees.forEach(System.out::println);
		} catch (Exception e) {
			JpaUtil.rollback();
			throw e;
		}
	}

	public static void main(String[] args) {
		try {
			System.out.println("----- create schema -----");
			JpaUtil.getEntityManagerFactory(); // initializes the database schema
			Employee founder = new Employee("Bill", "Gates", LocalDate.of(1970, 1, 21));
			Employee cofounder = new Employee("Steve", "Ballmer", LocalDate.of(1956, 3, 24));
			cofounder.setAddress(new Address("4232", "Hagenberg", "Softwarepark 11"));

			System.out.println("----- insertEmployee -----");
			founder = saveEntity(founder);
			cofounder = saveEntity(cofounder);

			System.out.println("----- listEmployees -----");
			listEmployees();

			System.out.println("----- addLogbookEntries -----");
			LogbookEntry entryA = new LogbookEntry(
					"Analysis",
					LocalDateTime.of(2019, 3, 12, 8, 0),
					LocalDateTime.of(2019, 3, 12, 10, 0)
			);

			LogbookEntry entryB = new LogbookEntry(
					"Implementation",
					LocalDateTime.of(2019, 3, 12, 10, 0),
					LocalDateTime.of(2019, 3, 12, 14, 0)
			);
			addLogbookEntries(founder, entryA, entryB);

			System.out.println("----- listEmployees -----");
			listEmployees();
		} finally {
			JpaUtil.closeEntityManagerFactory();
		}
	}
}

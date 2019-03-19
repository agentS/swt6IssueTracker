package swt6.orm.jpa;

import swt6.orm.domain.*;
import swt6.util.JpaUtil;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Root;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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

			employees.forEach(employee -> {
				System.out.println(employee);
				if (employee.getAddress() != null) {
					System.out.println("\taddress:");
					System.out.println("\t" + employee.getAddress());
				}
				if (employee.getProjects().size() > 0) {
					System.out.println("\tprojects:");
					employee.getProjects().forEach(project -> System.out.printf("\t%s%n", project.getName()));
				}
			});

			JpaUtil.commit();
		} catch (Exception e) {
			JpaUtil.rollback();
			throw e;
		}
	}

	private static Employee assignProjectToEmployee(Employee employee, Project... projects) { // employee and projects are detached
		try {
			EntityManager entityManager = JpaUtil.getTransactedEntityManager();

			employee = entityManager.merge(employee);
			for (Project project : projects) {
				Project targetProject = entityManager.merge(project);
				employee.assignProject(targetProject);
			}

			JpaUtil.commit();
			return employee;
		} catch (Exception e) {
			JpaUtil.rollback();
			throw e;
		}
	}

	private static void testFetchingStrategies() {
		// prepare: fetch valid ids for employee and logbookentry
		Long entryId = null;
		Long employeeId = null;

		try {
			EntityManager em = JpaUtil.getTransactedEntityManager();

			Optional<LogbookEntry> entry =
					em.createQuery("select le from LogbookEntry le", LogbookEntry.class)
							.setMaxResults(1)
							.getResultList().stream().findAny();
			if (!entry.isPresent()) return;
			entryId = entry.get().getId();

			Optional<Employee> employee =
					em.createQuery("select e from Employee e", Employee.class)
							.setMaxResults(1)
							.getResultList().stream().findAny();
			if (!employee.isPresent()) return;
			employeeId = employee.get().getId();

			JpaUtil.commit();
		}
		catch (Exception e) {
			JpaUtil.rollback();
			throw e;
		}

		System.out.println("############################################");

		try {
			EntityManager entityManager = JpaUtil.getTransactedEntityManager();

			System.out.println("###> Fetching employee");
			Employee employee = entityManager.find(Employee.class, employeeId);
			System.out.println("###> Fetched employee");
			System.out.println("###> Fetching associated logbook entries");
			Set<LogbookEntry> entries = employee.getLogbookEntries();
			System.out.println("###> Fetched associated logbook entries");
			System.out.println("###> Accessing associated logbook entries");
			for (LogbookEntry entry : entries) {
				System.out.println("\t" + entry);
			}
			System.out.println("###> Accessed associated logbook entries");

			JpaUtil.commit();
		}
		catch (Exception e) {
			JpaUtil.rollback();
			throw e;
		}

		System.out.println("############################################");

		try {
			EntityManager em = JpaUtil.getTransactedEntityManager();

			// TODO

			JpaUtil.commit();
		}
		catch (Exception e) {
			JpaUtil.rollback();
			throw e;
		}

		System.out.println("############################################");
	}

	private static void listEntriesOfEmployee(Employee employee) {
		try {
			EntityManager entityManager = JpaUtil.getTransactedEntityManager();

			System.out
					.printf("logbook entries of employee: %s (%d)%n",
							employee.getLastName(), employee.getId());

			// Keep in mind: HQL/JPQL queries refer to Java (domain) objects not to
			// database tables.

			//Version Alpha: Concatenate SQL query string manually
			// watch out for SQL injections
//			TypedQuery<LogbookEntry> query =
//					entityManager.createQuery("from LogbookEntry where employee.id = " +
//							employee.getId(), LogbookEntry.class);

			// Version Bravo: Use named SQL parameters
//			TypedQuery<LogbookEntry> query = entityManager.createQuery(
//					"FROM LogbookEntry WHERE employee.id = :employeeId",
//					LogbookEntry.class
//			);
//			query.setParameter("employeeId", employee.getId());

			// Version Charlie: Use domain objects instead of IDs and named SQL parameters
			employee.setDateOfBirth(LocalDate.of(1955, 10, 28));
			TypedQuery<LogbookEntry> query = entityManager.createQuery(
					"FROM LogbookEntry WHERE employee = :employee",
					LogbookEntry.class
			);
			query.setParameter("employee", employee);

			query.getResultList().forEach(System.out::println);

			JpaUtil.commit();
		}
		catch (Exception e) {
			JpaUtil.rollback();
			throw e;
		}
	}

	private static void testJoinQuery(String postalCode) {
		try {
			EntityManager entityManager = JpaUtil.getTransactedEntityManager();
			System.out.printf("logbook entries of postal code %s%n", postalCode);

			TypedQuery<LogbookEntry> query = entityManager.createQuery(
					"SELECT L FROM LogbookEntry AS L WHERE L.employee.address.zipCode = :postalCode",
					LogbookEntry.class
			);
			query.setParameter("postalCode", postalCode);
			query.getResultStream().forEach(System.out::println);

			JpaUtil.commit();
		}
		catch (Exception e) {
			JpaUtil.rollback();
			throw e;
		}
	}

	private static void testFetchJoinQuery() {
		try {
			EntityManager entityManager = JpaUtil.getTransactedEntityManager();
			System.out.printf("all employees%n");

			TypedQuery<Employee> query = entityManager.createQuery(
					"SELECT DISTINCT E FROM Employee AS E JOIN FETCH E.logbookEntries AS L",
					Employee.class
			);
			query.getResultStream().forEach(System.out::println);

			JpaUtil.commit();
		}
		catch (Exception e) {
			JpaUtil.rollback();
			throw e;
		}
	}

	private static void listEntriesOfEmployeeCQ(Employee employee) {
		try {
			EntityManager em = JpaUtil.getTransactedEntityManager();

			CriteriaBuilder cb      = em.getCriteriaBuilder();
			CriteriaQuery<LogbookEntry> entryCQ = cb.createQuery(LogbookEntry.class);
			Root<LogbookEntry> entry   = entryCQ.from(LogbookEntry.class);
			ParameterExpression<Employee> p       = cb.parameter(Employee.class);

			CriteriaQuery<LogbookEntry> entriesOfEmplCQ =
					entryCQ.where(cb.equal(entry.get(LogbookEntry_.employee), p)).select(entry);

			TypedQuery<LogbookEntry> entriesOfEmplQry = em.createQuery(entriesOfEmplCQ);
			entriesOfEmplQry.setParameter(p, employee);
			entriesOfEmplQry.getResultStream().forEach(System.out::println);

			JpaUtil.commit();
		}
		catch (Exception e) {
			JpaUtil.rollback();
			throw e;
		}
	}

	public static void main(String[] args) {
		try {
			System.out.println("----- create schema -----");
			JpaUtil.getEntityManagerFactory(); // initializes the database schema
			PermanentEmployee permanentEmployee = new PermanentEmployee("Bill", "Gates", LocalDate.of(1970, 1, 21));
			permanentEmployee.setSalary(6666.999);
			Employee founder = permanentEmployee;
			TemporaryEmployee temporaryEmployee = new TemporaryEmployee("Steve", "Ballmer", LocalDate.of(1956, 3, 24));
			temporaryEmployee.setStartDate(LocalDate.of(2019, 1, 1));
			temporaryEmployee.setEndDate(LocalDate.of(2019, 12, 31));
			temporaryEmployee.setHourlyRate(90);
			temporaryEmployee.setRenter("Microsoft");
			Employee cofounder = temporaryEmployee;
			cofounder.setAddress(new Address("4232", "Hagenberg", "Softwarepark 11"));

			Project projectA = new Project("Office");
			Project projectB = new Project("Enterprise Server");

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

			LogbookEntry entryC = new LogbookEntry(
					"Give a conference talk",
					LocalDateTime.of(2019, 3, 19, 10, 0),
					LocalDateTime.of(2019, 3, 19, 11, 0)
			);
			addLogbookEntries(cofounder, entryC);

			System.out.println("----- listEmployees -----");
			listEmployees();

			System.out.println("----- insertProjects -----");
			projectA = saveEntity(projectA);
			projectB = saveEntity(projectB);

			System.out.println("----- assignProjectsToEmployees -----");
			founder = assignProjectToEmployee(founder, projectB);
			cofounder = assignProjectToEmployee(cofounder, projectA, projectB);

			System.out.println("----- listEmployees -----");
			listEmployees();

			//System.out.println("----- testFetchingStrategies -----");
			//testFetchingStrategies();

			System.out.println("----- listEntriesOfEmployee -----");
			listEntriesOfEmployee(founder);

			System.out.println("----- testJoinQuery -----");
			testJoinQuery("4232");

			//System.out.println("----- testFetchJoinQuery -----");
			//testFetchJoinQuery();

			System.out.println("----- listEntriesOfEmployeeCQ -----");
			listEntriesOfEmployeeCQ(founder);
		} finally {
			JpaUtil.closeEntityManagerFactory();
		}
	}
}

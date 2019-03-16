package swt6.issuetracker.dal.jpa.test;

import org.junit.BeforeClass;
import org.junit.Test;
import swt6.issuetracker.dal.DalTransaction;
import swt6.issuetracker.dal.EmployeeDao;
import swt6.issuetracker.dal.jpa.DalTransactionJpa;
import swt6.issuetracker.dal.jpa.EmployeeDaoJpa;
import swt6.issuetracker.domain.Address;
import swt6.issuetracker.domain.Employee;
import swt6.issuetracker.domain.LogBookEntry;
import swt6.issuetracker.domain.ProjectPhase;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hibernate.testing.transaction.TransactionUtil.*;
import static org.junit.Assert.*;

public class EmployeeDaoTest {
	@BeforeClass
	public static void initializeDemoData() {
		doInJPA(JpaTestingUtil::getEntityManagerFactory, entityManager -> {
			Employee founder = new Employee(
					"Charles Montgomery", "Burns",
					LocalDate.of(1882, 1, 1),
					new Address("00815", "Springfield", "1000 Mammon Lane")
			);
			entityManager.persist(founder);

			Employee formerEmployeeA = new Employee(
					"Frank", "Grimes",
					LocalDate.of(1975, 6, 18),
					new Address("00815", "Springfield", "57 Bowling Valley")
			);
			entityManager.persist(formerEmployeeA);

			Employee formerEmployeeB = new Employee(
					"Canary M.", "Burns",
					LocalDate.of(1995, 9, 30),
					new Address("00815", "Springfield", "1 Nuclear Way")
			);
			entityManager.persist(formerEmployeeB);

			Employee updatedEmployeeA = new Employee(
					"Queenie", "Chicken",
					LocalDate.of(1985, 04, 01),
					new Address("00815", "Springfield", "12 Farm Road")
			);
			entityManager.persist(updatedEmployeeA);

			Employee updatedEmployeeB = new Employee(
					"Monty", "Burns",
					LocalDate.of(1882, 1, 1),
					new Address("00815", "Springfield", "1001 Mammon Lane")
			);
			entityManager.persist(updatedEmployeeB);

			Employee busyEmployeeA = new Employee(
					"Waylon Jr", "Smithers",
					LocalDate.of(1954, 7, 30),
					new Address("00815", "Springfield", "99 Bachelor Rd")
			);
			busyEmployeeA.addLogbookEntry(new LogBookEntry(
					"Prepare Mr Burn's breakfast",
					LocalDateTime.of(2019, 3, 14, 6, 0),
					LocalDateTime.of(2019, 3, 14, 6, 5),
					ProjectPhase.IMPLEMENTATION
			));
			entityManager.persist(busyEmployeeA);

			Employee lazyEmployee = new Employee(
					"Homer", "Simpson",
					LocalDate.of(1964, 1, 30),
					new Address("00815", "Springfield", "742 Evergreen Terrace")
			);
			lazyEmployee.addLogbookEntry(new LogBookEntry(
					"Guard bee during nuclear safety inspection",
					LocalDateTime.of(2019, 3, 14, 12, 0),
					LocalDateTime.of(2019, 3, 14, 15, 5),
					ProjectPhase.IMPLEMENTATION
			));
			entityManager.persist(lazyEmployee);

			Employee busyEmployeeC = new Employee(
					"Lenford", "Leonard",
					LocalDate.of(1964, 4, 29),
					new Address("00815", "Springfield", "98 Walnut Street")
			);
			busyEmployeeC.addLogbookEntry(new LogBookEntry(
					"Perform nuclear safety audit",
					LocalDateTime.of(2019, 3, 14, 9, 0),
					LocalDateTime.of(2019, 3, 14, 10, 5),
					ProjectPhase.TESTING
			));
			entityManager.persist(busyEmployeeC);

			Employee busyEmployeeD = new Employee(
					"Carl", "Carlson",
					LocalDate.of(1964, 4, 29),
					new Address("00815", "Springfield", "100 Walnut Street")
			);
			entityManager.persist(busyEmployeeD);
		});
	}

	@Test
	public void testInsertEmployee() {
		doInJPA(JpaTestingUtil::getEntityManagerFactory, entityManager -> {
			EmployeeDao employeeDao = new EmployeeDaoJpa();
			Employee employee = new Employee(
					"Homer", "Simpson",
					LocalDate.of(1964, 1, 30),
					new Address("00815", "Springfield", "Evergreen Terrace")
			);
			Employee addedEmployee = employeeDao.add(new DalTransactionJpa(entityManager), employee);
			assertTrue(entityManager.contains(addedEmployee));
		});
	}

	@Test
	public void testFindAll() {
		doInJPA(JpaTestingUtil::getEntityManagerFactory, entityManager -> {
			EmployeeDao employeeDao = new EmployeeDaoJpa();
			List<Employee> employees = employeeDao.findAll(
					new DalTransactionJpa(entityManager)
			);
			assertNotNull(employees);
			assertTrue(employees.size() > 0);
		});
	}

	@Test
	public void testFindById() {
		doInJPA(JpaTestingUtil::getEntityManagerFactory, entityManager -> {
			EmployeeDao	employeeDao = new EmployeeDaoJpa();
			Optional<Employee> employeeContainer = employeeDao.findById(
					new DalTransactionJpa(entityManager),
					1
			);
			assertNotNull(employeeContainer);
			assertTrue(employeeContainer.isPresent());

			Employee employee = employeeContainer.get();
			assertNotNull(employee);
		});
	}

	@Test
	public void testSearchNonExistingId() {
		doInJPA(JpaTestingUtil::getEntityManagerFactory, entityManager -> {
			EmployeeDao employeeDao = new EmployeeDaoJpa();
			Optional<Employee> employeeContainer = employeeDao.findById(
					new DalTransactionJpa(entityManager),
					1180
			);
			assertNotNull(employeeContainer);
			assertTrue(employeeContainer.isEmpty());
		});
	}

	@Test
	public void testDeleteByObject() {
		doInJPA(JpaTestingUtil::getEntityManagerFactory, entityManager -> {
			EmployeeDao	employeeDao = new EmployeeDaoJpa();
			DalTransaction transaction = new DalTransactionJpa(entityManager);
			Employee deletedEmployee = employeeDao.findAll(transaction).stream()
					.filter(employee -> employee.getFirstName().equals("Canary M."))
					.filter(employee -> employee.getLastName().equals("Burns"))
					.findFirst()
					.orElseThrow();

			assertTrue(entityManager.contains(deletedEmployee));
			employeeDao.delete(transaction, deletedEmployee);
			assertFalse(entityManager.contains(deletedEmployee));
		});
	}

	@Test
	public void testDeleteById() {
		doInJPA(JpaTestingUtil::getEntityManagerFactory, entityManager -> {
			EmployeeDao	employeeDao = new EmployeeDaoJpa();
			DalTransaction transaction = new DalTransactionJpa(entityManager);
			Employee employee = employeeDao.findAll(transaction).stream()
					.filter(e -> e.getFirstName().equals("Frank"))
					.filter(e -> e.getLastName().equals("Grimes"))
					.findFirst()
					.orElseThrow();
			assertTrue(employeeDao.delete(transaction, employee.getId()));
		});
	}

	@Test
	public void testUpdateLastName() {
		doInJPA(JpaTestingUtil::getEntityManagerFactory, entityManager -> {
			EmployeeDao employeeDao = new EmployeeDaoJpa();
			DalTransaction transaction = new DalTransactionJpa(entityManager);
			Employee updatedEmployee = employeeDao.findAll(transaction).stream()
					.filter(employee -> employee.getFirstName().equals("Queenie"))
					.filter(employee -> employee.getLastName().equals("Chicken"))
					.findFirst()
					.orElseThrow();

			assertEquals("Chicken", updatedEmployee.getLastName());
			updatedEmployee = employeeDao.updateLastName(transaction, updatedEmployee, "Simpson");
			assertEquals("Simpson", updatedEmployee.getLastName());
		});
	}

	@Test
	public void testUpdateAddress() {
		doInJPA(JpaTestingUtil::getEntityManagerFactory, entityManager -> {
			EmployeeDao employeeDao = new EmployeeDaoJpa();
			DalTransaction transaction = new DalTransactionJpa(entityManager);
			Employee updatedEmployee = employeeDao.findAll(transaction).stream()
					.filter(employee -> employee.getFirstName().equals("Monty"))
					.filter(employee -> employee.getLastName().equals("Burns"))
					.findFirst()
					.orElseThrow();

			assertEquals("1001 Mammon Lane", updatedEmployee.getAddress().getStreet());
			updatedEmployee = employeeDao.updateAddress(
					transaction, updatedEmployee,
					new Address("00815", "Springfield", "666 Waterfront")
			);
			assertEquals("666 Waterfront", updatedEmployee.getAddress().getStreet());
			assertTrue(entityManager.contains(updatedEmployee));
		});
	}

	@Test
	public void testAddLogBookEntry() {
		doInJPA(JpaTestingUtil::getEntityManagerFactory, entityManager -> {
			EmployeeDao employeeDao = new EmployeeDaoJpa();
			DalTransaction transaction = new DalTransactionJpa(entityManager);
			Employee employee = employeeDao.findAll(transaction).stream()
					.filter(e -> e.getFirstName().equals("Waylon Jr"))
					.filter(e -> e.getLastName().equals("Smithers"))
					.findFirst()
					.orElseThrow();
			assertNotNull(employee);
			final int previousLogBookEntries = employee.getLogbookEntries().size();

			LogBookEntry logBookEntry = new LogBookEntry(
					"Deliver the report to Mr. Burns",
					LocalDateTime.of(2019, 3, 14, 6, 7),
					LocalDateTime.of(2019, 3, 14, 6, 20),
					ProjectPhase.ANALYSIS
			);
			employeeDao.addLogBookEntryToEmployee(transaction, logBookEntry, employee);
			final int currentLogBookEntries = employee.getLogbookEntries().size();
			assertEquals((previousLogBookEntries + 1), currentLogBookEntries);
		});
	}

	@Test
	public void testRemoveLogBookEntry() {
		doInJPA(JpaTestingUtil::getEntityManagerFactory, entityManager -> {
			EmployeeDao employeeDao = new EmployeeDaoJpa();
			DalTransaction transaction = new DalTransactionJpa(entityManager);
			Employee employee = employeeDao.findAll(transaction).stream()
					.filter(e -> e.getFirstName().equals("Homer"))
					.filter(e -> e.getLastName().equals("Simpson"))
					.findFirst()
					.orElseThrow();
			assertNotNull(employee);
			assertEquals(1, employee.getLogbookEntries().size());
			LogBookEntry logBookEntry = employee.getLogbookEntries().iterator().next();

			employeeDao.removeLogBookEntryFromEmployee(transaction, logBookEntry, employee);
			assertEquals(0, employee.getLogbookEntries().size());
		});
	}

	@Test
	public void testReassignLogBookEntry() {
		doInJPA(JpaTestingUtil::getEntityManagerFactory, entityManager -> {
			EmployeeDao employeeDao = new EmployeeDaoJpa();
			DalTransaction transaction = new DalTransactionJpa(entityManager);

			Employee previousAssignee = employeeDao.findAll(transaction).stream()
					.filter(e -> e.getFirstName().equals("Lenford"))
					.filter(e -> e.getLastName().equals("Leonard"))
					.findFirst()
					.orElseThrow();
			assertEquals(1, previousAssignee.getLogbookEntries().size());
			Employee newAssignee = employeeDao.findAll(transaction).stream()
					.filter(e -> e.getFirstName().equals("Carl"))
					.filter(e -> e.getLastName().equals("Carlson"))
					.findFirst()
					.orElseThrow();
			assertEquals(0, newAssignee.getLogbookEntries().size());
			LogBookEntry logBookEntry = previousAssignee.getLogbookEntries().iterator().next();

			employeeDao.reassignLogBookEntry(transaction, logBookEntry, newAssignee);
			assertEquals(0, previousAssignee.getLogbookEntries().size());
			assertEquals(1, newAssignee.getLogbookEntries().size());
		});
	}
}

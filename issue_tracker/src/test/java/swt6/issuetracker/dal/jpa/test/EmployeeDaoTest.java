package swt6.issuetracker.dal.jpa.test;

import org.junit.BeforeClass;
import org.junit.Test;
import swt6.issuetracker.dal.DalTransaction;
import swt6.issuetracker.dal.EmployeeDao;
import swt6.issuetracker.dal.jpa.DalTransactionJpa;
import swt6.issuetracker.dal.jpa.EmployeeDaoJpa;
import swt6.issuetracker.domain.*;

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
			Project projectAssignedToFormerEmployee = new Project("Improve tax evasion strategies");
			projectAssignedToFormerEmployee.addEmployee(formerEmployeeB);
			entityManager.persist(projectAssignedToFormerEmployee);
			Issue issueAssignedToFormerEmployee = new Issue(
					"Analyze tax code",
					Issue.IssueState.OPEN, Issue.IssuePriority.HIGH,
					30, 20
			);
			issueAssignedToFormerEmployee.attachProject(projectAssignedToFormerEmployee);
			issueAssignedToFormerEmployee.attachEmployee(formerEmployeeB);
			entityManager.persist(issueAssignedToFormerEmployee);
			LogBookEntry logBookEntryAssignedToFormerEmployee = new LogBookEntry(
					"Buy current edition of the tax code book",
					LocalDateTime.of(2019, 3, 15, 13, 00),
					LocalDateTime.of(2019, 3, 15, 13, 30),
					ProjectPhase.ANALYSIS
			);
			logBookEntryAssignedToFormerEmployee.attachIssue(issueAssignedToFormerEmployee);
			logBookEntryAssignedToFormerEmployee.attachEmployee(formerEmployeeB);
			entityManager.persist(logBookEntryAssignedToFormerEmployee);

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
}

package swt6.issuetracker.dal.jpa.test;

import org.junit.BeforeClass;
import org.junit.Test;
import swt6.issuetracker.dal.*;
import swt6.issuetracker.dal.jpa.*;
import swt6.issuetracker.domain.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.hibernate.testing.transaction.TransactionUtil.*;

public class LogBookEntryDaoTest {
	@BeforeClass
	public static void initializeDemoData() {
		doInJPA(JpaTestingUtil::getEntityManagerFactory, entityManager -> {
			Employee busyEmployeeA = new Employee(
					"Waylon Jr", "Smithers",
					LocalDate.of(1954, 7, 30),
					new Address("00815", "Springfield", "99 Bachelor Rd")
			);
			entityManager.persist(busyEmployeeA);

			Project project = new Project("Build rocket");
			entityManager.persist(project);

			Issue issueA = new Issue(
					"Gather requirements",
					Issue.IssueState.NEW,
					Issue.IssuePriority.MEDIUM,
					0, 6
			);
			issueA.attachProject(project);
			issueA.attachEmployee(busyEmployeeA);
			entityManager.persist(issueA);

			LogBookEntry entryA = new LogBookEntry(
					"Meet clients for lunch",
					LocalDateTime.of(2019, 3, 16, 16, 00),
					LocalDateTime.of(2019, 3, 16, 17, 00),
					ProjectPhase.ANALYSIS
			);
			entryA.attachEmployee(busyEmployeeA);
			entryA.attachIssue(issueA);
			entityManager.persist(entryA);

			LogBookEntry entryB = new LogBookEntry(
					"Buy new Malibu Stacey doll",
					LocalDateTime.of(2019, 3, 16, 11, 00),
					LocalDateTime.of(2019, 3, 16, 11, 15),
					ProjectPhase.IMPLEMENTATION
			);
			entryB.attachEmployee(busyEmployeeA);
			entryB.attachIssue(issueA);
			entityManager.persist(entryB);

			Employee busyEmployeeB = new Employee(
					"Lenford", "Leonard",
					LocalDate.of(1964, 4, 29),
					new Address("00815", "Springfield", "98 Walnut Street")
			);
			entityManager.persist(busyEmployeeB);
			LogBookEntry entryC = new LogBookEntry(
					"Calculate waste barrel capacity",
					LocalDateTime.of(2019, 3, 14, 9, 0),
					LocalDateTime.of(2019, 3, 14, 10, 5),
					ProjectPhase.DESIGN
			);
			entryC.attachIssue(issueA);
			entryC.attachEmployee(busyEmployeeB);
			entityManager.persist(entryC);
			Employee busyEmployeeC = new Employee(
					"Carl", "Carlson",
					LocalDate.of(1964, 4, 29),
					new Address("00815", "Springfield", "100 Walnut Street")
			);
			entityManager.persist(busyEmployeeC);

			Issue issueB = new Issue(
					"Build ramp",
					Issue.IssueState.OPEN, Issue.IssuePriority.MEDIUM,
					50, 2500
			);
			issueB.attachProject(project);
			entityManager.persist(issueB);
			Issue issueC = new Issue(
					"Test ramp",
					Issue.IssueState.OPEN, Issue.IssuePriority.MEDIUM,
					20, 30
			);
			issueC.attachProject(project);
			entityManager.persist(issueC);
			LogBookEntry entryD = new LogBookEntry(
					"Negotiate with concrete supplier",
					LocalDateTime.of(2019, 3, 15, 12, 00),
					LocalDateTime.of(2019, 3, 15, 12, 45),
					ProjectPhase.DESIGN
			);
			entryD.attachIssue(issueC);
			entryD.attachEmployee(busyEmployeeA);
			entityManager.persist(entryD);
		});
	}

	@Test
	public void testFindById() {
		doInJPA(JpaTestingUtil::getEntityManagerFactory, entityManager -> {
			LogBookEntryDao logBookEntryDao = new LogBookEntryDaoJpa();
			Optional<LogBookEntry> entry = logBookEntryDao.findById(new DalTransactionJpa(entityManager), 1);
			assertNotNull(entry);
			assertTrue(entry.isPresent());
			assertNotNull(entry.get());
		});
	}

	@Test
	public void testSearchNonExistingId() {
		doInJPA(JpaTestingUtil::getEntityManagerFactory, entityManager -> {
			LogBookEntryDao logBookEntryDao = new LogBookEntryDaoJpa();
			Optional<LogBookEntry> entry = logBookEntryDao.findById(new DalTransactionJpa(entityManager), 1180);
			assertNotNull(entry);
			assertTrue(entry.isEmpty());
		});
	}

	@Test
	public void testFindAllByIssueId() {
		doInJPA(JpaTestingUtil::getEntityManagerFactory, entityManager -> {
			DalTransaction transaction = new DalTransactionJpa(entityManager);

			ProjectDao projectDao = new ProjectDaoJpa();
			Project project = projectDao.findAll(transaction).stream()
					.filter(p -> p.getName().equals("Build rocket"))
					.findFirst()
					.orElseThrow();

			IssueDao issueDao = new IssueDaoJpa();
			Issue issue = issueDao.findAllByProjectId(transaction, project.getId())
					.stream()
					.filter(i -> i.getName().equals("Gather requirements"))
					.findFirst()
					.orElseThrow();

			LogBookEntryDao logBookEntryDao = new LogBookEntryDaoJpa();
			List<LogBookEntry> entries = logBookEntryDao.findAllByIssueId(transaction, issue.getId());
			assertNotNull(entries);
			assertTrue(entries.size() > 0);
		});
	}

	@Test
	public void testFindAllByEmployeeId() {
		doInJPA(JpaTestingUtil::getEntityManagerFactory, entityManager -> {
			EmployeeDao employeeDao = new EmployeeDaoJpa();
			DalTransaction transaction = new DalTransactionJpa(entityManager);
			Employee employee = employeeDao.findAll(transaction).stream()
					.filter(e -> e.getFirstName().equals("Waylon Jr"))
					.filter(e -> e.getLastName().equals("Smithers"))
					.findFirst()
					.orElseThrow();
			assertNotNull(employee);

			LogBookEntryDao logBookEntryDao = new LogBookEntryDaoJpa();
			List<LogBookEntry> entries = logBookEntryDao.findAllByEmployeeId(transaction, employee.getId());
			assertNotNull(entries);
			assertTrue(entries.size() > 0);
		});
	}

	@Test
	public void testAdd() {
		doInJPA(JpaTestingUtil::getEntityManagerFactory, entityManager -> {
			EmployeeDao employeeDao = new EmployeeDaoJpa();
			DalTransaction transaction = new DalTransactionJpa(entityManager);
			Employee employee = employeeDao.findAll(transaction).stream()
					.filter(e -> e.getFirstName().equals("Waylon Jr"))
					.filter(e -> e.getLastName().equals("Smithers"))
					.findFirst()
					.orElseThrow();

			ProjectDao projectDao = new ProjectDaoJpa();
			Project project = projectDao.findAll(transaction).stream()
					.filter(p -> p.getName().equals("Build rocket"))
					.findFirst()
					.orElseThrow();

			IssueDao issueDao = new IssueDaoJpa();
			Issue issue = issueDao.findAllByProjectId(transaction, project.getId())
					.stream()
					.filter(i -> i.getName().equals("Gather requirements"))
					.findFirst()
					.orElseThrow();

			LogBookEntry logBookEntry = new LogBookEntry(
					"Deliver the report to Mr. Burns",
					LocalDateTime.of(2019, 3, 14, 6, 7),
					LocalDateTime.of(2019, 3, 14, 6, 20),
					ProjectPhase.ANALYSIS
			);
			LogBookEntryDao logBookEntryDao = new LogBookEntryDaoJpa();
			assertFalse(entityManager.contains(logBookEntry));
			LogBookEntry addedLogBookEntry = logBookEntryDao.add(transaction, logBookEntry, employee, issue);
			assertTrue(entityManager.contains(addedLogBookEntry));
		});
	}

	@Test
	public void testRemoveLogBookEntry() {
		doInJPA(JpaTestingUtil::getEntityManagerFactory, entityManager -> {
			EmployeeDao employeeDao = new EmployeeDaoJpa();
			DalTransaction transaction = new DalTransactionJpa(entityManager);
			Employee employee = employeeDao.findAll(transaction).stream()
					.filter(e -> e.getFirstName().equals("Waylon Jr"))
					.filter(e -> e.getLastName().equals("Smithers"))
					.findFirst()
					.orElseThrow();
			assertNotNull(employee);

			LogBookEntryDao logBookEntryDao = new LogBookEntryDaoJpa();
			LogBookEntry logBookEntry = logBookEntryDao.findAllByEmployeeId(transaction, employee.getId())
					.stream()
					.filter(l -> l.getActivity().equals("Buy new Malibu Stacey doll"))
					.findFirst()
					.orElseThrow();

			assertTrue(entityManager.contains(logBookEntry));
			logBookEntryDao.remove(transaction, logBookEntry);
			assertFalse(entityManager.contains(logBookEntry));
		});
	}

	@Test
	public void testReassignLogBookEntryToDifferentEmployee() {
		doInJPA(JpaTestingUtil::getEntityManagerFactory, entityManager -> {
			DalTransaction transaction = new DalTransactionJpa(entityManager);
			LogBookEntryDao logBookEntryDao = new LogBookEntryDaoJpa();

			EmployeeDao employeeDao = new EmployeeDaoJpa();
			Employee previousAssignee = employeeDao.findAll(transaction).stream()
					.filter(e -> e.getFirstName().equals("Lenford"))
					.filter(e -> e.getLastName().equals("Leonard"))
					.findFirst()
					.orElseThrow();
			int entryCountPreviousAssigneeBeforeOperation =
					logBookEntryDao.findAllByEmployeeId(transaction, previousAssignee.getId()).size();
			assertEquals(1, entryCountPreviousAssigneeBeforeOperation);
			Employee newAssignee = employeeDao.findAll(transaction).stream()
					.filter(e -> e.getFirstName().equals("Carl"))
					.filter(e -> e.getLastName().equals("Carlson"))
					.findFirst()
					.orElseThrow();
			int entryCountCurrentAssigneeBeforeOperation =
					logBookEntryDao.findAllByEmployeeId(transaction, newAssignee.getId()).size();
			assertEquals(0, entryCountCurrentAssigneeBeforeOperation);


			LogBookEntry logBookEntry = logBookEntryDao.findAllByEmployeeId(transaction, previousAssignee.getId())
					.stream()
					.filter(l -> l.getActivity().equals("Calculate waste barrel capacity"))
					.findFirst()
					.orElseThrow();
			logBookEntryDao.reassignToEmployee(transaction, logBookEntry, newAssignee);

			int entryCountPreviousAssigneeAfterOperation =
					logBookEntryDao.findAllByEmployeeId(transaction, previousAssignee.getId()).size();
			assertEquals(0, entryCountPreviousAssigneeAfterOperation);
			int entryCountCurrentAssigneeAfterOperation =
					logBookEntryDao.findAllByEmployeeId(transaction, newAssignee.getId()).size();
			assertEquals(1, entryCountCurrentAssigneeAfterOperation);
		});
	}

	@Test
	public void testReassignLogBookEntryToDifferentIssue() {
		doInJPA(JpaTestingUtil::getEntityManagerFactory, entityManager -> {
			DalTransaction transaction = new DalTransactionJpa(entityManager);
			LogBookEntryDao logBookEntryDao = new LogBookEntryDaoJpa();

			ProjectDao projectDao = new ProjectDaoJpa();
			Project project = projectDao.findAll(transaction)
					.stream()
					.filter(p -> p.getName().equals("Build rocket"))
					.findFirst()
					.orElseThrow();

			IssueDao issueDao = new IssueDaoJpa();
			Issue previousIssue = issueDao.findAllByProjectId(transaction, project.getId())
					.stream()
					.filter(i -> i.getName().equals("Test ramp"))
					.findFirst()
					.orElseThrow();
			int entryCountPreviousAssigneeBeforeOperation =
					logBookEntryDao.findAllByIssueId(transaction, previousIssue.getId()).size();
			assertEquals(1, entryCountPreviousAssigneeBeforeOperation);
			Issue newIssue = issueDao.findAllByProjectId(transaction, project.getId())
					.stream()
					.filter(i -> i.getName().equals("Build ramp"))
					.findFirst()
					.orElseThrow();
			int entryCountCurrentAssigneeBeforeOperation =
					logBookEntryDao.findAllByIssueId(transaction, newIssue.getId()).size();
			assertEquals(0, entryCountCurrentAssigneeBeforeOperation);


			LogBookEntry logBookEntry = logBookEntryDao.findAllByIssueId(transaction, previousIssue.getId())
					.stream()
					.filter(l -> l.getActivity().equals("Negotiate with concrete supplier"))
					.findFirst()
					.orElseThrow();
			logBookEntryDao.reassignToIssue(transaction, logBookEntry, newIssue);

			int entryCountPreviousAssigneeAfterOperation =
					logBookEntryDao.findAllByIssueId(transaction, previousIssue.getId()).size();
			assertEquals(0, entryCountPreviousAssigneeAfterOperation);
			int entryCountCurrentAssigneeAfterOperation =
					logBookEntryDao.findAllByIssueId(transaction, newIssue.getId()).size();
			assertEquals(1, entryCountCurrentAssigneeAfterOperation);
		});
	}
}

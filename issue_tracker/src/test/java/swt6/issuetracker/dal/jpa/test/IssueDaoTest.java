package swt6.issuetracker.dal.jpa.test;

import org.junit.BeforeClass;
import org.junit.Test;
import swt6.issuetracker.dal.DalTransaction;
import swt6.issuetracker.dal.EmployeeDao;
import swt6.issuetracker.dal.IssueDao;
import swt6.issuetracker.dal.ProjectDao;
import swt6.issuetracker.dal.jpa.DalTransactionJpa;
import swt6.issuetracker.dal.jpa.EmployeeDaoJpa;
import swt6.issuetracker.dal.jpa.IssueDaoJpa;
import swt6.issuetracker.dal.jpa.ProjectDaoJpa;
import swt6.issuetracker.domain.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.hibernate.testing.transaction.TransactionUtil.*;

public class IssueDaoTest {
	@BeforeClass
	public static void initializeDemoData() {
		doInJPA(JpaTestingUtil::getEntityManagerFactory, entityManager -> {
			Project project = new Project("New reactor control console");
			entityManager.persist(project);

			Employee employeeA = new Employee(
					"Mindy", "Simmons",
					LocalDate.of(1975, 1, 30),
					new Address("00815", "Springfield", "746 Evergreen Terrace")
			);
			entityManager.persist(employeeA);

			Issue issueA = new Issue(
					"Hook up video recorder to surveillance system",
					Issue.IssueState.NEW, Issue.IssuePriority.MEDIUM,
					0, 1
			);
			issueA.attachProject(project);
			issueA.attachEmployee(employeeA);
			entityManager.persist(issueA);

			Issue updatedIssue = new Issue(
					"Tempt Homer Simpson",
					Issue.IssueState.NEW, Issue.IssuePriority.MEDIUM,
					0
			);
			updatedIssue.attachProject(project);
			entityManager.persist(updatedIssue);

			Issue deletedIssue = new Issue(
					"Have Smithers take a vacation",
					Issue.IssueState.REJECTED, Issue.IssuePriority.HIGH,
					20, 120
			);
			deletedIssue.attachProject(project);
			deletedIssue.attachEmployee(employeeA);
			entityManager.persist(deletedIssue);
			LogBookEntry logBookEntryOfDeletedIssue = new LogBookEntry(
					"Find a replacement",
					LocalDateTime.of(2019, 3, 15, 8, 0),
					LocalDateTime.of(2019, 3, 15, 8, 30),
					ProjectPhase.DESIGN
			);
			logBookEntryOfDeletedIssue.attachIssue(deletedIssue);
			logBookEntryOfDeletedIssue.attachEmployee(employeeA);
			entityManager.persist(logBookEntryOfDeletedIssue);
		});
	}

	@Test
	public void testFindById() {
		doInJPA(JpaTestingUtil::getEntityManagerFactory, entityManager -> {
			IssueDao issueDao = new IssueDaoJpa();
			Optional<Issue> issueContainer = issueDao.findById(new DalTransactionJpa(entityManager), 1);
			assertNotNull(issueContainer);
			assertTrue(issueContainer.isPresent());

			Issue issue = issueContainer.get();
			assertNotNull(issue);
		});
	}

	@Test
	public void testSearchForNonExistingId() {
		doInJPA(JpaTestingUtil::getEntityManagerFactory, entityManager -> {
			IssueDao issueDao = new IssueDaoJpa();
			Optional<Issue> issueContainer = issueDao.findById(new DalTransactionJpa(entityManager), 1180);
			assertNotNull(issueContainer);
			assertTrue(issueContainer.isEmpty());
		});
	}

	@Test
	public void testFindAllByEmployee() {
		doInJPA(JpaTestingUtil::getEntityManagerFactory, entityManager -> {
			DalTransaction transaction = new DalTransactionJpa(entityManager);

			EmployeeDao employeeDao = new EmployeeDaoJpa();
			Employee assignee = employeeDao.findAll(transaction).stream()
					.filter(employee -> employee.getFirstName().equals("Mindy"))
					.filter(employee -> employee.getLastName().equals("Simmons"))
					.findFirst()
					.orElseThrow();

			IssueDao issueDao = new IssueDaoJpa();
			List<Issue> issues = issueDao.findAllByEmployeeId(transaction, assignee.getId());
			assertNotNull(issues);
			assertTrue(issues.size() > 0);
		});
	}

	@Test
	public void testFindAllByEmployeeIdGroupByIssueState() {
		doInJPA(JpaTestingUtil::getEntityManagerFactory, entityManager -> {
			DalTransaction transaction = new DalTransactionJpa(entityManager);

			EmployeeDao employeeDao = new EmployeeDaoJpa();
			Employee assignee = employeeDao.findAll(transaction).stream()
					.filter(employee -> employee.getFirstName().equals("Mindy"))
					.filter(employee -> employee.getLastName().equals("Simmons"))
					.findFirst()
					.orElseThrow();

			IssueDao issueDao = new IssueDaoJpa();
			Map<Issue.IssueState, List<Issue>> issues = issueDao.findAllByEmployeeIdGroupByIssueState(
					transaction,
					assignee.getId()
			);
			assertNotNull(issues);
			assertTrue(issues.size() > 0);
		});
	}

	@Test
	public void testFindAllByProject() {
		doInJPA(JpaTestingUtil::getEntityManagerFactory, entityManager -> {
			DalTransaction transaction = new DalTransactionJpa(entityManager);

			ProjectDao projectDao = new ProjectDaoJpa();
			Project project = projectDao.findAll(transaction).stream()
					.filter(p -> p.getName().equals("New reactor control console"))
					.findFirst()
					.orElseThrow();

			IssueDao issueDao = new IssueDaoJpa();
			List<Issue> issues = issueDao.findAllByProjectId(transaction, project.getId());
			assertNotNull(issues);
			assertTrue(issues.size() > 0);
		});
	}

	@Test
	public void testFindAllByProjectGroupByIssueState() {
		doInJPA(JpaTestingUtil::getEntityManagerFactory, entityManager -> {
			DalTransaction transaction = new DalTransactionJpa(entityManager);

			ProjectDao projectDao = new ProjectDaoJpa();
			Project project = projectDao.findAll(transaction).stream()
					.filter(p -> p.getName().equals("New reactor control console"))
					.findFirst()
					.orElseThrow();

			IssueDao issueDao = new IssueDaoJpa();
			Map<Issue.IssueState, List<Issue>> issues = issueDao.findAllByProjectIdGroupByIssueState(
					transaction,
					project.getId()
			);
			assertNotNull(issues);
			assertTrue(issues.size() > 0);
		});
	}

	@Test
	public void testAdd() {
		doInJPA(JpaTestingUtil::getEntityManagerFactory, entityManager -> {
			DalTransaction transaction = new DalTransactionJpa(entityManager);

			ProjectDao projectDao = new ProjectDaoJpa();
			Project project = projectDao.findAll(transaction).stream()
					.filter(p -> p.getName().equals("New reactor control console"))
					.findFirst()
					.orElseThrow();

			EmployeeDao employeeDao = new EmployeeDaoJpa();
			Employee assignee = employeeDao.findAll(transaction).stream()
					.filter(employee -> employee.getFirstName().equals("Mindy"))
					.filter(employee -> employee.getLastName().equals("Simmons"))
					.findFirst()
					.orElseThrow();

			IssueDao issueDao = new IssueDaoJpa();
			Issue issue = new Issue(
					"Analyze weaknesses of current system",
					Issue.IssueState.OPEN,
					Issue.IssuePriority.HIGH,
					10
			);

			assertFalse(entityManager.contains(issue));
			Issue addedIssue = issueDao.add(transaction, issue, project, assignee);
			assertTrue(entityManager.contains(addedIssue));
		});
	}

	@Test
	public void testAddWithoutAssignee() {
		doInJPA(JpaTestingUtil::getEntityManagerFactory, entityManager -> {
			DalTransaction transaction = new DalTransactionJpa(entityManager);

			ProjectDao projectDao = new ProjectDaoJpa();
			Project project = projectDao.findAll(transaction).stream()
					.filter(p -> p.getName().equals("New reactor control console"))
					.findFirst()
					.orElseThrow();

			IssueDao issueDao = new IssueDaoJpa();
			Issue issue = new Issue(
					"Eat donuts",
					Issue.IssueState.CLOSED,
					Issue.IssuePriority.CRITICAL,
					100,
					0.5
			);

			assertFalse(entityManager.contains(issue));
			Issue addedIssue = issueDao.add(transaction, issue, project);
			assertTrue(entityManager.contains(addedIssue));
		});
	}

	@Test
	public void testUpdateAssignee() {
		doInJPA(JpaTestingUtil::getEntityManagerFactory, entityManager -> {
			DalTransaction transaction = new DalTransactionJpa(entityManager);

			ProjectDao projectDao = new ProjectDaoJpa();
			Project project = projectDao.findAll(transaction).stream()
					.filter(p -> p.getName().equals("New reactor control console"))
					.findFirst()
					.orElseThrow();

			IssueDao issueDao = new IssueDaoJpa();
			Issue issue = issueDao.findAllByProjectId(transaction, project.getId()).stream()
					.filter(i -> Objects.equals(i.getName(), "Tempt Homer Simpson"))
					.findFirst()
					.orElseThrow();
			assertNull(issue.getEmployee());

			EmployeeDao employeeDao = new EmployeeDaoJpa();
			Employee assignee = employeeDao.findAll(transaction).stream()
					.filter(employee -> employee.getFirstName().equals("Mindy"))
					.filter(employee -> employee.getLastName().equals("Simmons"))
					.findFirst()
					.orElseThrow();
			Issue updatedIssue = issueDao.updateAssignee(transaction, issue, assignee);
			assertNotNull(updatedIssue.getEmployee());
			assertTrue(entityManager.contains(updatedIssue));
		});
	}

	@Test
	public void testUpdateState() {
		doInJPA(JpaTestingUtil::getEntityManagerFactory, entityManager -> {
			DalTransaction transaction = new DalTransactionJpa(entityManager);

			ProjectDao projectDao = new ProjectDaoJpa();
			Project project = projectDao.findAll(transaction).stream()
					.filter(p -> p.getName().equals("New reactor control console"))
					.findFirst()
					.orElseThrow();

			IssueDao issueDao = new IssueDaoJpa();
			Issue issue = issueDao.findAllByProjectId(transaction, project.getId()).stream()
					.filter(i -> Objects.equals(i.getName(), "Tempt Homer Simpson"))
					.findFirst()
					.orElseThrow();

			assertEquals(Issue.IssueState.NEW, issue.getState());
			Issue updatedIssue = issueDao.updateState(transaction, issue, Issue.IssueState.OPEN);
			assertEquals(Issue.IssueState.OPEN, issue.getState());
			assertTrue(entityManager.contains(updatedIssue));
		});
	}

	@Test
	public void testUpdatePriority() {
		doInJPA(JpaTestingUtil::getEntityManagerFactory, entityManager -> {
			DalTransaction transaction = new DalTransactionJpa(entityManager);

			ProjectDao projectDao = new ProjectDaoJpa();
			Project project = projectDao.findAll(transaction).stream()
					.filter(p -> p.getName().equals("New reactor control console"))
					.findFirst()
					.orElseThrow();

			IssueDao issueDao = new IssueDaoJpa();
			Issue issue = issueDao.findAllByProjectId(transaction, project.getId()).stream()
					.filter(i -> Objects.equals(i.getName(), "Tempt Homer Simpson"))
					.findFirst()
					.orElseThrow();

			assertEquals(Issue.IssuePriority.MEDIUM, issue.getPriority());
			Issue updatedIssue = issueDao.updatePriority(transaction, issue, Issue.IssuePriority.HIGH);
			assertEquals(Issue.IssuePriority.HIGH, issue.getPriority());
			assertTrue(entityManager.contains(updatedIssue));
		});
	}

	@Test
	public void testUpdateProgress() {
		doInJPA(JpaTestingUtil::getEntityManagerFactory, entityManager -> {
			DalTransaction transaction = new DalTransactionJpa(entityManager);

			ProjectDao projectDao = new ProjectDaoJpa();
			Project project = projectDao.findAll(transaction).stream()
					.filter(p -> p.getName().equals("New reactor control console"))
					.findFirst()
					.orElseThrow();

			IssueDao issueDao = new IssueDaoJpa();
			Issue issue = issueDao.findAllByProjectId(transaction, project.getId()).stream()
					.filter(i -> Objects.equals(i.getName(), "Tempt Homer Simpson"))
					.findFirst()
					.orElseThrow();

			assertEquals(0, issue.getProgress());
			Issue updatedIssue = issueDao.updateProgress(transaction, issue, 50);
			assertEquals(50, issue.getProgress());
			assertTrue(entityManager.contains(updatedIssue));
		});
	}

	@Test
	public void testUpdateEstimatedTime() {
		doInJPA(JpaTestingUtil::getEntityManagerFactory, entityManager -> {
			DalTransaction transaction = new DalTransactionJpa(entityManager);

			ProjectDao projectDao = new ProjectDaoJpa();
			Project project = projectDao.findAll(transaction).stream()
					.filter(p -> p.getName().equals("New reactor control console"))
					.findFirst()
					.orElseThrow();

			IssueDao issueDao = new IssueDaoJpa();
			Issue issue = issueDao.findAllByProjectId(transaction, project.getId()).stream()
					.filter(i -> Objects.equals(i.getName(), "Tempt Homer Simpson"))
					.findFirst()
					.orElseThrow();

			assertEquals(0d, issue.getEstimatedTime(), 0);
			Issue updatedIssue = issueDao.updateEstimatedTime(transaction, issue, 16);
			assertEquals(16d, issue.getEstimatedTime(), 0);
			assertTrue(entityManager.contains(updatedIssue));
		});
	}

	@Test
	public void deleteIssue() {
		doInJPA(JpaTestingUtil::getEntityManagerFactory, entityManager -> {
			DalTransaction transaction = new DalTransactionJpa(entityManager);

			ProjectDao projectDao = new ProjectDaoJpa();
			Project project = projectDao.findAll(transaction).stream()
					.filter(p -> p.getName().equals("New reactor control console"))
					.findFirst()
					.orElseThrow();

			IssueDao issueDao = new IssueDaoJpa();
			Issue issue = issueDao.findAllByProjectId(transaction, project.getId()).stream()
					.filter(i -> Objects.equals(i.getName(), "Have Smithers take a vacation"))
					.findFirst()
					.orElseThrow();

			assertTrue(entityManager.contains(issue));
			issueDao.delete(transaction, issue);
			assertFalse(entityManager.contains(issue));
		});
	}
}

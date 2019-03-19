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
import java.util.Map;

import static org.hibernate.testing.transaction.TransactionUtil.doInJPA;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class AdvancedDaoQueriesTest {
	@BeforeClass
	public static void initializeDemoData() {
		doInJPA(JpaTestingUtil::getEntityManagerFactory, entityManager -> {
			Project project = new Project("Fly to space");
			entityManager.persist(project);
			Employee employeeA = new Employee(
					"Homer", "Simpson",
					LocalDate.of(1964, 1, 30),
					new Address("00815", "Springfield", "998 Mammon Lane")
			);
			project.addEmployee(employeeA);
			entityManager.persist(employeeA);
			Employee employeeB = new Employee(
					"Ruffles", "Crisps",
					LocalDate.of(1994, 2, 23),
					new Address("00815", "Springfield", "34 Walnut Street")
			);
			project.addEmployee(employeeB);
			entityManager.persist(employeeB);
			Issue issueA = new Issue(
					"Train at NASA facility",
					Issue.IssueState.CLOSED, Issue.IssuePriority.CRITICAL,
					100, 80
			);
			issueA.attachEmployee(employeeA);
			issueA.attachProject(project);
			entityManager.persist(issueA);
			Issue issueB = new Issue(
					"Lift off",
					Issue.IssueState.OPEN, Issue.IssuePriority.CRITICAL,
					30, 100.5
			);
			issueB.attachEmployee(employeeA);
			issueB.attachProject(project);
			entityManager.persist(issueB);
			Issue issueC = new Issue(
					"Perform scientific experiments",
					Issue.IssueState.NEW, Issue.IssuePriority.MEDIUM,
					0, 48
			);
			issueC.attachProject(project);
			issueC.attachEmployee(employeeB);
			entityManager.persist(issueC);
			LogBookEntry logBookEntryA = new LogBookEntry(
					"Pass fitness test",
					LocalDateTime.of(1994, 1, 10, 6, 0),
					LocalDateTime.of(1994, 1, 10, 10, 0),
					ProjectPhase.DESIGN
			);
			logBookEntryA.attachIssue(issueA);
			logBookEntryA.attachEmployee(employeeA);
			entityManager.persist(logBookEntryA);
			LogBookEntry logBookEntryB = new LogBookEntry(
					"Learn about navigating the space shuttle",
					LocalDateTime.of(1994, 1, 12, 8, 0),
					LocalDateTime.of(1994, 1, 12, 16, 0),
					ProjectPhase.IMPLEMENTATION
			);
			logBookEntryB.attachIssue(issueA);
			logBookEntryB.attachEmployee(employeeA);
			entityManager.persist(logBookEntryB);
			LogBookEntry logBookEntryC = new LogBookEntry(
					"Check all systems",
					LocalDateTime.of(1994, 1, 12, 8, 0),
					LocalDateTime.of(1994, 1, 12, 16, 0),
					ProjectPhase.TESTING
			);
			logBookEntryC.attachEmployee(employeeA);
			logBookEntryC.attachIssue(issueB);
			entityManager.persist(logBookEntryC);
			LogBookEntry logBookEntryD = new LogBookEntry(
					"Clog the instruments",
					LocalDateTime.of(1994, 2, 25, 3, 0),
					LocalDateTime.of(1994, 2, 25, 3, 30),
					ProjectPhase.IMPLEMENTATION
			);
			logBookEntryD.attachEmployee(employeeB);
			logBookEntryD.attachIssue(issueC);
			entityManager.persist(logBookEntryD);
		});
	}

	@Test
	public void testCalculateWorkTimePerProjectPhase() {
		doInJPA(JpaTestingUtil::getEntityManagerFactory, entityManager -> {
			DalTransaction transaction = new DalTransactionJpa(entityManager);
			ProjectDao projectDao = new ProjectDaoJpa();

			Project project = projectDao.findAll(transaction)
					.stream()
					.filter(p -> p.getName().equals("Fly to space"))
					.findFirst()
					.orElseThrow();

			IssueDao issueDao = new IssueDaoJpa();
			Map<ProjectPhase, Double> workingTimes =
					issueDao.calculateWorkingTimePerProjectPhase(transaction, project);
			assertNotNull(workingTimes);
			assertEquals(3, workingTimes.size());
			assertEquals(4, workingTimes.get(ProjectPhase.DESIGN), 0);
			assertEquals(8.5, workingTimes.get(ProjectPhase.IMPLEMENTATION), 0.01);
			assertEquals(8, workingTimes.get(ProjectPhase.TESTING), 0);
		});
	}

	@Test
	public void testCalculateWorkingTimeAndEstimatedTimeByEmployeeIdGroupByIssueState() {
		doInJPA(JpaTestingUtil::getEntityManagerFactory, entityManager -> {
			DalTransaction transaction = new DalTransactionJpa(entityManager);

			EmployeeDao employeeDao = new EmployeeDaoJpa();
			Employee employee = employeeDao.findAll(transaction)
					.stream()
					.filter(e -> e.getFirstName().equals("Homer"))
					.filter(e -> e.getLastName().equals("Simpson"))
					.filter(e -> e.getAddress().getStreet().equals("998 Mammon Lane"))
					.findFirst()
					.orElseThrow();

			IssueDao issueDao = new IssueDaoJpa();
			Map<Issue.IssueState, Pair<Double, Double>> issueTimes = issueDao.calculateWorkingTimeAndEstimatedTimeByEmployeeGroupByIssueState(
					transaction,
					employee
			);
			assertNotNull(issueTimes);
			assertEquals(2, issueTimes.size());
			assertEquals(8d, issueTimes.get(Issue.IssueState.OPEN).getFirst(), 0);
			assertEquals(100.5d, issueTimes.get(Issue.IssueState.OPEN).getSecond(), 0);
		});
	}

	@Test
	public void testCalculateWorkingTimeAndEstimatedTimeByProjectIdGroupByIssueState() {
		doInJPA(JpaTestingUtil::getEntityManagerFactory, entityManager -> {
			DalTransaction transaction = new DalTransactionJpa(entityManager);

			ProjectDao projectDao = new ProjectDaoJpa();
			Project project = projectDao.findAll(transaction)
					.stream()
					.filter(p -> p.getName().equals("Fly to space"))
					.findFirst()
					.orElseThrow();

			IssueDao issueDao = new IssueDaoJpa();
			Map<Issue.IssueState, Pair<Double, Double>> issueTimes = issueDao.calculateWorkingTimeAndEstimatedTimeByProjectGroupByIssueState(
					transaction,
					project
			);
			assertNotNull(issueTimes);
			assertEquals(3, issueTimes.size());
			assertEquals(12d, issueTimes.get(Issue.IssueState.CLOSED).getFirst(), 0);
			assertEquals(80d, issueTimes.get(Issue.IssueState.CLOSED).getSecond(), 0);
		});
	}

	@Test
	public void testCalculateWorkingTimeByEmployeeIdGroupedByIssueState() {
		doInJPA(JpaTestingUtil::getEntityManagerFactory, entityManager -> {
			DalTransaction transaction = new DalTransactionJpa(entityManager);

			EmployeeDao employeeDao = new EmployeeDaoJpa();
			Employee employee = employeeDao.findAll(transaction)
					.stream()
					.filter(e -> e.getFirstName().equals("Homer"))
					.filter(e -> e.getLastName().equals("Simpson"))
					.filter(e -> e.getAddress().getStreet().equals("998 Mammon Lane"))
					.findFirst()
					.orElseThrow();

			IssueDao issueDao = new IssueDaoJpa();
			var workingTimes = issueDao.calculateWorkingTimeByEmployeeGroupedByIssueState(
					transaction,
					employee
			);
			assertNotNull(workingTimes);
			System.out.println(workingTimes.keySet());
			assertEquals(2, workingTimes.size());
			assertEquals(1, workingTimes.get(Issue.IssueState.OPEN).getFirst().longValue());
			assertEquals(8d, workingTimes.get(Issue.IssueState.OPEN).getSecond(), 0);
		});
	}
}

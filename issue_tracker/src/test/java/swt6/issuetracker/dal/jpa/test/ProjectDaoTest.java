package swt6.issuetracker.dal.jpa.test;

import org.junit.BeforeClass;
import org.junit.Test;
import swt6.issuetracker.dal.DalTransaction;
import swt6.issuetracker.dal.ProjectDao;
import swt6.issuetracker.dal.jpa.DalTransactionJpa;
import swt6.issuetracker.dal.jpa.ProjectDaoJpa;
import swt6.issuetracker.domain.Address;
import swt6.issuetracker.domain.Employee;
import swt6.issuetracker.domain.Project;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.hibernate.testing.transaction.TransactionUtil.*;

public class ProjectDaoTest {
	@BeforeClass
	public static void initializeDemoData() {
		doInJPA(JpaTestingUtil::getEntityManagerFactory, entityManager -> {
			Project project = new Project("Roll out new time tracking software");
			entityManager.persist(project);

			Project finishedProjectA = new Project("WETR");
			entityManager.persist(finishedProjectA);

			Project finishedProjectB = new Project("COMPLETE");
			entityManager.persist(finishedProjectB);

			Project updatedProject = new Project("Implement debugger");
			entityManager.persist(updatedProject);

			Project unstaffedProject = new Project("Recycle nuclear waste");
			entityManager.persist(unstaffedProject);

			Project staffedProject = new Project("Reactor core simulation");
			staffedProject.addEmployee(new Employee(
					"John", "Frink",
					LocalDate.of(1959, 6, 18),
					new Address(
							"00815", "Springfield", "65 Chestnut Street"
					)
			));
			entityManager.persist(staffedProject);
		});
	}

	@Test
	public void testFindAll() {
		doInJPA(JpaTestingUtil::getEntityManagerFactory, entityManager -> {
			ProjectDao projectDao = new ProjectDaoJpa();
			List<Project> projects = projectDao.findAll(new DalTransactionJpa(entityManager));
			assertNotNull(projects);
			assertTrue(projects.size() > 0);
		});
	}

	@Test
	public void testFindProjectById() {
		doInJPA(JpaTestingUtil::getEntityManagerFactory, entityManager -> {
			ProjectDao projectDao = new ProjectDaoJpa();
			Optional<Project> projectContainer = projectDao.findById(new DalTransactionJpa(entityManager), 1);
			assertNotNull(projectContainer);
			assertTrue(projectContainer.isPresent());

			Project project = projectContainer.get();
			assertNotNull(project);
		});
	}

	@Test
	public void testSearchNonExistingProjectById() {
		doInJPA(JpaTestingUtil::getEntityManagerFactory, entityManager -> {
			ProjectDao projectDao = new ProjectDaoJpa();
			Optional<Project> projectContainer = projectDao.findById(new DalTransactionJpa(entityManager), 1180);
			assertNotNull(projectContainer);
			assertTrue(projectContainer.isEmpty());
		});
	}

	@Test
	public void testAddProject() {
		doInJPA(JpaTestingUtil::getEntityManagerFactory, entityManager -> {
			ProjectDao projectDao = new ProjectDaoJpa();
			Project project = new Project("Tax evasion software");
			Project addedProject = projectDao.add(new DalTransactionJpa(entityManager), project);
			assertTrue(entityManager.contains(addedProject));
		});
	}

	@Test
	public void testDeleteProjectByObject() {
		doInJPA(JpaTestingUtil::getEntityManagerFactory, entityManager -> {
			ProjectDao projectDao = new ProjectDaoJpa();
			DalTransaction transaction = new DalTransactionJpa(entityManager);
			Project project = projectDao.findAll(transaction).stream()
					.filter(p -> p.getName().equals("WETR"))
					.findFirst()
					.orElseThrow();

			assertTrue(entityManager.contains(project));
			projectDao.delete(transaction, project);
			assertFalse(entityManager.contains(project));
		});
	}

	@Test
	public void testDeleteProjectById() {
		doInJPA(JpaTestingUtil::getEntityManagerFactory, entityManager -> {
			ProjectDao projectDao = new ProjectDaoJpa();
			DalTransaction transaction = new DalTransactionJpa(entityManager);
			Project project = projectDao.findAll(transaction).stream()
					.filter(p -> p.getName().equals("COMPLETE"))
					.findFirst()
					.orElseThrow();
			assertTrue(projectDao.delete(transaction, project.getId()));
		});
	}

	@Test
	public void testUpdateProjectName() {
		doInJPA(JpaTestingUtil::getEntityManagerFactory, entityManager -> {
			ProjectDao projectDao = new ProjectDaoJpa();
			DalTransaction transaction = new DalTransactionJpa(entityManager);
			Project project = projectDao.findAll(transaction).stream()
					.filter(p -> p.getName().equals("Implement debugger"))
					.findFirst()
					.orElseThrow();

			assertEquals("Implement debugger", project.getName());
			Project updatedProject = projectDao.updateName(transaction, project, "Implement debugger for embedded systems");
			assertEquals("Implement debugger for embedded systems", project.getName());
		});
	}

	@Test
	public void testAddEmployee() {
		doInJPA(JpaTestingUtil::getEntityManagerFactory, entityManager -> {
			ProjectDao projectDao = new ProjectDaoJpa();
			DalTransaction transaction = new DalTransactionJpa(entityManager);

			Project project = projectDao.findAll(transaction).stream()
					.filter(p -> p.getName().equals("Recycle nuclear waste"))
					.findFirst()
					.orElseThrow();
			assertEquals(0, project.getEmployees().size());
			Employee employee = new Employee(
					"Mindy", "Simmons",
					LocalDate.of(1975, 7, 23),
					new Address("00815", "Springfield", "Chestnut Street 34")
			);
			assertEquals(0, employee.getProjects().size());

			projectDao.assignEmployeeToProject(transaction, employee, project);
			assertEquals(1, project.getEmployees().size());
			assertEquals(1, employee.getProjects().size());
		});
	}

	@Test
	public void testDismissEmployee() {
		doInJPA(JpaTestingUtil::getEntityManagerFactory, entityManager -> {
			ProjectDao projectDao = new ProjectDaoJpa();
			DalTransaction transaction = new DalTransactionJpa(entityManager);

			Project project = projectDao.findAll(transaction).stream()
					.filter(p -> p.getName().equals("Reactor core simulation"))
					.findFirst()
					.orElseThrow();
			assertEquals(1, project.getEmployees().size());
			Employee employee = project.getEmployees().iterator().next();
			assertEquals(1, employee.getProjects().size());

			projectDao.dismissEmployeeFromProject(transaction, employee, project);
			assertEquals(0, project.getEmployees().size());
			assertEquals(0, employee.getProjects().size());
		});
	}
}

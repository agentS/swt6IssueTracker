package swt6.issuetracker.dal.jpa;

import swt6.issuetracker.dal.DalTransaction;
import swt6.issuetracker.dal.ProjectDao;
import swt6.issuetracker.domain.*;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class ProjectDaoJpa implements ProjectDao {
	@Override
	public List<Project> findAll(DalTransaction transaction) {
		EntityManager entityManager = DaoUtilJpa.getEntityManager(transaction);
		return entityManager
				.createQuery("SELECT P FROM Project AS P ORDER BY P.name DESC", Project.class)
				.getResultList();
	}

	@Override
	public Optional<Project> findById(DalTransaction transaction, long id) {
		EntityManager entityManager = DaoUtilJpa.getEntityManager(transaction);
		Project project = entityManager.find(Project.class, id);
		if (project != null) {
			return Optional.of(project);
		} else {
			return Optional.empty();
		}
	}

	@Override
	public Project add(DalTransaction transaction, Project project) {
		EntityManager entityManager = DaoUtilJpa.getEntityManager(transaction);
		return entityManager.merge(project);
	}

	@Override
	public Project updateName(DalTransaction transaction, Project project, String newName) {
		EntityManager entityManager = DaoUtilJpa.getEntityManager(transaction);
		Project updatedProject = entityManager.merge(project);
		updatedProject.setName(newName);
		return updatedProject;
	}

	@Override
	public void delete(DalTransaction transaction, Project project) {
		EntityManager entityManager = DaoUtilJpa.getEntityManager(transaction);

		for (Employee employee : project.getEmployees()) {
			employee.getProjects().remove(project);
		}

		entityManager.remove(project);
	}

	@Override
	public boolean delete(DalTransaction transaction, long id) {
		EntityManager entityManager = DaoUtilJpa.getEntityManager(transaction);
		Query deleteStatement = entityManager.createQuery("DELETE FROM Project AS P WHERE P.id = :id");
		deleteStatement.setParameter("id", id);
		return deleteStatement.executeUpdate() > 0;
	}

	@Override
	public void assignEmployeeToProject(DalTransaction transaction, Employee employee, Project project) {
		EntityManager entityManager = DaoUtilJpa.getEntityManager(transaction);
		Project targetProject = entityManager.merge(project);
		targetProject.addEmployee(employee);
		entityManager.merge(employee);
	}

	@Override
	public void dismissEmployeeFromProject(DalTransaction transaction, Employee employee, Project project) {
		EntityManager entityManager = DaoUtilJpa.getEntityManager(transaction);
		Project targetProject = entityManager.merge(project);
		targetProject.removeEmployee(employee);
		entityManager.merge(employee);
	}
}

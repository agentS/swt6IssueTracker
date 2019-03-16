package swt6.issuetracker.dal.jpa;

import swt6.issuetracker.dal.DalTransaction;
import swt6.issuetracker.dal.IssueDao;
import swt6.issuetracker.domain.Employee;
import swt6.issuetracker.domain.Issue;
import swt6.issuetracker.domain.Project;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

public class IssueDaoJpa implements IssueDao {
	@Override
	public List<Issue> findAllByEmployeeId(DalTransaction transaction, long employeeId) {
		EntityManager entityManager = DaoUtilJpa.getEntityManager(transaction);
		TypedQuery<Issue> query =  entityManager
				.createQuery("SELECT I FROM Issue AS I WHERE I.employee.id = :id ORDER BY I.id", Issue.class);
		query.setParameter("id", employeeId);
		return query.getResultList();
	}

	@Override
	public List<Issue> findAllByProjectId(DalTransaction transaction, long projectId) {
		EntityManager entityManager = DaoUtilJpa.getEntityManager(transaction);
		TypedQuery<Issue> query = entityManager
				.createQuery("SELECT I FROM Issue AS I WHERE I.project.id = :id ORDER BY I.id", Issue.class);
		query.setParameter("id", projectId);
		return query.getResultList();
	}

	@Override
	public Optional<Issue> findById(DalTransaction transaction, long id) {
		EntityManager entityManager = DaoUtilJpa.getEntityManager(transaction);
		Issue issue = entityManager.find(Issue.class, id);
		if (issue != null) {
			return Optional.of(issue);
		} else {
			return Optional.empty();
		}
	}

	@Override
	public Issue add(DalTransaction transaction, Issue issue, Project project) {
		EntityManager entityManager = DaoUtilJpa.getEntityManager(transaction);
		Project targetProject = entityManager.merge(project);
		issue.attachProject(targetProject);
		return entityManager.merge(issue);
	}

	@Override
	public Issue add(DalTransaction transaction, Issue issue, Project project, Employee assignee) {
		EntityManager entityManager = DaoUtilJpa.getEntityManager(transaction);
		Employee targetAssignee = entityManager.merge(assignee);
		issue.attachEmployee(targetAssignee);
		Project targetProject = entityManager.merge(project);
		issue.attachProject(targetProject);
		return entityManager.merge(issue);
	}

	@Override
	public Issue updateAssignee(DalTransaction transaction, Issue issue, Employee newAssignee) {
		EntityManager entityManager = DaoUtilJpa.getEntityManager(transaction);

		Issue targetIssue = entityManager.merge(issue);
		targetIssue.attachEmployee(newAssignee);
		return targetIssue;
	}

	@Override
	public Issue updateState(DalTransaction transaction, Issue issue, Issue.IssueState newState) {
		EntityManager entityManager = DaoUtilJpa.getEntityManager(transaction);

		Issue targetIssue = entityManager.merge(issue);
		targetIssue.setState(newState);
		return targetIssue;
	}

	@Override
	public Issue updatePriority(DalTransaction transaction, Issue issue, Issue.IssuePriority newPriority) {
		EntityManager entityManager = DaoUtilJpa.getEntityManager(transaction);

		Issue targetIssue = entityManager.merge(issue);
		targetIssue.setPriority(newPriority);
		return targetIssue;
	}

	@Override
	public Issue updateProgress(DalTransaction transaction, Issue issue, int newProgress) {
		EntityManager entityManager = DaoUtilJpa.getEntityManager(transaction);

		Issue targetIssue = entityManager.merge(issue);
		targetIssue.setProgress(newProgress);
		return targetIssue;
	}

	@Override
	public Issue updateEstimatedTime(DalTransaction transaction, Issue issue, double newEstimatedTime) {
		EntityManager entityManager = DaoUtilJpa.getEntityManager(transaction);

		Issue targetIssue = entityManager.merge(issue);
		targetIssue.setEstimatedTime(newEstimatedTime);
		return targetIssue;
	}

	@Override
	public void delete(DalTransaction transaction, Issue issue) {
		EntityManager entityManager = DaoUtilJpa.getEntityManager(transaction);
		Issue targetIssue = entityManager.merge(issue);
		issue.detachProject();
		issue.detachEmployee();
		entityManager.remove(targetIssue);
	}
}

package swt6.issuetracker.dal.jpa;

import swt6.issuetracker.dal.DalTransaction;
import swt6.issuetracker.dal.IssueDao;
import swt6.issuetracker.domain.*;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.*;
import java.util.stream.Collectors;

public class IssueDaoJpa implements IssueDao {
	@Override
	public List<Issue> findAllByEmployeeId(DalTransaction transaction, long employeeId) {
		EntityManager entityManager = DaoUtilJpa.getEntityManager(transaction);
		TypedQuery<Issue> query = entityManager.createQuery(
				"SELECT I FROM Issue AS I WHERE I.employee.id = :id ORDER BY I.id DESC",
				Issue.class
		);
		query.setParameter("id", employeeId);
		return query.getResultList();
	}

	@Override
	public Map<Issue.IssueState, List<Issue>> findAllByEmployeeIdGroupByIssueState(DalTransaction transaction, long employeeId) {
		return this.findAllByEmployeeId(transaction, employeeId)
				.stream()
				.collect(Collectors.groupingBy(
						Issue::getState,
						Collectors.toList()
				));
	}

	@Override
	public List<Issue> findAllByProjectId(DalTransaction transaction, long projectId) {
		EntityManager entityManager = DaoUtilJpa.getEntityManager(transaction);
		TypedQuery<Issue> query = entityManager.createQuery(
				"SELECT I FROM Issue AS I WHERE I.project.id = :id ORDER BY I.id DESC",
				Issue.class
		);
		query.setParameter("id", projectId);
		return query.getResultList();
	}

	@Override
	public Map<Issue.IssueState, List<Issue>> findAllByProjectIdGroupByIssueState(DalTransaction transaction, long projectId) {
		return this.findAllByProjectId(transaction, projectId)
				.stream()
				.collect(Collectors.groupingBy(
						Issue::getState,
						Collectors.toList()
				));
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

	@Override
	public Map<Issue.IssueState, Pair<Double, Double>> calculateWorkingTimeAndEstimatedTimeByEmployeeIdGroupByIssueState(
			DalTransaction transaction,
			long employeeId
	) {
		EntityManager entityManager = DaoUtilJpa.getEntityManager(transaction);
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

		var criteriaQuery = criteriaBuilder.createQuery(Object[].class);
		Root<Employee> subQueryRoot = criteriaQuery.from(Employee.class);
		Join<Employee, Issue> issueJoin = subQueryRoot.join("issues");
		Join<Issue, LogBookEntry> logBookEntryJoin = issueJoin.join("logBookEntries");
		criteriaQuery
				.multiselect(
						issueJoin.get("state").alias("state"),
						criteriaBuilder.sumAsDouble(issueJoin.get("estimatedTime")).alias("workTime"),
						issueJoin.get("estimatedTime").alias("estimatedTime")
				)
				.where(criteriaBuilder.equal(subQueryRoot.get("id"), employeeId))
				//.where(criteriaBuilder.equal(logBookEntryJoin.get("employee").get("id"), employeeId))
				.groupBy(issueJoin.get("state"), issueJoin.get("id"), issueJoin.get("estimatedTime"));

		List<Triple<Issue.IssueState, Double, Double>> issueTimes = new ArrayList<>();
		for (Object[] row : entityManager.createQuery(criteriaQuery).getResultList()) {
			issueTimes.add(new Triple<>(
					((Issue.IssueState) row[0]),
					((Double) row[1]),
					((Double) row[2])
			));
		}
		// subqueries in from clauses are not supported by JPA: https://stackoverflow.com/questions/7269010/jpa-hibernate-subquery-in-from-clause
		return reduceByIssueState(issueTimes);
	}

	private static Map<Issue.IssueState, Pair<Double, Double>> reduceByIssueState(
			List<Triple<Issue.IssueState, Double, Double>> issueTimes
	) {
		Map<Issue.IssueState, Pair<Double, Double>> result = new HashMap<>();
		for (Triple<Issue.IssueState, Double, Double> processedIssueTimes : issueTimes) {
			if (result.containsKey(processedIssueTimes.getFirst())) {
				Pair<Double, Double> modifiedIssueTimes = result.get(processedIssueTimes.getFirst());
				modifiedIssueTimes.setFirst(modifiedIssueTimes.getFirst() + processedIssueTimes.getSecond());
				modifiedIssueTimes.setSecond(modifiedIssueTimes.getSecond() + processedIssueTimes.getThird());
			} else {
				result.put(
						processedIssueTimes.getFirst(),
						new Pair<>(processedIssueTimes.getSecond(), processedIssueTimes.getThird())
				);
			}
		}
		return result;
	}

	@Override
	public Map<Issue.IssueState, Pair<Double, Double>> calculateWorkingTimeAndEstimatedTimeByProjectIdGroupByIssueState(
			DalTransaction transaction,
			long projectId
	) {
		EntityManager entityManager = DaoUtilJpa.getEntityManager(transaction);
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

		var criteriaQuery = criteriaBuilder.createQuery(Object[].class);
		Root<Project> root = criteriaQuery.from(Project.class);
		Join<Project, Issue> issueJoin = root.join("issues");
		Join<Issue, LogBookEntry> logBookEntryJoin = issueJoin.join("logBookEntries");
		criteriaQuery
				.multiselect(
						issueJoin.get("state").alias("state"),
						criteriaBuilder.sumAsDouble(issueJoin.get("estimatedTime")).alias("workTime"),
						issueJoin.get("estimatedTime").alias("estimatedTime")
				)
				.where(criteriaBuilder.equal(root.get("id"), projectId))
				.groupBy(issueJoin.get("state"), issueJoin.get("id"), issueJoin.get("estimatedTime"));

		List<Triple<Issue.IssueState, Double, Double>> issueTimes = new ArrayList<>();
		for (Object[] row : entityManager.createQuery(criteriaQuery).getResultList()) {
			issueTimes.add(new Triple<>(
					((Issue.IssueState) row[0]),
					((Double) row[2]),
					((Double) row[1])
			));
		}
		// subqueries in from clauses are not supported by JPA: https://stackoverflow.com/questions/7269010/jpa-hibernate-subquery-in-from-clause
		return reduceByIssueState(issueTimes);
	}

	@Override
	public List<Triple<Issue.IssueState, Long, Double>> calculateWorkingTimeByEmployeeIdGroupedByIssueState(
			DalTransaction transaction,
			long employeeId
	) {
		EntityManager entityManager = DaoUtilJpa.getEntityManager(transaction);
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

		var criteriaQuery = criteriaBuilder.createQuery(Object[].class);
		Root<Employee> root = criteriaQuery.from(Employee.class);
		Join<Employee, Issue> issueJoin = root.join("issues");
		Join<Employee, LogBookEntry> logBookEntryJoin = issueJoin.join("logBookEntries");
		criteriaQuery
				.multiselect(
						issueJoin.get("state"),
						criteriaBuilder.count(issueJoin.get("id")),
						criteriaBuilder.sumAsDouble(issueJoin.get("estimatedTime"))
				)
				.where(criteriaBuilder.equal(root.get("id"), employeeId))
				.groupBy(issueJoin.get("state"));

		TypedQuery<Object[]> query = entityManager.createQuery(criteriaQuery);
		return query.getResultStream()
				.map(row -> new Triple<>(
						((Issue.IssueState) row[0]),
						((Long) row[1]),
						((Double) row[2])
				))
				.collect(Collectors.toList());
	}
}

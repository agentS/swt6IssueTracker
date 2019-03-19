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
	public List<Issue> findAllByEmployee(DalTransaction transaction, Employee employee) {
		EntityManager entityManager = DaoUtilJpa.getEntityManager(transaction);
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

		CriteriaQuery<Issue> criteriaQuery = criteriaBuilder.createQuery(Issue.class);
		Root<Issue> root = criteriaQuery.from(Issue.class);
		ParameterExpression<Employee> employeeParameter = criteriaBuilder.parameter(Employee.class);
		TypedQuery<Issue> query = entityManager.createQuery(
				criteriaQuery
						.select(root)
						.where(criteriaBuilder.equal(root.get(Issue_.employee), employeeParameter))
						.orderBy(criteriaBuilder.desc(root.get(Issue_.id)))
		);
		query.setParameter(employeeParameter, employee);
		return query.getResultList();
	}

	@Override
	public Map<Issue.IssueState, List<Issue>> findAllByEmployeeGroupByIssueState(DalTransaction transaction, Employee employee) {
		return this.findAllByEmployee(transaction, employee)
				.stream()
				.collect(Collectors.groupingBy(
						Issue::getState,
						Collectors.toList()
				));
	}

	@Override
	public List<Issue> findAllByProject(DalTransaction transaction, Project project) {
		EntityManager entityManager = DaoUtilJpa.getEntityManager(transaction);
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

		CriteriaQuery<Issue> criteriaQuery = criteriaBuilder.createQuery(Issue.class);
		Root<Issue> root = criteriaQuery.from(Issue.class);
		ParameterExpression<Project> projectParameter = criteriaBuilder.parameter(Project.class);

		TypedQuery<Issue> query = entityManager.createQuery(
				criteriaQuery
						.select(root)
						.where(criteriaBuilder.equal(root.get(Issue_.project), projectParameter))
						.orderBy(criteriaBuilder.desc(root.get(Issue_.id)))
		);
		query.setParameter(projectParameter, project);
		return query.getResultList();
	}

	@Override
	public Map<Issue.IssueState, List<Issue>> findAllByProjectGroupByIssueState(DalTransaction transaction, Project project) {
		return this.findAllByProject(transaction, project)
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
	public Map<Issue.IssueState, Pair<Double, Double>> calculateWorkingTimeAndEstimatedTimeByEmployeeGroupByIssueState(
			DalTransaction transaction,
			Employee employee
	) {
		EntityManager entityManager = DaoUtilJpa.getEntityManager(transaction);
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

		var criteriaQuery = criteriaBuilder.createQuery(Object[].class);
		Root<Employee> root = criteriaQuery.from(Employee.class);
		Join<Employee, Issue> issueJoin = root.join(Employee_.issues);
		Join<Issue, LogBookEntry> logBookEntryJoin = issueJoin.join(Issue_.logBookEntries);
		ParameterExpression<Employee> employeeParameter = criteriaBuilder.parameter(Employee.class);
		criteriaQuery
				.multiselect(
						issueJoin.get(Issue_.state),
						criteriaBuilder.sumAsDouble(issueJoin.get(Issue_.ESTIMATED_TIME)),
						issueJoin.get(Issue_.estimatedTime)
				)
				.where(criteriaBuilder.equal(root.get(Employee_.id), employeeParameter))
				.where(criteriaBuilder.equal(logBookEntryJoin.get(LogBookEntry_.employee), employeeParameter))
				.groupBy(issueJoin.get(Issue_.state), issueJoin.get(Issue_.id), issueJoin.get(Issue_.estimatedTime));

		List<Triple<Issue.IssueState, Double, Double>> issueTimes = new ArrayList<>();
		TypedQuery<Object[]> query = entityManager.createQuery(criteriaQuery)
				.setParameter(employeeParameter, employee);
		for (Object[] row : query.getResultList()) {
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
	public Map<Issue.IssueState, Pair<Double, Double>> calculateWorkingTimeAndEstimatedTimeByProjectGroupByIssueState(
			DalTransaction transaction,
			Project project
	) {
		EntityManager entityManager = DaoUtilJpa.getEntityManager(transaction);
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

		var criteriaQuery = criteriaBuilder.createQuery(Object[].class);
		Root<Project> root = criteriaQuery.from(Project.class);
		Join<Project, Issue> issueJoin = root.join(Project_.issues);
		Join<Issue, LogBookEntry> logBookEntryJoin = issueJoin.join(Issue_.logBookEntries);
		ParameterExpression<Project> projectParameter = criteriaBuilder.parameter(Project.class);
		criteriaQuery
				.multiselect(
						issueJoin.get(Issue_.state),
						criteriaBuilder.sumAsDouble(issueJoin.get(Issue_.ESTIMATED_TIME)),
						issueJoin.get(Issue_.ESTIMATED_TIME)
				)
				.where(criteriaBuilder.equal(root, projectParameter))
				.groupBy(issueJoin.get(Issue_.state), issueJoin.get(Issue_.id), issueJoin.get(Issue_.estimatedTime));

		List<Triple<Issue.IssueState, Double, Double>> issueTimes = new ArrayList<>();
		TypedQuery<Object[]> query = entityManager
				.createQuery(criteriaQuery)
				.setParameter(projectParameter, project);
		for (Object[] row : query.getResultList()) {
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
	public Map<Issue.IssueState, Pair<Long, Double>> calculateWorkingTimeByEmployeeGroupedByIssueState(
			DalTransaction transaction,
			Employee employee
	) {
		EntityManager entityManager = DaoUtilJpa.getEntityManager(transaction);
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

		var criteriaQuery = criteriaBuilder.createQuery(Object[].class);
		Root<Employee> root = criteriaQuery.from(Employee.class);
		Join<Employee, Issue> issueJoin = root.join(Employee_.issues);
		Join<Issue, LogBookEntry> logBookEntryJoin = issueJoin.join(Issue_.logBookEntries);
		ParameterExpression<Employee> employeeParameter = criteriaBuilder.parameter(Employee.class);
		criteriaQuery
				.multiselect(
						issueJoin.get(Issue_.state),
						criteriaBuilder.count(issueJoin.get(Issue_.id)),
						criteriaBuilder.sumAsDouble(issueJoin.get(Issue_.ESTIMATED_TIME))
				)
				.where(criteriaBuilder.equal(root, employeeParameter))
				.where(criteriaBuilder.equal(logBookEntryJoin.get(LogBookEntry_.employee), employeeParameter))
				.groupBy(issueJoin.get(Issue_.state));

		TypedQuery<Object[]> query = entityManager.createQuery(criteriaQuery)
				.setParameter(employeeParameter, employee);
		return query.getResultStream()
				.map(row -> new Pair<>(
						((Issue.IssueState) row[0]),
						new Pair<>(
								((Long) row[1]),
								((Double) row[2])
						)
				))
				.collect(Collectors.toMap(
						Pair::getFirst,
						Pair::getSecond
				));
	}

	@Override
	public Map<ProjectPhase, Double> calculateWorkingTimePerProjectPhase(DalTransaction transaction, Project project) {
		EntityManager entityManager = DaoUtilJpa.getEntityManager(transaction);
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

		var criteriaQuery = criteriaBuilder.createQuery(Object[].class);
		Root<Project> root = criteriaQuery.from(Project.class);
		ParameterExpression<Project> projectParameter = criteriaBuilder.parameter(Project.class);
		Join<Project, Issue> issueJoin = root.join(Project_.issues);
		Join<Issue, LogBookEntry> logBookEntryJoin = issueJoin.join(Issue_.logBookEntries);
		criteriaQuery
				.multiselect(
						logBookEntryJoin.get(LogBookEntry_.projectPhase),
						criteriaBuilder.sumAsDouble(issueJoin.get(Issue_.ESTIMATED_TIME))
						/*
						criteriaBuilder.sum(
								// watch the idiots discussing: https://stackoverflow.com/questions/22412234/using-timestampdiff-with-jpa-criteria-query-and-hibernate-as-the-provider
								criteriaBuilder.function(
										"TIMESTAMPDIFF",
										Double.class,
										criteriaBuilder.literal("SQL_TSI_HOUR"),
										logBookEntryJoin.get("startTime"),
										logBookEntryJoin.get("endTime")
								)
						)*/
				)
				.where(criteriaBuilder.equal(root, projectParameter))
				.groupBy(logBookEntryJoin.get(LogBookEntry_.projectPhase));

		TypedQuery<Object[]> query = entityManager.createQuery(criteriaQuery);
		query.setParameter(projectParameter, project);
		return query.getResultStream()
				.map(row -> new Pair<>(
						((ProjectPhase) row[0]),
						((Double) row[1])
				))
				.collect(Collectors.toMap(
						Pair::getFirst,
						Pair::getSecond
				));
	}
}

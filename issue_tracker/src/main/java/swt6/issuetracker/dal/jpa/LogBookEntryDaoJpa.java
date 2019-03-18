package swt6.issuetracker.dal.jpa;

import swt6.issuetracker.dal.DalTransaction;
import swt6.issuetracker.dal.LogBookEntryDao;
import swt6.issuetracker.domain.*;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class LogBookEntryDaoJpa implements LogBookEntryDao {
	@Override
	public Optional<LogBookEntry> findById(DalTransaction transaction, long id) {
		EntityManager entityManager = DaoUtilJpa.getEntityManager(transaction);
		LogBookEntry entry = entityManager.find(LogBookEntry.class, id);
		if (entry != null) {
			return Optional.of(entry);
		} else {
			return Optional.empty();
		}
	}

	@Override
	public List<LogBookEntry> findAllByIssue(DalTransaction transaction, Issue issue) {
		EntityManager entityManager = DaoUtilJpa.getEntityManager(transaction);
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

		CriteriaQuery<LogBookEntry> criteriaQuery = criteriaBuilder.createQuery(LogBookEntry.class);
		Root<LogBookEntry> root = criteriaQuery.from(LogBookEntry.class);
		ParameterExpression<Issue> issueParameter = criteriaBuilder.parameter(Issue.class);
		TypedQuery<LogBookEntry> query = entityManager.createQuery(
				criteriaQuery
						.select(root)
						.where(criteriaBuilder.equal(root.get(LogBookEntry_.issue), issueParameter))
						.orderBy(criteriaBuilder.desc(root.get(LogBookEntry_.endTime)))
		);
		query.setParameter(issueParameter, issue);
		return query.getResultList();
	}

	public Map<ProjectPhase, List<LogBookEntry>> findAllByIssueGroupByProjectPhase(DalTransaction transaction, Issue issue) {
		List<LogBookEntry> entries = this.findAllByIssue(transaction, issue);
		return entries.stream()
				.collect(
						Collectors.groupingBy(
								LogBookEntry::getProjectPhase,
								Collectors.toList()
						)
				);
	}

	@Override
	public List<LogBookEntry> findAllByEmployee(DalTransaction transaction, Employee employee) {
		EntityManager entityManager = DaoUtilJpa.getEntityManager(transaction);
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

		CriteriaQuery<LogBookEntry> criteriaQuery = criteriaBuilder.createQuery(LogBookEntry.class);
		Root<LogBookEntry> root = criteriaQuery.from(LogBookEntry.class);
		ParameterExpression<Employee> employeeParameter = criteriaBuilder.parameter(Employee.class);

		TypedQuery<LogBookEntry> query = entityManager.createQuery(
				criteriaQuery
						.select(root)
						.where(criteriaBuilder.equal(root.get(LogBookEntry_.employee), employeeParameter))
						.orderBy(criteriaBuilder.desc(root.get(LogBookEntry_.endTime)))
		);
		query.setParameter(employeeParameter, employee);
		return query.getResultList();
	}

	@Override
	public LogBookEntry add(DalTransaction transaction, LogBookEntry entry, Employee employee, Issue issue) {
		EntityManager entityManager = DaoUtilJpa.getEntityManager(transaction);
		entry.attachEmployee(employee);
		entry.attachIssue(issue);
		return entityManager.merge(entry);
	}

	@Override
	public void remove(DalTransaction transaction, LogBookEntry entry) {
		EntityManager entityManager = DaoUtilJpa.getEntityManager(transaction);
		LogBookEntry targetEntry = entityManager.merge(entry);
		targetEntry.detachEmployee();
		targetEntry.detachIssue();
		entityManager.remove(targetEntry);
	}

	@Override
	public LogBookEntry reassignToEmployee(DalTransaction transaction, LogBookEntry entry, Employee newAssignee) {
		EntityManager entityManager = DaoUtilJpa.getEntityManager(transaction);
		LogBookEntry targetEntry = entityManager.merge(entry);
		targetEntry.attachEmployee(newAssignee);
		return targetEntry;
	}

	@Override
	public LogBookEntry reassignToIssue(DalTransaction transaction, LogBookEntry entry, Issue newAssignedIssue) {
		EntityManager entityManager = DaoUtilJpa.getEntityManager(transaction);
		LogBookEntry targetEntry = entityManager.merge(entry);
		targetEntry.attachIssue(newAssignedIssue);
		return targetEntry;
	}
}

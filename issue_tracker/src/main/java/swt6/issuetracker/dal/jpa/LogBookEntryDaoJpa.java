package swt6.issuetracker.dal.jpa;

import swt6.issuetracker.dal.DalTransaction;
import swt6.issuetracker.dal.LogBookEntryDao;
import swt6.issuetracker.domain.Employee;
import swt6.issuetracker.domain.Issue;
import swt6.issuetracker.domain.LogBookEntry;
import swt6.issuetracker.domain.ProjectPhase;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
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
	public List<LogBookEntry> findAllByIssueId(DalTransaction transaction, long issueId) {
		EntityManager entityManager = DaoUtilJpa.getEntityManager(transaction);
		TypedQuery<LogBookEntry> query = entityManager.createQuery(
				"SELECT L FROM LogBookEntry AS L WHERE L.issue.id = :id ORDER BY L.startTime DESC",
				LogBookEntry.class
		);
		query.setParameter("id", issueId);
		return query.getResultList();
	}

	public Map<ProjectPhase, List<LogBookEntry>> findAllByIssueIdGroupByProjectPhase(DalTransaction transaction, long issueId) {
		List<LogBookEntry> entries = this.findAllByIssueId(transaction, issueId);
		return entries.stream()
				.collect(
						Collectors.groupingBy(
								LogBookEntry::getProjectPhase,
								Collectors.toList()
						)
				);
	}

	@Override
	public List<LogBookEntry> findAllByEmployeeId(DalTransaction transaction, long employeeId) {
		EntityManager entityManager = DaoUtilJpa.getEntityManager(transaction);
		TypedQuery<LogBookEntry> query = entityManager.createQuery(
				"SELECT L FROM LogBookEntry AS L WHERE L.employee.id = :id ORDER BY L.startTime DESC",
				LogBookEntry.class
		);
		query.setParameter("id", employeeId);
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

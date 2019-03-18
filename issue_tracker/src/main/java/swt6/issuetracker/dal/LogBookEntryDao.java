package swt6.issuetracker.dal;

import swt6.issuetracker.domain.Employee;
import swt6.issuetracker.domain.Issue;
import swt6.issuetracker.domain.LogBookEntry;
import swt6.issuetracker.domain.ProjectPhase;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface LogBookEntryDao {
	Optional<LogBookEntry> findById(DalTransaction transaction, long id);
	List<LogBookEntry> findAllByIssue(DalTransaction transaction, Issue issue);
	Map<ProjectPhase, List<LogBookEntry>> findAllByIssueGroupByProjectPhase(DalTransaction transaction, Issue issue);
	List<LogBookEntry> findAllByEmployee(DalTransaction transaction, Employee employee);
	LogBookEntry add(DalTransaction transaction, LogBookEntry entry, Employee employee, Issue issue);
	void remove(DalTransaction transaction, LogBookEntry entry);
	LogBookEntry reassignToEmployee(DalTransaction transaction, LogBookEntry entry, Employee newAssignee);
	LogBookEntry reassignToIssue(DalTransaction transaction, LogBookEntry entry, Issue newAssignedIssue);
}

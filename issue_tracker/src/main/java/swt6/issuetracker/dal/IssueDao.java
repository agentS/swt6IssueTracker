package swt6.issuetracker.dal;

import swt6.issuetracker.domain.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface IssueDao {
	List<Issue> findAllByEmployeeId(DalTransaction transaction, long employeeId);
	List<Issue> findAllByProjectId(DalTransaction transaction, long projectId);
	Map<Issue.IssueState, List<Issue>> findAllByEmployeeIdGroupByIssueState(DalTransaction transaction, long employeeId);
	Map<Issue.IssueState, List<Issue>> findAllByProjectIdGroupByIssueState(DalTransaction transaction, long projectId);
	Optional<Issue> findById(DalTransaction transaction, long id);
	Issue add(DalTransaction transaction, Issue issue, Project project);
	Issue add(DalTransaction transaction, Issue issue, Project project, Employee assignee);
	Issue updateAssignee(DalTransaction transaction, Issue issue, Employee newAssignee);
	Issue updateState(DalTransaction transaction, Issue issue, Issue.IssueState newState);
	Issue updatePriority(DalTransaction transaction, Issue issue, Issue.IssuePriority newPriority);
	Issue updateProgress(DalTransaction transaction, Issue issue, int newProgress);
	Issue updateEstimatedTime(DalTransaction transaction, Issue issue, double newEstimatedTime);
	void delete(DalTransaction transaction, Issue issue);

	Map<Issue.IssueState, Pair<Double, Double>> calculateWorkingTimeAndEstimatedTimeByEmployeeIdGroupByIssueState(
			DalTransaction transaction,
			long employeeId
	);

	Map<Issue.IssueState, Pair<Double, Double>> calculateWorkingTimeAndEstimatedTimeByProjectIdGroupByIssueState(
			DalTransaction transaction,
			long projectId
	);

	List<Triple<Issue.IssueState, Long, Double>> calculateWorkingTimeByEmployeeIdGroupedByIssueState(
			DalTransaction transaction,
			long employeeId
	);
}

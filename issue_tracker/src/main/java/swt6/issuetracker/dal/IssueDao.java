package swt6.issuetracker.dal;

import swt6.issuetracker.domain.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface IssueDao {
	List<Issue> findAllByEmployee(DalTransaction transaction, Employee employee);
	List<Issue> findAllByProject(DalTransaction transaction, Project project);
	Map<Issue.IssueState, List<Issue>> findAllByEmployeeGroupByIssueState(DalTransaction transaction, Employee employee);
	Map<Issue.IssueState, List<Issue>> findAllByProjectGroupByIssueState(DalTransaction transaction, Project project);
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
			Employee employee
	);

	Map<Issue.IssueState, Pair<Double, Double>> calculateWorkingTimeAndEstimatedTimeByProjectIdGroupByIssueState(
			DalTransaction transaction,
			Project project
	);

	Map<Issue.IssueState, Pair<Long, Double>> calculateWorkingTimeByEmployeeIdGroupedByIssueState(
			DalTransaction transaction,
			Employee employee
	);

	Map<ProjectPhase, Double> calculateWorkingTimePerProjectPhase(DalTransaction transaction, Project project);
}

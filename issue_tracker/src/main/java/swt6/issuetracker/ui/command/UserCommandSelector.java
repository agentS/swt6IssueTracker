package swt6.issuetracker.ui.command;

public final class UserCommandSelector {
	private UserCommandSelector() {
	}

	public static UserCommand createUserCommand(String enteredCommand) {
		switch (enteredCommand) {
			case "commit":
				return new CommitCommand();
			case "rollback":
				return new RollbackCommand();
			case "listEmployees":
				return new ListEmployeesCommand();
			case "findEmployeeById":
				return new FindEmployeeByIdCommand();
			case "addEmployee":
				return new AddEmployeeCommand();
			case "deleteEmployee":
				return new DeleteEmployeeCommand();
			case "updateEmployeeLastName":
				return new UpdateEmployeeLastNameCommand();
			case "updateEmployeeAddress":
				return new UpdateEmployeeAddressCommand();
			case "listProjects":
				return new ListProjectsCommand();
			case "findProjectById":
				return new FindProjectByIdCommand();
			case "addProject":
				return new AddProjectCommand();
			case "updateProjectName":
				return new UpdateProjectNameCommand();
			case "deleteProject":
				return new DeleteProjectCommand();
			case "assignEmployeeToProject":
				return new AssignEmployeeToProjectCommand();
			case "dismissEmployeeFromProject":
				return new DismissEmployeeFromProjectCommand();
			case "findIssueById":
				return new FindIssueByIdCommand();
			case "listIssuesOfProject":
				return new ListIssuesOfProjectCommand();
			case "listIssuesOfEmployee":
				return new ListIssuesOfEmployeeCommand();
			case "addIssue":
				return new AddIssueCommand();
			case "assignIssueToEmployee":
				return new AssignIssueToEmployeeCommand();
			case "deleteIssue":
				return new DeleteIssueCommand();
			case "updateIssueProgress":
				return new UpdateIssueProgressCommand();
			case "updateIssueEstimatedTime":
				return new UpdateIssueEstimatedTimeCommand();
			case "updateIssueState":
				return new UpdateIssueStateCommand();
			case "updateIssuePriority":
				return new UpdateIssuePriorityCommand();
			case "findLogBookEntryById":
				return new FindLogBookEntryByIdCommand();
			case "listLogBookEntriesOfIssue":
				return new ListLogBookEntriesOfIssueCommand();
			case "listLogBookEntriesOfEmployee":
				return new ListLogBookEntriesOfEmployeeCommand();
			case "addLogBookEntry":
				return new AddLogBookEntryCommand();
			case "removeLogBookEntry":
				return new RemoveLogBookEntryCommand();
			case "reassignLogBookEntryToIssue":
				return new ReassignLogBookEntryToIssueCommand();
			case "reassignLogBookEntryToEmployee":
				return new ReassignLogBookEntryToEmployeeCommand();
			case "calculateWorkingTimeAndEstimatedTimeByEmployee":
				return new CalculateWorkingTimeAndEstimatedTimeByEmployeeCommand();
			case "calculateWorkingTimeAndEstimatedTimeByProject":
				return new CalculateWorkingTimeAndEstimatedTimeByProjectCommand();
			case "calculateWorkingTimeByEmployee":
				return new CalculateWorkingTimeByEmployeeCommand();
			case "calculateWorkingTimePerProjectPhase":
				return new CalculateWorkingTimePerProjectPhase();
			default:
				throw new IllegalArgumentException("Command " + enteredCommand + " is unknown!");
		}
	}
}

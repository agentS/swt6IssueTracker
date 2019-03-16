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
			case "listLogBookEntries":
				return new ListLogBookEntriesCommand();
			case "addLogBookEntry":
				return new AddLogBookEntryCommand();
			case "removeLogBookEntry":
				return new RemoveLogBookEntryCommand();
			case "reassignLogBookEntry":
				return new ReassignLogBookEntryCommand();
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
			default:
				throw new IllegalArgumentException("Command " + enteredCommand + " is unknown!");
		}
	}
}

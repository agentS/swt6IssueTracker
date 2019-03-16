package swt6.issuetracker.ui.command;

import swt6.issuetracker.dal.*;
import swt6.issuetracker.domain.Employee;
import swt6.issuetracker.domain.Issue;
import swt6.issuetracker.domain.Project;
import swt6.issuetracker.ui.CommandLineReader;

import java.util.Optional;

public class AddIssueCommand extends DataCommand {
	@Override
	protected TransactionStrategy processDataCommand(DalTransaction transaction, DaoFactory daoFactory) {
		ProjectDao projectDao = daoFactory.createProjectDao();
		Optional<Project> project = projectDao.findById(transaction, this.promptForLong("project id"));

		if (project.isPresent()) {
			if (this.promptForYesOrNo("Assign an employee to the issue?")) {
				EmployeeDao employeeDao = daoFactory.createEmployeeDao();
				Optional<Employee> employee = employeeDao.findById(transaction, this.promptForLong("employee id"));
				if (employee.isPresent()) {
					IssueDao issueDao = daoFactory.createIssueDao();
					issueDao.add(transaction, this.promptForIssue(), project.get(), employee.get());
					return TransactionStrategy.COMMIT;
				} else {
					System.out.println("Employee does not exist.");
				}
			} else {
				IssueDao issueDao = daoFactory.createIssueDao();
				issueDao.add(transaction, this.promptForIssue(), project.get());
				return TransactionStrategy.COMMIT;
			}
		} else {
			System.out.println("Project does not exist.");
		}
		return TransactionStrategy.NO_COMMIT;
	}

	private Issue promptForIssue() {
		String task = CommandLineReader.getInstance().promptFor("task");
		Issue.IssueState issueState = this.promptForIssueState();
		Issue.IssuePriority issuePriority = this.promptForIssuePriority();
		int progress = this.promptForPerCent("issue progress");
		double estimatedTime = 0;
		if (this.promptForYesOrNo("Do you want to enter the estimated time in hours?")) {
			estimatedTime = this.promptForDouble("estimated time");
		}

		return new Issue(task, issueState, issuePriority, progress, estimatedTime);
	}
}

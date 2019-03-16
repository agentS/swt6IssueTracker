package swt6.issuetracker.ui.command;

import swt6.issuetracker.dal.*;
import swt6.issuetracker.domain.Employee;
import swt6.issuetracker.domain.Issue;
import swt6.issuetracker.domain.LogBookEntry;
import swt6.issuetracker.domain.ProjectPhase;
import swt6.issuetracker.ui.CommandLineReader;

import java.time.LocalDateTime;
import java.util.Optional;

public class AddLogBookEntryCommand extends DataCommand {
	@Override
	protected TransactionStrategy processDataCommand(DalTransaction transaction, DaoFactory daoFactory) {
		IssueDao issueDao = daoFactory.createIssueDao();
		Optional<Issue> issue = issueDao.findById(transaction, this.promptForLong("issue id"));
		if (issue.isPresent()) {
			EmployeeDao employeeDao = daoFactory.createEmployeeDao();
			Optional<Employee> employee = employeeDao.findById(transaction, this.promptForLong("employee id"));
			if (employee.isPresent()) {
				LogBookEntry logBookEntry = this.promptForLogBookEntry();
				LogBookEntryDao logBookEntryDao = daoFactory.getLogBookEntryDao();
				logBookEntryDao.add(transaction, logBookEntry, employee.get(), issue.get());
				System.out.println("Logbook entry added.");
				return TransactionStrategy.COMMIT;
			} else {
				System.out.println("Employee does not exist.");
			}
		} else {
			System.out.println("Issue does not exist.");
		}
		return TransactionStrategy.NO_COMMIT;

	}

	private LogBookEntry promptForLogBookEntry() {
		String task = CommandLineReader.getInstance().promptFor("task");
		LocalDateTime startDateTime = this.promptForDateTime("start date time");
		LocalDateTime endDateTime = this.promptForDateTime("end date time");
		ProjectPhase projectPhase = this.promptForProjectPhase();
		return new LogBookEntry(task, startDateTime, endDateTime, projectPhase);
	}
}

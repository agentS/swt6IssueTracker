package swt6.issuetracker.ui.command;

import swt6.issuetracker.dal.DalTransaction;
import swt6.issuetracker.dal.DaoFactory;
import swt6.issuetracker.dal.EmployeeDao;
import swt6.issuetracker.domain.Employee;
import swt6.issuetracker.domain.LogBookEntry;
import swt6.issuetracker.domain.ProjectPhase;
import swt6.issuetracker.ui.CommandLineReader;

import java.time.LocalDateTime;
import java.util.Optional;

public class AddLogBookEntryCommand extends DataCommand {
	@Override
	protected void processDataCommand(DalTransaction transaction, DaoFactory daoFactory) {
		EmployeeDao employeeDao = daoFactory.createEmployeeDao();
		Optional<Employee> employeeContainer = employeeDao.findById(transaction, this.promptForLong("employee id"));
		if (employeeContainer.isPresent()) {
			LogBookEntry logBookEntry = this.promptForLogBookEntry();
			employeeDao.addLogBookEntryToEmployee(
					transaction,
					logBookEntry,
					employeeContainer.get()
			);
			System.out.println("Logbook entry added.");
		} else {
			System.out.println("Employee does not exist.");
		}
	}

	private LogBookEntry promptForLogBookEntry() {
		String task = CommandLineReader.getInstance().promptFor("task");
		LocalDateTime startDateTime = this.promptForDateTime("start date time");
		LocalDateTime endDateTime = this.promptForDateTime("end date time");
		ProjectPhase projectPhase = this.promptForProjectPhase();
		return new LogBookEntry(task, startDateTime, endDateTime, projectPhase);
	}
}

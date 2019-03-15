package swt6.issuetracker.ui.command;

import swt6.issuetracker.dal.DalTransaction;
import swt6.issuetracker.dal.DaoFactory;
import swt6.issuetracker.dal.EmployeeDao;
import swt6.issuetracker.domain.Employee;
import swt6.issuetracker.domain.LogBookEntry;

import java.util.Optional;

public class RemoveLogBookEntryCommand extends DataCommand {
	@Override
	protected void processDataCommand(DalTransaction transaction, DaoFactory daoFactory) {
		EmployeeDao employeeDao = daoFactory.createEmployeeDao();
		Optional<Employee> employeeContainer = employeeDao.findById(transaction, this.promptForLong("employee id"));
		if (employeeContainer.isPresent()) {
			long logBookEntryId = this.promptForLong("logbook entry id");
			Optional<LogBookEntry> logBookEntryContainer = employeeContainer.get().getLogbookEntries()
					.stream()
					.filter(entry -> entry.getId() == logBookEntryId)
					.findFirst();
			if (logBookEntryContainer.isPresent()) {
				employeeDao.removeLogBookEntryFromEmployee(
						transaction,
						logBookEntryContainer.get(),
						employeeContainer.get()
				);
				System.out.println("Logbook entry deleted.");
			} else {
				System.out.println("Logbook entry does not exist.");
			}
		} else {
			System.out.println("Employee does not exist.");
		}
	}
}

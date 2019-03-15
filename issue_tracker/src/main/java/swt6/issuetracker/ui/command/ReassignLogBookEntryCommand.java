package swt6.issuetracker.ui.command;

import swt6.issuetracker.dal.DalTransaction;
import swt6.issuetracker.dal.DaoFactory;
import swt6.issuetracker.dal.EmployeeDao;
import swt6.issuetracker.domain.Employee;
import swt6.issuetracker.domain.LogBookEntry;

import java.util.Optional;

public class ReassignLogBookEntryCommand extends DataCommand {
	@Override
	protected void processDataCommand(DalTransaction transaction, DaoFactory daoFactory) {
		EmployeeDao employeeDao = daoFactory.createEmployeeDao();
		Optional<Employee> previouslyAssignedEmployee = employeeDao.findById(transaction, this.promptForLong("previously assigned employee ID"));
		if (previouslyAssignedEmployee.isPresent()) {
			Optional<Employee> newlyAssignedEmployee = employeeDao.findById(transaction, this.promptForLong("newly assigned employee ID"));
			if (newlyAssignedEmployee.isPresent()) {
				long logBookEntryId = this.promptForLong("logbook entry id");
				Optional<LogBookEntry> logBookEntry = previouslyAssignedEmployee.get().getLogbookEntries()
						.stream()
						.filter(entry -> entry.getId() == logBookEntryId)
						.findFirst();
				if (logBookEntry.isPresent()) {
					employeeDao.reassignLogBookEntry(
							transaction,
							logBookEntry.get(),
							newlyAssignedEmployee.get()
					);
					System.out.println("Logbook entry reassigned.");
				} else {
					System.out.println("Logbook entry does not exist.");
				}
			} else {
				System.out.println("Employee does not exist.");
			}
		} else {
			System.out.println("Employee does not exist.");
		}
	}
}

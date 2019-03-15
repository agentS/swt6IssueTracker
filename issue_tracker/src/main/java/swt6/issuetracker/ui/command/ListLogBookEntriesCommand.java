package swt6.issuetracker.ui.command;

import swt6.issuetracker.dal.DalTransaction;
import swt6.issuetracker.dal.DaoFactory;
import swt6.issuetracker.dal.EmployeeDao;
import swt6.issuetracker.domain.Employee;
import swt6.issuetracker.domain.LogBookEntry;

import java.util.Optional;

public class ListLogBookEntriesCommand extends DataCommand {
	@Override
	protected void processDataCommand(DalTransaction transaction, DaoFactory daoFactory) {
		EmployeeDao employeeDao = daoFactory.createEmployeeDao();
		Optional<Employee> employeeContainer = employeeDao.findById(transaction, this.promptForLong("employee id"));
		if (employeeContainer.isPresent()) {
			for (LogBookEntry logBookEntry : employeeContainer.get().getLogbookEntries()) {
				System.out.println(logBookEntry);
			}
		} else {
			System.out.println("Employee does not exist.");
		}
	}
}

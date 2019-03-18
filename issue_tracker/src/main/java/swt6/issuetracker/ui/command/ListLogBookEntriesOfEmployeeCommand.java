package swt6.issuetracker.ui.command;

import swt6.issuetracker.dal.DalTransaction;
import swt6.issuetracker.dal.DaoFactory;
import swt6.issuetracker.dal.EmployeeDao;
import swt6.issuetracker.dal.LogBookEntryDao;
import swt6.issuetracker.domain.Employee;
import swt6.issuetracker.domain.LogBookEntry;

import java.util.Optional;

public class ListLogBookEntriesOfEmployeeCommand extends DataCommand {
	@Override
	protected TransactionStrategy processDataCommand(DalTransaction transaction, DaoFactory daoFactory) {
		long employeeId = this.promptForLong("employee id");
		EmployeeDao employeeDao = daoFactory.createEmployeeDao();
		Optional<Employee> employee = employeeDao.findById(transaction, employeeId);
		if (employee.isPresent()) {
			LogBookEntryDao logBookEntryDao = daoFactory.getLogBookEntryDao();
			for (LogBookEntry entry : logBookEntryDao.findAllByEmployee(transaction, employee.get())) {
				System.out.println(entry);
			}
		} else {
			System.out.println("Employee does not exist.");
		}
		return TransactionStrategy.NO_COMMIT;
	}
}

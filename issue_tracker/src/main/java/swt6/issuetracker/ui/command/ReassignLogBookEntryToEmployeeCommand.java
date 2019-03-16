package swt6.issuetracker.ui.command;

import swt6.issuetracker.dal.DalTransaction;
import swt6.issuetracker.dal.DaoFactory;
import swt6.issuetracker.dal.EmployeeDao;
import swt6.issuetracker.dal.LogBookEntryDao;
import swt6.issuetracker.domain.Employee;
import swt6.issuetracker.domain.LogBookEntry;

import java.util.Optional;

public class ReassignLogBookEntryToEmployeeCommand extends DataCommand {
	@Override
	protected TransactionStrategy processDataCommand(DalTransaction transaction, DaoFactory daoFactory) {

		LogBookEntryDao logBookEntryDao = daoFactory.getLogBookEntryDao();
		Optional<LogBookEntry> entry = logBookEntryDao.findById(
				transaction,
				this.promptForLong("logbook entry id")
		);
		if (entry.isPresent()) {
			EmployeeDao employeeDao = daoFactory.createEmployeeDao();
			Optional<Employee> employee = employeeDao.findById(
					transaction,
					this.promptForLong("employee ID")
			);
			if (employee.isPresent()) {
				logBookEntryDao.reassignToEmployee(transaction, entry.get(), employee.get());
				System.out.printf("Logbook entry %s assigned to employee %s, %s.%n", entry.get().getActivity(), employee.get().getLastName(), employee.get().getFirstName());
				return TransactionStrategy.COMMIT;
			} else {
				System.out.println("Employee does not exist.");
			}
		} else {
			System.out.println("Logbook entry does not exist.");
		}
		return TransactionStrategy.NO_COMMIT;
	}
}

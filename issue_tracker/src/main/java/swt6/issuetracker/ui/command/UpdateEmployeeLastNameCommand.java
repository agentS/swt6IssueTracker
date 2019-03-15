package swt6.issuetracker.ui.command;

import swt6.issuetracker.dal.DalTransaction;
import swt6.issuetracker.dal.DaoFactory;
import swt6.issuetracker.dal.EmployeeDao;
import swt6.issuetracker.domain.Employee;
import swt6.issuetracker.ui.CommandLineReader;

import java.util.Optional;

public class UpdateEmployeeLastNameCommand extends DataCommand {
	@Override
	protected void processDataCommand(DalTransaction transaction, DaoFactory daoFactory) {
		EmployeeDao employeeDao = daoFactory.createEmployeeDao();
		Optional<Employee> employeeContainer = employeeDao.findById(transaction, this.promptForId());
		if (employeeContainer.isPresent()) {
			employeeDao.updateLastName(
					transaction,
					employeeContainer.get(),
					CommandLineReader.getInstance().promptFor("last name")
			);
			System.out.println("Last name updated.");
		} else {
			System.out.println("Employee does not exist.");
		}
	}
}

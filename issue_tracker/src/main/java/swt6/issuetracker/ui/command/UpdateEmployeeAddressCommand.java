package swt6.issuetracker.ui.command;

import swt6.issuetracker.dal.DalTransaction;
import swt6.issuetracker.dal.DaoFactory;
import swt6.issuetracker.dal.EmployeeDao;
import swt6.issuetracker.domain.Address;
import swt6.issuetracker.domain.Employee;
import swt6.issuetracker.ui.CommandLineReader;

import java.util.Optional;

public class UpdateEmployeeAddressCommand extends DataCommand {
	@Override
	protected TransactionStrategy processDataCommand(DalTransaction transaction, DaoFactory daoFactory) {
		EmployeeDao employeeDao = daoFactory.createEmployeeDao();
		Optional<Employee> employeeContainer = employeeDao.findById(transaction, this.promptForId());
		if (employeeContainer.isPresent()) {
			employeeDao.updateAddress(
					transaction,
					employeeContainer.get(),
					this.promptForAddress()
			);
			System.out.println("Address updated.");
			return TransactionStrategy.COMMIT;
		} else {
			System.out.println("Employee does not exist.");
		}
		return TransactionStrategy.NO_COMMIT;
	}

	private Address promptForAddress() {
		String street = CommandLineReader.getInstance().promptFor("street");
		String postalCode = CommandLineReader.getInstance().promptFor("postal code");
		String town = CommandLineReader.getInstance().promptFor("town");
		return new Address(postalCode, town, street);
	}
}

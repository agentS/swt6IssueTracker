package swt6.issuetracker.ui.command;

import swt6.issuetracker.dal.DalTransaction;
import swt6.issuetracker.dal.DaoFactory;
import swt6.issuetracker.domain.Address;
import swt6.issuetracker.domain.Employee;
import swt6.issuetracker.ui.CommandLineReader;
import swt6.issuetracker.ui.settings.UIConstants;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class AddEmployeeCommand extends DataCommand {
	@Override
	protected void processDataCommand(DalTransaction transaction, DaoFactory daoFactory) {
		String firstName = CommandLineReader.getInstance().promptFor("first name");
		String lastName = CommandLineReader.getInstance().promptFor("last name");
		LocalDate dateOfBirth = null;
		do {
			try {
				dateOfBirth = LocalDate.parse(
						CommandLineReader.getInstance().promptFor("date of birth"),
						UIConstants.DATE_FORMATTER
				);
			} catch (DateTimeParseException exception) {
				System.err.println("Invalid date of birth. Please use the format DD.MM.YYYY");
				dateOfBirth = null;
			}
		} while (dateOfBirth == null);
		String street = CommandLineReader.getInstance().promptFor("street");
		String postalCode = CommandLineReader.getInstance().promptFor("postal code");
		String town = CommandLineReader.getInstance().promptFor("town");

		daoFactory.createEmployeeDao().add(
				transaction,
				new Employee(
						firstName, lastName, dateOfBirth,
						new Address(postalCode, town, street)
				)
		);
	}
}

package swt6.issuetracker;

import swt6.issuetracker.dal.DalTransaction;
import swt6.issuetracker.dal.DaoFactory;
import swt6.issuetracker.dal.jpa.DaoFactoryJpa;
import swt6.issuetracker.ui.CommandLineReader;
import swt6.issuetracker.ui.command.UserCommand;
import swt6.issuetracker.ui.command.UserCommandSelector;
import swt6.util.JpaUtil;

public class IssueTracker {
	private static final String AVAILABLE_COMMANDS = "Commands: (q)uit, " +
			"commit, rollback, listEmployees, findEmployeeById, addEmployee, deleteEmployee, updateEmployeeLastName, updateEmployeeAddress, " +
			"listLogBookEntries, addLogBookEntry, removeLogBookEntry, reassignLogBookEntry, " +
			"listProjects, findProjectById, addProject, updateProjectName, deleteProject, " +
			"assignEmployeeToProject, dismissEmployeeFromProject";

	public static void main(String[] args) {
		DaoFactory daoFactory = new DaoFactoryJpa(JpaUtil.getEntityManagerFactory());
		DalTransaction transaction = daoFactory.createTransaction();
		transaction.begin();

		System.out.println(AVAILABLE_COMMANDS);
		String enteredCommand = CommandLineReader.getInstance().promptFor("");
		try {
			while (!isQuitCommand(enteredCommand)) {
				UserCommand userCommand = null;
				try {
					userCommand = UserCommandSelector.createUserCommand(enteredCommand);
				} catch (IllegalArgumentException unknownCommandException) {
					System.out.println("Unknown command!");
				}
				if (userCommand != null) {
					transaction = userCommand.process(transaction, daoFactory);
				}

				System.out.println(AVAILABLE_COMMANDS);
				enteredCommand = CommandLineReader.getInstance().promptFor("");
			}

			if ((transaction != null) && (transaction.isOpen())) {
				transaction.commit();
				System.out.println("Commited unsaved changes.");
			}
		} catch (Throwable exception) {
			exception.printStackTrace();
			if (transaction != null) {
				transaction.rollback();
				System.out.println("Rolled back unsaved changes.");
			}
		} finally {
			JpaUtil.closeEntityManagerFactory();
		}
	}

	private static boolean isQuitCommand(String command) {
		return ((command.equals("q")) || (command.equals("quit")));
	}
}

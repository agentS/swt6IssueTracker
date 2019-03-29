package swt6.issuetracker;

import swt6.issuetracker.dal.DalTransaction;
import swt6.issuetracker.dal.DaoFactory;
import swt6.issuetracker.dal.jpa.DaoFactoryJpa;
import swt6.issuetracker.ui.CommandLineReader;
import swt6.issuetracker.ui.command.UserCommand;
import swt6.issuetracker.ui.command.UserCommandSelector;
import swt6.util.JpaUtil;

public class IssueTracker {
	private static final String APPLICATION_TITLE =
			"  ___________      _____________________ .___                              ___________                     __                 \n" +
					" /   _____/  \\    /  \\__    ___/  _____/ |   | ______ ________ __   ____   \\__    ___/___________    ____ |  | __ ___________ \n" +
					" \\_____  \\\\   \\/\\/   / |    | /   __  \\  |   |/  ___//  ___/  |  \\_/ __ \\    |    |  \\_  __ \\__  \\ _/ ___\\|  |/ // __ \\_  __ \\\n" +
					" /        \\\\        /  |    | \\  |__\\  \\ |   |\\___ \\ \\___ \\|  |  /\\  ___/    |    |   |  | \\// __ \\\\  \\___|    <\\  ___/|  | \\/\n" +
					"/_______  / \\__/\\  /   |____|  \\_____  / |___/____  >____  >____/  \\___  >   |____|   |__|  (____  /\\___  >__|_ \\\\___  >__|   \n" +
					"        \\/       \\/                  \\/           \\/     \\/            \\/                        \\/     \\/     \\/    \\/       ";

	private static final String AVAILABLE_COMMANDS = "Commands: (q)uit, " +
			"commit, rollback, listEmployees, findEmployeeById, addEmployee, deleteEmployee, updateEmployeeLastName, updateEmployeeAddress, " +
			"listProjects, findProjectById, addProject, updateProjectName, deleteProject, " +
			"assignEmployeeToProject, dismissEmployeeFromProject, " +
			"findIssueById, listIssuesOfProject, listIssuesOfEmployee, addIssue, assignIssueToEmployee, deleteIssue, " +
			"updateIssueProgress, updateIssueEstimatedTime, updateIssueState, updateIssuePriority" +
			"findLogBookEntryById, listLogBookEntriesOfIssue, listLogBookEntriesOfEmployee, " +
			"addLogBookEntry, removeLogBookEntry, reassignLogBookEntryToIssue, reassignLogBookEntryToEmployee, " +
			"calculateWorkingTimeAndEstimatedTimeByEmployee, calculateWorkingTimeAndEstimatedTimeByProject, calculateWorkingTimeByEmployee, calculateWorkingTimePerProjectPhase";

	public static void main(String[] args) {
		DaoFactory daoFactory = new DaoFactoryJpa(JpaUtil.getEntityManagerFactory());
		DalTransaction transaction = daoFactory.createTransaction();
		transaction.begin();

		System.out.println(APPLICATION_TITLE);
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

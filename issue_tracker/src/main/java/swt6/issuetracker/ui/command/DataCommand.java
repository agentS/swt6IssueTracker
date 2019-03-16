package swt6.issuetracker.ui.command;

import swt6.issuetracker.dal.DalTransaction;
import swt6.issuetracker.dal.DaoFactory;
import swt6.issuetracker.domain.Issue;
import swt6.issuetracker.domain.ProjectPhase;
import swt6.issuetracker.ui.CommandLineReader;
import swt6.issuetracker.ui.settings.UIConstants;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Arrays;

public abstract class DataCommand implements UserCommand {
	protected enum TransactionStrategy {
		COMMIT, ROLLBACK, NO_COMMIT
	}

	@Override
	public DalTransaction process(DalTransaction transaction, DaoFactory daoFactory) throws Throwable {
		if (transaction.isOpen()) {
			TransactionStrategy transactionStrategy = this.processDataCommand(transaction, daoFactory);
			switch (transactionStrategy) {
				case COMMIT:
					return new CommitCommand().process(transaction, daoFactory);
				case ROLLBACK:
					return new RollbackCommand().process(transaction, daoFactory);
				default:
					break;
			}
		} else {
			throw new IllegalStateException("Transaction must be active in order to execute a data command.");
		}

		return transaction;
	}

	protected abstract TransactionStrategy processDataCommand(DalTransaction transaction, DaoFactory daoFactory);

	protected long promptForId() {
		return this.promptForLong("id");
	}

	protected long promptForLong(String prompt) {
		long value = (-1);
		do {
			try {
				value = Long.parseLong(CommandLineReader.getInstance().promptFor(prompt));
			} catch (NumberFormatException exception) {
				System.err.println("Invalid number! Please enter a valid integer number.");
			}
		} while (value <= 0);
		return value;
	}

	protected  double promptForDouble(String prompt) {
		double value = (-1);
		do {
			try {
				value = Double.parseDouble(CommandLineReader.getInstance().promptFor(prompt));
				if (value < 0) {
					System.err.println("Please enter a number greater or equal to 0.");
				}
			} catch (NumberFormatException exception) {
				System.err.println("Invalid number! Please enter a number greater or equal to 0.");
			}
		} while (value < 0);
		return value;
	}

	protected LocalDateTime promptForDateTime(String prompt) {
		LocalDateTime dateTime = null;
		do {
			try {
				dateTime = LocalDateTime.parse(
						CommandLineReader.getInstance().promptFor(prompt),
						UIConstants.DATE_TIME_FORMATTER
				);
			} catch (DateTimeParseException exception) {
				System.err.println("Invalid date format! Please use the format DD.MM.YYYY hh:mm");
			}
		} while (dateTime == null);
		return dateTime;
	}

	protected ProjectPhase promptForProjectPhase() {
		ProjectPhase projectPhase = null;
		do {
			try {
				String input = CommandLineReader.getInstance().promptFor("project phase " + Arrays.toString(ProjectPhase.values()))
						.toUpperCase();
				projectPhase = ProjectPhase.valueOf(input);
			} catch (IllegalArgumentException exception) {
				System.err.println("Invalid project phase. Available project phases are " + Arrays.toString(ProjectPhase.values()));
			}
		} while (projectPhase == null);
		return projectPhase;
	}

	protected boolean promptForYesOrNo(String prompt) {
		boolean invalidInput = false;
		String input = CommandLineReader.getInstance().promptFor(prompt + " (yes/no)");
		do {
			switch (input.toLowerCase()) {
				case "y":
					return true;
				case "yes":
					return true;
				case "n":
					return false;
				case "no":
					return false;
				default:
					invalidInput = true;
					System.out.println("Invalid input! Please enter yes or no.");
					input = CommandLineReader.getInstance().promptFor(prompt + " (yes/no)");
					break;
			}
		} while (invalidInput);
		return false;
	}

	protected Issue.IssueState promptForIssueState() {
		Issue.IssueState issueState = null;
		do {
			try {
				String input = CommandLineReader.getInstance().promptFor("issue state " + Arrays.toString(Issue.IssueState.values()))
						.toUpperCase();
				issueState = Issue.IssueState.valueOf(input);
			} catch (IllegalArgumentException exception) {
				System.err.println("Invalid issue state. Available issue states are " + Arrays.toString(Issue.IssueState.values()));
			}
		} while (issueState == null);
		return issueState;
	}

	protected Issue.IssuePriority promptForIssuePriority() {
		Issue.IssuePriority issuePriority = null;
		do {
			try {
				String input = CommandLineReader.getInstance().promptFor("issue priority " + Arrays.toString(Issue.IssuePriority.values()))
						.toUpperCase();
				issuePriority = Issue.IssuePriority.valueOf(input);
			} catch (IllegalArgumentException exception) {
				System.err.println("Invalid issue priority. Available issue priorities are " + Arrays.toString(Issue.IssuePriority.values()));
			}
		} while (issuePriority == null);
		return issuePriority;
	}

	protected int promptForPerCent(String prompt) {
		int perCent = (-1);
		do {
			try {
				perCent = Integer.parseInt(CommandLineReader.getInstance().promptFor(prompt + "[0-100]"));
				if ((perCent < 0) || (perCent > 100)) {
					perCent = (-1);
					System.err.println("Please enter a number greater or equal to 0 and less or equal to 100.");
				}
			} catch (NumberFormatException exception) {
				System.err.println("Invalid input! Please enter an integer number between 0 and 100.");
			}
		} while (perCent == (-1));
		return perCent;
	}
}

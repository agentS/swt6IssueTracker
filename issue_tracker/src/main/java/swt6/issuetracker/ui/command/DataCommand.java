package swt6.issuetracker.ui.command;

import swt6.issuetracker.dal.DalTransaction;
import swt6.issuetracker.dal.DaoFactory;
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
}

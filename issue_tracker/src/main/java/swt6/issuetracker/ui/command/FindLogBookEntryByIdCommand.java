package swt6.issuetracker.ui.command;

import swt6.issuetracker.dal.DalTransaction;
import swt6.issuetracker.dal.DaoFactory;
import swt6.issuetracker.dal.LogBookEntryDao;
import swt6.issuetracker.domain.LogBookEntry;

import java.util.Optional;

public class FindLogBookEntryByIdCommand extends DataCommand {
	@Override
	protected TransactionStrategy processDataCommand(DalTransaction transaction, DaoFactory daoFactory) {
		LogBookEntryDao logBookEntryDao = daoFactory.getLogBookEntryDao();
		Optional<LogBookEntry> logBookEntry = logBookEntryDao.findById(transaction, this.promptForId());
		if (logBookEntry.isPresent()) {
			System.out.println(logBookEntry.get());
		} else {
			System.out.println("Logbook entry does not exist.");
		}
		return TransactionStrategy.NO_COMMIT;
	}
}

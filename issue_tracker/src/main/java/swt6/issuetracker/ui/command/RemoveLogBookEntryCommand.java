package swt6.issuetracker.ui.command;

import swt6.issuetracker.dal.DalTransaction;
import swt6.issuetracker.dal.DaoFactory;
import swt6.issuetracker.dal.LogBookEntryDao;
import swt6.issuetracker.domain.LogBookEntry;

import java.util.Optional;

public class RemoveLogBookEntryCommand extends DataCommand {
	@Override
	protected TransactionStrategy processDataCommand(DalTransaction transaction, DaoFactory daoFactory) {
		LogBookEntryDao logBookEntryDao = daoFactory.getLogBookEntryDao();
		Optional<LogBookEntry> entry = logBookEntryDao.findById(transaction, this.promptForId());
		if (entry.isPresent()) {
			logBookEntryDao.remove(transaction, entry.get());
			System.out.println("Logbook entry removed.");
			return TransactionStrategy.COMMIT;
		} else {
			System.out.println("Logbook entry does not exist.");
		}
		return TransactionStrategy.NO_COMMIT;
	}
}

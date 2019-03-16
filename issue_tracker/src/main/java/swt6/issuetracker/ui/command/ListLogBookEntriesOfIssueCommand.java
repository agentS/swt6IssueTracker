package swt6.issuetracker.ui.command;

import swt6.issuetracker.dal.DalTransaction;
import swt6.issuetracker.dal.DaoFactory;
import swt6.issuetracker.dal.LogBookEntryDao;
import swt6.issuetracker.domain.LogBookEntry;

public class ListLogBookEntriesOfIssueCommand extends DataCommand {
	@Override
	protected TransactionStrategy processDataCommand(DalTransaction transaction, DaoFactory daoFactory) {
		long issueId = this.promptForLong("issue id");
		LogBookEntryDao logBookEntryDao = daoFactory.getLogBookEntryDao();
		for (LogBookEntry entry : logBookEntryDao.findAllByIssueId(transaction, issueId)) {
			System.out.println(entry);
		}
		return TransactionStrategy.NO_COMMIT;
	}
}

package swt6.issuetracker.ui.command;

import swt6.issuetracker.dal.DalTransaction;
import swt6.issuetracker.dal.DaoFactory;
import swt6.issuetracker.dal.LogBookEntryDao;
import swt6.issuetracker.domain.LogBookEntry;

public class ListLogBookEntriesOfEmployeeCommand extends DataCommand {
	@Override
	protected TransactionStrategy processDataCommand(DalTransaction transaction, DaoFactory daoFactory) {
		long employeeId = this.promptForLong("employee id");
		LogBookEntryDao logBookEntryDao = daoFactory.getLogBookEntryDao();
		for (LogBookEntry entry : logBookEntryDao.findAllByEmployeeId(transaction, employeeId)) {
			System.out.println(entry);
		}
		return TransactionStrategy.NO_COMMIT;
	}
}

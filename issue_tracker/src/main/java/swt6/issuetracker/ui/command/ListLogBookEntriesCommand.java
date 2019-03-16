package swt6.issuetracker.ui.command;

import swt6.issuetracker.dal.DalTransaction;
import swt6.issuetracker.dal.DaoFactory;
import swt6.issuetracker.dal.EmployeeDao;
import swt6.issuetracker.domain.Employee;
import swt6.issuetracker.domain.LogBookEntry;

import java.util.Optional;

public class ListLogBookEntriesCommand extends DataCommand {
	@Override
	protected TransactionStrategy processDataCommand(DalTransaction transaction, DaoFactory daoFactory) {

		return TransactionStrategy.NO_COMMIT;
	}
}

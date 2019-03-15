package swt6.issuetracker.ui.command;

import swt6.issuetracker.dal.DalTransaction;
import swt6.issuetracker.dal.DaoFactory;

public class RollbackCommand extends TransactionCommand {
	@Override
	protected void finishTransaction(DalTransaction transaction) {
		transaction.rollback();
		System.out.println("Rolled transaction back.");
	}
}

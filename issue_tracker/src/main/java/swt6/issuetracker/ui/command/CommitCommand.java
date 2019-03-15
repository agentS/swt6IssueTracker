package swt6.issuetracker.ui.command;

import swt6.issuetracker.dal.DalTransaction;
import swt6.issuetracker.dal.DaoFactory;

public class CommitCommand extends TransactionCommand {
	@Override
	protected void finishTransaction(DalTransaction transaction) {
		transaction.commit();
		System.out.println("Commited transaction.");
	}
}

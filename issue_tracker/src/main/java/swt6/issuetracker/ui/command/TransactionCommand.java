package swt6.issuetracker.ui.command;

import swt6.issuetracker.dal.DalTransaction;
import swt6.issuetracker.dal.DaoFactory;

public abstract class TransactionCommand implements UserCommand {
	@Override
	public DalTransaction process(DalTransaction oldTransaction, DaoFactory daoFactory) throws Throwable {
		if (oldTransaction != null) {
			this.finishTransaction(oldTransaction);
		}

		DalTransaction transaction = daoFactory.createTransaction();
		transaction.begin();
		System.out.println("New transaction started.");
		return transaction;
	}

	protected abstract void finishTransaction(DalTransaction transaction);
}

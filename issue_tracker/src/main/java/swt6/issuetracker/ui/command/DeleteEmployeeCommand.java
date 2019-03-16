package swt6.issuetracker.ui.command;

import swt6.issuetracker.dal.DalTransaction;
import swt6.issuetracker.dal.DaoFactory;
import swt6.issuetracker.dal.EmployeeDao;

public class DeleteEmployeeCommand extends DataCommand {
	@Override
	protected TransactionStrategy processDataCommand(DalTransaction transaction, DaoFactory daoFactory) {
		EmployeeDao employeeDao = daoFactory.createEmployeeDao();
		boolean successful = employeeDao.delete(transaction, this.promptForId());
		if (successful) {
			System.out.println("Employee deleted.");
			return TransactionStrategy.COMMIT;
		} else {
			System.out.println("Employee does not exit.");
		}
		return TransactionStrategy.NO_COMMIT;
	}
}

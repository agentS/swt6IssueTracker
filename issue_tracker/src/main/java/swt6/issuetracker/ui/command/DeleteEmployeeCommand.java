package swt6.issuetracker.ui.command;

import swt6.issuetracker.dal.DalTransaction;
import swt6.issuetracker.dal.DaoFactory;
import swt6.issuetracker.dal.EmployeeDao;

public class DeleteEmployeeCommand extends DataCommand {
	@Override
	protected void processDataCommand(DalTransaction transaction, DaoFactory daoFactory) {
		EmployeeDao employeeDao = daoFactory.createEmployeeDao();
		boolean successful = employeeDao.delete(transaction, this.promptForId());
		if (successful) {
			System.out.println("Employee deleted.");
		} else {
			System.out.println("Employee does not exit.");
		}
	}
}

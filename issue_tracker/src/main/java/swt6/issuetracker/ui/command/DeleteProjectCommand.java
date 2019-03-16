package swt6.issuetracker.ui.command;

import swt6.issuetracker.dal.DalTransaction;
import swt6.issuetracker.dal.DaoFactory;
import swt6.issuetracker.dal.ProjectDao;
import swt6.issuetracker.domain.Project;

import java.util.Optional;

public class DeleteProjectCommand extends DataCommand {
	@Override
	protected TransactionStrategy processDataCommand(DalTransaction transaction, DaoFactory daoFactory) {
		ProjectDao projectDao = daoFactory.createProjectDao();
		boolean successful = projectDao.delete(transaction, this.promptForId());
		if (successful) {
			System.out.println("Project deleted.");
			return TransactionStrategy.COMMIT;
		} else {
			System.out.println("Project does not exist.");
		}
		return TransactionStrategy.COMMIT;
	}
}

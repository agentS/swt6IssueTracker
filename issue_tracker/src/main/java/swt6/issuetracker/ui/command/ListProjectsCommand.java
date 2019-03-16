package swt6.issuetracker.ui.command;

import swt6.issuetracker.dal.DalTransaction;
import swt6.issuetracker.dal.DaoFactory;
import swt6.issuetracker.dal.ProjectDao;
import swt6.issuetracker.domain.Project;

public class ListProjectsCommand extends DataCommand {
	@Override
	protected TransactionStrategy processDataCommand(DalTransaction transaction, DaoFactory daoFactory) {
		ProjectDao projectDao = daoFactory.createProjectDao();
		for (Project project : projectDao.findAll(transaction)) {
			System.out.println(project);
		}
		return TransactionStrategy.NO_COMMIT;
	}
}

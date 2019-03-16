package swt6.issuetracker.ui.command;

import swt6.issuetracker.dal.DalTransaction;
import swt6.issuetracker.dal.DaoFactory;
import swt6.issuetracker.dal.ProjectDao;
import swt6.issuetracker.domain.Project;
import swt6.issuetracker.ui.CommandLineReader;

public class AddProjectCommand extends DataCommand {
	@Override
	protected TransactionStrategy processDataCommand(DalTransaction transaction, DaoFactory daoFactory) {
		ProjectDao projectDao = daoFactory.createProjectDao();
		projectDao.add(transaction, this.promptForProject());
		return TransactionStrategy.COMMIT;
	}

	private Project promptForProject() {
		String name = CommandLineReader.getInstance().promptFor("name");
		return new Project(name);
	}
}

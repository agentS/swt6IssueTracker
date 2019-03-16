package swt6.issuetracker.ui.command;

import swt6.issuetracker.dal.DalTransaction;
import swt6.issuetracker.dal.DaoFactory;
import swt6.issuetracker.dal.ProjectDao;
import swt6.issuetracker.domain.Project;
import swt6.issuetracker.ui.CommandLineReader;

import java.util.Optional;

public class UpdateProjectNameCommand extends DataCommand {
	@Override
	protected TransactionStrategy processDataCommand(DalTransaction transaction, DaoFactory daoFactory) {
		ProjectDao projectDao = daoFactory.createProjectDao();
		Optional<Project> projectContainer = projectDao.findById(transaction, this.promptForId());
		if (projectContainer.isPresent()) {
			projectDao.updateName(transaction, projectContainer.get(), this.promptForName());
			System.out.println("Project name updated.");
			return TransactionStrategy.COMMIT;
		} else {
			System.out.println("Project does not exist.");
		}
		return TransactionStrategy.NO_COMMIT;
	}

	private String promptForName() {
		return CommandLineReader.getInstance().promptFor("name");
	}
}

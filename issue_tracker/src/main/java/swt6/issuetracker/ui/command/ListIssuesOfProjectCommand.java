package swt6.issuetracker.ui.command;

import swt6.issuetracker.dal.DalTransaction;
import swt6.issuetracker.dal.DaoFactory;
import swt6.issuetracker.dal.IssueDao;
import swt6.issuetracker.dal.ProjectDao;
import swt6.issuetracker.domain.Issue;
import swt6.issuetracker.domain.Project;

import java.util.Optional;

public class ListIssuesOfProjectCommand extends DataCommand {
	@Override
	protected TransactionStrategy processDataCommand(DalTransaction transaction, DaoFactory daoFactory) {
		ProjectDao projectDao = daoFactory.createProjectDao();
		Optional<Project> project = projectDao.findById(transaction, this.promptForLong("project id"));
		if (project.isPresent()) {
			System.out.println("Project " + project.get().getName());

			IssueDao issueDao = daoFactory.createIssueDao();
			for (Issue issue : issueDao.findAllByProjectId(transaction, project.get().getId())) {
				System.out.println(issue);
			}
		} else {
			System.out.println("Project does not exist.");
		}
		return TransactionStrategy.NO_COMMIT;
	}
}

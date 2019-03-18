package swt6.issuetracker.ui.command;

import swt6.issuetracker.dal.DalTransaction;
import swt6.issuetracker.dal.DaoFactory;
import swt6.issuetracker.dal.IssueDao;
import swt6.issuetracker.dal.ProjectDao;
import swt6.issuetracker.domain.Issue;
import swt6.issuetracker.domain.Project;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ListIssuesOfProjectCommand extends DataCommand {
	@Override
	protected TransactionStrategy processDataCommand(DalTransaction transaction, DaoFactory daoFactory) {
		ProjectDao projectDao = daoFactory.createProjectDao();
		Optional<Project> project = projectDao.findById(transaction, this.promptForLong("project id"));
		if (project.isPresent()) {
			System.out.println("Project " + project.get().getName());

			IssueDao issueDao = daoFactory.createIssueDao();
			Map<Issue.IssueState, List<Issue>> results = issueDao.findAllByProjectGroupByIssueState(transaction, project.get());
			for (Issue.IssueState issueState : results.keySet()) {
				System.out.println(issueState);
				for (Issue issue : results.get(issueState)) {
					System.out.println("    " + issue);
				}
			}
		} else {
			System.out.println("Project does not exist.");
		}
		return TransactionStrategy.NO_COMMIT;
	}
}

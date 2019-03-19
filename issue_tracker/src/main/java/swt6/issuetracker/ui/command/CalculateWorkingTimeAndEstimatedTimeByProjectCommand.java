package swt6.issuetracker.ui.command;

import swt6.issuetracker.dal.DalTransaction;
import swt6.issuetracker.dal.DaoFactory;
import swt6.issuetracker.dal.IssueDao;
import swt6.issuetracker.dal.ProjectDao;
import swt6.issuetracker.domain.Issue;
import swt6.issuetracker.domain.Pair;
import swt6.issuetracker.domain.Project;

import java.util.Optional;

public class CalculateWorkingTimeAndEstimatedTimeByProjectCommand extends DataCommand {
	@Override
	protected TransactionStrategy processDataCommand(DalTransaction transaction, DaoFactory daoFactory) {
		ProjectDao projectDao = daoFactory.createProjectDao();
		Optional<Project> project = projectDao.findById(
				transaction,
				this.promptForLong("project id")
		);
		if (project.isPresent()) {
			IssueDao issueDao = daoFactory.createIssueDao();
			var workingTimesAndEstimatedTimes =
					issueDao.calculateWorkingTimeAndEstimatedTimeByProjectGroupByIssueState(
							transaction,
							project.get()
					);
			double totalWorkingTime = 0;
			double totalEstimatedTime = 0;
			for (Issue.IssueState issueState : workingTimesAndEstimatedTimes.keySet()) {
				Pair<Double, Double> entry = workingTimesAndEstimatedTimes.get(issueState);
				totalWorkingTime += entry.getFirst();
				totalEstimatedTime += entry.getSecond();
				System.out.printf(
						"%s: working time: %.2fh estimated time: %.2fh%n",
						issueState, entry.getFirst(), entry.getSecond()
				);
			}
			System.out.printf(
					"TOTAL: working time: %.2fh estimated time: %.2fh%n",
					totalWorkingTime, totalEstimatedTime
			);
		} else {
			System.out.println("Project does not exist.");
		}
		return TransactionStrategy.NO_COMMIT;
	}
}

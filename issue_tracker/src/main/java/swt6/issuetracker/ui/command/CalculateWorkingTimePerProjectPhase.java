package swt6.issuetracker.ui.command;

import swt6.issuetracker.dal.DalTransaction;
import swt6.issuetracker.dal.DaoFactory;
import swt6.issuetracker.dal.IssueDao;
import swt6.issuetracker.dal.ProjectDao;
import swt6.issuetracker.domain.Project;
import swt6.issuetracker.domain.ProjectPhase;

import java.util.Optional;

public class CalculateWorkingTimePerProjectPhase extends DataCommand {
	@Override
	protected TransactionStrategy processDataCommand(DalTransaction transaction, DaoFactory daoFactory) {
		ProjectDao projectDao = daoFactory.createProjectDao();
		Optional<Project> project = projectDao.findById(
				transaction,
				this.promptForLong("project id")
		);
		if (project.isPresent()) {
			IssueDao issueDao = daoFactory.createIssueDao();

			var workingTimesPerProjectPhase = issueDao.calculateWorkingTimePerProjectPhase(
					transaction,
					project.get()
			);
			double totalWorkingTimes = 0;
			for (ProjectPhase projectPhase : workingTimesPerProjectPhase.keySet()) {
				double workingtimes = workingTimesPerProjectPhase.get(projectPhase);
				totalWorkingTimes += workingtimes;
				System.out.printf("%s: %.2fh%n", projectPhase, workingtimes);
			}
			System.out.printf("TOTAL: %.2fh%n", totalWorkingTimes);
		} else {
			System.out.println("Project does not exist.");
		}
		return TransactionStrategy.NO_COMMIT;
	}
}

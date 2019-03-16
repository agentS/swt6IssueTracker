package swt6.issuetracker.ui.command;

import swt6.issuetracker.dal.DalTransaction;
import swt6.issuetracker.dal.DaoFactory;
import swt6.issuetracker.dal.IssueDao;
import swt6.issuetracker.domain.Issue;

import java.util.Optional;

public class UpdateIssueProgressCommand extends DataCommand {
	@Override
	protected TransactionStrategy processDataCommand(DalTransaction transaction, DaoFactory daoFactory) {
		IssueDao issueDao = daoFactory.createIssueDao();
		Optional<Issue> issue = issueDao.findById(transaction, this.promptForId());
		if (issue.isPresent()) {
			Issue updatedIssue = issueDao.updateProgress(
					transaction,
					issue.get(),
					this.promptForPerCent("progress")
			);
			System.out.printf("Updated progress of issue '%s' to %d%%.%n", updatedIssue.getName(), updatedIssue.getProgress());
			return TransactionStrategy.COMMIT;
		} else {
			System.out.println("Issue does not exist.");
		}
		return TransactionStrategy.NO_COMMIT;
	}
}

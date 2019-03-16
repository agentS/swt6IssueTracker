package swt6.issuetracker.ui.command;

import swt6.issuetracker.dal.DalTransaction;
import swt6.issuetracker.dal.DaoFactory;
import swt6.issuetracker.dal.IssueDao;
import swt6.issuetracker.domain.Issue;

import java.util.Optional;

public class UpdateIssueEstimatedTimeCommand extends DataCommand {
	@Override
	protected TransactionStrategy processDataCommand(DalTransaction transaction, DaoFactory daoFactory) {
		IssueDao issueDao = daoFactory.createIssueDao();
		Optional<Issue> issue = issueDao.findById(transaction, this.promptForId());
		if (issue.isPresent()) {
			Issue updatedIssue = issueDao.updateEstimatedTime(
					transaction,
					issue.get(),
					this.promptForDouble("estimated time")
			);
			System.out.printf("Updated estimated time of issue '%s' to %.2f%%.%n", updatedIssue.getName(), updatedIssue.getEstimatedTime());
			return TransactionStrategy.COMMIT;
		} else {
			System.out.println("Issue does not exist.");
		}
		return TransactionStrategy.NO_COMMIT;
	}
}

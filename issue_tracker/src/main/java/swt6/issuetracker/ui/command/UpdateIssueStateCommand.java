package swt6.issuetracker.ui.command;

import swt6.issuetracker.dal.DalTransaction;
import swt6.issuetracker.dal.DaoFactory;
import swt6.issuetracker.dal.IssueDao;
import swt6.issuetracker.domain.Issue;

import java.util.Optional;

public class UpdateIssueStateCommand extends DataCommand {
	@Override
	protected TransactionStrategy processDataCommand(DalTransaction transaction, DaoFactory daoFactory) {
		IssueDao issueDao = daoFactory.createIssueDao();
		Optional<Issue> issue = issueDao.findById(transaction, this.promptForId());
		if (issue.isPresent()) {
			Issue updatedIssue = issueDao.updateState(
					transaction,
					issue.get(),
					this.promptForIssueState()
			);
			System.out.printf("Updated state of issue '%s' to %s.%n", updatedIssue.getName(), updatedIssue.getState());
			return TransactionStrategy.COMMIT;
		} else {
			System.out.println("Issue does not exist.");
		}
		return TransactionStrategy.NO_COMMIT;
	}
}

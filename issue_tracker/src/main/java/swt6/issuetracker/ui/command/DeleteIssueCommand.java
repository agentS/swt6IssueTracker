package swt6.issuetracker.ui.command;

import swt6.issuetracker.dal.DalTransaction;
import swt6.issuetracker.dal.DaoFactory;
import swt6.issuetracker.dal.IssueDao;
import swt6.issuetracker.domain.Issue;

import java.util.Optional;

public class DeleteIssueCommand extends DataCommand {
	@Override
	protected TransactionStrategy processDataCommand(DalTransaction transaction, DaoFactory daoFactory) {
		IssueDao issueDao = daoFactory.createIssueDao();
		Optional<Issue> issue = issueDao.findById(transaction, this.promptForId());
		if (issue.isPresent()) {
			issueDao.delete(transaction, issue.get());
			System.out.println("Issue deleted.");
			return TransactionStrategy.COMMIT;
		} else {
			System.out.println("Issue does not exist.");
		}
		return TransactionStrategy.NO_COMMIT;
	}
}

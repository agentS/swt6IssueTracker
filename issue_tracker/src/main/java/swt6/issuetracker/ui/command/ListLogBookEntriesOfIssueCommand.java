package swt6.issuetracker.ui.command;

import swt6.issuetracker.dal.DalTransaction;
import swt6.issuetracker.dal.DaoFactory;
import swt6.issuetracker.dal.IssueDao;
import swt6.issuetracker.dal.LogBookEntryDao;
import swt6.issuetracker.domain.Issue;
import swt6.issuetracker.domain.LogBookEntry;

import java.util.Optional;

public class ListLogBookEntriesOfIssueCommand extends DataCommand {
	@Override
	protected TransactionStrategy processDataCommand(DalTransaction transaction, DaoFactory daoFactory) {
		long issueId = this.promptForLong("issue id");
		IssueDao issueDao = daoFactory.createIssueDao();
		Optional<Issue> issue = issueDao.findById(transaction, issueId);
		if (issue.isPresent()) {
			LogBookEntryDao logBookEntryDao = daoFactory.getLogBookEntryDao();
			for (LogBookEntry entry : logBookEntryDao.findAllByIssue(transaction, issue.get())) {
				System.out.println(entry);
			}
		} else {
			System.out.println("Issue does not exist.");
		}
		return TransactionStrategy.NO_COMMIT;
	}
}

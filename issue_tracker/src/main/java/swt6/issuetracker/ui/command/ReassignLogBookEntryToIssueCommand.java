package swt6.issuetracker.ui.command;

import swt6.issuetracker.dal.DalTransaction;
import swt6.issuetracker.dal.DaoFactory;
import swt6.issuetracker.dal.IssueDao;
import swt6.issuetracker.dal.LogBookEntryDao;
import swt6.issuetracker.domain.Issue;
import swt6.issuetracker.domain.LogBookEntry;

import java.util.Optional;

public class ReassignLogBookEntryToIssueCommand extends DataCommand {
	@Override
	protected TransactionStrategy processDataCommand(DalTransaction transaction, DaoFactory daoFactory) {
		LogBookEntryDao logBookEntryDao = daoFactory.getLogBookEntryDao();
		Optional<LogBookEntry> entry = logBookEntryDao.findById(
				transaction,
				this.promptForLong("logbook entry id")
		);
		if (entry.isPresent()) {
			IssueDao issueDao = daoFactory.createIssueDao();
			Optional<Issue> issue = issueDao.findById(
					transaction,
					this.promptForLong("issue id")
			);
			if (issue.isPresent()) {
				logBookEntryDao.reassignToIssue(transaction, entry.get(), issue.get());
				System.out.printf("Logbook entry %s assigned to issue %s.%n", entry.get().getActivity(), issue.get().getName());
				return TransactionStrategy.COMMIT;
			} else {
				System.out.println("Issue does not exist.");
			}
		} else {
			System.out.println("Logbook entry does not exist.");
		}
		return TransactionStrategy.NO_COMMIT;
	}
}

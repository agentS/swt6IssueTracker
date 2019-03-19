package swt6.issuetracker.ui.command;

import swt6.issuetracker.dal.DalTransaction;
import swt6.issuetracker.dal.DaoFactory;
import swt6.issuetracker.dal.EmployeeDao;
import swt6.issuetracker.dal.IssueDao;
import swt6.issuetracker.domain.Employee;
import swt6.issuetracker.domain.Issue;
import swt6.issuetracker.domain.Pair;

import java.util.Optional;

public class CalculateWorkingTimeByEmployeeCommand extends DataCommand {
	@Override
	protected TransactionStrategy processDataCommand(DalTransaction transaction, DaoFactory daoFactory) {
		EmployeeDao employeeDao = daoFactory.createEmployeeDao();
		Optional<Employee> employee = employeeDao.findById(
				transaction,
				this.promptForLong("employee id")
		);
		if (employee.isPresent()) {
			IssueDao issueDao = daoFactory.createIssueDao();
			var workingTimes = issueDao.calculateWorkingTimeByEmployeeGroupedByIssueState(
					transaction,
					employee.get()
			);
			long totalIssues = 0;
			double totalWorkingTimes = 0;
			for (Issue.IssueState issueState : workingTimes.keySet()) {
				Pair<Long, Double> entry = workingTimes.get(issueState);
				totalIssues += entry.getFirst();
				totalWorkingTimes += entry.getSecond();
				System.out.printf("%s: %d issues, %.2fh%n", issueState, entry.getFirst(), entry.getSecond());
			}
			System.out.printf("TOTAL: %d issues, %.2fh%n", totalIssues, totalWorkingTimes);
		} else {
			System.out.println("Employee does not exist.");
		}
		return TransactionStrategy.NO_COMMIT;
	}
}

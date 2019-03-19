package swt6.issuetracker.ui.command;

import swt6.issuetracker.dal.DalTransaction;
import swt6.issuetracker.dal.DaoFactory;
import swt6.issuetracker.dal.EmployeeDao;
import swt6.issuetracker.dal.IssueDao;
import swt6.issuetracker.domain.Employee;
import swt6.issuetracker.domain.Issue;
import swt6.issuetracker.domain.Pair;

import java.util.Optional;

public class CalculateWorkingTimeAndEstimatedTimeByEmployeeCommand extends DataCommand {
	@Override
	protected TransactionStrategy processDataCommand(DalTransaction transaction, DaoFactory daoFactory) {
		EmployeeDao employeeDao = daoFactory.createEmployeeDao();
		Optional<Employee> employee = employeeDao.findById(
				transaction,
				this.promptForLong("employee id")
		);
		if (employee.isPresent()) {
			IssueDao issueDao = daoFactory.createIssueDao();
			var workingTimesAndEstimatedTimes =
					issueDao.calculateWorkingTimeAndEstimatedTimeByEmployeeGroupByIssueState(
							transaction,
							employee.get()
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
			System.out.println("Employee does not exist.");
		}
		return TransactionStrategy.NO_COMMIT;
	}
}

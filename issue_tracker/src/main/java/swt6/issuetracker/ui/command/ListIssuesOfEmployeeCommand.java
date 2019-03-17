package swt6.issuetracker.ui.command;

import swt6.issuetracker.dal.DalTransaction;
import swt6.issuetracker.dal.DaoFactory;
import swt6.issuetracker.dal.EmployeeDao;
import swt6.issuetracker.dal.IssueDao;
import swt6.issuetracker.domain.Employee;
import swt6.issuetracker.domain.Issue;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ListIssuesOfEmployeeCommand extends DataCommand {
	@Override
	protected TransactionStrategy processDataCommand(DalTransaction transaction, DaoFactory daoFactory) {
		EmployeeDao employeeDao = daoFactory.createEmployeeDao();
		Optional<Employee> employee = employeeDao.findById(transaction, this.promptForLong("employee id"));
		if (employee.isPresent()) {
			IssueDao issueDao = daoFactory.createIssueDao();
			System.out.printf("Employee %s, %s:%n", employee.get().getLastName(), employee.get().getFirstName());
			Map<Issue.IssueState, List<Issue>> results = issueDao.findAllByEmployeeIdGroupByIssueState(transaction, employee.get().getId());
			for (Issue.IssueState issueState : results.keySet()) {
				System.out.println(issueState);
				for (Issue issue : results.get(issueState)) {
					System.out.println("    " + issue);
				}
			}
		} else {
			System.out.println("Employee does not exist.");
		}
		return TransactionStrategy.NO_COMMIT;
	}
}

package swt6.issuetracker.ui.command;

import swt6.issuetracker.dal.DalTransaction;
import swt6.issuetracker.dal.DaoFactory;
import swt6.issuetracker.dal.EmployeeDao;
import swt6.issuetracker.dal.IssueDao;
import swt6.issuetracker.domain.Employee;
import swt6.issuetracker.domain.Issue;

import java.util.Optional;

public class AssignIssueToEmployeeCommand extends DataCommand {
	@Override
	protected TransactionStrategy processDataCommand(DalTransaction transaction, DaoFactory daoFactory) {
		IssueDao issueDao = daoFactory.createIssueDao();
		Optional<Issue> issue = issueDao.findById(transaction, this.promptForLong("issue id"));
		if (issue.isPresent()) {
			EmployeeDao employeeDao = daoFactory.createEmployeeDao();
			Optional<Employee> employee = employeeDao.findById(transaction, this.promptForLong("employee id"));
			if (employee.isPresent()) {
				Issue updatedIssue = issueDao.updateAssignee(transaction, issue.get(), employee.get());
				System.out.printf(
						"Issue #%d '%s' assigned to employee %s, %s%n",
						updatedIssue.getId(), updatedIssue.getName(),
						employee.get().getLastName(), employee.get().getFirstName()
				);
				return TransactionStrategy.COMMIT;
			} else {
				System.out.println("Employee does not exist.");
			}
		} else {
			System.out.println("Issue does not exist.");
		}
		return TransactionStrategy.NO_COMMIT;
	}
}

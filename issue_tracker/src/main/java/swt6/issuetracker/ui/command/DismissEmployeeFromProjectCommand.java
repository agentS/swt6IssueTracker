package swt6.issuetracker.ui.command;

import swt6.issuetracker.dal.DalTransaction;
import swt6.issuetracker.dal.DaoFactory;
import swt6.issuetracker.dal.EmployeeDao;
import swt6.issuetracker.dal.ProjectDao;
import swt6.issuetracker.domain.Employee;
import swt6.issuetracker.domain.Project;

import java.util.Optional;

public class DismissEmployeeFromProjectCommand extends DataCommand {
	@Override
	protected TransactionStrategy processDataCommand(DalTransaction transaction, DaoFactory daoFactory) {
		ProjectDao projectDao = daoFactory.createProjectDao();
		EmployeeDao employeeDao = daoFactory.createEmployeeDao();

		Optional<Employee> employee = employeeDao.findById(transaction, this.promptForLong("employee id"));
		if (employee.isPresent()) {
			Optional<Project> project = projectDao.findById(transaction, this.promptForLong("project id"));
			if (project.isPresent()) {
				if (project.get().getEmployees().contains(employee.get())) {
					projectDao.dismissEmployeeFromProject(
							transaction,
							employee.get(),
							project.get()
					);
					System.out.printf("Employee %s, %s dismissed from project %s.%n", employee.get().getLastName(), employee.get().getFirstName(), project.get().getName());
					return TransactionStrategy.COMMIT;
				} else {
					System.out.printf("Employee %s, %s is not assigned to the project %s.%n", employee.get().getLastName(), employee.get().getFirstName(), project.get().getName());
				}
			} else {
				System.out.println("Project does not exist.");
			}
		} else {
			System.out.println("Employee does not exist.");
		}
		return TransactionStrategy.NO_COMMIT;
	}
}

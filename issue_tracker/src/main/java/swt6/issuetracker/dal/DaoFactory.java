package swt6.issuetracker.dal;

public interface DaoFactory {
	DalTransaction createTransaction();
	EmployeeDao createEmployeeDao();
	ProjectDao createProjectDao();
	IssueDao createIssueDao();
	LogBookEntryDao getLogBookEntryDao();
}

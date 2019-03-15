package swt6.issuetracker.dal;

import swt6.issuetracker.domain.Employee;
import swt6.issuetracker.domain.Project;

import java.util.List;
import java.util.Optional;

public interface ProjectDao {
	List<Project> findAll(DalTransaction transaction);
	Optional<Project> findById(DalTransaction transaction, long id);
	Project add(DalTransaction transaction, Project project);
	Project updateName(DalTransaction transaction, Project project, String newName);
	void delete(DalTransaction transaction, Project project);
	boolean delete(DalTransaction transaction, long id);

	void assignEmployeeToProject(DalTransaction transaction, Employee employee, Project project);
	void dismissEmployeeFromProject(DalTransaction transaction, Employee employee, Project project);
}

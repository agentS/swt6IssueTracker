package swt6.issuetracker.domain;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
public final class Project {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(nullable = false)
	private String name;

	@ManyToMany(mappedBy = "projects", cascade = { CascadeType.MERGE, CascadeType.PERSIST })
	private Set<Employee> employees = new HashSet<>();

	@OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
	private Set<Issue> issues = new HashSet<>();

	public Project() {
	}

	public Project(String name) {
		this.name = name;
	}

	public long getId() {
		return this.id;
	}

	private void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Set<Employee> getEmployees() {
		return this.employees;
	}

	public void setEmployees(Set<Employee> employees) {
		this.employees = employees;
	}

	public void addEmployee(Employee employee) {
		this.getEmployees().add(employee);
		employee.getProjects().add(this);
	}

	public void removeEmployee(Employee employee) {
		this.getEmployees().remove(employee);
		employee.getProjects().remove(this);
	}

	public Set<Issue> getIssues() {
		return this.issues;
	}

	public void setIssues(Set<Issue> issues) {
		this.issues = issues;
	}

	public void addIssue(Issue issue) {
		if (issue.getProject() != null) {
			issue.getProject().getIssues().remove(issue);
		}

		this.getIssues().add(issue);
		issue.setProject(this);
	}

	public void removeIssue(Issue issue) {
		this.getIssues().remove(issue);
		if (issue.getProject() != null) {
			issue.setProject(null);
		}
	}

	@Override
	public String toString() {
		StringBuilder outputBuilder = new StringBuilder();
		outputBuilder.append("Project: ")
				.append("id=").append(this.id)
				.append(", name='")
				.append('\'');

		if (this.getEmployees().size() > 0) {
			outputBuilder.append('\n').append("    Employees: ");
			for (Employee employee : this.getEmployees()) {
				outputBuilder.append("\n    ")
						.append("id=").append(employee.getId())
						.append(": ")
						.append(employee.getLastName()).append(", ").append(employee.getFirstName());
			}
		}

		return outputBuilder.toString();
	}
}
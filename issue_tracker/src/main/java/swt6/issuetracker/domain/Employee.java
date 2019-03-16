package swt6.issuetracker.domain;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;

@Entity
public final class Employee {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(nullable = false)
	private String firstName;

	@Column(nullable = false)
	private String lastName;

	@Column(nullable = false)
	private LocalDate dateOfBirth;

	@OneToOne(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true)
	private Address address;

	// mappedBy defines the data component of the other side that references to this side
	@OneToMany(mappedBy = "employee", cascade = CascadeType.ALL)
	private Set<LogBookEntry> logbookEntries = new HashSet<>();

	@ManyToMany(cascade = { CascadeType.MERGE, CascadeType.PERSIST })
	@JoinTable(
			name = "ProjectEmployee",
			joinColumns = {@JoinColumn(name = "employeeId")},
			inverseJoinColumns = {@JoinColumn(name = "projectId")}
	)
	private Set<Project> projects = new HashSet<>();

	@OneToMany(mappedBy = "employee", cascade = { CascadeType.MERGE, CascadeType.PERSIST })
	private Set<Issue> issues = new HashSet<>();

	// classes persisted with Hibernate must have a default constructor
	public Employee() {
	}

	public Employee(String firstName, String lastName, LocalDate dateOfBirth, Address address) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.dateOfBirth = dateOfBirth;
		this.attachAddress(address);
	}

	public long getId() {
		return this.id;
	}

	// private prevents modifying the ID from outside Hibernate
	private void setId(long id) {
		this.id = id;
	}

	public String getFirstName() {
		return this.firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return this.lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public LocalDate getDateOfBirth() {
		return this.dateOfBirth;
	}

	public void setDateOfBirth(LocalDate dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public Set<LogBookEntry> getLogbookEntries() {
		return this.logbookEntries;
	}

	public void attachAddress(Address address) {
		if (address.getEmployee() != null) {
			address.getEmployee().setAddress(null);
		}
		if (this.getAddress() != null) {
			this.getAddress().setEmployee(null);
		}

		address.setEmployee(this);
		this.setAddress(address);
	}

	public void detachAddress() {
		if (this.getAddress() != null) {
			this.getAddress().setEmployee(null);
		}
		this.setAddress(null);
	}

	public void setLogbookEntries(Set<LogBookEntry> logbookEntries) {
		this.logbookEntries = logbookEntries;
	}

	public void addLogbookEntry(LogBookEntry logbookEntry) {
		// if entry is already linked to some employee, remove this link, because do not want to have an entry linked to multiple employees
		if (logbookEntry.getEmployee() != null) {
			logbookEntry.getEmployee().getLogbookEntries().remove(logbookEntry);
		}

		this.getLogbookEntries().add(logbookEntry);
		logbookEntry.setEmployee(this);
	}

	public void removeLogbookEntry(LogBookEntry logbookEntry) {
		if (this.logbookEntries.contains(logbookEntry)) {
			this.getLogbookEntries().remove(logbookEntry);
		}
		if (logbookEntry.getEmployee() == this) {
			logbookEntry.setEmployee(null);
		}
	}

	public Set<Project> getProjects() {
		return this.projects;
	}

	public void setProjects(Set<Project> projects) {
		this.projects = projects;
	}

	public void addProject(Project project) {
		this.getProjects().add(project);
		project.getEmployees().add(this);
	}

	public void removeProject(Project project) {
		this.getProjects().remove(project);
		project.getEmployees().remove(this);
	}

	public Set<Issue> getIssues() {
		return this.issues;
	}

	public void setIssues(Set<Issue> issues) {
		this.issues = issues;
	}

	public void addIssue(Issue issue) {
		if (issue.getEmployee() != null) {
			issue.getEmployee().getIssues().remove(issue);
		}

		this.getIssues().add(issue);
		issue.setEmployee(this);
	}

	public void removeIssue(Issue issue) {
		this.getIssues().remove(issue);
		if (issue.getEmployee() == this) {
			issue.setEmployee(null);
		}
	}

	@Override
	public String toString() {
		StringBuilder outputBuilder = new StringBuilder();
		outputBuilder
				.append("Employee: ")
				.append("id=").append(this.id)
				.append(", firstName='").append(this.firstName).append("\'")
				.append(", lastName='").append(this.lastName).append('\'')
				.append(", dateOfBirth=").append(this.dateOfBirth.format(DateTimeFormatter.ISO_DATE));

		if (this.getAddress() != null) {
			outputBuilder.append(", Address: ")
				.append(this.getAddress());
		}

		if (this.getProjects().size() > 0) {
			outputBuilder.append('\n').append("    Projects: ");
			for (Project project : this.getProjects()) {
				outputBuilder.append("\n    ")
						.append("id=").append(project.getId())
						.append(": ").append(project.getName());
			}
		}

		return outputBuilder.toString();
	}
}

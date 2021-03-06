package swt6.issuetracker.domain;

import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL)
public final class Issue {
	public enum IssueState {
		NEW, OPEN, RESOLVED, CLOSED, REJECTED
	}

	public enum IssuePriority {
		LOW, MEDIUM, HIGH, CRITICAL
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(nullable = false)
	private String name;

	@Column
	@Access(AccessType.PROPERTY)
	private double estimatedTime;

	@Column(nullable = false)
	@Access(AccessType.PROPERTY)
	private int progress;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private IssueState state;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private IssuePriority priority;

	@ManyToOne(optional = true, cascade = { CascadeType.MERGE, CascadeType.PERSIST }, fetch = FetchType.LAZY)
	@JoinColumn(name = "employeeId")
	@Fetch(FetchMode.SELECT)
	private Employee employee;

	@ManyToOne(optional = false, cascade = { CascadeType.MERGE, CascadeType.PERSIST }, fetch = FetchType.LAZY)
	@JoinColumn(name = "projectId")
	@Fetch(FetchMode.SELECT)
	private Project project;

	// when querying an issue, we are always interested in the log book entries associated with the issue, therefore those entries are fetched eagerly
	// possible examples are calculating the total time spent on an issue and compring it to the time estimated for that issue
	@OneToMany(mappedBy = "issue", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	@Fetch(FetchMode.SELECT)
	private Set<LogBookEntry> logBookEntries = new HashSet<>();

	public Issue() {
	}

	public Issue(String name, IssueState state, IssuePriority priority, int progress) {
		this(name, state, priority, progress, 0);
	}

	public Issue(String name, IssueState state, IssuePriority priority, int progress, double estimatedTime) {
		this.name = name;
		this.state = state;
		this.priority = priority;
		this.progress = progress;
		this.estimatedTime = estimatedTime;
	}

	public long getId() {
		return id;
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

	public double getEstimatedTime() {
		return this.estimatedTime;
	}

	public void setEstimatedTime(double estimatedTime) {
		if (estimatedTime >= 0) {
			this.estimatedTime = estimatedTime;
		}
	}

	public int getProgress() {
		return progress;
	}

	public void setProgress(int progress) {
		if ((progress >= 0) && (progress <= 100)) {
			this.progress = progress;
		}
	}

	public IssueState getState() {
		return this.state;
	}

	public void setState(IssueState state) {
		this.state = state;
	}

	public IssuePriority getPriority() {
		return this.priority;
	}

	public void setPriority(IssuePriority priority) {
		this.priority = priority;
	}

	public Employee getEmployee() {
		return this.employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public void attachEmployee(Employee employee) {
		if (this.getEmployee() != null) {
			this.getEmployee().getIssues().remove(this);
		}

		if (employee != null) {
			employee.getIssues().add(this);
		}
		this.setEmployee(employee);
	}

	public void detachEmployee() {
		if (this.getEmployee() != null) {
			this.getEmployee().getIssues().remove(this);
		}
		this.setEmployee(null);
	}

	public Project getProject() {
		return this.project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public void attachProject(Project project) {
		if (this.getProject() != null) {
			this.getProject().getIssues().remove(this);
		}

		if (project != null) {
			project.getIssues().add(this);
		}
		this.setProject(project);
	}

	public void detachProject() {
		if (this.getProject() != null) {
			this.getProject().getIssues().remove(this);
		}
		this.setProject(null);
	}

	public Set<LogBookEntry> getLogBookEntries() {
		return this.logBookEntries;
	}

	private void setLogBookEntries(Set<LogBookEntry> logBookEntries) {
		this.logBookEntries = logBookEntries;
	}

	public void addLogBookEntry(LogBookEntry entry) {
		if (entry.getIssue() != null) {
			entry.getIssue().getLogBookEntries().remove(entry);
		}

		entry.setIssue(this);
		this.getLogBookEntries().add(entry);
	}

	public void removeLogBookEntry(LogBookEntry entry) {
		if (entry.getIssue() == this) {
			entry.setIssue(null);
		}
		this.getLogBookEntries().remove(entry);
	}

	@Override
	public String toString() {
		return "Issue{" +
				"id=" + this.getId() +
				", name='" + this.getName() + '\'' +
				", state=" + this.getState() +
				", priority=" + this.getPriority() +
				", estimatedTime=" + this.getEstimatedTime() +
				", progress=" + this.getProgress() + "%" +
				'}';
	}
}

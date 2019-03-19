package swt6.issuetracker.domain;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
public final class LogBookEntry {
	@Id
	@GeneratedValue
	private long id;

	@Column(nullable = false)
	private String activity;

	@Column(nullable = false)
	private LocalDateTime startTime;

	@Column(nullable = false)
	private LocalDateTime endTime;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private ProjectPhase projectPhase;

	@ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE }, optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "employeeId")
	@Fetch(FetchMode.SELECT)
	private Employee employee;

	@ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE }, optional = false, fetch = FetchType.EAGER)
	@JoinColumn(name = "issueId")
	@Fetch(FetchMode.JOIN)
	private Issue issue;

	public LogBookEntry() {

	}

	public LogBookEntry(String activity, LocalDateTime startTime, LocalDateTime endTime, ProjectPhase projectPhase) {
		this.activity = activity;
		this.startTime = startTime;
		this.endTime = endTime;
		this.projectPhase = projectPhase;
	}

	public long getId() {
		return this.id;
	}

	private void setId(long id) {
		this.id = id;
	}

	public String getActivity() {
		return this.activity;
	}

	public void setActivity(String activity) {
		this.activity = activity;
	}

	public LocalDateTime getStartTime() {
		return this.startTime;
	}

	public void setStartTime(LocalDateTime startTime) {
		this.startTime = startTime;
	}

	public LocalDateTime getEndTime() {
		return this.endTime;
	}

	public void setEndTime(LocalDateTime endTime) {
		this.endTime = endTime;
	}

	public Employee getEmployee() {
		return this.employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public void attachEmployee(Employee employee) {
		this.setEmployee(employee);
	}

	public void detachEmployee() {
		this.setEmployee(null);
	}

	public ProjectPhase getProjectPhase() {
		return this.projectPhase;
	}

	public void setProjectPhase(ProjectPhase projectPhase) {
		this.projectPhase = projectPhase;
	}

	public Issue getIssue() {
		return this.issue;
	}

	public void setIssue(Issue issue) {
		this.issue = issue;
	}

	public void attachIssue(Issue issue) {
		if (this.getIssue() != null) {
			this.getIssue().getLogBookEntries().remove(this);
		}

		if (issue != null) {
			issue.getLogBookEntries().add(this);
		}
		this.setIssue(issue);
	}

	public void detachIssue() {
		if (this.getIssue() != null) {
			this.getIssue().getLogBookEntries().remove(this);
		}
		this.setIssue(null);
	}

	@Override
	public String toString() {
		return "#" + this.id + ": " +
				"\'" + this.activity + "\': " +
				"startTime=" + this.startTime.format(DateTimeFormatter.ISO_DATE_TIME) +
				", endTime=" + this.endTime.format(DateTimeFormatter.ISO_DATE_TIME) +
				", phase=" + this.projectPhase;
	}
}

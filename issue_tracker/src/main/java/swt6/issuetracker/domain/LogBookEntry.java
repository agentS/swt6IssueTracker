package swt6.issuetracker.domain;

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

	@ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE }, optional = false)
	private Employee employee;

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

	public ProjectPhase getProjectPhase() {
		return this.projectPhase;
	}

	public void setProjectPhase(ProjectPhase projectPhase) {
		this.projectPhase = projectPhase;
	}

	public Employee getEmployee() {
		return this.employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public void attachEmployee(Employee employee) {
		// if entry is already linked to some employee, remove this link, because do not want to have an entry linked to multiple employees
		if (this.employee != null) {
			this.employee.getLogbookEntries().remove(this);
		}

		if (employee != null) {
			employee.getLogbookEntries().add(this);
		}
		this.setEmployee(employee);
	}

	public void detachEmployee() {
		if (this.employee != null) {
			this.employee.getLogbookEntries().remove(this);
		}
		this.setEmployee(null);
	}

	@Override
	public String toString() {
		return '#' + this.id + ' ' +
				"\'" + this.activity + "\': " +
				"startTime=" + this.startTime.format(DateTimeFormatter.ISO_DATE_TIME) +
				", endTime=" + this.endTime.format(DateTimeFormatter.ISO_DATE_TIME) +
				", phase=" + this.projectPhase;
	}
}

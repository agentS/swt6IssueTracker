package swt6.orm.domain;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
public class LogbookEntry {
	@Id
	@GeneratedValue
	private long id;

	private String activity;
	private LocalDateTime startTime;
	private LocalDateTime endTime;

	@ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE }, optional = false)
	private Employee employee;

	public LogbookEntry() {

	}

	public LogbookEntry(String activity, LocalDateTime startTime, LocalDateTime endTime) {
		this.activity = activity;
		this.startTime = startTime;
		this.endTime = endTime;
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
		// if entry is already linked to some employee, remove this link, because do not want to have an entry linked to multiple employees
		if (this.employee != null) {
			this.employee.getLogbookEntries().remove(this);
		}

		if (employee != null) {
			employee.getLogbookEntries().add(this);
		}
		this.setEmployee(employee);
	}

	public void detachEmployee(Employee employee) {
		// TODO
	}

	@Override
	public String toString() {
		return "\'" + this.activity + "\': " +
				"startTime=" + this.startTime.format(DateTimeFormatter.ISO_DATE_TIME) +
				", endTime=" + this.endTime.format(DateTimeFormatter.ISO_DATE_TIME);
	}
}

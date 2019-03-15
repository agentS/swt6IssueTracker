package swt6.orm.simple.domain;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Employee {
	private long id;
	private String firstName;
	private String lastName;
	private LocalDate dateOfBirth;

	// classes persisted with Hibernate must have a default constructor
	public Employee() {
	}

	public Employee(String firstName, String lastName, LocalDate dateOfBirth) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.dateOfBirth = dateOfBirth;
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

	@Override
	public String toString() {
		return "Employee{" +
				"id=" + this.id +
				", firstName='" + this.firstName + '\'' +
				", lastName='" + this.lastName + '\'' +
				", dateOfBirth=" + this.dateOfBirth.format(DateTimeFormatter.ISO_DATE) +
				'}';
	}
}

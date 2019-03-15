package swt6.orm.domain;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Employee {
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	private long id;

	private String firstName;
	private String lastName;
	private LocalDate dateOfBirth;

	// version 0
	// when  no association is specified, the associated Address object is serialized using Java's serialization mechanism and stored in the address column
	//private Address address;

	// version 1
	// address is embedded into the same table
	// column names have to be altered, in order to avoid name clashes
	@Embedded
	@AttributeOverride(name = "zipCode", column = @Column(name = "address_zipCode"))
	@AttributeOverride(name = "city", column = @Column(name = "address_city"))
	@AttributeOverride(name = "street", column = @Column(name = "address_street"))
	private Address address;

	// version 2
	// address is stored as separate table
	// less performant, but this version is more easily extensible and better with handling null values
	/*@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
	private Address address;*/

	// mappedBy defines the data component of the other side that references to this side
	@OneToMany(mappedBy = "employee", cascade = CascadeType.ALL)
	private Set<LogbookEntry> logbookEntries = new HashSet<>();

	// classes persisted with Hibernate must have a default constructor
	public Employee() {
	}

	public Employee(String firstName, String lastName, LocalDate dateOfBirth) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.dateOfBirth = dateOfBirth;
	}

	public Employee(String firstName, String lastName, LocalDate dateOfBirth, Address address) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.dateOfBirth = dateOfBirth;
		this.address = address;
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

	public Set<LogbookEntry> getLogbookEntries() {
		return this.logbookEntries;
	}

	public void setLogbookEntries(Set<LogbookEntry> logbookEntries) {
		this.logbookEntries = logbookEntries;
	}

	public void addLogbookEntry(LogbookEntry logbookEntry) {
		// if entry is already linked to some employee, remove this link, because do not want to have an entry linked to multiple employees
		if (logbookEntry.getEmployee() != null) {
			logbookEntry.getEmployee().getLogbookEntries().remove(logbookEntry);
		}

		this.getLogbookEntries().add(logbookEntry);
		logbookEntry.setEmployee(this);
	}

	public void removeLogbookEntry(LogbookEntry logbookEntry) {
		// TODO
	}

	@Override
	public String toString() {
		StringBuilder outputBuilder = new StringBuilder();
		outputBuilder
				.append("Employee:")
				.append("id=").append(this.id)
				.append(", firstName='").append(this.firstName).append("\'")
				.append(", lastName='").append(this.lastName).append('\'')
				.append(", dateOfBirth=").append(this.dateOfBirth.format(DateTimeFormatter.ISO_DATE));

		if (this.getAddress() != null) {
			outputBuilder.append(", ")
				.append(this.getAddress());
		}
		outputBuilder.append('\n');

		return outputBuilder.toString();
	}
}

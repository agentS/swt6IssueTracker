package swt6.issuetracker.domain;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@IdClass(AddressId.class)
public final class Address implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "zipCode", insertable = false, updatable = false)
	private String zipCode;

	@Id
	@Column(name = "city", insertable = false, updatable = false)
	private String city;

	@Id
	@Column(name = "street", insertable = false, updatable = false)
	private String street;

	@OneToOne(optional = false, cascade = { CascadeType.MERGE, CascadeType.PERSIST }, orphanRemoval = true)
	@JoinColumn(name = "employeeId")
	private Employee employee;

	public Address() {
	}

	public Address(String zipCode, String city, String street) {
		this.zipCode = zipCode;
		this.city = city;
		this.street = street;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getZipCode() {
		return zipCode;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

	public Employee getEmployee() {
		return this.employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public void attachEmployee(Employee employee) {
		if (this.getEmployee() != null) {
			this.getEmployee().setAddress(null);
		}

		employee.setAddress(this);
		this.setEmployee(employee);
	}

	public void detachEmployee() {
		if (this.getEmployee() != null) {
			this.getEmployee().setAddress(null);
		}
		this.setEmployee(null);
	}

	public String toString() {
		return String.format("%s, %s %s", street, zipCode, city);
	}
}

package swt6.issuetracker.domain;

import java.io.Serializable;
import java.util.Objects;

public final class AddressId implements Serializable {
	private String   street;
	private String   zipCode;
	private String   city;

	public AddressId() {
	}

	public AddressId(String street, String zipCode, String city) {
		this.street = street;
		this.zipCode = zipCode;
		this.city = city;
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

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		AddressId addressId = (AddressId) o;
		return getStreet().equals(addressId.getStreet()) &&
				getZipCode().equals(addressId.getZipCode()) &&
				getCity().equals(addressId.getCity());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getStreet(), getZipCode(), getCity());
	}
}

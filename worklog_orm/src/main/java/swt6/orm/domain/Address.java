package swt6.orm.domain;

import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;

// version bravo (required for @Embedded)
@Embeddable

// version charlie (one-to-one relationship)
//@Entity
public class Address implements Serializable {
  private static final long serialVersionUID = 1L;

  // version charlie
  // remove for version bravo
  /*@Id
  @GeneratedValue
  private Long     id;*/

  private String   zipCode;
  private String   city;
  private String   street;
  
  public Address() {  
  }

  public Address(String zipCode, String city, String street) {
    this.zipCode = zipCode;
    this.city = city;
    this.street = street;
  }

/*  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }*/

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

  public String toString() {
    return String.format("%s %s, %s", zipCode, city, street);
  }
}

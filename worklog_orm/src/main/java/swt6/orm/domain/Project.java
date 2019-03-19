package swt6.orm.domain;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Project implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Long     id;
	private String   name;

	@ManyToMany(cascade = CascadeType.ALL)
	// join table is superfluous in this case, as the name of the join table and its attributes are generated automatically, if the annotation is ommited.
	@JoinTable(
			name = "PROJECT_EMPLOYEE",
			joinColumns = @JoinColumn(name = "projectId"),
			inverseJoinColumns = @JoinColumn(name = "employeeId")
	)
	private Set<Employee> members = new HashSet<>();

	public Project() {
	}

	public Project(String name) {
		this.name = name;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Set<Employee> getMembers() {
		return this.members;
	}

	private void setMembers(Set<Employee> members) {
		this.members = members;
	}

	public void addMember(Employee member) {
		if (member == null) {
			throw new IllegalArgumentException("Member must not be null");
		}
		this.getMembers().add(member);
	}

	public void removeMember(Employee member) {
		if (member == null) {
			throw new IllegalArgumentException("Member must not be null");
		}
		this.getMembers().remove(member);
	}

	public String toString() {
		return name;
	}
}

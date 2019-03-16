package swt6.issuetracker.dal;

import swt6.issuetracker.domain.Address;
import swt6.issuetracker.domain.Employee;
import swt6.issuetracker.domain.LogBookEntry;

import java.util.List;
import java.util.Optional;

public interface EmployeeDao {

	List<Employee> findAll(DalTransaction transaction);
	Optional<Employee> findById(DalTransaction transaction, long id);
	Employee add(DalTransaction transaction, Employee employee);
	Employee updateLastName(DalTransaction transaction, Employee employee, String newLastName);
	Employee updateAddress(DalTransaction transaction, Employee employee, Address newAddress);
	void delete(DalTransaction transaction, Employee employee);
	boolean delete(DalTransaction transaction, long id);
}

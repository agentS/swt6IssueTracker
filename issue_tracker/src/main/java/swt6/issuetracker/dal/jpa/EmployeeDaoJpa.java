package swt6.issuetracker.dal.jpa;

import swt6.issuetracker.dal.DalTransaction;
import swt6.issuetracker.dal.EmployeeDao;
import swt6.issuetracker.domain.Address;
import swt6.issuetracker.domain.Employee;
import swt6.issuetracker.domain.LogBookEntry;
import swt6.issuetracker.domain.Project;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;
import java.util.Optional;

public class EmployeeDaoJpa implements EmployeeDao {
	@Override
	public List<Employee> findAll(DalTransaction transaction) {
		EntityManager entityManager = DaoUtilJpa.getEntityManager(transaction);
		return entityManager
				.createQuery("SELECT E FROM Employee AS E ORDER BY E.lastName DESC", Employee.class)
				.getResultList();
	}

	@Override
	public Optional<Employee> findById(DalTransaction transaction, long id) {
		EntityManager entityManager = DaoUtilJpa.getEntityManager(transaction);
		Employee employee = entityManager.find(Employee.class, id);
		if (employee != null) {
			return Optional.of(employee);
		} else {
			return Optional.empty();
		}
	}

	@Override
	public Employee add(DalTransaction transaction, Employee employee) {
		EntityManager entityManager = DaoUtilJpa.getEntityManager(transaction);
		return entityManager.merge(employee);
	}

	@Override
	public Employee updateLastName(DalTransaction transaction, Employee employee, String newLastName) {
		Employee updatedEmployee = this.mergeEmployee(transaction, employee);
		updatedEmployee.setLastName(newLastName);
		return updatedEmployee;
	}

	private Employee mergeEmployee(DalTransaction transaction, Employee employee) {
		EntityManager entityManager = DaoUtilJpa.getEntityManager(transaction);
		return entityManager.merge(employee);
	}

	@Override
	public Employee updateAddress(DalTransaction transaction, Employee employee, Address newAddress) {
		Employee updatedEmployee = this.mergeEmployee(transaction, employee);
		updatedEmployee.setAddress(newAddress);
		return updatedEmployee;
	}

	@Override
	public void delete(DalTransaction transaction, Employee employee) {
		EntityManager entityManager = DaoUtilJpa.getEntityManager(transaction);

		for (Project project : employee.getProjects()) {
			project.removeEmployee(employee);
		}

		entityManager.remove(this.mergeEmployee(transaction, employee));
	}

	@Override
	public boolean delete(DalTransaction transaction, long id) {
		EntityManager entityManager = DaoUtilJpa.getEntityManager(transaction);
		Query deleteStatement = entityManager.createQuery("DELETE FROM Employee AS E WHERE E.id = :id");
		deleteStatement.setParameter("id", id);
		return deleteStatement.executeUpdate() > 0;
	}

	@Override
	public List<LogBookEntry> findAllLogBookEntries(DalTransaction transaction, Employee employee) {
		return null;
	}

	@Override
	public void addLogBookEntryToEmployee(DalTransaction transaction, LogBookEntry logBookEntry, Employee employee) {
		EntityManager entityManager = DaoUtilJpa.getEntityManager(transaction);
		Employee targetEmployee = entityManager.merge(employee);
		targetEmployee.addLogbookEntry(logBookEntry);
		entityManager.merge(logBookEntry);
	}

	@Override
	public void removeLogBookEntryFromEmployee(DalTransaction transaction, LogBookEntry logBookEntry, Employee employee) {
		EntityManager entityManager = DaoUtilJpa.getEntityManager(transaction);
		Employee targetEmployee = entityManager.merge(employee);
		targetEmployee.removeLogbookEntry(logBookEntry);
		entityManager.remove(logBookEntry);
	}

	@Override
	public void reassignLogBookEntry(DalTransaction transaction, LogBookEntry logBookEntry, Employee newHolder) {
		EntityManager entityManager = DaoUtilJpa.getEntityManager(transaction);
		Employee targetCurrentHolder = entityManager.merge(newHolder);
		LogBookEntry targetLogBookEntry = entityManager.merge(logBookEntry);
		targetCurrentHolder.addLogbookEntry(targetLogBookEntry);
	}
}

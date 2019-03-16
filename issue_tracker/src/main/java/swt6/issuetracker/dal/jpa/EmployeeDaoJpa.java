package swt6.issuetracker.dal.jpa;

import swt6.issuetracker.dal.DalTransaction;
import swt6.issuetracker.dal.EmployeeDao;
import swt6.issuetracker.domain.*;

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
		employee = null;
		EntityManager entityManager = DaoUtilJpa.getEntityManager(transaction);

		if (updatedEmployee.getAddress() != null) {
			entityManager.remove(updatedEmployee.getAddress());
			entityManager.persist(updatedEmployee);
		}

		updatedEmployee.attachAddress(newAddress);
		return updatedEmployee;
	}

	@Override
	public void delete(DalTransaction transaction, Employee employee) {
		EntityManager entityManager = DaoUtilJpa.getEntityManager(transaction);

		for (Project project : employee.getProjects()) {
			project.removeEmployee(employee);
		}
		for (Issue issue : employee.getIssues()) {
			issue.detachEmployee();
		}

		Employee targetEmployee = this.mergeEmployee(transaction, employee);
		entityManager.remove(targetEmployee.getAddress());
		entityManager.remove(targetEmployee);
	}

	@Override
	public boolean delete(DalTransaction transaction, long id) {
		EntityManager entityManager = DaoUtilJpa.getEntityManager(transaction);
		Query addressDeleteStatement = entityManager.createQuery("DELETE FROM Address AS A WHERE employee.id = :id");
		addressDeleteStatement.setParameter("id", id);
		addressDeleteStatement.executeUpdate();
		Query employeeDeleteStatement = entityManager.createQuery("DELETE FROM Employee AS E WHERE E.id = :id");
		employeeDeleteStatement.setParameter("id", id);
		return employeeDeleteStatement.executeUpdate() > 0;
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

package swt6.orm.simple.hibernate;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;
import swt6.orm.simple.domain.Employee;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class EmployeeManager {
	private final static String HIBERNATE_CONFIGURATION_FILE_PATH = "hibernate.cfg.xml";

	static String promptFor(BufferedReader in, String p) {
		System.out.print(p + "> ");
		System.out.flush();
		try {
			return in.readLine();
		}
		catch (Exception e) {
			return promptFor(in, p);
		}
	}

	// version 0
	public static void saveEmployeeVersion0(Employee employee) {
		SessionFactory sessionFactory =
				new Configuration()
						.configure(HIBERNATE_CONFIGURATION_FILE_PATH)
						.buildSessionFactory();

		Session session = sessionFactory.openSession();
		Transaction transaction = session.beginTransaction();

		session.save(employee);

		transaction.commit();
		session.close();
		sessionFactory.close();
	}

	// version 1
	public static void saveEmployeeVersion1(Employee employee) {
		SessionFactory sessionFactory =
				new Configuration()
						.configure(HIBERNATE_CONFIGURATION_FILE_PATH)
						.buildSessionFactory();

		// requires settings the property "hibernate.current_session_context_class" to "thread"
		Session session = sessionFactory.getCurrentSession();
		Transaction transaction = session.beginTransaction();

		session.save(employee);

		transaction.commit(); // session is also closed after the commit
		sessionFactory.close();
	}

	// version 2
	private static void saveEmployee(Employee employee) {
		Session session = HibernateUtil.getCurrentSession();
		Transaction transaction = session.beginTransaction();

		session.save(employee);

		transaction.commit(); // session is also closed after the commit
	}

	private static Employee findEmployee(long id) {
		Session session = HibernateUtil.getCurrentSession();
		Transaction transaction = session.beginTransaction();

		Employee employee = session.find(Employee.class, id);
		transaction.commit();

		return employee;
	}

	private static List<Employee> findAllEmployees() {
		Session session = HibernateUtil.getCurrentSession();
		Transaction transaction = session.beginTransaction();

		List<Employee> employees = session
				.createQuery("SELECT E FROM Employee AS E ORDER BY lastName", Employee.class)
				.getResultList();

		transaction.commit();
		return employees;
	}

	private static List<Employee> findByLastName(String lastName) {
		Session session = HibernateUtil.getCurrentSession();
		Transaction transaction = session.beginTransaction();

		Query<Employee> query = session.createQuery(
			"SELECT E FROM Employee AS E WHERE E.lastName LIKE :lastName ORDER BY firstName",
			 Employee.class
		);
		query.setParameter("lastName", '%' + lastName + '%');
		List<Employee> employees = query.getResultList();

		transaction.commit();
		return employees;
	}

	private static boolean updateEmployee(
			long employeeId,
			String firstName,
			String lastName,
			LocalDate dateOfBirth
	) {
		Session session = HibernateUtil.getCurrentSession();
		Transaction transaction = session.beginTransaction();

		Employee employee = session.find(Employee.class, employeeId);

		if (employee != null) {
			employee.setFirstName(firstName);
			employee.setLastName(lastName);
			employee.setDateOfBirth(dateOfBirth);
		}

		// automatic dirty checking ensures that persistent objects, which are changed, are persisted when the transaction is commited
		transaction.commit();
		return employee != null;
	}

	private static boolean deleteEmployee(long employeeId) {
		Session session = HibernateUtil.getCurrentSession();
		Transaction transaction = session.beginTransaction();

		// version 0
		/*Employee employee = session.find(Employee.class, employeeId);
		if (employee != null) {
			session.remove(employee);
			// issue: employee reference is still a valid reference, which can lead to exceptions
		}
		boolean deleted = employee != null;*/

		// version 1
		Query<?> deleteStatement = session.createQuery("DELETE FROM Employee E WHERE E.id = :id");
		deleteStatement.setParameter("id", employeeId);
		boolean deleted = deleteStatement.executeUpdate() > 0;

		transaction.commit();
		return deleted;
	}

	public static void main(String[] args) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d.M.yyyy");
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		String availCmds = "commands: quit, insert, update, delete, list, findById, findByLastName";

		System.out.println("Hibernate Employee Admin");
		System.out.println(availCmds);
		String userCmd = promptFor(in, "");

		try {
			while (!userCmd.equals("quit")) {
				switch (userCmd) {
					case "list":
						for (Employee employee : findAllEmployees()) {
							System.out.println(employee);
						}
						break;
					case "findById":
						try {
							long id = Long.parseLong(promptFor(in, "id"));
							System.out.println(findEmployee(id));
						} catch (NumberFormatException exception) {
							System.err.println("Invalid number format! Please enter an integer number.");
						}
						break;
					case "findByLastName":
						for (Employee employee : findByLastName(promptFor(in, "lastName"))) {
							System.out.println(employee);
						}
						break;
					case "insert":
						try {
							Employee employee = new Employee(
									promptFor(in, "first name"),
									promptFor(in, "last name"),
									LocalDate.parse(promptFor(in, "date of birth (dd.mm.yyyy)"), formatter)
							);
							saveEmployee(employee);
						} catch (DateTimeParseException exception) {
							System.err.println("Invalid date format! Please use the date format dd.mm.yyyy");
						}
						break;
					case "update":
						try {
							boolean success = updateEmployee(
									Long.parseLong(promptFor(in, "id")),
									promptFor(in, "first name"),
									promptFor(in, "last name"),
									LocalDate.parse(promptFor(in, "date of birth (dd.mm.yyyy)"), formatter)
							);
							System.out.println(success ? "employee updated" : "employee not found");
						} catch (NumberFormatException exception) {
							System.err.println("Invalid number format! Please enter an integer number.");
						} catch (DateTimeParseException exception) {
							System.err.println("Invalid date format! Please use the date format dd.mm.yyyy");
						}
						break;
					case "delete":
						try {
							boolean success = deleteEmployee(
									Long.parseLong(promptFor(in, "id"))
							);
							System.out.println(success ? "employee deleted" : "employee not found");
						} catch (NumberFormatException exception) {
							System.err.println("Invalid number format! Please enter an integer number.");
						}
						break;
					default:
						System.out.println("ERROR: invalid command");
						break;
				}

				System.out.println(availCmds);
				userCmd = promptFor(in, "");
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		} finally {
			HibernateUtil.closeSessionFactory();
		}
	}
}

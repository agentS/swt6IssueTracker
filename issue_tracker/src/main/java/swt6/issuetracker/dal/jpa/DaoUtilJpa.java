package swt6.issuetracker.dal.jpa;

import swt6.issuetracker.dal.DalTransaction;

import javax.persistence.EntityManager;
import java.security.InvalidParameterException;

public abstract class DaoUtilJpa {
	static EntityManager getEntityManager(DalTransaction transaction) {
		if (transaction instanceof DalTransactionJpa) {
			return ((DalTransactionJpa) transaction).getEntityManager();
		} else {
			throw new InvalidParameterException("Provide a transaction of type " + DalTransactionJpa.class.getName() + "!");
		}
	}
}

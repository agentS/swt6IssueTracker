package swt6.issuetracker.dal;

public interface DalTransaction {
	void begin();
	void commit();
	void rollback();
	boolean isOpen();
}

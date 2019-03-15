package swt6.issuetracker.ui.command;

import swt6.issuetracker.dal.DalTransaction;
import swt6.issuetracker.dal.DaoFactory;

import java.util.Scanner;

public interface UserCommand {
	DalTransaction process(DalTransaction transaction, DaoFactory daoFactory) throws Throwable;
}

package swt6.issuetracker.ui;

import java.util.Scanner;

public final class CommandLineReader {
	private static CommandLineReader instance;
	private static final Object lockObject = new Object();

	private Scanner scanner;

	public static CommandLineReader getInstance() {
		if (instance == null) {
			synchronized (lockObject) {
				if (instance == null) {
					instance = new CommandLineReader();
				}
			}
		}
		return instance;
	}

	private CommandLineReader() {
		this.scanner = new Scanner(System.in);
	}

	public String promptFor(String prompt) {
		System.out.print(prompt + "> ");
		System.out.flush();

		try {
			return this.scanner.nextLine();
		} catch (Exception exception) {
			return promptFor(prompt);
		}
	}
}

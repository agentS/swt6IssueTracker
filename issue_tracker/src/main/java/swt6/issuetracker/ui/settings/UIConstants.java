package swt6.issuetracker.ui.settings;

import java.time.format.DateTimeFormatter;

public final class UIConstants {
	public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");
	public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

	private UIConstants() {
	}
}

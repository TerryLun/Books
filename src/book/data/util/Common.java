package book.data.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * @author Terry Tianwei Lun
 *
 */
public class Common {

	public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("MMM dd yyyy");
	public static final DateTimeFormatter DATE_FORMAT_2 = DateTimeFormatter.ofPattern("yyyy-mm-dd");

	/**
	 * If the input string exceeds the length then truncate the string to the length - 3 characters and add "..."
	 * 
	 * @param title book title
	 * @param length max length
	 * @return a string
	 */
	public static String truncateIfRequired(String title, int length) {
		if (title.length() > length) {
			title = title.substring(0, length - 3) + "...";
		}

		return title;
	}

	/**
	 * Converts string into LocaDate
	 * 
	 * @param date date
	 * @return local date
	 */
	public static LocalDate toLocalDate(String date) {

		LocalDate localDate = null;
		try {
			localDate = LocalDate.parse(date, DATE_FORMAT_2);
		} catch (Exception e) {
			return null;
		}

		return localDate;
	}

}

package book.data.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Validate data.
 * 
 * @author Terry Tianwei Lun
 *
 */
public class Validator {

	private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	private static final String YYYYMMDD_PATTERN = "(20\\d{2})(\\d{2})(\\d{2})"; // valid for years 2000-2099
	private static final String YYYY_MM_DD_PATTERN = "(20\\d{2})-(\\d{2})-(\\d{2})"; // valid for years 2000-2099

	private static Pattern emailPattern;
	private static Pattern yyyymmddPattern;
	private static Pattern yyyy_mm_ddPattern;

	private Validator() {
	}

	/**
	 * Validate an email string.
	 * 
	 * @param email
	 *            the email string.
	 * @return true if the email address is valid, false otherwise.
	 */
	public static boolean validateEmail(final String email) {
		if (emailPattern == null) {
			emailPattern = Pattern.compile(EMAIL_PATTERN);
		}

		Matcher matcher = emailPattern.matcher(email);
		return matcher.matches();
	}

	public static boolean validateJoinedDate(String yyyymmdd) {
		if (yyyymmddPattern == null) {
			yyyymmddPattern = Pattern.compile(YYYYMMDD_PATTERN);
		}

		Matcher matcher = yyyymmddPattern.matcher(yyyymmdd);
		return matcher.matches();
	}

	public static boolean validateJoinedDateUpdate(String yyyy_mm_dd) {
		if (yyyy_mm_ddPattern == null) {
			yyyy_mm_ddPattern = Pattern.compile(YYYY_MM_DD_PATTERN);
		}

		Matcher matcher = yyyymmddPattern.matcher(yyyy_mm_dd);
		return matcher.matches();
	}

}

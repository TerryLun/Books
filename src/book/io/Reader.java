package book.io;

import java.util.Arrays;

import book.ApplicationException;

/**
 * @author Terry Tianwei Lun
 *
 */
public class Reader {

	public static final String RECORD_DELIMITER = ":";
	public static final String FIELD_DELIMITER = "\\|";

	/**
	 * Given a FIELD_DELIMITER delimited input string, parts the string into a String array.
	 *
	 * @param row
	 *            input string
	 * @return the parsed String array
	 * @throws ApplicationException
	 *             if the element count doesn't match the expected count.
	 */
	public static String[] getElements(String row, int attributeCount) throws ApplicationException {
		String[] elements = row.split(FIELD_DELIMITER);
		if (elements.length != attributeCount) {
			throw new ApplicationException(String.format("Expected %d but got %d: %s", attributeCount, elements.length, Arrays.toString(elements)));
		}

		return elements;
	}
}

/**
 * Project: Books
 *
 */
package book;

/**
 * @author Terry Tianwei Lun
 *  oringinal file bookoptions
 */
public class BookOptions {

	private static boolean byAuthor;//book
	private static boolean descending;//book
	
	private static boolean byJoinDate;//customer
	private static boolean byLastName;//purchase
	
	private static boolean byTitle;//purchase
	private static boolean byCustomerId;//purchase

	/**
	 * @return the Author option setting
	 */
	public static boolean isByAuthorOptionSet() {
		return byAuthor;
	}

	/**
	 * @param byAuthor
	 *            the byAuthor to set
	 */
	public static void setByAuthor(boolean byAuthor) {
		BookOptions.byAuthor = byAuthor;
	}

	/**
	 * @return the descending option setting
	 */
	public static boolean isDescendingOptionSet() {
		return descending;
	}

	/**
	 * @param descending
	 *            the descending to set
	 */
	public static void setDescending(boolean descending) {
		BookOptions.descending = descending;
	}

	/**
	 * @return the join date option setting
	 */
	public static boolean isByJoinDateOptionSet() {
		return byJoinDate;
	}

	/**
	 * @param byJoinDate
	 *            the byJoinDate to set
	 */
	public static void setByJoinDate(boolean byJoinDate) {
		BookOptions.byJoinDate = byJoinDate;
	}

	/**
	 * @return the byLastName
	 */
	public static boolean isByLastNameOptionSet() {
		return byLastName;
	}

	/**
	 * @param byLastName
	 *            the byLastName to set
	 */
	public static void setByLastName(boolean byLastName) {
		BookOptions.byLastName = byLastName;
	}

	/**
	 * @return the byTitle
	 */
	public static boolean isByTitleOptionSet() {
		return byTitle;
	}

	/**
	 * @param byTitle
	 *            the byTitle to set
	 */
	public static void setByTitle(boolean byTitle) {
		BookOptions.byTitle = byTitle;
	}

	/**
	 * @return the byCustomerId
	 */
	public static boolean isByCustomerIdOptionSet() {
		return byCustomerId;
	}

	/**
	 * @param byCustomerId
	 *            the byCustomerId to set
	 */
	public static void setByCustomerId(boolean byCustomerId) {
		BookOptions.byCustomerId = byCustomerId;
	}

}

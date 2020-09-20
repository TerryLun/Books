/**
 * Project: Books

 */

package book.io;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import book.BookOptions;
import book.data.Book;
import book.data.Customer;
import book.data.Purchase;
import book.data.util.Common;
import book.db.BookDao;
import book.db.CustomerDao;

/**
 * @author Terry Tianwei Lun
 *
 */
public class PurchasesReport {

	private static List<Item> items;
	private static Logger LOG = LogManager.getLogger();

	/**
	 * Print the report.
	 * 
	 * @param out
	 * @return
	 */
	public static List<Item> filters(List<Purchase> purchases) {
		try {
			items = new ArrayList<>();
			for (Purchase purchase : purchases) {
				long customerId = purchase.getCustomerId();
				long bookId = purchase.getBookId();
				Book book = BookDao.getTheInstance().getBook(bookId);
				Customer customer = CustomerDao.getTheInstance().getCustomer(customerId);
				float price = purchase.getPrice();
				Item item = new Item(customer.getFirstName(), customer.getLastName(), book.getTitle(), price);
				items.add(item);
			}
		} catch (Exception e1) {
			LOG.error("ERROR: ", e1);
		}

		if (BookOptions.isByLastNameOptionSet()) {
			if (BookOptions.isDescendingOptionSet()) {
				Collections.sort(items, new CompareByLastNameDescending());
			} else {
				Collections.sort(items, new CompareByLastName());
			}
		}

		if (BookOptions.isByTitleOptionSet()) {
			if (BookOptions.isDescendingOptionSet()) {
				Collections.sort(items, new CompareByTitleDescending());
			} else {
				Collections.sort(items, new CompareByTitle());
			}
		}
		return items;
	}

	public static class CompareByLastName implements Comparator<Item> {
		@Override
		public int compare(Item item1, Item item2) {
			return item1.lastName.compareTo(item2.lastName);
		}
	}

	public static class CompareByLastNameDescending implements Comparator<Item> {
		@Override
		public int compare(Item item1, Item item2) {
			return item2.lastName.compareTo(item1.lastName);
		}
	}

	public static class CompareByTitle implements Comparator<Item> {
		@Override
		public int compare(Item item1, Item item2) {
			return item1.title.compareToIgnoreCase(item2.title);
		}
	}

	public static class CompareByTitleDescending implements Comparator<Item> {
		@Override
		public int compare(Item item1, Item item2) {
			return item2.title.compareToIgnoreCase(item1.title);
		}
	}

	public static class Item {
		private String firstName;
		private String lastName;

		/**
		 * @return the firstName
		 */
		public String getFirstName() {
			return firstName;
		}

		/**
		 * @return the lastName
		 */
		public String getLastName() {
			return lastName;
		}

		/**
		 * @return the title
		 */
		public String getTitle() {
			return title;
		}

		/**
		 * @return the price
		 */
		public float getPrice() {
			return price;
		}

		private String title;
		private float price;

		/**
		 * @param firstName
		 * @param lastName
		 * @param title
		 * @param price
		 */
		public Item(String firstName, String lastName, String title, float price) {
			this.firstName = firstName;
			this.lastName = lastName;
			this.title = Common.truncateIfRequired(title, 80);
			this.price = price;
		}

		/*
		 * (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "Item [firstName=" + firstName + ", lastName=" + lastName + ", title=" + title + ", price=" + price + "]";
		}

	}
}

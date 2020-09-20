/**
 * Project: Books
 */

package book.data.util;

import java.util.Comparator;

import book.data.Book;
import book.data.Customer;

/**
 * @author Terry Tianwei Lun
 *
 */
public class Sorters {

	public static class CompareCustomerByJoinedDate implements Comparator<Customer> {
		@Override
		public int compare(Customer customer1, Customer customer2) {
			return customer1.getJoinedDate().compareTo(customer2.getJoinedDate());
		}
	}

	public static class CompareCustomerByJoinedDateDescending implements Comparator<Customer> {
		@Override
		public int compare(Customer customer1, Customer customer2) {
			return customer2.getJoinedDate().compareTo(customer1.getJoinedDate());
		}
	}

	public static class CompareCustomerByID implements Comparator<Customer> {
		@Override
		public int compare(Customer customer1, Customer customer2) {
			return (int) (customer1.getId() - customer2.getId());
		}
	}

	public static class CompareBookByAuthor implements Comparator<Book> {
		@Override
		public int compare(Book book1, Book book2) {
			return book1.getAuthors().compareToIgnoreCase(book2.getAuthors());
		}
	}

	public static class CompareBookByAuthorDescending implements Comparator<Book> {
		@Override
		public int compare(Book book1, Book book2) {
			return -book1.getAuthors().compareToIgnoreCase(book2.getAuthors());
		}
	}

	public static class CompareBookByID implements Comparator<Book> {
		@Override
		public int compare(Book book1, Book book2) {
			return (int) (book1.getId() - book2.getId());
		}
	}
}

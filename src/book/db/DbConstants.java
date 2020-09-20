/**
 * Project: Books
 *
 */
package book.db;

/**
 * @author Terry Tianwei Lun
 *
 */
public interface DbConstants {

	String DB_PROPERTIES_FILENAME = "db.properties";
	String DB_DRIVER_KEY = "db.driver";
	String DB_URL_KEY = "db.url";
	String DB_USER_KEY = "db.user";
	String DB_PASSWORD_KEY = "db.password";
	String TABLE_ROOT = "A00855225_NEW";//create data base name
	String CUSTOMER_TABLE_NAME = TABLE_ROOT + "Customer";
	String BOOK_TABLE_NAME = TABLE_ROOT + "Book";
	String PURCHASE_TABLE_NAME = TABLE_ROOT + "Purchase";
}

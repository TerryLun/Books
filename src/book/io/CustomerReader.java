package book.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.time.DateTimeException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import book.ApplicationException;
import book.data.Customer;
import book.db.CustomerDao;
import book.data.util.Validator;

/**
 * @author Terry Tianwei Lun
 *
 */
public class CustomerReader extends Reader {

	public static final String RECORD_DELIMITER = ":";
	public static final String FIELD_DELIMITER = "\\|";

	private static final Logger LOG = LogManager.getLogger();

	/**
	 * private constructor to prevent instantiation
	 */
	private CustomerReader() {
	}

	/**
	 * Read the customer input data.
	 * 
	 * @return A collection of customers.
	 * @throws ApplicationException Application Exception
	 */
	public static int read(File customerDataFile, CustomerDao dao) throws ApplicationException {
		BufferedReader customerReader = null;
		int customerCount = 0;

		LOG.debug("Reading" + customerDataFile.getAbsolutePath());
		Map<Long, Customer> customers = new HashMap<>();
		try {
			customerReader = new BufferedReader(new FileReader(customerDataFile));

			String line = null;
			line = customerReader.readLine(); // skip the header line
			while ((line = customerReader.readLine()) != null) {
				try {
					Customer customer = readCustomerString(line);
					customers.put(customer.getId(), customer);
					LOG.debug("Added " + customer.toString() + " as " + customer.getId());
				} catch (ApplicationException e) {
					LOG.error(e.getMessage());
				}
			}
		} catch (IOException e) {
			throw new ApplicationException(e.getMessage());
		} finally {
			try {
				if (customerReader != null) {
					customerReader.close();
				}
			} catch (IOException e) {
				throw new ApplicationException(e.getMessage());
			}
		}

		List<Customer> customersList = new ArrayList<>(customers.values());
		try {
			for (Customer customer : customersList) {
				dao.add(customer);
				customerCount++;
			}
		} catch (SQLException e) {
			throw new ApplicationException(e);
		}

		return customerCount;
	}

	/**
	 * Parse a CustomerForSqlServer data string into a Customer object;
	 * 
	 * @param data raw data
	 * @throws ApplicationException Application Exception
	 */
	private static Customer readCustomerString(String data) throws ApplicationException {
		String[] elements = data.split(FIELD_DELIMITER);
		if (elements.length != Customer.ATTRIBUTE_COUNT) {
			throw new ApplicationException(
					String.format("Expected %d but got %d: %s", Customer.ATTRIBUTE_COUNT, elements.length, Arrays.toString(elements)));
		}

		int index = 0;
		long id = Integer.parseInt(elements[index++]);
		String firstName = elements[index++];
		String lastName = elements[index++];
		String street = elements[index++];
		String city = elements[index++];
		String postalCode = elements[index++];
		String phone = elements[index++];
		String emailAddress = elements[index++];
		if (!Validator.validateEmail(emailAddress)) {
			throw new ApplicationException(String.format("Invalid email: %s", emailAddress));
		}
		String yyyymmdd = elements[index];
		if (!Validator.validateJoinedDate(yyyymmdd)) {
			throw new ApplicationException(String.format("Invalid joined date: %s for customer %d", yyyymmdd, id));
		}
		int year = Integer.parseInt(yyyymmdd.substring(0, 4));
		int month = Integer.parseInt(yyyymmdd.substring(4, 6));
		int day = Integer.parseInt(yyyymmdd.substring(6, 8));

		Customer customer = null;
		try {
			customer = new Customer.Builder(id, phone).setFirstName(firstName).setLastName(lastName).setStreet(street).setCity(city)
					.setPostalCode(postalCode).setEmailAddress(emailAddress).setJoinedDate(year, month, day).build();
		} catch (DateTimeException e) {
			throw new ApplicationException(e.getMessage());
		}

		return customer;
	}
}

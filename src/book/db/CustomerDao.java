package book.db;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import book.ApplicationException;
import book.data.Customer;
import book.io.CustomerReader;

/**
 * @author Terry Tianwei Lun
 *
 */
public class CustomerDao extends Dao {

	public static final String TABLE_NAME = DbConstants.CUSTOMER_TABLE_NAME;

	private static final String CUSTOMERS_DATA_FILENAME = "customers.dat";
	private static final Logger LOG = LogManager.getLogger(CustomerDao.class);
	private static final CustomerDao theInstance = new CustomerDao();
	private static Database database;

	private CustomerDao() {
		super(TABLE_NAME);

		database = Database.getTheInstance();
	}

	public static CustomerDao getTheInstance() {
		return theInstance;
	}

	/**
	 * @throws ApplicationException Application Exception
	 */
	public void init() throws ApplicationException {
		File customerDataFile = new File(CUSTOMERS_DATA_FILENAME);

		try {
			if (!Database.tableExists(CustomerDao.TABLE_NAME) || Database.dbTableDropRequested()) {
				if (Database.tableExists(CustomerDao.TABLE_NAME) && Database.dbTableDropRequested()) {
					drop();
				}

				create();

				LOG.debug("Inserting the customers");

				if (!customerDataFile.exists()) {
					throw new ApplicationException(String.format("Required '%s' is missing.", CUSTOMERS_DATA_FILENAME));
				}

				CustomerReader.read(customerDataFile, this);

				LOG.debug("Customers added to the db");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new ApplicationException(e);
		}
	}

	@Override
	public void create() throws SQLException {
		LOG.debug("Creating database table " + TABLE_NAME);

		// With Derby, the JOINED_DATE was a TIMESTAMP data type. With MS SQL Server, it needs to be changed to DATETIME.
		String sqlString = String.format("CREATE TABLE %s(" //
				+ "%s BIGINT, " // ID
				+ "%s VARCHAR(%d), " // FIRST_NAME
				+ "%s VARCHAR(%d), " // LAST_NAME
				+ "%s VARCHAR(%d), " // STREET
				+ "%s VARCHAR(%d), " // CITY
				+ "%s VARCHAR(%d), " // POSTAL_CODE
				+ "%s VARCHAR(%d), " // PHONE
				+ "%s VARCHAR(%d), " // EMAIL_ADDRESS
				+ (Database.isMsSqlServer() ? "%s DATETIME, " : "%s TIMESTAMP, ") // JOINED_DATE
				+ "PRIMARY KEY (%s))", // ID
				TABLE_NAME, //
				Column.ID.name, //
				Column.FIRST_NAME.name, Column.FIRST_NAME.length, //
				Column.LAST_NAME.name, Column.LAST_NAME.length, //
				Column.STREET.name, Column.STREET.length, //
				Column.CITY.name, Column.CITY.length, //
				Column.POSTAL_CODE.name, Column.POSTAL_CODE.length, //
				Column.PHONE.name, Column.PHONE.length, //
				Column.EMAIL_ADDRESS.name, Column.EMAIL_ADDRESS.length, //
				Column.JOINED_DATE.name, //
				Column.ID.name);
		super.create(sqlString);
	}

	public void add(Customer customer) throws SQLException {
		String sqlString = String.format("INSERT INTO %s values(?, ?, ?, ?, ?, ?, ?, ?, ?)", TABLE_NAME);
		boolean result = execute(sqlString, //
				customer.getId(), //
				customer.getFirstName(), //
				customer.getLastName(), //
				customer.getStreet(), //
				customer.getCity(), //
				customer.getPostalCode(), //
				customer.getPhone(), //
				customer.getEmailAddress(), //
				Database.isMsSqlServer() ? customer.getJoinedDate() : toTimestamp(customer.getJoinedDate()));
		LOG.debug(String.format("Adding %s was %s", customer, result ? "successful" : "unsuccessful"));
	}

	/**
	 * Update the customer.
	 * 
	 * @param customer customer to update
	 * @throws SQLException SQL Exception
	 */
	public void update(Customer customer) throws SQLException {
		String sqlString = String.format("UPDATE %s SET %s=?, %s=?, %s=?, %s=?, %s=?, %s=?, %s=?, %s=? WHERE %s=?", TABLE_NAME, //
				Column.FIRST_NAME.name, //
				Column.LAST_NAME.name, //
				Column.STREET.name, //
				Column.CITY.name, //
				Column.POSTAL_CODE.name, //
				Column.PHONE.name, //
				Column.EMAIL_ADDRESS.name, //
				Column.JOINED_DATE.name, //
				Column.ID.name);
		LOG.debug("Update statment: " + sqlString);

		boolean result = execute(sqlString, //
				customer.getFirstName(), //
				customer.getLastName(), //
				customer.getStreet(), //
				customer.getCity(), //
				customer.getPostalCode(), //
				customer.getPhone(), //
				customer.getEmailAddress(), //
				Database.isMsSqlServer() ? customer.getJoinedDate() : toTimestamp(customer.getJoinedDate()), //
				customer.getId());
		LOG.debug(String.format("Updating %s was %s", customer, result ? "successful" : "unsuccessful"));
	}

	/**
	 * Delete the customer from the database.
	 * 
	 * @param customer customer to delete
	 * @throws SQLException SQL Exception
	 */
	public void delete(Customer customer) throws SQLException {
		Connection connection;
		Statement statement = null;
		try {
			connection = database.getConnection();
			statement = connection.createStatement();

			String sqlString = String.format("DELETE FROM %s WHERE %s='%s'", TABLE_NAME, Column.ID.name, customer.getId());
			LOG.debug(sqlString);
			int rowcount = statement.executeUpdate(sqlString);
			LOG.debug(String.format("Deleted %d rows", rowcount));
		} finally {
			close(statement);
		}
	}

	/**
	 * Retrieve all the customer IDs from the database
	 * 
	 * @return the list of customer IDs
	 * @throws SQLException SQL Exception
	 */
	public List<Long> getCustomerIds() throws SQLException {
		List<Long> ids = new ArrayList<>();

		String selectString = String.format("SELECT %s FROM %s", Column.ID.name, TABLE_NAME);
		// String selectString = "select id from A01019366_Customer";
		LOG.debug(selectString);

		Statement statement = null;
		ResultSet resultSet = null;
		try {
			Connection connection = database.getConnection();
			statement = connection.createStatement();
			resultSet = statement.executeQuery(selectString);

			while (resultSet.next()) {
				ids.add(resultSet.getLong(Column.ID.name));
			}

		} finally {
			close(statement);
		}

		LOG.debug(String.format("Loaded %d customer IDs from the database", ids.size()));

		return ids;
	}

	/**
	 * @param customerId customer id
	 * @return customer object
	 * @throws Exception Exception
	 */
	public Customer getCustomer(Long customerId) throws Exception {
		String sqlString = String.format("SELECT * FROM %s WHERE %s = %d", TABLE_NAME, Column.ID.name, customerId);
//		LOG.debug(sqlString);

		Statement statement = null;
		ResultSet resultSet = null;
		try {
			Connection connection = database.getConnection();
			statement = connection.createStatement();
			resultSet = statement.executeQuery(sqlString);

			int count = 0;
			while (resultSet.next()) {
				count++;
				if (count > 1) {
					throw new ApplicationException(String.format("Expected one result, got %d", count));
				}

				Timestamp timestamp = resultSet.getTimestamp(Column.JOINED_DATE.name);
				LocalDate date = timestamp.toLocalDateTime().toLocalDate();

				Customer customer = new Customer.Builder(resultSet.getInt(Column.ID.name), resultSet.getString(Column.PHONE.name)) //
						.setFirstName(resultSet.getString(Column.FIRST_NAME.name)) //
						.setLastName(resultSet.getString(Column.LAST_NAME.name)) //
						.setStreet(resultSet.getString(Column.STREET.name)) //
						.setCity(resultSet.getString(Column.CITY.name)) //
						.setPostalCode(resultSet.getString(Column.POSTAL_CODE.name)) //
						.setEmailAddress(resultSet.getString(Column.EMAIL_ADDRESS.name)) //
						.setJoinedDate(date).build();

				return customer;
			}
		} finally {
			close(statement);
		}

		return null;
	}

	public int countAllCustomers() throws Exception {
		Statement statement = null;
		int count = 0;
		try {
			Connection connection = database.getConnection();
			statement = connection.createStatement();
			// Execute a statement
			String sqlString = String.format("SELECT COUNT(*) AS total FROM %s", TABLE_NAME);
			ResultSet resultSet = statement.executeQuery(sqlString);
			if (resultSet.next()) {
				count = resultSet.getInt("total");
			}
		} finally {
			close(statement);
		}
		return count;
	}

	public enum Column {
		ID("id", 16), //
		FIRST_NAME("firstName", 20), //
		LAST_NAME("lastName", 20), //
		STREET("street", 40), //
		CITY("city", 25), //
		POSTAL_CODE("postalCode", 12), //
		PHONE("phone", 15), //
		EMAIL_ADDRESS("emailAddress", 40), //
		JOINED_DATE("joinedDate", 8); //

		String name;
		int length;

		private Column(String name, int length) {
			this.name = name;
			this.length = length;
		}

	}

}
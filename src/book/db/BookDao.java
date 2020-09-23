package book.db;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import book.ApplicationException;
import book.data.Book;
import book.io.BookReader;

/**
 * @author Terry Tianwei Lun
 *
 */
public class BookDao extends Dao {

	public static final String TABLE_NAME = DbConstants.BOOK_TABLE_NAME;

	private static final String BOOKS_DATA_FILENAME = "books500.csv";
	
	private static final Logger LOG = LogManager.getLogger();
	private static final BookDao theInstance = new BookDao();
	private static Database database;

	private BookDao() {
		super(TABLE_NAME);

		database = Database.getTheInstance();
	}

	public static BookDao getTheInstance() {
		return theInstance;
	}

	/**
	 * @throws ApplicationException Application Exception
	 */
	public void init() throws ApplicationException {
		File bookDataFile = new File(BOOKS_DATA_FILENAME);
		try {
			if (!Database.tableExists(BookDao.TABLE_NAME) || Database.dbTableDropRequested()) {
				if (Database.tableExists(BookDao.TABLE_NAME) && Database.dbTableDropRequested()) {
					drop();
				}

				create();

				LOG.debug("Inserting the books");

				if (!bookDataFile.exists()) {
					throw new ApplicationException(String.format("Required '%s' is missing.", BOOKS_DATA_FILENAME));
				}

				BookReader.read(bookDataFile, this);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new ApplicationException(e);
		}
	}

	@Override
	public void create() throws SQLException {
		LOG.debug("Creating database table " + TABLE_NAME);

		String sqlString = String.format("CREATE TABLE %s(" //
				+ "%s BIGINT, " // ID
				+ "%s VARCHAR(%d), " // ISBN
				+ "%s VARCHAR(%d), " // AUTHORS
				+ "%s INT, " // YEAR
				+ "%s VARCHAR(%d), " // TITLE
				+ "%s FLOAT, " // RATING
				+ "%s INT, " // RATING_COUNT
				+ "%s VARCHAR(%d), " // IMAGE_URL
				+ "PRIMARY KEY (%s))", // ID
				TABLE_NAME, //
				Column.ID.name, //
				Column.ISBN.name, Column.ISBN.length, //
				Column.AUTHORS.name, Column.AUTHORS.length, //
				Column.YEAR.name, //
				Column.TITLE.name, Column.TITLE.length, //
				Column.RATING.name, //
				Column.RATING_COUNT.name, //
				Column.IMAGE_URL.name, Column.IMAGE_URL.length, //
				Column.ID.name);

		super.create(sqlString);
	}

	public void add(Book book) throws SQLException {
		String sqlString = String.format("INSERT INTO %s values(?, ?, ?, ?, ?, ?, ?, ?)", TABLE_NAME);
		boolean result = execute(sqlString, //
				book.getId(), //
				book.getIsbn(), //
				book.getAuthors(), //
				book.getYear(), //
				book.getTitle(), //
				book.getRating(), //
				book.getRatingsCount(), //
				book.getImageUrl());
		LOG.debug(String.format("Adding %s was %s", book, result ? "successful" : "unsuccessful"));
	}

	/**
	 * Update the book.
	 *
	 * @param book book to update
	 * @throws SQLException SQL Exception
	 */
	public void update(Book book) throws SQLException {
		String sqlString = String.format("UPDATE %s SET %s=?, %s=?, %s=?, %s=?, %s=?, %s=?, %s=? WHERE %s=?", TABLE_NAME, //
				Column.ISBN.name, //
				Column.AUTHORS.name, //
				Column.YEAR.name, //
				Column.TITLE.name, //
				Column.RATING.name, //
				Column.RATING_COUNT.name, //
				Column.IMAGE_URL.name, //
				Column.ID.name);
		LOG.debug("Update statment: " + sqlString);

		boolean result = execute(sqlString, //
				book.getIsbn(), //
				book.getAuthors(), //
				book.getYear(), //
				book.getTitle(), //
				book.getRating(), //
				book.getRatingsCount(), //
				book.getImageUrl(), //
				book.getId());
		LOG.debug(String.format("Updating %s was %s", book, result ? "successful" : "unsuccessful"));
	}

	/**
	 * Delete the book from the database.
	 *
	 * @param book book to delete
	 * @throws SQLException SQL Exception
	 */
	public void delete(Book book) throws SQLException {
		Connection connection;
		Statement statement = null;
		try {
			connection = database.getConnection();
			statement = connection.createStatement();

			String sqlString = String.format("DELETE FROM %s WHERE %s='%s'", TABLE_NAME, Column.ID.name, book.getId());
			LOG.debug(sqlString);
			int rowcount = statement.executeUpdate(sqlString);
			LOG.debug(String.format("Deleted %d rows", rowcount));
		} finally {
			close(statement);
		}
	}

	/**
	 * Retrieve all the book IDs from the database
	 *
	 * @return the list of book IDs
	 * @throws SQLException SQL Exception
	 */
	public List<Long> getBookIds() throws SQLException {
		List<Long> ids = new ArrayList<>();

		String selectString = String.format("SELECT %s FROM %s", Column.ID.name, TABLE_NAME);
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

		LOG.debug(String.format("Loaded %d books IDs from the database", ids.size()));

		return ids;
	}

	/**
	 * @param bookId book Id
	 * @return book object
	 * @throws Exception Exception
	 */
	public Book getBook(Long bookId) throws Exception {
		String sqlString = String.format("SELECT * FROM %s WHERE %s = %d", TABLE_NAME, Column.ID.name, bookId);
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

				Book book = new Book.Builder(resultSet.getInt(Column.ID.name)). //
						setIsbn(resultSet.getString(Column.ISBN.name)). //
						setAuthors(resultSet.getString(Column.AUTHORS.name)). //
						setYear(resultSet.getInt(Column.YEAR.name)). //
						setTitle(resultSet.getString(Column.TITLE.name)). //
						setRating(resultSet.getFloat(Column.RATING.name)). // //
						setRatingsCount(resultSet.getInt(Column.RATING_COUNT.name)). //
						setImageUrl(resultSet.getString(Column.IMAGE_URL.name)).//
						build();

				return book;
			}
		} finally {
			close(statement);
		}

		return null;
	}

	/**
	 * @return book count
	 * @throws Exception Exception
	 */
	public int countAllBooks() throws Exception {
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
		ISBN("isbn", 15), //
		AUTHORS("authors", 100), //
		YEAR("publicationYear", 5), //
		TITLE("title", 100), //
		RATING("rating", 5), //
		RATING_COUNT("ratingsCount", 10), //
		IMAGE_URL("imageUrl", 100); //

		String name;
		int length;

		private Column(String name, int length) {
			this.name = name;
			this.length = length;
		}

	}

}
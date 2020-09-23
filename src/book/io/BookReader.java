package book.io;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import book.ApplicationException;
import book.data.Book;
import book.db.BookDao;

/**
 * @author Terry Tianwei Lun
 *
 */
public class BookReader extends Reader {

	// public static final String FILENAME = "books500.csv";

	private static final Logger LOG = LogManager.getLogger(BookReader.class);

	/**
	 * private constructor to prevent instantiation
	 */
	private BookReader() {
	}

	/**
	 * Read the book input data.
	 * 
	 * @return A collection of books.
	 * @throws ApplicationException Application Exception
	 */
	public static int read(File bookDataFile, BookDao dao) throws ApplicationException {
		int bookCount = 0;
		FileReader in;
		Iterable<CSVRecord> records;
		try {
			in = new FileReader(bookDataFile);
			records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(in);
		} catch (IOException e) {
			throw new ApplicationException(e);
		}

		Map<Long, Book> books = new HashMap<>();

		LOG.debug("Reading" + bookDataFile.getAbsolutePath());
		for (CSVRecord record : records) {
			String id = record.get("book_id");
			String isbn = record.get("isbn");
			String authors = record.get("authors");
			String original_publication_year = record.get("original_publication_year");
			String original_title = record.get("original_title");
			String average_rating = record.get("average_rating");
			String ratings_count = record.get("ratings_count");
			String image_url = record.get("image_url");

			Book book = new Book.Builder(Long.parseLong(id)). //
					setIsbn(isbn). //
					setAuthors(authors). //
					setYear(Integer.parseInt(original_publication_year)). //
					setTitle(original_title). //
					setRating(Float.parseFloat(average_rating)). // //
					setRatingsCount(Integer.parseInt(ratings_count)). //
					setImageUrl(image_url).//
					build();

			books.put(book.getId(), book);
			LOG.debug("Added " + book.toString() + " as " + id);
		}

		if (in != null) {
			try {
				in.close();
			} catch (IOException e) {
				throw new ApplicationException(e);
			}
		}
		List<Book> booksList = new ArrayList<>(books.values());
		try {
			for (Book book : booksList) {
				dao.add(book);
				bookCount++;
			}
		} catch (SQLException e) {
			throw new ApplicationException(e);
		}
		return bookCount;
	}
}

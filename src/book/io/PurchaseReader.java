/**
 * Project: Books

 */

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
import book.data.Purchase;
import book.db.PurchaseDao;

/**
 * @author Terry Tianwei Lun
 *
 */
public class PurchaseReader extends Reader {

	// public static final String FILENAME = "purchases.csv";

	private static final Logger LOG = LogManager.getLogger();

	/**
	 * private constructor to prevent instantiation
	 */
	private PurchaseReader() {
	}

	/**
	 * Read the inventory input data.
	 * 
	 * @return the inventory.
	 * @throws ApplicationException
	 */
	public static int read(File purchaseDataFile, PurchaseDao dao) throws ApplicationException {
		int purchaseCount = 0;
		FileReader in;
		Iterable<CSVRecord> records;
		try {
			in = new FileReader(purchaseDataFile);
			records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(in);
		} catch (IOException e) {
			throw new ApplicationException(e);
		}

		Map<Long, Purchase> purchases = new HashMap<>();
		LOG.debug("Reading" + purchaseDataFile.getAbsolutePath());
		for (CSVRecord record : records) {
			long id = Long.parseLong(record.get("id"));
			long customerId = Long.parseLong(record.get("customer_id"));
			long bookId = Long.parseLong(record.get("book_id"));
			float price = Float.parseFloat(record.get("price"));

			Purchase purchase = new Purchase.Builder(id, customerId, bookId, price).build();
			purchases.put(purchase.getId(), purchase);
			LOG.debug("Added " + purchase.toString() + " as " + purchase.getId());
		}

		LOG.debug("All purchases added");
		List<Purchase> purchasesList = new ArrayList<>(purchases.values());
		try {
			for (Purchase purchase : purchasesList) {
				dao.add(purchase);
				LOG.debug("Added " + purchase.toString() + " to the database as " + purchase.getId());
				purchaseCount++;
			}
			LOG.debug("All purchases added");
		} catch (SQLException e) {
			throw new ApplicationException(e);
		}

		return purchaseCount;
	}

}

/**
 * Project: Books
 *
 */
package book;

import java.awt.EventQueue;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;

import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import org.apache.commons.cli.ParseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.DefaultConfiguration;

import book.db.BookDao;
import book.db.CustomerDao;
import book.db.Database;
import book.db.PurchaseDao;
import book.ui.MainFrame;

/**
 * @author Terry Tianwei Lun
 *
 */
public class Books2 {

	private static final Instant startTime = Instant.now();
	private static final String LOG4J_CONFIG_FILENAME = "log4j2.xml";
	static {
		configureLogging();
	}
	private static final Logger LOG = LogManager.getLogger();

	/**
	 * 
	 * @param args
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	public static void main(String[] args) {
		
		LOG.info("Starting Books");
		Instant startTime = Instant.now();
		LOG.info(startTime);

		// start the Bookstore System
		Books2 bookStore = null;
		try {
			bookStore = new Books2();

			bookStore.run();
		} catch (

		Exception e) {
			LOG.debug(e.getMessage());
		}

		Instant endTime = Instant.now();
		LOG.info(endTime);
		LOG.info(String.format("Duration: %d ms", Duration.between(startTime, endTime).toMillis()));
		LOG.info("Books has stopped");
	}

	/**
	 * 
	 * @throws ApplicationException
	 * @throws IOException
	 * @throws FileNotFoundException
	 * @throws ParseException
	 */
	public Books2() throws ApplicationException, FileNotFoundException, IOException {
		Database.getTheInstance().init();

		CustomerDao.getTheInstance().init();
		BookDao.getTheInstance().init();
		PurchaseDao.getTheInstance().init();
	}

	/**
	 * Configures log4j2 from the external configuration file specified in LOG4J_CONFIG_FILENAME.
	 * If the configuration file isn't found then log4j2's DefaultConfiguration is used.
	 */
	private static void configureLogging() {
		ConfigurationSource source;
		try {
			source = new ConfigurationSource(new FileInputStream(LOG4J_CONFIG_FILENAME));
			Configurator.initialize(null, source);
		} catch (IOException e) {
			System.out.println(String.format("WARNING! Can't find the log4j logging configuration file %s; using DefaultConfiguration for logging.",
					LOG4J_CONFIG_FILENAME));
			Configurator.initialize(new DefaultConfiguration());
		}
	}

	/**
	 * @throws ApplicationException
	 * @throws FileNotFoundException
	 * 
	 */
	private void run() {
		try {
			CustomerDao customerDao = CustomerDao.getTheInstance();
			BookDao bookDao = BookDao.getTheInstance();
			PurchaseDao purchaseDao = PurchaseDao.getTheInstance();
			createUI(customerDao, bookDao, purchaseDao);
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error(e.getMessage());
		}
	}

	/**
	 * @return the startTime
	 */
	public static Instant getStartTime() {
		return startTime;
	}

	public static void createUI(final CustomerDao newCustomerDao, final BookDao newBookDao, final PurchaseDao newPurchaseDao) {
		LOG.debug("Creating the UI");
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
						if ("Nimbus".equals(info.getName())) {
							UIManager.setLookAndFeel(info.getClassName());
							break;
						}
					}

					MainFrame frame = new MainFrame(newCustomerDao, newBookDao, newPurchaseDao);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
					LOG.error(e.getMessage());
				}
			}
		});
	}
}

package book.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import book.data.Book;
import book.data.Customer;
import book.data.Purchase;
import book.db.BookDao;
import book.db.CustomerDao;
import book.db.PurchaseDao;
import book.io.PurchasesReport;
import book.BookOptions;

import book.Books2;
import book.data.util.Sorters.CompareBookByAuthor;
import book.data.util.Sorters.CompareBookByAuthorDescending;
import book.data.util.Sorters.CompareBookByID;
import book.data.util.Sorters.CompareCustomerByID;
import book.data.util.Sorters.CompareCustomerByJoinedDate;



/**
 * @author Terry Tianwei Lun
 *
 */
@SuppressWarnings("serial")
public class MainFrame extends JFrame {

	private static final Logger LOG = LogManager.getLogger();

	private final CustomerDao customerDao;
	private final BookDao bookDao;
	private final PurchaseDao purchaseDao;
	
	private static List<Customer> customers;
	private static List<Book> books;
	private static List<Purchase> purchases;

	/**
	 * Create the frame.
	 */
	public MainFrame(final CustomerDao newCustomerDao, final BookDao newBookDao, final PurchaseDao newPurchaseDao) {
		LOG.debug("\nCreating MainFrame");
		customerDao = newCustomerDao;
		bookDao = newBookDao;
		purchaseDao = newPurchaseDao;
		
		try {
			List<Long> customerIds;
			customerIds = customerDao.getCustomerIds();
			customers = new ArrayList<>();
			for (Long id : customerIds) {
				customers.add(customerDao.getCustomer(id));
			}

			List<Long> bookIds;
			bookIds = bookDao.getBookIds();
			books = new ArrayList<>();
			for (Long id : bookIds) {
				books.add(bookDao.getBook(id));
			}

			List<Long> purchaseIds;
			purchaseIds = purchaseDao.getPurchaseIds();
			purchases = new ArrayList<>();
			for (Long id : purchaseIds) {
				purchases.add(purchaseDao.getPurchase(id));
			}
		} catch (Exception e1) {
			LOG.error("ERROR: ", e1);
		}
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				exit();
			}
		});
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(450,194);

		setLocationRelativeTo(null);
		
		setTitle("Bookstore");
		
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);

		JMenuItem mntmDrop = new JMenuItem("Drop");
		mntmDrop.setAccelerator(KeyStroke.getKeyStroke("alt D"));
		mntmDrop.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int buttonPressed = JOptionPane.showConfirmDialog(MainFrame.this, "Do you want to delete all BookStore data?", "Delete Data",
						JOptionPane.YES_NO_OPTION);
				if (buttonPressed == JOptionPane.YES_OPTION) {
					try {
						LOG.debug("-Dropping Purchase table");
						purchaseDao.drop();
						LOG.debug("-Dropping Customer table");
						customerDao.drop();
						LOG.debug("-Dropping Book table");
						bookDao.drop();
						LOG.debug("Tables are deleted successfully!");
						JOptionPane.showMessageDialog(MainFrame.this, "Tables are deleted successfully!");
					} catch (SQLException e1) {
						LOG.error("ERROR: ", e1);
					} finally {
						exit();
					}
				}
			}
		});
		mnFile.add(mntmDrop);

	

		JMenuItem mntmQuit = new JMenuItem("Quit");
		mntmQuit.setAccelerator(KeyStroke.getKeyStroke("alt X"));
		mntmQuit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				exit();
			}
		});
		mnFile.add(mntmQuit);

		JMenu mnBooks = new JMenu("Books");
		menuBar.add(mnBooks);

		JMenuItem mntmCount = new JMenuItem("Count");
		mntmCount.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					JOptionPane.showMessageDialog(MainFrame.this, "Total Books: " + bookDao.countAllBooks(), "Count Books",
							JOptionPane.INFORMATION_MESSAGE);
				} catch (Exception e1) {
					LOG.error("ERROR: ", e1);
				}
			}
		});
		mnBooks.add(mntmCount);

	

		JCheckBoxMenuItem chckbxmntmByAuthor = new JCheckBoxMenuItem("By Author");
		mnBooks.add(chckbxmntmByAuthor);

		JCheckBoxMenuItem chckbxmntmDescending = new JCheckBoxMenuItem("Descending");
		mnBooks.add(chckbxmntmDescending);

		chckbxmntmByAuthor.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (chckbxmntmByAuthor.isSelected()) {
					chckbxmntmDescending.setEnabled(true);
				} else {
					chckbxmntmDescending.setEnabled(false);
					chckbxmntmDescending.setSelected(false);
					BookOptions.setDescending(false);
				}
			}
		});

	
		JMenuItem mntmList = new JMenuItem("List");
		mntmList.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (chckbxmntmByAuthor.isSelected()) {
					BookOptions.setByAuthor(true);
					if (chckbxmntmDescending.isSelected()) {
						BookOptions.setDescending(true);
						books.sort(new CompareBookByAuthorDescending());
					} else {
						BookOptions.setDescending(false);
						books.sort(new CompareBookByAuthor());
					}
				} else {
					BookOptions.setByAuthor(false);
					BookOptions.setDescending(false);
					books.sort(new CompareBookByID());
				}

				BookDialog.callDialog(books);
			}
		});
		mnBooks.add(mntmList);

		JMenu mnCustomers = new JMenu("Customers");
		menuBar.add(mnCustomers);

		JMenuItem mntmCount_1 = new JMenuItem("Count");
		mntmCount_1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					JOptionPane.showMessageDialog(MainFrame.this, "Total Customers: " + customerDao.countAllCustomers(), "Count Customers",
							JOptionPane.INFORMATION_MESSAGE);
				} catch (Exception e1) {
					LOG.error("ERROR: ", e1);
				}
			}
		});
		mnCustomers.add(mntmCount_1);


		JCheckBoxMenuItem chckbxmntmByJoinDate = new JCheckBoxMenuItem("By Join Date");
		mnCustomers.add(chckbxmntmByJoinDate);


		JMenuItem mntmList_1 = new JMenuItem("List");
		mntmList_1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (chckbxmntmByJoinDate.isSelected()) {
					BookOptions.setByJoinDate(true);
					customers.sort(new CompareCustomerByJoinedDate());
				} else {
					BookOptions.setByJoinDate(false);
					customers.sort(new CompareCustomerByID());
				}
				CustomerListDialog.callDialog(customers);
			}
		});
		mnCustomers.add(mntmList_1);

		JMenu mnPurchases = new JMenu("Purchases");
		menuBar.add(mnPurchases);

		JMenuItem mntmTotal = new JMenuItem("Total");
		mntmTotal.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				float total = 0.00f;
				if (purchases != null && !purchases.isEmpty()) {
					for (Purchase purchase : purchases) {
						total += purchase.getPrice();
					}
				}
				JOptionPane.showMessageDialog(MainFrame.this, String.format("Total Purchases: CAD$%,.2f", total), "Total Purchase",
						JOptionPane.INFORMATION_MESSAGE);

			}
		});
		mnPurchases.add(mntmTotal);


		JCheckBoxMenuItem chckbxmntmByLastName = new JCheckBoxMenuItem("By Last Name");
		mnPurchases.add(chckbxmntmByLastName);

		JCheckBoxMenuItem chckbxmntmByTitle = new JCheckBoxMenuItem("By Title");
		mnPurchases.add(chckbxmntmByTitle);

		JCheckBoxMenuItem chckbxmntmDescending_1 = new JCheckBoxMenuItem("Descending");
		mnPurchases.add(chckbxmntmDescending_1);

		chckbxmntmByLastName.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (chckbxmntmByLastName.isSelected()) {
					chckbxmntmByTitle.setSelected(false);
					chckbxmntmDescending_1.setEnabled(true);
				} else {
					chckbxmntmDescending_1.setEnabled(false);
					chckbxmntmDescending_1.setSelected(false);
					BookOptions.setDescending(false);
				}
			}
		});

		chckbxmntmByTitle.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (chckbxmntmByTitle.isSelected()) {
					chckbxmntmByLastName.setSelected(false);
					chckbxmntmDescending_1.setEnabled(true);
				} else {
					chckbxmntmDescending_1.setEnabled(false);
					chckbxmntmDescending_1.setSelected(false);
					BookOptions.setDescending(false);
				}
			}
		});

	

		JMenuItem mntmFilterByCustomer = new JMenuItem("Filter by Customer ID");
		mntmFilterByCustomer.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// menuBar.remove(menuPurchases);
				if (chckbxmntmByLastName.isSelected()) {
					BookOptions.setByLastName(true);
					if (chckbxmntmDescending_1.isSelected()) {
						BookOptions.setDescending(true);
					} else {
						BookOptions.setDescending(false);
					}
				} else {
					BookOptions.setByLastName(false);
				}

				if (chckbxmntmByTitle.isSelected()) {
					BookOptions.setByTitle(true);
					if (chckbxmntmDescending_1.isSelected()) {
						BookOptions.setDescending(true);
					} else {
						BookOptions.setDescending(false);
					}
				} else {
					BookOptions.setByTitle(false);
				}
				List<Long> purchaseIds;
				try {
					purchaseIds = purchaseDao.getPurchaseIds();
					purchases = new ArrayList<>();
					for (Long id : purchaseIds) {
						purchases.add(purchaseDao.getPurchase(id));
					}
				} catch (Exception e2) {
					LOG.error("ERROR: ", e2);
				}
				String answer = JOptionPane.showInputDialog(MainFrame.this, "Customer ID:", "Filter by Customer ID", JOptionPane.INFORMATION_MESSAGE);
				long filterId = 0;
				if (answer != null && answer.length() > 0) {
					try {
						filterId = Long.parseLong(answer);
					} catch (Exception e1) {
						LOG.error("ERROR: ", e1);
					}
					int count = 0;
					List<Purchase> purchaseFilter = new ArrayList<>();
					for (Purchase purchase : purchases) {
						if (String.valueOf(purchase.getCustomerId()).contains(String.valueOf(filterId))) {
							try {
								purchaseFilter.add(purchase);
							} catch (Exception e1) {
								LOG.error("ERROR: ", e1);
							}
							count++;
						}
					}
					System.out.println("The count is: " + count);
					if (count > 0) {
						MainFrame.purchases = purchaseFilter;
					}

					PurchaseDialog.callDialog(PurchasesReport.filters(purchases));
				}
			}
		});
		mnPurchases.add(mntmFilterByCustomer);

		JMenu mnHelp = new JMenu("Help");
		menuBar.add(mnHelp);

		JMenuItem mntmAbout = new JMenuItem("About");
		mntmAbout.setAccelerator(KeyStroke.getKeyStroke("F1"));
		mntmAbout.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(MainFrame.this, "Bookstore\nBy Terry Tianwei Lun", "About",
						JOptionPane.INFORMATION_MESSAGE);
			}
		});
		mnHelp.add(mntmAbout);
		JPanel contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
	}

	private void exit() {
		Instant endTime = Instant.now();
		LOG.info("End: " + endTime);
		LOG.info(String.format("Duration: %d ms", Duration.between(Books2.getStartTime(), endTime).toMillis()));
		System.exit(0);
	}

}

/**
 * Project: Books
 * File:CustomerDialog.java
 *
 */
package book.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.DateTimeException;
import java.time.format.DateTimeFormatter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import book.ApplicationException;
import book.db.CustomerDao;
import book.data.Customer;

import book.data.util.Validator;
import net.miginfocom.swing.MigLayout;

/**
 * @author Terry Tianwei Lun
 *
 */
@SuppressWarnings("serial")
public class CustomerDialog extends JDialog {
	private final JPanel contentPanel = new JPanel();
	private static final Logger LOG = LogManager.getLogger();

	private JTextField textField;//id
	private JTextField textField_1;//first name
	private JTextField textField_2;//last name
	private JTextField textField_3;//street
	private JTextField textField_4;//city
	private JTextField textField_5;//postal code
	private JTextField textField_6;//phone
	private JTextField textField_7;//email
	private JTextField textField_8;//date

	/**
	 * Create the dialog.
	 */
	public CustomerDialog() {
		setTitle("Customer Info");
		setBounds(100, 100, 450, 450);
		setLocationRelativeTo(null);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setForeground(new Color(255, 255, 255));
		contentPanel.setBackground(new Color(205, 92, 92));
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new MigLayout("", "[70.00][grow]", "[][][][][][][][][]"));
		

		JLabel lblNewLabel = new JLabel("ID");
		contentPanel.add(lblNewLabel, "cell 0 0,alignx trailing");

		textField = new JTextField();
		textField.setEnabled(false);
		textField.setEditable(false);
		contentPanel.add(textField, "cell 1 0,growx");
		textField.setColumns(10);

		JLabel lblNewLabel_1 = new JLabel("First Name");
		contentPanel.add(lblNewLabel_1, "cell 0 1,alignx trailing");

		textField_1= new JTextField();
		contentPanel.add(textField_1, "cell 1 1,growx");
		textField_1.setColumns(10);

		JLabel lblNewLabel_2 = new JLabel("Last Name");
		contentPanel.add(lblNewLabel_2, "cell 0 2,alignx trailing");

		textField_2 = new JTextField();
		contentPanel.add(textField_2, "cell 1 2,growx");
		textField_2.setColumns(10);

		JLabel lblNewLabel_3 = new JLabel("Street");
		contentPanel.add(lblNewLabel_3, "cell 0 3,alignx trailing");

		textField_3 = new JTextField();
		contentPanel.add(textField_3, "cell 1 3,growx");
		textField_3.setColumns(10);

		JLabel lblNewLabel_4 = new JLabel("City");
		contentPanel.add(lblNewLabel_4, "cell 0 4,alignx trailing");

		textField_4 = new JTextField();
		contentPanel.add(textField_4, "cell 1 4,growx");
		textField_4.setColumns(10);

		JLabel lblNewLabel_5 = new JLabel("Postal Code");
		contentPanel.add(lblNewLabel_5, "cell 0 5,alignx trailing");

		textField_5 = new JTextField();
		contentPanel.add(textField_5, "cell 1 5,growx");
		textField_5.setColumns(10);

		JLabel lblNewLabel_6 = new JLabel("Phone");
		contentPanel.add(lblNewLabel_6, "cell 0 6,alignx trailing");

		textField_6 = new JTextField();
		contentPanel.add(textField_6, "cell 1 6,growx");
		textField_6.setColumns(10);

		JLabel lblNewLabel_7= new JLabel("Email");
		contentPanel.add(lblNewLabel_7, "cell 0 7,alignx trailing");

		textField_7= new JTextField();
		contentPanel.add(textField_7, "cell 1 7,growx");
		textField_7.setColumns(10);

		JLabel lblNewLabel_8 = new JLabel("Joined Date");
		contentPanel.add(lblNewLabel_8, "cell 0 8,alignx trailing");

		textField_8 = new JTextField();
		contentPanel.add(textField_8, "cell 1 8,growx");
		textField_8.setColumns(10);
		
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);

			JButton okButton = new JButton("OK");
			okButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					CustomerDialog.this.dispose();
				}
			});
			okButton.setActionCommand("OK");
			okButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						Customer customer = null;
						long id = Integer.parseInt(textField.getText());
						String firstName = textField_1.getText();
						String lastName = textField_2.getText();
						String street = textField_3.getText();
						String city = textField_4.getText();
						String postalCode = textField_5.getText();
						String phone = textField_6.getText();
						String emailAddress = textField_7.getText();
						if (!Validator.validateEmail(emailAddress)) {
							throw new ApplicationException(String.format("Invalid email: %s", emailAddress));
						}
						String yyyymmdd = textField_8.getText();
						if (!Validator.validateJoinedDate(yyyymmdd)) {
							customer = new Customer.Builder(id, phone).setFirstName(firstName).setLastName(lastName).setStreet(street).setCity(city)
									.setPostalCode(postalCode).setEmailAddress(emailAddress)
									.setJoinedDate(CustomerDao.getTheInstance().getCustomer(id).getJoinedDate()).build();
						} else {
							int year = Integer.parseInt(yyyymmdd.substring(0, 4));
							int month = Integer.parseInt(yyyymmdd.substring(5, 7));
							int day = Integer.parseInt(yyyymmdd.substring(8, 10));

							try {
								customer = new Customer.Builder(id, phone).setFirstName(firstName).setLastName(lastName).setStreet(street)
										.setCity(city).setPostalCode(postalCode).setEmailAddress(emailAddress).setJoinedDate(year, month, day)
										.build();
							} catch (DateTimeException e1) {
								throw new ApplicationException(e1.getMessage());
							}
						}
						CustomerDao.getTheInstance().update(customer);
						CustomerListDialog.setRefresh(true);

					} catch (Exception e1) {
						LOG.error("ERROR- ", e1);
					}
					// }
					CustomerDialog.this.dispose();
				}
				// }
			});
			buttonPane.add(okButton);
			getRootPane().setDefaultButton(okButton);

			JButton cancelButton = new JButton("Cancel");
			cancelButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					CustomerDialog.this.dispose();
				}

			});
			cancelButton.setActionCommand("Cancel");
			buttonPane.add(cancelButton);
		}
	}

	/**
	 * @param customer
	 */
	public void setCustomer(Customer customer) {
		textField.setText(Long.toString(customer.getId()));
		textField_1.setText(customer.getFirstName());
		textField_2.setText(customer.getLastName());
		textField_3.setText(customer.getStreet());
		textField_4.setText(customer.getCity());
		textField_5.setText(customer.getPostalCode());
		textField_6.setText(customer.getPhone());
		textField_7.setText(customer.getEmailAddress());
		textField_8.setText(customer.getJoinedDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
	}
}

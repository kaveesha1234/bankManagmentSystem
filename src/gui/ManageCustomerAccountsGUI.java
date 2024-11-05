package gui;

import models.Customer;
import models.User;
import services.CustomerService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class ManageCustomerAccountsGUI extends JFrame {
    private User user;
    private CustomerService customerService;
    private DefaultTableModel tableModel;
    private JTable customerTable;
    private JTextField searchField;

    public ManageCustomerAccountsGUI(User user) {
        this.user = user;
        this.customerService = new CustomerService();
        setTitle("Manage Customers");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton backButton = new JButton("Back");
        searchField = new JTextField(20);
        JButton searchButton = new JButton("Search");
        JButton addButton = new JButton("Add New Customer");

        topPanel.add(backButton);
        topPanel.add(new JLabel("Search:"));
        topPanel.add(searchField);
        topPanel.add(searchButton);
        topPanel.add(addButton);

        String[] columnNames = {"Customer ID", "First Name", "Last Name", "Email", "Phone Number", "Actions"};
        tableModel = new DefaultTableModel(columnNames, 0);
        customerTable = new JTable(tableModel);
        customerTable.setRowHeight(30);
        JScrollPane scrollPane = new JScrollPane(customerTable);
        populateTable(null);

        TableColumnModel columnModel = customerTable.getColumnModel();
        TableColumn actionColumn = columnModel.getColumn(5);
        actionColumn.setCellRenderer(new ButtonRenderer());
        actionColumn.setCellEditor(new ButtonEditor(new JCheckBox()));

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openCustomerModal(null);
            }
        });

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String keyword = searchField.getText();
                populateTable(keyword);
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                BankWorkerDashboardGUI bk = new BankWorkerDashboardGUI(user);
                bk.setVisible(true);
                dispose();
            }
        });

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        setVisible(true);
    }

    private void populateTable(String keyword) {
        tableModel.setRowCount(0);

        List<Customer> customers = customerService.getAllCustomers();
        if (customers != null && !customers.isEmpty()) {
            for (Customer customer : customers) {
                if (keyword == null || customer.getFirstName().contains(keyword) || customer.getLastName().contains(keyword)) {
                    tableModel.addRow(new Object[]{
                            customer.getCustomerId(),
                            customer.getFirstName(),
                            customer.getLastName(),
                            customer.getEmail(),
                            customer.getPhoneNumber(),
                            "Edit/Delete"
                    });
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "No customers found.", "Information", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void openCustomerModal(Customer customer) {
        JDialog modal = new JDialog(this, customer == null ? "Add New Customer" : "Edit Customer", true);
        modal.setSize(600, 400);
        modal.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel firstNameLabel = new JLabel("First Name:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        modal.add(firstNameLabel, gbc);

        JTextField firstNameField = new JTextField(20);
        gbc.gridx = 1;
        modal.add(firstNameField, gbc);

        JLabel lastNameLabel = new JLabel("Last Name:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        modal.add(lastNameLabel, gbc);

        JTextField lastNameField = new JTextField(20);
        gbc.gridx = 1;
        modal.add(lastNameField, gbc);

        JLabel emailLabel = new JLabel("Email:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        modal.add(emailLabel, gbc);

        JTextField emailField = new JTextField(20);
        gbc.gridx = 1;
        modal.add(emailField, gbc);

        JLabel phoneNumberLabel = new JLabel("Phone Number:");
        gbc.gridx = 0;
        gbc.gridy = 3;
        modal.add(phoneNumberLabel, gbc);

        JTextField phoneNumberField = new JTextField(20);
        gbc.gridx = 1;
        modal.add(phoneNumberField, gbc);

        JLabel addressLabel = new JLabel("Address:");
        gbc.gridx = 0;
        gbc.gridy = 4;
        modal.add(addressLabel, gbc);

        JTextField addressField = new JTextField(20);
        gbc.gridx = 1;
        modal.add(addressField, gbc);

        JLabel dobLabel = new JLabel("Date of Birth:");
        gbc.gridx = 0;
        gbc.gridy = 5;
        modal.add(dobLabel, gbc);

        JSpinner dobSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dobSpinner, "yyyy-MM-dd");
        dobSpinner.setEditor(dateEditor);
        gbc.gridx = 1;
        modal.add(dobSpinner, gbc);

        JLabel accountTypeLabel = new JLabel("Account Type:");
        gbc.gridx = 0;
        gbc.gridy = 6;
        modal.add(accountTypeLabel, gbc);

        // Updated account types
        String[] accountTypes = {"Corporate", "Personal", "Business", "Other"};
        JComboBox<String> accountTypeComboBox = new JComboBox<>(accountTypes);
        gbc.gridx = 1;
        modal.add(accountTypeComboBox, gbc);

        JButton saveButton = new JButton("Save");
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        modal.add(saveButton, gbc);

        JButton cancelButton = new JButton("Cancel");
        gbc.gridx = 1;
        gbc.gridy = 7;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        modal.add(cancelButton, gbc);

        if (customer != null) {
            firstNameField.setText(customer.getFirstName());
            lastNameField.setText(customer.getLastName());
            emailField.setText(customer.getEmail());
            phoneNumberField.setText(customer.getPhoneNumber());
            addressField.setText(customer.getAddress());
            dobSpinner.setValue(java.sql.Date.valueOf(customer.getDateOfBirth()));
            accountTypeComboBox.setSelectedItem(customer.getAccountType());
        }

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                List<String> errorMessages = new ArrayList<>();

                String firstName = firstNameField.getText().trim();
                String lastName = lastNameField.getText().trim();
                String email = emailField.getText().trim();
                String phoneNumber = phoneNumberField.getText().trim();
                String address = addressField.getText().trim();
                String dateOfBirth = new SimpleDateFormat("yyyy-MM-dd").format(dobSpinner.getValue());
                String accountType = (String) accountTypeComboBox.getSelectedItem();

                // Validate First Name
                if (firstName.isEmpty()) {
                    errorMessages.add("First Name cannot be empty.");
                }

                // Validate Last Name
                if (lastName.isEmpty()) {
                    errorMessages.add("Last Name cannot be empty.");
                }

                // Validate Email
                if (email.isEmpty()) {
                    errorMessages.add("Email cannot be empty.");
                } else if (!email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
                    errorMessages.add("Email format is invalid.");
                }

                // Validate Phone Number
                if (phoneNumber.isEmpty()) {
                    errorMessages.add("Phone Number cannot be empty.");
                } else if (!phoneNumber.matches("\\d{10}")) { // Assuming a 10-digit phone number
                    errorMessages.add("Phone Number must be 10 digits.");
                }

                // Validate Address
                if (address.isEmpty()) {
                    errorMessages.add("Address cannot be empty.");
                }

                // Validate Date of Birth (optional: you might want to check age restrictions, etc.)
                if (dateOfBirth.isEmpty()) {
                    errorMessages.add("Date of Birth cannot be empty.");
                }

                // Validate Account Type
                if (accountType == null || accountType.isEmpty()) {
                    errorMessages.add("Account Type must be selected.");
                }

                // If there are validation errors, show them in a message dialog
                if (!errorMessages.isEmpty()) {
                    String message = String.join("\n", errorMessages);
                    JOptionPane.showMessageDialog(modal, message, "Input Validation Errors", JOptionPane.ERROR_MESSAGE);
                    return; // Do not proceed with saving
                }

                // Proceed with creating or updating the customer
                try {
                    if (customer == null) {
                        Customer newCustomer = new Customer(
                                0,
                                firstName,
                                lastName,
                                email,
                                phoneNumber,
                                address,
                                dateOfBirth,
                                accountType,
                                java.time.LocalDateTime.now().toString()
                        );
                        if (customerService.createCustomer(newCustomer)) {
                            populateTable(null);
                            JOptionPane.showMessageDialog(modal, "Customer created successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                            modal.dispose();
                        } else {
                            JOptionPane.showMessageDialog(modal, "Failed to create customer.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } else {
                        customer.setFirstName(firstName);
                        customer.setLastName(lastName);
                        customer.setEmail(email);
                        customer.setPhoneNumber(phoneNumber);
                        customer.setAddress(address);
                        customer.setDateOfBirth(dateOfBirth);
                        customer.setAccountType(accountType);
                        if (customerService.updateCustomer(customer)) {
                            populateTable(null);
                            JOptionPane.showMessageDialog(modal, "Customer updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                            modal.dispose();
                        } else {
                            JOptionPane.showMessageDialog(modal, "Failed to update customer.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(modal, "An unexpected error occurred: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                modal.dispose();
            }
        });

        modal.setLocationRelativeTo(this);
        modal.setVisible(true);
    }

    class ButtonRenderer extends JPanel implements TableCellRenderer {
        private JButton editButton = new JButton("Edit");
        private JButton deleteButton = new JButton("Delete");

        public ButtonRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
            add(editButton);
            add(deleteButton);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            return this;
        }
    }

    class ButtonEditor extends DefaultCellEditor {
        protected JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        private JButton editButton = new JButton("Edit");
        private JButton deleteButton = new JButton("Delete");

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            panel.add(editButton);
            panel.add(deleteButton);

            editButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int row = customerTable.getSelectedRow();
                    Customer customer = customerService.getCustomerById((int) customerTable.getValueAt(row, 0));
                    openCustomerModal(customer);
                    fireEditingStopped();
                }
            });

            deleteButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int row = customerTable.getSelectedRow();
                    int customerId = (int) customerTable.getValueAt(row, 0);
                    int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this customer?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        if (customerService.deleteCustomer(customerId)) {
                            populateTable(null);
                            JOptionPane.showMessageDialog(null, "Customer deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(null, "Failed to delete customer.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                    fireEditingStopped();
                }
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            return panel;
        }
    }
}

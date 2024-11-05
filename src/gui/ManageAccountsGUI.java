package gui;

import models.Account;
import models.Customer;
import models.User;
import services.AccountService;
import services.CustomerService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ManageAccountsGUI extends JFrame {
    private User user;
    private AccountService accountService;
    private CustomerService customerService;
    private DefaultTableModel tableModel;
    private JTable accountTable;
    private JTextField searchField;

    public ManageAccountsGUI(User user) {
        this.user = user;
        this.accountService = new AccountService();
        this.customerService = new CustomerService();

        setTitle("Manage Accounts");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton backButton = new JButton("Back");
        JButton addButton = new JButton("Add New Account");
        searchField = new JTextField(20);
        searchField.setToolTipText("Search Accounts");
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                searchTable();
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                searchTable();
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                searchTable();
            }
        });

        topPanel.add(backButton);
        topPanel.add(addButton);
        topPanel.add(new JLabel("Search:"));
        topPanel.add(searchField);

        String[] columnNames = {"Account Number", "Balance", "Account Type", "Status", "Actions"};
        tableModel = new DefaultTableModel(columnNames, 0);
        accountTable = new JTable(tableModel) {
            public boolean isCellEditable(int row, int column) {
                return column == 4;
            }
        };
        JScrollPane scrollPane = new JScrollPane(accountTable);
        populateTable();

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAddAccountDialog();
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

    private void populateTable() {
        tableModel.setRowCount(0);

        List<Account> accounts = accountService.getAccountsByCustomerId(user.getUserId());
        if (accounts != null && !accounts.isEmpty()) {
            for (Account account : accounts) {
                JButton editButton = new JButton("Edit");
                JButton deleteButton = new JButton("Delete");

                editButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        showEditAccountDialog(account);
                    }
                });

                deleteButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this account?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
                        if (confirm == JOptionPane.YES_OPTION) {
                            if (accountService.deleteAccount(account.getAccountNumber())) {
                                populateTable();
                            } else {
                                JOptionPane.showMessageDialog(null, "Failed to delete account.", "Error", JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    }
                });

                tableModel.addRow(new Object[]{account.getAccountNumber(), account.getBalance(), account.getAccountType(), account.getStatus(), editButton});
            }
        } else {
            JOptionPane.showMessageDialog(this, "No accounts found for this customer.", "Information", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void showAddAccountDialog() {
        JDialog dialog = new JDialog(this, "Add New Account", true);
        dialog.setSize(400, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel customerLabel = new JLabel("Customer:");
        JComboBox<Customer> customerBox = new JComboBox<>(getCustomerListModel());
        gbc.gridx = 0;
        gbc.gridy = 0;
        dialog.add(customerLabel, gbc);
        gbc.gridx = 1;
        dialog.add(customerBox, gbc);

        JLabel accountNumberLabel = new JLabel("Account Number:");
        JTextField accountNumberField = new JTextField(20);
        gbc.gridx = 0;
        gbc.gridy = 1;
        dialog.add(accountNumberLabel, gbc);
        gbc.gridx = 1;
        dialog.add(accountNumberField, gbc);

        JLabel balanceLabel = new JLabel("Balance:");
        JTextField balanceField = new JTextField(20);
        gbc.gridx = 0;
        gbc.gridy = 2;
        dialog.add(balanceLabel, gbc);
        gbc.gridx = 1;
        dialog.add(balanceField, gbc);

        JLabel accountTypeLabel = new JLabel("Account Type:");
        JComboBox<String> accountTypeBox = new JComboBox<>(new String[]{"Saving", "NRFC", "Current"});
        gbc.gridx = 0;
        gbc.gridy = 3;
        dialog.add(accountTypeLabel, gbc);
        gbc.gridx = 1;
        dialog.add(accountTypeBox, gbc);

        JLabel statusLabel = new JLabel("Status:");
        JComboBox<String> statusBox = new JComboBox<>(new String[]{"Active", "Inactive"});
        gbc.gridx = 0;
        gbc.gridy = 4;
        dialog.add(statusLabel, gbc);
        gbc.gridx = 1;
        dialog.add(statusBox, gbc);

        JButton createButton = new JButton("Create");
        JButton cancelButton = new JButton("Cancel");
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(createButton);
        buttonPanel.add(cancelButton);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        dialog.add(buttonPanel, gbc);

        createButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String accountNumber = accountNumberField.getText().trim();
                Customer selectedCustomer = (Customer) customerBox.getSelectedItem();
                String balanceStr = balanceField.getText().trim();
                String accountType = (String) accountTypeBox.getSelectedItem();
                String status = (String) statusBox.getSelectedItem();

                // Validate account number: must be 12 digits and numeric
                if (!accountNumber.matches("\\d{12}")) {
                    JOptionPane.showMessageDialog(dialog, "Account Number must be exactly 12 digits and numeric.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (selectedCustomer == null || balanceStr.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Please fill in all fields.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                double balance;
                try {
                    balance = Double.parseDouble(balanceStr);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(dialog, "Please enter a valid balance.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String currentDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

                // Save the selected customer's ID when creating the account
                Account account = new Account(0, selectedCustomer.getCustomerId(), accountNumber, balance, accountType, status, currentDate);
                if (accountService.createAccount(account)) {
                    populateTable();
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Failed to create account.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });

        dialog.setVisible(true);
    }

    private void showEditAccountDialog(Account account) {
        JDialog dialog = new JDialog(this, "Edit Account", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel balanceLabel = new JLabel("Balance:");
        JTextField balanceField = new JTextField(20);
        balanceField.setText(String.valueOf(account.getBalance()));
        gbc.gridx = 0;
        gbc.gridy = 0;
        dialog.add(balanceLabel, gbc);
        gbc.gridx = 1;
        dialog.add(balanceField, gbc);

        JLabel statusLabel = new JLabel("Status:");
        JComboBox<String> statusBox = new JComboBox<>(new String[]{"Active", "Inactive"});
        statusBox.setSelectedItem(account.getStatus());
        gbc.gridx = 0;
        gbc.gridy = 1;
        dialog.add(statusLabel, gbc);
        gbc.gridx = 1;
        dialog.add(statusBox, gbc);

        JButton updateButton = new JButton("Update");
        JButton cancelButton = new JButton("Cancel");
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(updateButton);
        buttonPanel.add(cancelButton);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        dialog.add(buttonPanel, gbc);

        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String balanceStr = balanceField.getText().trim();
                String status = (String) statusBox.getSelectedItem();

                if (balanceStr.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Please fill in all fields.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                double balance;
                try {
                    balance = Double.parseDouble(balanceStr);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(dialog, "Please enter a valid balance.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                account.setBalance(balance);
                account.setStatus(status);

                if (accountService.updateAccount(account)) {
                    populateTable();
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Failed to update account.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });

        dialog.setVisible(true);
    }

    private void searchTable() {
        String searchText = searchField.getText().trim();
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        accountTable.setRowSorter(sorter);

        if (searchText.length() == 0) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + searchText));
        }
    }

    private DefaultComboBoxModel<Customer> getCustomerListModel() {
        List<Customer> customers = customerService.getAllCustomers();
        DefaultComboBoxModel<Customer> model = new DefaultComboBoxModel<>();
        for (Customer customer : customers) {
            model.addElement(customer);
        }
        return model;
    }
}

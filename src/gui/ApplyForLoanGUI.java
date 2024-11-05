package gui;

import models.Customer;
import models.Loan;
import models.User;
import services.CustomerService;
import services.LoanService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.List;

public class ApplyForLoanGUI extends JFrame {
    private User user;
    private LoanService loanService;
    private CustomerService customerService;
    private DefaultTableModel tableModel;
    private JTable loanTable;
    private JTextField searchField;

    public ApplyForLoanGUI(User user) {
        this.user = user;
        this.loanService = new LoanService();
        this.customerService = new CustomerService();
        setTitle("Apply for Loan");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Top panel for search, apply for new loan, and back button
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton backButton = new JButton("Back");
        searchField = new JTextField(20);
        JButton searchButton = new JButton("Search");
        JButton addButton = new JButton("Apply for New Loan");

        topPanel.add(backButton);
        topPanel.add(new JLabel("Search:"));
        topPanel.add(searchField);
        topPanel.add(searchButton);
        topPanel.add(addButton);

        // Center panel for displaying loans in a table
        String[] columnNames = {"Loan ID", "Amount", "Interest Rate", "Term (Years)", "Status", "Actions"};
        tableModel = new DefaultTableModel(columnNames, 0);
        loanTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(loanTable);
        populateTable(null);

        // Action to apply for a new loan
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openLoanModal(null); // Pass null for creating a new loan
            }
        });

        // Action to search for loans
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String keyword = searchField.getText();
                populateTable(keyword);
            }
        });

        // Action for "Back" button
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
        tableModel.setRowCount(0); // Clear existing rows

        List<Loan> loans = loanService.getLoansByUserId(user.getUserId());
        if (loans != null && !loans.isEmpty()) {
            DecimalFormat df = new DecimalFormat("#.##");
            for (Loan loan : loans) {
                if (keyword == null || String.valueOf(loan.getLoanId()).contains(keyword)) {
                    JButton editButton = new JButton("Edit");
                    JButton deleteButton = new JButton("Delete");

                    editButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            openLoanModal(loan); // Open modal with the loan details for editing
                        }
                    });

                    deleteButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this loan?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
                            if (confirm == JOptionPane.YES_OPTION) {
                                if (loanService.deleteLoan(loan.getLoanId())) {
                                    populateTable(null); // Refresh the table to remove the deleted loan
                                } else {
                                    JOptionPane.showMessageDialog(null, "Failed to delete loan.", "Error", JOptionPane.ERROR_MESSAGE);
                                }
                            }
                        }
                    });

                    tableModel.addRow(new Object[]{
                            loan.getLoanId(),
                            df.format(loan.getLoanAmount()),
                            df.format(loan.getInterestRate()),
                            loan.getLoanTerm(),
                            loan.getStatus(),
                            editButton
                    });
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "No loans found.", "Information", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void openLoanModal(Loan loan) {
        JDialog modal = new JDialog(this, loan == null ? "Apply for Loan" : "Edit Loan", true);
        modal.setSize(400, 400);
        modal.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Add Customer Dropdown
        JLabel customerLabel = new JLabel("Customer:");
        JComboBox<Customer> customerBox = new JComboBox<>(getCustomerListModel());
        gbc.gridx = 0;
        gbc.gridy = 0;
        modal.add(customerLabel, gbc);
        gbc.gridx = 1;
        modal.add(customerBox, gbc);

        JLabel loanAmountLabel = new JLabel("Loan Amount:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        modal.add(loanAmountLabel, gbc);

        JTextField loanAmountField = new JTextField(20);
        gbc.gridx = 1;
        modal.add(loanAmountField, gbc);

        JLabel interestRateLabel = new JLabel("Interest Rate (%):");
        gbc.gridx = 0;
        gbc.gridy = 2;
        modal.add(interestRateLabel, gbc);

        JTextField interestRateField = new JTextField(20);
        gbc.gridx = 1;
        modal.add(interestRateField, gbc);

        JLabel loanTermLabel = new JLabel("Loan Term (Years):");
        gbc.gridx = 0;
        gbc.gridy = 3;
        modal.add(loanTermLabel, gbc);

        JTextField loanTermField = new JTextField(20);
        gbc.gridx = 1;
        modal.add(loanTermField, gbc);

        JButton saveButton = new JButton("Save");
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        modal.add(saveButton, gbc);

        JButton cancelButton = new JButton("Cancel");
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        modal.add(cancelButton, gbc);

        if (loan != null) {
            loanAmountField.setText(String.valueOf(loan.getLoanAmount()));
            interestRateField.setText(String.valueOf(loan.getInterestRate()));
            loanTermField.setText(String.valueOf(loan.getLoanTerm()));

            // Pre-select the customer in the dropdown if editing a loan
            for (int i = 0; i < customerBox.getItemCount(); i++) {
                if (customerBox.getItemAt(i).getCustomerId() == loan.getCustomerId()) {
                    customerBox.setSelectedIndex(i);
                    break;
                }
            }
        }

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    double loanAmount = Double.parseDouble(loanAmountField.getText());
                    double interestRate = Double.parseDouble(interestRateField.getText());
                    int loanTerm = Integer.parseInt(loanTermField.getText());
                    Customer selectedCustomer = (Customer) customerBox.getSelectedItem();

                    if (selectedCustomer == null) {
                        JOptionPane.showMessageDialog(modal, "Please select a customer.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    if (loan == null) {
                        Loan newLoan = new Loan(0, selectedCustomer.getCustomerId(), loanAmount, interestRate, loanTerm, "Pending", java.time.LocalDateTime.now().toString());
                        if (loanService.applyForLoan(newLoan)) {
                            populateTable(null);
                            modal.dispose();
                        } else {
                            JOptionPane.showMessageDialog(modal, "Failed to apply for loan.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } else {
                        loan.setLoanAmount(loanAmount);
                        loan.setInterestRate(interestRate);
                        loan.setLoanTerm(loanTerm);
                        loan.setCustomerId(selectedCustomer.getCustomerId());
                        if (loanService.updateLoan(loan)) {
                            populateTable(null);
                            modal.dispose();
                        } else {
                            JOptionPane.showMessageDialog(modal, "Failed to update loan.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(modal, "Please enter valid data.", "Input Error", JOptionPane.ERROR_MESSAGE);
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

    private DefaultComboBoxModel<Customer> getCustomerListModel() {
        List<Customer> customers = customerService.getAllCustomers();
        DefaultComboBoxModel<Customer> model = new DefaultComboBoxModel<>();
        for (Customer customer : customers) {
            model.addElement(customer);
        }
        return model;
    }
}

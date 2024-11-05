package gui;

import models.Account;
import models.Transaction;
import models.User;
import services.AccountService;
import services.TransactionService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class ViewTransactionsGUI extends JFrame {
    private User user;
    private TransactionService transactionService;
    private AccountService accountService;

    public ViewTransactionsGUI(User user) {
        this.user = user;
        this.transactionService = new TransactionService();
        this.accountService = new AccountService();
        setTitle("View Transactions");
        setSize(800, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        JLabel selectAccountLabel = new JLabel("Select Account:");
        selectAccountLabel.setBounds(50, 50, 200, 25);
        add(selectAccountLabel);

        JComboBox<String> accountComboBox = new JComboBox<>();
        accountComboBox.setBounds(200, 50, 300, 25);
        add(accountComboBox);

        // Populate the accountComboBox with the user's accounts

        JTable transactionsTable = new JTable();
        transactionsTable.setBounds(50, 100, 700, 200);
        add(transactionsTable);

        JButton viewButton = new JButton("View Transactions");
        viewButton.setBounds(550, 50, 150, 25);
        add(viewButton);

        viewButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedAccount = (String) accountComboBox.getSelectedItem();
                if (selectedAccount != null) {
                    Account account = accountService.getAccountByNumber(selectedAccount);
                    if (account != null) {
                        List<Transaction> transactions = transactionService.getTransactionsByAccountId(account.getAccountId());
                        updateTransactionsTable(transactionsTable, transactions);
                    }
                }
            }
        });

        JButton closeButton = new JButton("Close");
        closeButton.setBounds(350, 320, 100, 25);
        add(closeButton);

        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        setVisible(true);
    }

    private void updateTransactionsTable(JTable table, List<Transaction> transactions) {
        String[] columns = {"Transaction ID", "Type", "Amount", "Date", "Description"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);

        for (Transaction transaction : transactions) {
            Object[] row = {
                    transaction.getTransactionId(),
                    transaction.getTransactionType(),
                    transaction.getAmount(),
                    transaction.getTransactionDate(),
                    transaction.getDescription()
            };
            model.addRow(row);
        }

        table.setModel(model);
    }
}


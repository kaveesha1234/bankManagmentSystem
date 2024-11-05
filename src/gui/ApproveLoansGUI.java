package gui;

import models.Loan;
import models.User;
import services.LoanService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class ApproveLoansGUI extends JFrame {
    private User user;
    private LoanService loanService;
    private JTable loansTable;

    public ApproveLoansGUI(User user) {
        this.user = user;
        this.loanService = new LoanService();
        setTitle("Approve Loans");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Top panel with "Back" button
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton backButton = new JButton("Back");
        topPanel.add(backButton);
        add(topPanel, BorderLayout.NORTH);

        // Panel for the table of loans
        loansTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(loansTable);
        add(scrollPane, BorderLayout.CENTER);

        // Panel for approve and reject buttons
        JPanel buttonPanel = new JPanel();
        JButton approveButton = new JButton("Approve Loan");
        JButton rejectButton = new JButton("Reject Loan");
        buttonPanel.add(approveButton);
        buttonPanel.add(rejectButton);
        add(buttonPanel, BorderLayout.SOUTH);

        loadPendingLoans();

        approveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                approveOrRejectLoan("Approved");
            }
        });

        rejectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                approveOrRejectLoan("Rejected");
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

        setVisible(true);
    }

    private void loadPendingLoans() {
        List<Loan> loans = loanService.getPendingLoans();
        updateLoansTable(loans);
    }

    private void updateLoansTable(List<Loan> loans) {
        String[] columns = {"Loan ID", "Customer ID", "Amount", "Interest Rate", "Term", "Status"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);

        for (Loan loan : loans) {
            Object[] row = {
                    loan.getLoanId(),
                    loan.getCustomerId(),
                    loan.getLoanAmount(),
                    loan.getInterestRate(),
                    loan.getLoanTerm(),
                    loan.getStatus()
            };
            model.addRow(row);
        }

        loansTable.setModel(model);
    }

    private void approveOrRejectLoan(String status) {
        int selectedRow = loansTable.getSelectedRow();
        if (selectedRow != -1) {
            int loanId = (int) loansTable.getValueAt(selectedRow, 0);
            Loan loan = loanService.getLoanById(loanId);
            if (loan != null) {
                if ("Rejected".equals(status)) {
                    // If rejected, remove the loan
                    if (loanService.deleteLoan(loanId)) {
                        JOptionPane.showMessageDialog(this, "Loan rejected and removed successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to remove loan.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    // If approved, update the status
                    loan.setStatus(status);
                    if (loanService.updateLoan(loan)) {
                        JOptionPane.showMessageDialog(this, "Loan approved successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to approve loan.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
                loadPendingLoans(); // Refresh the table after approval/rejection
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a loan to " + status.toLowerCase() + ".", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

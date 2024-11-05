package gui;

import models.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class BankWorkerDashboardGUI extends JFrame {
    private User user;
    private JLabel welcomeLabel;
    private JButton manageAccountsButton;
    private JButton manageCustomerAccountsButton;
    private JButton applyForLoanButton;
    private JButton approveLoansButton;
    private JButton viewTransactionsButton;
    private JButton logoutButton;

    public BankWorkerDashboardGUI(User user) {
        this.user = user;

        setTitle("BankSysPro - Bank Worker Dashboard");
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Full-screen
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15); // Increased padding for better spacing

        // Panel for dashboard elements
        JPanel dashboardPanel = new JPanel(new GridBagLayout());
        dashboardPanel.setBackground(new Color(245, 245, 245)); // Light grey background for a modern look
        dashboardPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 210, 210), 2),
                BorderFactory.createEmptyBorder(20, 30, 20, 30)
        )); // Added a border to the form panel for a polished look

        // Welcome Label
        welcomeLabel = new JLabel("Welcome, " + user.getUsername() + "!");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 40));
        welcomeLabel.setForeground(new Color(33, 37, 41)); // Slightly darker color for better contrast
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        dashboardPanel.add(welcomeLabel, gbc);

        int gridY = 1; // To keep track of the row for placing buttons

        // Add buttons based on the user's role
        if (user.getRole().equals("Teller")) {
            // Teller can manage their accounts and view transactions
            addButton(dashboardPanel, "Manage My Accounts", new Color(0, 123, 255), e -> manageAccounts(), gbc, gridY++);
            addButton(dashboardPanel, "Apply for Loan", new Color(255, 165, 0), e -> applyForLoan(), gbc, gridY++);
            addButton(dashboardPanel, "View Transactions", new Color(138, 43, 226), e -> viewTransactions(), gbc, gridY++);
        } else if (user.getRole().equals("Account Officer")) {
            // Account Officers can manage customer accounts and view transactions
            addButton(dashboardPanel, "Manage My Accounts", new Color(0, 123, 255), e -> manageAccounts(), gbc, gridY++);
            addButton(dashboardPanel, "Manage Customer Accounts", new Color(34, 139, 34), e -> manageCustomerAccounts(), gbc, gridY++);
            addButton(dashboardPanel, "View Transactions", new Color(138, 43, 226), e -> viewTransactions(), gbc, gridY++);
        } else if (user.getRole().equals("Loan Officer")) {
            // Loan Officers can apply for and approve loans
            addButton(dashboardPanel, "Apply for Loan", new Color(255, 165, 0), e -> applyForLoan(), gbc, gridY++);
            addButton(dashboardPanel, "Approve Loans", new Color(255, 140, 0), e -> approveLoans(), gbc, gridY++);
        }

        // Logout Button (all roles can log out)
        logoutButton = new JButton("Logout");
        styleButton(logoutButton, new Color(220, 20, 60)); // Crimson
        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int confirm = JOptionPane.showConfirmDialog(BankWorkerDashboardGUI.this, "Are you sure you want to log out?", "Logout", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    new LoginGUI().setVisible(true);
                    dispose();
                }
            }
        });
        gbc.gridy = gridY;
        dashboardPanel.add(logoutButton, gbc);

        // Adding dashboard panel to main layout
        add(dashboardPanel);

        revalidate();
        repaint();

        setVisible(true);
    }

    private void addButton(JPanel panel, String text, Color backgroundColor, ActionListener action, GridBagConstraints gbc, int gridY) {
        JButton button = new JButton(text);
        styleButton(button, backgroundColor);
        button.addActionListener(action);
        gbc.gridy = gridY;
        gbc.fill = GridBagConstraints.HORIZONTAL; // Ensure buttons fill horizontally like in the login screen
        panel.add(button, gbc);
    }

    private void styleButton(JButton button, Color backgroundColor) {
        button.setFont(new Font("Arial", Font.BOLD, 25));
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createLineBorder(backgroundColor));
        button.setOpaque(true);  // Ensure the background is painted
        button.setContentAreaFilled(true);  // Ensure the content area is filled with background color
    }


    private void manageAccounts() {
        new ManageAccountsGUI(user).setVisible(true);
        dispose();
    }

    private void manageCustomerAccounts() {
        new ManageCustomerAccountsGUI(user).setVisible(true);
        dispose();
    }

    private void applyForLoan() {
        new ApplyForLoanGUI(user).setVisible(true);
        dispose();
    }

    private void approveLoans() {
        new ApproveLoansGUI(user).setVisible(true);
        dispose();
    }

    private void viewTransactions() {
        new ViewTransactionsGUI(user).setVisible(true);
        dispose();
    }
}

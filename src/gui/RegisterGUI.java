package gui;

import models.User;
import services.UserService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RegisterGUI extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JComboBox<String> roleComboBox;
    private JButton registerButton;
    private JLabel loginLabel;
    private boolean passwordVisible = false;

    private Icon eyeIcon = new ImageIcon(new ImageIcon(getClass().getResource("/assets/eye_icon.png"))
            .getImage().getScaledInstance(18, 18, Image.SCALE_SMOOTH));
    private Icon crossEyeIcon = new ImageIcon(new ImageIcon(getClass().getResource("/assets/cross_eye_icon.png"))
            .getImage().getScaledInstance(18, 18, Image.SCALE_SMOOTH));

    public RegisterGUI() {
        setTitle("BankSysPro - Register");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(245, 245, 245));
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 210, 210), 2),
                BorderFactory.createEmptyBorder(20, 30, 20, 30)
        ));

        JLabel titleLabel = new JLabel("Bank Worker Registration");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 40));
        titleLabel.setForeground(new Color(33, 37, 41));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(titleLabel, gbc);

        // Username Label and Field
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        usernameLabel.setForeground(new Color(50, 50, 50));
        formPanel.add(usernameLabel, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        usernameField = new JTextField(20);
        usernameField.setFont(new Font("Arial", Font.PLAIN, 20));
        usernameField.setPreferredSize(new Dimension(350, 35));
        usernameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        usernameField.setBackground(Color.WHITE);
        formPanel.add(usernameField, gbc);

        // Password Label and Field with Eye Icon
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        passwordLabel.setForeground(new Color(50, 50, 50));
        formPanel.add(passwordLabel, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JPanel passwordPanel = new JPanel(new BorderLayout());
        passwordPanel.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180)));
        passwordPanel.setPreferredSize(usernameField.getPreferredSize());

        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Arial", Font.PLAIN, 20));
        passwordField.setBorder(null);
        passwordField.setBackground(Color.WHITE);
        passwordPanel.add(passwordField, BorderLayout.CENTER);

        JButton togglePasswordButton = new JButton(eyeIcon);
        togglePasswordButton.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        togglePasswordButton.setContentAreaFilled(false);
        togglePasswordButton.setFocusPainted(false);
        togglePasswordButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        togglePasswordButton.addActionListener(e -> togglePasswordVisibility(togglePasswordButton, passwordField));
        passwordPanel.add(togglePasswordButton, BorderLayout.EAST);

        formPanel.add(passwordPanel, gbc);

        // Confirm Password Label and Field with Eye Icon
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        JLabel confirmPasswordLabel = new JLabel("Confirm Password:");
        confirmPasswordLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        confirmPasswordLabel.setForeground(new Color(50, 50, 50));
        formPanel.add(confirmPasswordLabel, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JPanel confirmPasswordPanel = new JPanel(new BorderLayout());
        confirmPasswordPanel.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180)));
        confirmPasswordPanel.setPreferredSize(usernameField.getPreferredSize());

        confirmPasswordField = new JPasswordField();
        confirmPasswordField.setFont(new Font("Arial", Font.PLAIN, 20));
        confirmPasswordField.setBorder(null);
        confirmPasswordField.setBackground(Color.WHITE);
        confirmPasswordPanel.add(confirmPasswordField, BorderLayout.CENTER);

        JButton toggleConfirmPasswordButton = new JButton(eyeIcon);
        toggleConfirmPasswordButton.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        toggleConfirmPasswordButton.setContentAreaFilled(false);
        toggleConfirmPasswordButton.setFocusPainted(false);
        toggleConfirmPasswordButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        toggleConfirmPasswordButton.addActionListener(e -> togglePasswordVisibility(toggleConfirmPasswordButton, confirmPasswordField));
        confirmPasswordPanel.add(toggleConfirmPasswordButton, BorderLayout.EAST);

        formPanel.add(confirmPasswordPanel, gbc);

        // Role Label and ComboBox
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        JLabel roleLabel = new JLabel("Role:");
        roleLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        roleLabel.setForeground(new Color(50, 50, 50));
        formPanel.add(roleLabel, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        roleComboBox = new JComboBox<>(new String[]{"Teller", "Account Officer", "Loan Officer"});
        roleComboBox.setFont(new Font("Arial", Font.PLAIN, 20));
        roleComboBox.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180)));
        roleComboBox.setBackground(Color.WHITE);
        roleComboBox.setPreferredSize(usernameField.getPreferredSize());
        formPanel.add(roleComboBox, gbc);

        // Register Button with Login Button Style
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        registerButton = new JButton("Register");
        registerButton.setFont(new Font("Arial", Font.BOLD, 25));
        registerButton.setBackground(new Color(0, 123, 255)); // Bright blue for a more vibrant look
        registerButton.setForeground(Color.BLACK);
        registerButton.setFocusPainted(false);
        registerButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        registerButton.setBorder(BorderFactory.createLineBorder(new Color(0, 123, 255)));
        formPanel.add(registerButton, gbc);

        // Login Link
        gbc.gridy = 6;
        loginLabel = new JLabel("<HTML><U>Already have an account? Login here</U></HTML>");
        loginLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        loginLabel.setForeground(new Color(0, 123, 255)); // Modern blue color for the link
        loginLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginLabel.setHorizontalAlignment(SwingConstants.CENTER);
        loginLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                navigateToLogin();
            }
        });
        formPanel.add(loginLabel, gbc);

        // Adding form panel to main layout
        add(formPanel);

        // Action Listeners
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registerUser();
            }
        });

        revalidate();
        repaint();

        setVisible(true);
    }

    private void togglePasswordVisibility(JButton toggleButton, JPasswordField passwordField) {
        if (passwordVisible) {
            passwordField.setEchoChar('*');
            toggleButton.setIcon(eyeIcon);
        } else {
            passwordField.setEchoChar((char) 0);
            toggleButton.setIcon(crossEyeIcon);
        }
        passwordVisible = !passwordVisible;
    }

    private void registerUser() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        String role = (String) roleComboBox.getSelectedItem();

        if (!validateUsername(username)) {
            JOptionPane.showMessageDialog(this, "Username must start with 'BS/' and be 9 characters long (e.g., BS/100012).", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        UserService userService = new UserService();
        User user = new User(0, username, password, role, null);

        if (userService.createUser(user)) {
            JOptionPane.showMessageDialog(this, "Registration successful", "Success", JOptionPane.INFORMATION_MESSAGE);
            new LoginGUI().setVisible(true);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Registration failed", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean validateUsername(String username) {
        return username.matches("^BS/\\d{6}$");
    }

    private void navigateToLogin() {
        new LoginGUI().setVisible(true);
        dispose();
    }
}

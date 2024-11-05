package gui;

import models.User;
import services.UserService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Objects;

public class LoginGUI extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JLabel registerLabel;
    private JLabel loadingLabel;
    private boolean passwordVisible = false;

    // Icons for show/hide password
    private Icon eyeIcon = new ImageIcon(new ImageIcon(getClass().getResource("/assets/eye_icon.png"))
            .getImage().getScaledInstance(18, 18, Image.SCALE_SMOOTH));
    private Icon crossEyeIcon = new ImageIcon(new ImageIcon(getClass().getResource("/assets/cross_eye_icon.png"))
            .getImage().getScaledInstance(18, 18, Image.SCALE_SMOOTH));

    public LoginGUI() {
        setTitle("BankSysPro - Login");
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Set to full-screen
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15); // Increased padding for better spacing

        // Panel for form elements
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(245, 245, 245)); // Light grey background for a modern look
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 210, 210), 2),
                BorderFactory.createEmptyBorder(20, 30, 20, 30)
        )); // Added a border to the form panel for a polished look

        // Title
        JLabel titleLabel = new JLabel("Welcome to BankSysPro");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 40));
        titleLabel.setForeground(new Color(33, 37, 41)); // Slightly darker color for better contrast
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(titleLabel, gbc);

        // Username Label and Field
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        usernameLabel.setForeground(new Color(50, 50, 50));
        formPanel.add(usernameLabel, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        usernameField = new JTextField(20);
        usernameField.setFont(new Font("Arial", Font.PLAIN, 20));
        usernameField.setPreferredSize(new Dimension(350, 35)); // Slightly wider and taller input field
        usernameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        usernameField.setBackground(Color.WHITE);
        formPanel.add(usernameField, gbc);

        // Password Label and Field with Eye Icon
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        passwordLabel.setForeground(new Color(50, 50, 50));
        formPanel.add(passwordLabel, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // JPanel to hold password field and eye icon
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
        togglePasswordButton.addActionListener(e -> togglePasswordVisibility(togglePasswordButton));
        passwordPanel.add(togglePasswordButton, BorderLayout.EAST);

        formPanel.add(passwordPanel, gbc);

        // Login Button
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        loginButton = new JButton("Login");
        loginButton.setFont(new Font("Arial", Font.BOLD, 25));
        loginButton.setBackground(new Color(0, 123, 255)); // Bright blue for a more vibrant look
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginButton.setBorder(BorderFactory.createLineBorder(new Color(0, 123, 255)));
        formPanel.add(loginButton, gbc);

        // Register Link
        gbc.gridy = 4;
        registerLabel = new JLabel("<HTML><U>Don't have an account? Register now</U></HTML>");
        registerLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        registerLabel.setForeground(new Color(0, 123, 255)); // Modern blue color for the link
        registerLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        formPanel.add(registerLabel, gbc);

        registerLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                navigateToRegister();
            }
        });

        // Loading Label
        gbc.gridy = 5;
        loadingLabel = new JLabel("");
        loadingLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        loadingLabel.setForeground(new Color(255, 69, 0)); // OrangeRed
        formPanel.add(loadingLabel, gbc);

        // Right side for image
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = GridBagConstraints.REMAINDER;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.BOTH;
        ImageIcon imageIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/assets/bl.png")));
        Image image = imageIcon.getImage(); // Transform it
        Image scaledImage = image.getScaledInstance(450, 350, Image.SCALE_SMOOTH); // Scale it to desired size
        JLabel imageLabel = new JLabel(new ImageIcon(scaledImage));
        formPanel.add(imageLabel, gbc);

        // Adding form panel and image to main layout
        add(formPanel);

        // Action Listeners
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showLoading("Authenticating...");
                authenticateUser();
            }
        });

        // Adding animation for form components
        animateComponents();

        revalidate();
        repaint();

        setVisible(true);
    }

    private void togglePasswordVisibility(JButton toggleButton) {
        if (passwordVisible) {
            passwordField.setEchoChar('*');
            toggleButton.setIcon(eyeIcon); // Set to eye icon
        } else {
            passwordField.setEchoChar((char) 0);
            toggleButton.setIcon(crossEyeIcon); // Set to cross-eye icon
        }
        passwordVisible = !passwordVisible;
    }

    private void authenticateUser() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        UserService userService = new UserService();
        User user = userService.authenticateUser(username, password);

        if (user != null) {

                new BankWorkerDashboardGUI(user).setVisible(true);

            dispose();
        } else {
            showLoading("");
            JOptionPane.showMessageDialog(this, "Invalid username or password", "Login Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void navigateToRegister() {
        new RegisterGUI().setVisible(true);
        dispose();
    }

    private void showLoading(String message) {
        loadingLabel.setText(message);
    }

    private void animateComponents() {
        Timer timer = new Timer(50, new ActionListener() {
            private float opacity = 0;

            @Override
            public void actionPerformed(ActionEvent e) {
                opacity += 0.05;
                if (opacity > 1) {
                    opacity = 1;
                    ((Timer) e.getSource()).stop();
                }
                usernameField.setOpaque(true);
                passwordField.setOpaque(true);
                loginButton.setOpaque(true);
                registerLabel.setOpaque(true);
                usernameField.repaint();
                passwordField.repaint();
                loginButton.repaint();
                registerLabel.repaint();
            }
        });
        timer.start();
    }
}

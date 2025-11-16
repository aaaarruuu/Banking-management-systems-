package BankingManagementSystem;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class BankingAppGUI extends JFrame {
    private static final String url = "jdbc:mysql://localhost:3306/banking_systems";
    private static final String username = "root";
    private static final String password = "@Aryan1310";

    private Connection connection;
    private User user;
    private Accounts accounts;
    private AccountManager accountManager;

    private JPanel mainPanel;
    private CardLayout cardLayout;

    public BankingAppGUI() {
        // Initialize database connection
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(url, username, password);

            user = new User(connection, null);
            accounts = new Accounts(connection, null);
            accountManager = new AccountManager(connection, null);

        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(this, "Database Driver not found: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database connection failed: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        // Setup frame
        setTitle("Banking Management System");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create card layout for switching panels
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Add all panels
        mainPanel.add(createWelcomePanel(), "WELCOME");
        mainPanel.add(createRegisterPanel(), "REGISTER");
        mainPanel.add(createLoginPanel(), "LOGIN");

        add(mainPanel);
        setVisible(true);
    }

    private JPanel createWelcomePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(231, 221, 72));

        // Title
        JLabel titleLabel = new JLabel("BANKING MANAGEMENT SYSTEM", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(0, 51, 102));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(50, 0, 30, 0));
        panel.add(titleLabel, BorderLayout.NORTH);

        // Buttons panel
        JPanel buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setBackground(new Color(240, 248, 255));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JButton registerBtn = createStyledButton("Register New Account", new Color(46, 125, 50));
        JButton loginBtn = createStyledButton("Login to Account", new Color(25, 118, 210));
        JButton exitBtn = createStyledButton("Exit", new Color(211, 47, 47));

        gbc.gridx = 0;
        gbc.gridy = 0;
        buttonPanel.add(registerBtn, gbc);

        gbc.gridy = 1;
        buttonPanel.add(loginBtn, gbc);

        gbc.gridy = 2;
        buttonPanel.add(exitBtn, gbc);

        panel.add(buttonPanel, BorderLayout.CENTER);

        // Action listeners
        registerBtn.addActionListener(e -> cardLayout.show(mainPanel, "REGISTER"));
        loginBtn.addActionListener(e -> cardLayout.show(mainPanel, "LOGIN"));
        exitBtn.addActionListener(e -> {
            try {
                if (connection != null) connection.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            System.exit(0);
        });

        return panel;
    }

    private JPanel createRegisterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(231, 221, 72));

        // Title
        JLabel titleLabel = new JLabel("User Registration", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(new Color(0, 51, 102));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(30, 0, 20, 0));
        panel.add(titleLabel, BorderLayout.NORTH);

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(240, 248, 255));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel nameLabel = new JLabel("Full Name:");
        JTextField nameField = new JTextField(20);
        JLabel emailLabel = new JLabel("Email:");
        JTextField emailField = new JTextField(20);
        JLabel passwordLabel = new JLabel("Password:");
        JPasswordField passwordField = new JPasswordField(20);

        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(nameLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(emailLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(emailField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(passwordLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(passwordField, gbc);

        panel.add(formPanel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(new Color(240, 248, 255));
        JButton registerBtn = createStyledButton("Register", new Color(46, 125, 50));
        JButton backBtn = createStyledButton("Back", new Color(117, 117, 117));

        buttonPanel.add(registerBtn);
        buttonPanel.add(backBtn);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        // Action listeners
        registerBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String pass = new String(passwordField.getPassword());

            if (name.isEmpty() || email.isEmpty() || pass.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all fields!",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (user.user_exist(email)) {
                JOptionPane.showMessageDialog(this, "User already exists for this email!",
                        "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                if (user.registerGUI(name, email, pass)) {
                    JOptionPane.showMessageDialog(this, "Registration Successful!",
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                    nameField.setText("");
                    emailField.setText("");
                    passwordField.setText("");
                    cardLayout.show(mainPanel, "WELCOME");
                } else {
                    JOptionPane.showMessageDialog(this, "Registration Failed!",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        backBtn.addActionListener(e -> {
            nameField.setText("");
            emailField.setText("");
            passwordField.setText("");
            cardLayout.show(mainPanel, "WELCOME");
        });

        return panel;
    }

    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(231, 221, 72));

        // Title
        JLabel titleLabel = new JLabel("User Login", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(new Color(0, 51, 102));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(30, 0, 20, 0));
        panel.add(titleLabel, BorderLayout.NORTH);

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(240, 248, 255));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel emailLabel = new JLabel("Email:");
        JTextField emailField = new JTextField(20);
        JLabel passwordLabel = new JLabel("Password:");
        JPasswordField passwordField = new JPasswordField(20);

        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(emailLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(emailField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(passwordLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(passwordField, gbc);

        panel.add(formPanel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(new Color(240, 248, 255));
        JButton loginBtn = createStyledButton("Login", new Color(25, 118, 210));
        JButton backBtn = createStyledButton("Back", new Color(117, 117, 117));

        buttonPanel.add(loginBtn);
        buttonPanel.add(backBtn);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        // Action listeners
        loginBtn.addActionListener(e -> {
            String email = emailField.getText().trim();
            String pass = new String(passwordField.getPassword());

            if (email.isEmpty() || pass.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all fields!",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String loggedInEmail = user.loginGUI(email, pass);
            if (loggedInEmail != null) {
                emailField.setText("");
                passwordField.setText("");

                // Check if account exists
                if (!accounts.account_exist(loggedInEmail)) {
                    showAccountCreationDialog(loggedInEmail);
                } else {
                    long accountNumber = accounts.getAccount_number(loggedInEmail);
                    showAccountDashboard(loggedInEmail, accountNumber);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Incorrect Email or Password!",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        backBtn.addActionListener(e -> {
            emailField.setText("");
            passwordField.setText("");
            cardLayout.show(mainPanel, "WELCOME");
        });

        return panel;
    }

    private void showAccountCreationDialog(String email) {
        JDialog dialog = new JDialog(this, "Create Bank Account", true);
        dialog.setSize(400, 350);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(240, 248, 255));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("Open New Bank Account");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);

        gbc.gridwidth = 1;
        JLabel nameLabel = new JLabel("Full Name:");
        JTextField nameField = new JTextField(15);
        JLabel balanceLabel = new JLabel("Initial Amount:");
        JTextField balanceField = new JTextField(15);
        JLabel pinLabel = new JLabel("Security Pin:");
        JPasswordField pinField = new JPasswordField(15);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(nameLabel, gbc);
        gbc.gridx = 1;
        panel.add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(balanceLabel, gbc);
        gbc.gridx = 1;
        panel.add(balanceField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(pinLabel, gbc);
        gbc.gridx = 1;
        panel.add(pinField, gbc);

        JButton createBtn = createStyledButton("Create Account", new Color(46, 125, 50));
        JButton skipBtn = createStyledButton("Skip", new Color(117, 117, 117));

        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(createBtn, gbc);
        gbc.gridx = 1;
        panel.add(skipBtn, gbc);

        dialog.add(panel);

        createBtn.addActionListener(e -> {
            try {
                String name = nameField.getText().trim();
                double balance = Double.parseDouble(balanceField.getText().trim());
                String pin = new String(pinField.getPassword());

                if (name.isEmpty() || pin.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Please fill all fields!",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                long accountNumber = accounts.open_accountGUI(email, name, balance, pin);
                JOptionPane.showMessageDialog(dialog,
                        "Account Created Successfully!\nYour Account Number: " + accountNumber,
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                showAccountDashboard(email, accountNumber);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid amount entered!",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        skipBtn.addActionListener(e -> {
            dialog.dispose();
            cardLayout.show(mainPanel, "WELCOME");
        });

        dialog.setVisible(true);
    }

    private void showAccountDashboard(String email, long accountNumber) {
        JDialog dashboard = new JDialog(this, "Account Dashboard", true);
        dashboard.setSize(500, 400);
        dashboard.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(240, 248, 255));

        // Header
        JLabel headerLabel = new JLabel("Account: " + accountNumber, SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        headerLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        panel.add(headerLabel, BorderLayout.NORTH);

        // Button panel
        JPanel buttonPanel = new JPanel(new GridLayout(5, 1, 10, 10));
        buttonPanel.setBackground(new Color(240, 248, 255));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        JButton creditBtn = createStyledButton("Credit Money", new Color(46, 125, 50));
        JButton debitBtn = createStyledButton("Debit Money", new Color(255, 152, 0));
        JButton transferBtn = createStyledButton("Transfer Money", new Color(156, 39, 176));
        JButton balanceBtn = createStyledButton("Check Balance", new Color(25, 118, 210));
        JButton logoutBtn = createStyledButton("Logout", new Color(211, 47, 47));

        buttonPanel.add(creditBtn);
        buttonPanel.add(debitBtn);
        buttonPanel.add(transferBtn);
        buttonPanel.add(balanceBtn);
        buttonPanel.add(logoutBtn);

        panel.add(buttonPanel, BorderLayout.CENTER);
        dashboard.add(panel);

        // Action listeners
        creditBtn.addActionListener(e -> showCreditMoneyDialog(accountNumber));
        debitBtn.addActionListener(e -> showDebitMoneyDialog(accountNumber));
        transferBtn.addActionListener(e -> showTransferMoneyDialog(accountNumber));
        balanceBtn.addActionListener(e -> showBalanceDialog(accountNumber));
        logoutBtn.addActionListener(e -> {
            dashboard.dispose();
            cardLayout.show(mainPanel, "WELCOME");
        });

        dashboard.setVisible(true);
    }

    private void showCreditMoneyDialog(long accountNumber) {
        JDialog dialog = new JDialog(this, "Credit Money", true);
        dialog.setSize(350, 250);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(231, 221, 72));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel amountLabel = new JLabel("Amount:");
        JTextField amountField = new JTextField(15);
        JLabel pinLabel = new JLabel("Security Pin:");
        JPasswordField pinField = new JPasswordField(15);

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(amountLabel, gbc);
        gbc.gridx = 1;
        panel.add(amountField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(pinLabel, gbc);
        gbc.gridx = 1;
        panel.add(pinField, gbc);

        JButton submitBtn = createStyledButton("Submit", new Color(46, 125, 50));
        JButton cancelBtn = createStyledButton("Cancel", new Color(117, 117, 117));

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(submitBtn, gbc);
        gbc.gridx = 1;
        panel.add(cancelBtn, gbc);

        dialog.add(panel);

        submitBtn.addActionListener(e -> {
            try {
                double amount = Double.parseDouble(amountField.getText().trim());
                String pin = new String(pinField.getPassword());

                if (accountManager.credit_moneyGUI(accountNumber, amount, pin)) {
                    JOptionPane.showMessageDialog(dialog, "Rs." + amount + " credited successfully!",
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Transaction failed! Check your pin.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid amount!",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelBtn.addActionListener(e -> dialog.dispose());
        dialog.setVisible(true);
    }

    private void showDebitMoneyDialog(long accountNumber) {
        JDialog dialog = new JDialog(this, "Debit Money", true);
        dialog.setSize(350, 250);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(251, 70, 4));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel amountLabel = new JLabel("Amount:");
        JTextField amountField = new JTextField(15);
        JLabel pinLabel = new JLabel("Security Pin:");
        JPasswordField pinField = new JPasswordField(15);

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(amountLabel, gbc);
        gbc.gridx = 1;
        panel.add(amountField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(pinLabel, gbc);
        gbc.gridx = 1;
        panel.add(pinField, gbc);

        JButton submitBtn = createStyledButton("Submit", new Color(255, 152, 0));
        JButton cancelBtn = createStyledButton("Cancel", new Color(117, 117, 117));

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(submitBtn, gbc);
        gbc.gridx = 1;
        panel.add(cancelBtn, gbc);

        dialog.add(panel);

        submitBtn.addActionListener(e -> {
            try {
                double amount = Double.parseDouble(amountField.getText().trim());
                String pin = new String(pinField.getPassword());

                String result = accountManager.debit_moneyGUI(accountNumber, amount, pin);
                if (result.equals("SUCCESS")) {
                    JOptionPane.showMessageDialog(dialog, "Rs." + amount + " debited successfully!",
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, result,
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid amount!",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelBtn.addActionListener(e -> dialog.dispose());
        dialog.setVisible(true);
    }

    private void showTransferMoneyDialog(long senderAccountNumber) {
        JDialog dialog = new JDialog(this, "Transfer Money", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(10, 135, 244));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel receiverLabel = new JLabel("Receiver Account:");
        JTextField receiverField = new JTextField(15);
        JLabel amountLabel = new JLabel("Amount:");
        JTextField amountField = new JTextField(15);
        JLabel pinLabel = new JLabel("Security Pin:");
        JPasswordField pinField = new JPasswordField(15);

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(receiverLabel, gbc);
        gbc.gridx = 1;
        panel.add(receiverField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(amountLabel, gbc);
        gbc.gridx = 1;
        panel.add(amountField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(pinLabel, gbc);
        gbc.gridx = 1;
        panel.add(pinField, gbc);

        JButton submitBtn = createStyledButton("Transfer", new Color(156, 39, 176));
        JButton cancelBtn = createStyledButton("Cancel", new Color(250, 5, 5));

        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(submitBtn, gbc);
        gbc.gridx = 1;
        panel.add(cancelBtn, gbc);

        dialog.add(panel);

        submitBtn.addActionListener(e -> {
            try {
                long receiverAccount = Long.parseLong(receiverField.getText().trim());
                double amount = Double.parseDouble(amountField.getText().trim());
                String pin = new String(pinField.getPassword());

                String result = accountManager.transfer_moneyGUI(senderAccountNumber, receiverAccount, amount, pin);
                if (result.equals("SUCCESS")) {
                    JOptionPane.showMessageDialog(dialog,
                            "Rs." + amount + " transferred successfully!",
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, result,
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid input!",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelBtn.addActionListener(e -> dialog.dispose());
        dialog.setVisible(true);
    }

    private void showBalanceDialog(long accountNumber) {
        JDialog dialog = new JDialog(this, "Check Balance", true);
        dialog.setSize(350, 200);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(21, 244, 5));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel pinLabel = new JLabel("Security Pin:");
        JPasswordField pinField = new JPasswordField(15);

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(pinLabel, gbc);
        gbc.gridx = 1;
        panel.add(pinField, gbc);

        JButton checkBtn = createStyledButton("Check Balance", new Color(25, 118, 210));
        JButton cancelBtn = createStyledButton("Cancel", new Color(117, 117, 117));

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(checkBtn, gbc);
        gbc.gridx = 1;
        panel.add(cancelBtn, gbc);

        dialog.add(panel);

        checkBtn.addActionListener(e -> {
            String pin = new String(pinField.getPassword());
            Double balance = accountManager.getBalanceGUI(accountNumber, pin);

            if (balance != null) {
                JOptionPane.showMessageDialog(dialog,
                        "Your Balance: Rs." + String.format("%.2f", balance),
                        "Balance", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, "Invalid Pin!",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelBtn.addActionListener(e -> dialog.dispose());
        dialog.setVisible(true);
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(200, 40));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BankingAppGUI());
    }
}
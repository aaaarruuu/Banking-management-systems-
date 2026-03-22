package BankingManagementSystem;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.List;

public class BankingAppGUI extends JFrame {

    private static final String DB_URL  = "jdbc:mysql://localhost:3306/banking_systems";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "@Aryan1310";

    private static final Color C_NAVY   = new Color(18,  40,  80);
    private static final Color C_BLUE   = new Color(30, 100, 220);
    private static final Color C_BG     = new Color(240, 243, 250);
    private static final Color C_WHITE  = Color.WHITE;
    private static final Color C_GREEN  = new Color(34, 139,  34);
    private static final Color C_ORANGE = new Color(220, 110,   0);
    private static final Color C_PURPLE = new Color(120,  40, 170);
    private static final Color C_TEAL   = new Color(  0, 148, 133);
    private static final Color C_RED    = new Color(200,  40,  40);
    private static final Color C_GRAY   = new Color( 95,  99, 110);
    private static final Color C_BORDER = new Color(210, 215, 228);
    private static final Color C_TEXT   = new Color( 25,  28,  50);
    private static final Color C_MUTED  = new Color(110, 115, 135);

    private static final Font F_TITLE = new Font("SansSerif", Font.BOLD,  22);
    private static final Font F_HEAD  = new Font("SansSerif", Font.BOLD,  15);
    private static final Font F_LABEL = new Font("SansSerif", Font.PLAIN, 13);
    private static final Font F_BTN   = new Font("SansSerif", Font.BOLD,  13);
    private static final Font F_SMALL = new Font("SansSerif", Font.PLAIN, 12);
    private static final Font F_FIELD = new Font("SansSerif", Font.PLAIN, 13);

    private Connection     connection;
    private User           user;
    private Accounts       accounts;
    private AccountManager accountManager;

    private CardLayout cardLayout;
    private JPanel     mainPanel;

    public BankingAppGUI() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection     = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            user           = new User(connection, null);
            accounts       = new Accounts(connection, null);
            accountManager = new AccountManager(connection, null);
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, "Driver not found:\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "DB connection failed:\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        setTitle("My Bank — Banking Management System");
        setSize(640, 500);
        setMinimumSize(new Dimension(520, 420));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel  = new JPanel(cardLayout);
        mainPanel.setBackground(C_BG);

        mainPanel.add(buildWelcome(),  "WELCOME");
        mainPanel.add(buildRegister(), "REGISTER");
        mainPanel.add(buildLogin(),    "LOGIN");

        add(mainPanel);
        setVisible(true);
    }

    private JPanel buildWelcome() {
        JPanel outer = centeredOuter();

        JPanel card = card(420, 370);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(42, 56, 42, 56));

        JLabel icon = centered(new JLabel("🏦"));
        icon.setFont(new Font("SansSerif", Font.PLAIN, 44));

        JLabel title = centered(new JLabel("My Bank"));
        title.setFont(new Font("SansSerif", Font.BOLD, 30));
        title.setForeground(C_NAVY);

        JLabel sub = centered(new JLabel("Banking Management System"));
        sub.setFont(F_SMALL);
        sub.setForeground(C_MUTED);

        JButton regBtn  = btn("Register New Account", C_GREEN,  230, 42);
        JButton logBtn  = btn("Login to Account",     C_BLUE,   230, 42);
        JButton exitBtn = btn("Exit",                 C_GRAY,   230, 42);
        regBtn.setAlignmentX(CENTER_ALIGNMENT);
        logBtn.setAlignmentX(CENTER_ALIGNMENT);
        exitBtn.setAlignmentX(CENTER_ALIGNMENT);

        card.add(icon);
        card.add(vgap(10));
        card.add(title);
        card.add(vgap(4));
        card.add(sub);
        card.add(vgap(34));
        card.add(regBtn);
        card.add(vgap(10));
        card.add(logBtn);
        card.add(vgap(10));
        card.add(exitBtn);

        outer.add(card);

        regBtn.addActionListener(e -> cardLayout.show(mainPanel, "REGISTER"));
        logBtn.addActionListener(e -> cardLayout.show(mainPanel, "LOGIN"));
        exitBtn.addActionListener(e -> {
            try { if (connection != null) connection.close(); } catch (SQLException ignored) {}
            System.exit(0);
        });
        return outer;
    }

    private JPanel buildRegister() {
        JPanel outer = centeredOuter();

        JPanel card = card(470, 410);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(34, 46, 34, 46));

        JPanel top = new JPanel();
        top.setOpaque(false);
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        JLabel t = centered(new JLabel("Create Account"));
        t.setFont(F_TITLE); t.setForeground(C_NAVY);
        JLabel s = centered(new JLabel("Fill in your details below"));
        s.setFont(F_SMALL); s.setForeground(C_MUTED);
        top.add(t); top.add(vgap(4)); top.add(s); top.add(vgap(22));
        card.add(top, BorderLayout.NORTH);

        JTextField     nameField  = field("Full name");
        JTextField     emailField = field("Email address");
        JPasswordField passField  = passField("Password (min 6 characters)");

        JPanel form = new JPanel(new GridLayout(3, 2, 14, 14));
        form.setOpaque(false);
        form.add(lbl("Full Name"));  form.add(nameField);
        form.add(lbl("Email"));      form.add(emailField);
        form.add(lbl("Password"));   form.add(passField);
        card.add(form, BorderLayout.CENTER);

        JButton regBtn  = btn("Register", C_GREEN, 145, 40);
        JButton backBtn = btn("← Back",   C_GRAY,  110, 40);
        JPanel  bRow    = btnRow(regBtn, backBtn);
        bRow.setBorder(new EmptyBorder(20, 0, 0, 0));
        card.add(bRow, BorderLayout.SOUTH);

        outer.add(card);

        regBtn.addActionListener(e -> {
            String name  = nameField.getText().trim();
            String email = emailField.getText().trim();
            String pass  = new String(passField.getPassword());
            if (name.isEmpty() || email.isEmpty() || pass.isEmpty()) { err(card, "Please fill in all fields."); return; }
            if (!email.contains("@") || !email.contains(".")) { err(card, "Please enter a valid email address."); return; }
            if (pass.length() < 6) { err(card, "Password must be at least 6 characters."); return; }
            if (user.user_exist(email)) { err(card, "An account already exists for this email."); return; }
            if (user.registerGUI(name, email, pass)) {
                ok(card, "Registration successful! You can now log in.");
                nameField.setText(""); emailField.setText(""); passField.setText("");
                cardLayout.show(mainPanel, "WELCOME");
            } else {
                err(card, "Registration failed. Please try again.");
            }
        });

        backBtn.addActionListener(e -> {
            nameField.setText(""); emailField.setText(""); passField.setText("");
            cardLayout.show(mainPanel, "WELCOME");
        });
        return outer;
    }

    private JPanel buildLogin() {
        JPanel outer = centeredOuter();

        JPanel card = card(430, 340);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(34, 46, 34, 46));

        JPanel top = new JPanel();
        top.setOpaque(false);
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        JLabel t = centered(new JLabel("Welcome Back"));
        t.setFont(F_TITLE); t.setForeground(C_NAVY);
        JLabel s = centered(new JLabel("Sign in to your account"));
        s.setFont(F_SMALL); s.setForeground(C_MUTED);
        top.add(t); top.add(vgap(4)); top.add(s); top.add(vgap(22));
        card.add(top, BorderLayout.NORTH);

        JTextField     emailField = field("Email address");
        JPasswordField passField  = passField("Password");

        JPanel form = new JPanel(new GridLayout(2, 2, 14, 14));
        form.setOpaque(false);
        form.add(lbl("Email"));    form.add(emailField);
        form.add(lbl("Password")); form.add(passField);
        card.add(form, BorderLayout.CENTER);

        JButton loginBtn = btn("Login",  C_BLUE, 135, 40);
        JButton backBtn  = btn("← Back", C_GRAY, 110, 40);
        JPanel  bRow     = btnRow(loginBtn, backBtn);
        bRow.setBorder(new EmptyBorder(20, 0, 0, 0));
        card.add(bRow, BorderLayout.SOUTH);

        outer.add(card);

        Runnable doLogin = () -> {
            String email = emailField.getText().trim();
            String pass  = new String(passField.getPassword());
            if (email.isEmpty() || pass.isEmpty()) { err(card, "Please fill in both fields."); return; }
            String logged = user.loginGUI(email, pass);
            if (logged != null) {
                emailField.setText(""); passField.setText("");
                if (!accounts.account_exist(logged)) {
                    openAccountDialog(logged);
                } else {
                    long acc = accounts.getAccount_number(logged);
                    dashboard(logged, acc);
                }
            } else {
                err(card, "Incorrect email or password.");
            }
        };

        loginBtn.addActionListener(e -> doLogin.run());
        passField.addActionListener(e -> doLogin.run());
        backBtn.addActionListener(e -> {
            emailField.setText(""); passField.setText("");
            cardLayout.show(mainPanel, "WELCOME");
        });
        return outer;
    }

    private void openAccountDialog(String email) {
        JDialog dlg = dlg("Open Bank Account", 420, 360);
        dlg.setLayout(new BorderLayout());

        JPanel content = new JPanel();
        content.setBackground(C_WHITE);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(new EmptyBorder(26, 38, 26, 38));

        JLabel t = centered(new JLabel("Open Bank Account"));
        t.setFont(F_HEAD); t.setForeground(C_NAVY);
        JLabel s = centered(new JLabel("Enter your account details"));
        s.setFont(F_SMALL); s.setForeground(C_MUTED);

        content.add(t); content.add(vgap(4)); content.add(s); content.add(vgap(20));

        JTextField     nameField    = field("Full name");
        JTextField     balanceField = field("Initial deposit amount");
        JPasswordField pinField     = passField("4-digit security PIN");

        JPanel form = new JPanel(new GridLayout(3, 2, 12, 12));
        form.setOpaque(false);
        form.add(lbl("Full Name"));             form.add(nameField);
        form.add(lbl("Initial Deposit (Rs.)")); form.add(balanceField);
        form.add(lbl("Security PIN"));          form.add(pinField);
        form.setMaximumSize(new Dimension(Integer.MAX_VALUE, form.getPreferredSize().height));
        content.add(form);
        content.add(vgap(20));

        JButton createBtn = btn("Create Account", C_GREEN, 160, 40);
        JButton skipBtn   = btn("Skip",           C_GRAY,  90,  40);
        content.add(btnRow(createBtn, skipBtn));

        dlg.add(content, BorderLayout.CENTER);

        createBtn.addActionListener(e -> {
            String name   = nameField.getText().trim();
            String pin    = new String(pinField.getPassword());
            String balStr = balanceField.getText().trim();
            if (name.isEmpty() || pin.isEmpty() || balStr.isEmpty()) { err(dlg, "Please fill in all fields."); return; }
            if (pin.length() < 4) { err(dlg, "PIN must be at least 4 digits."); return; }
            double balance;
            try {
                balance = Double.parseDouble(balStr);
            } catch (NumberFormatException ex) {
                err(dlg, "Enter a valid deposit amount (numbers only)."); return;
            }
            if (balance < 0) { err(dlg, "Deposit amount cannot be negative."); return; }
            try {
                long accNum = accounts.open_accountGUI(email, name, balance, pin);
                ok(dlg, "Account created successfully!\nYour Account Number: " + accNum);
                dlg.dispose();
                dashboard(email, accNum);
            } catch (RuntimeException ex) {
                err(dlg, ex.getMessage());
            }
        });

        skipBtn.addActionListener(e -> {
            dlg.dispose();
            cardLayout.show(mainPanel, "WELCOME");
        });

        dlg.setVisible(true);
    }

    private void dashboard(String email, long accNum) {
        JDialog dlg = dlg("Dashboard", 540, 470);
        dlg.setLayout(new BorderLayout());

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(C_NAVY);
        topBar.setBorder(new EmptyBorder(16, 24, 16, 24));

        JPanel info = new JPanel();
        info.setOpaque(false);
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        JLabel accLbl = new JLabel("Account No: " + accNum);
        accLbl.setFont(new Font("SansSerif", Font.BOLD, 14));
        accLbl.setForeground(C_WHITE);
        JLabel emailLbl = new JLabel(email);
        emailLbl.setFont(F_SMALL);
        emailLbl.setForeground(new Color(170, 195, 240));
        info.add(accLbl); info.add(vgap(2)); info.add(emailLbl);

        JLabel bankLbl = new JLabel("My Bank 🏦");
        bankLbl.setFont(new Font("SansSerif", Font.BOLD, 16));
        bankLbl.setForeground(C_WHITE);
        topBar.add(info, BorderLayout.WEST);
        topBar.add(bankLbl, BorderLayout.EAST);
        dlg.add(topBar, BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout());
        center.setBackground(C_BG);
        center.setBorder(new EmptyBorder(20, 26, 20, 26));

        JLabel chooseLbl = new JLabel("What would you like to do?");
        chooseLbl.setFont(F_LABEL);
        chooseLbl.setForeground(C_MUTED);
        chooseLbl.setBorder(new EmptyBorder(0, 0, 12, 0));
        center.add(chooseLbl, BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(3, 2, 12, 12));
        grid.setOpaque(false);

        JButton creditBtn   = dashBtn("💰  Credit Money",         C_GREEN,  "Deposit funds");
        JButton debitBtn    = dashBtn("💸  Debit Money",          C_ORANGE, "Withdraw funds");
        JButton transferBtn = dashBtn("🔄  Transfer Money",       C_PURPLE, "Send to another account");
        JButton balanceBtn  = dashBtn("📊  Check Balance",        C_BLUE,   "View current balance");
        JButton historyBtn  = dashBtn("🧾  Transaction History",  C_TEAL,   "View last 50 transactions");
        JButton logoutBtn   = dashBtn("🚪  Logout",               C_RED,    "Sign out");

        grid.add(creditBtn); grid.add(debitBtn);
        grid.add(transferBtn); grid.add(balanceBtn);
        grid.add(historyBtn); grid.add(logoutBtn);
        center.add(grid, BorderLayout.CENTER);
        dlg.add(center, BorderLayout.CENTER);

        JPanel statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 5));
        statusBar.setBackground(new Color(228, 232, 244));
        JLabel sl = new JLabel("Logged in as: " + email);
        sl.setFont(F_SMALL); sl.setForeground(C_MUTED);
        statusBar.add(sl);
        dlg.add(statusBar, BorderLayout.SOUTH);

        creditBtn.addActionListener(e   -> creditDialog(dlg,   accNum));
        debitBtn.addActionListener(e    -> debitDialog(dlg,    accNum));
        transferBtn.addActionListener(e -> transferDialog(dlg, accNum));
        balanceBtn.addActionListener(e  -> balanceDialog(dlg,  accNum));
        historyBtn.addActionListener(e  -> historyDialog(dlg,  accNum));
        logoutBtn.addActionListener(e -> {
            dlg.dispose();
            cardLayout.show(mainPanel, "WELCOME");
        });

        dlg.setVisible(true);
    }

    private void creditDialog(JDialog parent, long accNum) {
        JDialog dlg = dlg("Credit Money", 360, 255);
        dlg.setLayout(new BorderLayout());
        JPanel content = formPanel("💰  Add Funds", "Deposit money into your account");

        JTextField     amtField = field("Amount in Rs.");
        JPasswordField pinField = passField("Security PIN");

        JPanel form = new JPanel(new GridLayout(2, 2, 12, 12));
        form.setOpaque(false);
        form.add(lbl("Amount (Rs.)")); form.add(amtField);
        form.add(lbl("Security PIN")); form.add(pinField);
        form.setMaximumSize(new Dimension(Integer.MAX_VALUE, form.getPreferredSize().height));
        content.add(form); content.add(vgap(16));

        JButton submitBtn = btn("Add Funds", C_GREEN, 135, 38);
        JButton cancelBtn = btn("Cancel",    C_GRAY,  100, 38);
        content.add(btnRow(submitBtn, cancelBtn));
        dlg.add(content, BorderLayout.CENTER);

        Runnable submit = () -> {
            double amt = parseAmt(dlg, amtField.getText()); if (amt < 0) return;
            String pin = new String(pinField.getPassword());
            if (pin.isEmpty()) { err(dlg, "Please enter your PIN."); return; }
            if (accountManager.credit_moneyGUI(accNum, amt, pin)) {
                ok(dlg, "Rs. " + String.format("%.2f", amt) + " credited successfully.");
                dlg.dispose();
            } else {
                err(dlg, "Transaction failed. Please check your PIN.");
            }
        };
        submitBtn.addActionListener(e -> submit.run());
        pinField.addActionListener(e -> submit.run());
        cancelBtn.addActionListener(e -> dlg.dispose());
        dlg.setVisible(true);
    }

    private void debitDialog(JDialog parent, long accNum) {
        JDialog dlg = dlg("Debit Money", 360, 255);
        dlg.setLayout(new BorderLayout());
        JPanel content = formPanel("💸  Withdraw Funds", "Withdraw money from your account");

        JTextField     amtField = field("Amount in Rs.");
        JPasswordField pinField = passField("Security PIN");

        JPanel form = new JPanel(new GridLayout(2, 2, 12, 12));
        form.setOpaque(false);
        form.add(lbl("Amount (Rs.)")); form.add(amtField);
        form.add(lbl("Security PIN")); form.add(pinField);
        form.setMaximumSize(new Dimension(Integer.MAX_VALUE, form.getPreferredSize().height));
        content.add(form); content.add(vgap(16));

        JButton submitBtn = btn("Withdraw", C_ORANGE, 130, 38);
        JButton cancelBtn = btn("Cancel",   C_GRAY,   100, 38);
        content.add(btnRow(submitBtn, cancelBtn));
        dlg.add(content, BorderLayout.CENTER);

        Runnable submit = () -> {
            double amt = parseAmt(dlg, amtField.getText()); if (amt < 0) return;
            String pin = new String(pinField.getPassword());
            if (pin.isEmpty()) { err(dlg, "Please enter your PIN."); return; }
            String result = accountManager.debit_moneyGUI(accNum, amt, pin);
            if ("SUCCESS".equals(result)) {
                ok(dlg, "Rs. " + String.format("%.2f", amt) + " withdrawn successfully.");
                dlg.dispose();
            } else {
                err(dlg, result);
            }
        };
        submitBtn.addActionListener(e -> submit.run());
        pinField.addActionListener(e -> submit.run());
        cancelBtn.addActionListener(e -> dlg.dispose());
        dlg.setVisible(true);
    }

    private void transferDialog(JDialog parent, long senderAcc) {
        JDialog dlg = dlg("Transfer Money", 390, 295);
        dlg.setLayout(new BorderLayout());
        JPanel content = formPanel("🔄  Send Money", "Transfer funds to another account");

        JTextField     recvField = field("Receiver account number");
        JTextField     amtField  = field("Amount in Rs.");
        JPasswordField pinField  = passField("Security PIN");

        JPanel form = new JPanel(new GridLayout(3, 2, 12, 12));
        form.setOpaque(false);
        form.add(lbl("Receiver Account")); form.add(recvField);
        form.add(lbl("Amount (Rs.)"));     form.add(amtField);
        form.add(lbl("Security PIN"));     form.add(pinField);
        form.setMaximumSize(new Dimension(Integer.MAX_VALUE, form.getPreferredSize().height));
        content.add(form); content.add(vgap(16));

        JButton submitBtn = btn("Transfer", C_PURPLE, 130, 38);
        JButton cancelBtn = btn("Cancel",   C_GRAY,   100, 38);
        content.add(btnRow(submitBtn, cancelBtn));
        dlg.add(content, BorderLayout.CENTER);

        Runnable submit = () -> {
            long recvAcc;
            try { recvAcc = Long.parseLong(recvField.getText().trim()); }
            catch (NumberFormatException ex) { err(dlg, "Invalid receiver account number."); return; }
            double amt = parseAmt(dlg, amtField.getText()); if (amt < 0) return;
            String pin = new String(pinField.getPassword());
            if (pin.isEmpty()) { err(dlg, "Please enter your PIN."); return; }
            String result = accountManager.transfer_moneyGUI(senderAcc, recvAcc, amt, pin);
            if ("SUCCESS".equals(result)) {
                ok(dlg, "Rs. " + String.format("%.2f", amt) + " transferred successfully.");
                dlg.dispose();
            } else {
                err(dlg, result);
            }
        };
        submitBtn.addActionListener(e -> submit.run());
        pinField.addActionListener(e -> submit.run());
        cancelBtn.addActionListener(e -> dlg.dispose());
        dlg.setVisible(true);
    }

    private void balanceDialog(JDialog parent, long accNum) {
        JDialog dlg = dlg("Check Balance", 340, 225);
        dlg.setLayout(new BorderLayout());
        JPanel content = formPanel("📊  Check Balance", "Enter your PIN to view balance");

        JPasswordField pinField = passField("Security PIN");
        JPanel form = new JPanel(new GridLayout(1, 2, 12, 12));
        form.setOpaque(false);
        form.add(lbl("Security PIN")); form.add(pinField);
        form.setMaximumSize(new Dimension(Integer.MAX_VALUE, form.getPreferredSize().height));
        content.add(form); content.add(vgap(16));

        JButton checkBtn  = btn("Check Balance", C_BLUE, 148, 38);
        JButton cancelBtn = btn("Cancel",        C_GRAY, 100, 38);
        content.add(btnRow(checkBtn, cancelBtn));
        dlg.add(content, BorderLayout.CENTER);

        Runnable check = () -> {
            String pin = new String(pinField.getPassword());
            if (pin.isEmpty()) { err(dlg, "Please enter your PIN."); return; }
            Double balance = accountManager.getBalanceGUI(accNum, pin);
            if (balance != null) {
                ok(dlg, "Your current balance:\nRs. " + String.format("%.2f", balance));
                dlg.dispose();
            } else {
                err(dlg, "Invalid PIN. Please try again.");
            }
        };
        checkBtn.addActionListener(e -> check.run());
        pinField.addActionListener(e -> check.run());
        cancelBtn.addActionListener(e -> dlg.dispose());
        dlg.setVisible(true);
    }

    private void historyDialog(JDialog parent, long accNum) {
        List<String[]> rows = accountManager.getTransactionHistoryGUI(accNum);

        if (rows.isEmpty()) {
            JOptionPane.showMessageDialog(parent,
                    "No transactions found for account " + accNum + ".\nMake a deposit to see history.",
                    "Transaction History", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JDialog dlg = dlg("Transaction History — Account: " + accNum, 820, 480);
        dlg.setLayout(new BorderLayout());

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(C_NAVY);
        header.setBorder(new EmptyBorder(14, 22, 14, 22));
        JLabel ht = new JLabel("🧾  Transaction History");
        ht.setFont(new Font("SansSerif", Font.BOLD, 15));
        ht.setForeground(C_WHITE);
        JLabel hc = new JLabel("Last " + rows.size() + " transactions");
        hc.setFont(F_SMALL);
        hc.setForeground(new Color(170, 195, 240));
        header.add(ht, BorderLayout.WEST);
        header.add(hc, BorderLayout.EAST);
        dlg.add(header, BorderLayout.NORTH);

        String[]   cols = {"Date & Time", "Type", "Amount (Rs.)", "Balance After (Rs.)", "Description"};
        Object[][] data = rows.toArray(new Object[0][]);

        DefaultTableModel model = new DefaultTableModel(data, cols) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        JTable table = new JTable(model);
        table.setRowHeight(30);
        table.setFont(F_FIELD);
        table.setGridColor(C_BORDER);
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 1));
        table.setSelectionBackground(new Color(210, 228, 255));
        table.setSelectionForeground(C_TEXT);
        table.setFillsViewportHeight(true);

        JTableHeader th = table.getTableHeader();
        th.setFont(new Font("SansSerif", Font.BOLD, 12));
        th.setBackground(new Color(232, 236, 248));
        th.setForeground(C_TEXT);
        th.setReorderingAllowed(false);
        th.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, C_BLUE));

        table.getColumnModel().getColumn(0).setPreferredWidth(155);
        table.getColumnModel().getColumn(1).setPreferredWidth(130);
        table.getColumnModel().getColumn(2).setPreferredWidth(115);
        table.getColumnModel().getColumn(3).setPreferredWidth(150);
        table.getColumnModel().getColumn(4).setPreferredWidth(200);

        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(
                    JTable t, Object val, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                setBorder(new EmptyBorder(0, 10, 0, 10));
                if (!sel) {
                    setBackground(row % 2 == 0 ? C_WHITE : new Color(246, 249, 255));
                    setForeground(C_TEXT);
                    setFont(F_FIELD);
                    if (col == 1 && val != null) {
                        switch (val.toString()) {
                            case "CREDIT":            setForeground(new Color(20, 130,  40)); setFont(F_BTN); break;
                            case "DEBIT":             setForeground(new Color(190,  30,  30)); setFont(F_BTN); break;
                            case "TRANSFER_SENT":     setForeground(new Color(195,  95,   0)); setFont(F_BTN); break;
                            case "TRANSFER_RECEIVED": setForeground(new Color( 20,  80, 185)); setFont(F_BTN); break;
                        }
                    }
                }
                return this;
            }
        };
        for (int i = 0; i < table.getColumnCount(); i++)
            table.getColumnModel().getColumn(i).setCellRenderer(renderer);

        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.getViewport().setBackground(C_WHITE);
        dlg.add(sp, BorderLayout.CENTER);

        JPanel foot = new JPanel(new BorderLayout());
        foot.setBackground(new Color(238, 241, 250));
        foot.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, C_BORDER));

        JPanel legends = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 7));
        legends.setOpaque(false);
        legends.add(legendDot(new Color( 20, 130,  40), "Credit"));
        legends.add(legendDot(new Color(190,  30,  30), "Debit"));
        legends.add(legendDot(new Color(195,  95,   0), "Transfer Sent"));
        legends.add(legendDot(new Color( 20,  80, 185), "Transfer Received"));

        JPanel closeWrap = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 6));
        closeWrap.setOpaque(false);
        JButton closeBtn = btn("Close", C_GRAY, 88, 30);
        closeBtn.setFont(F_SMALL);
        closeWrap.add(closeBtn);

        foot.add(legends,   BorderLayout.WEST);
        foot.add(closeWrap, BorderLayout.EAST);
        dlg.add(foot, BorderLayout.SOUTH);

        closeBtn.addActionListener(e -> dlg.dispose());
        dlg.setVisible(true);
    }

    private double parseAmt(Component parent, String text) {
        if (text == null || text.trim().isEmpty()) { err(parent, "Please enter an amount."); return -1; }
        try {
            double v = Double.parseDouble(text.trim());
            if (v <= 0) { err(parent, "Amount must be greater than zero."); return -1; }
            return v;
        } catch (NumberFormatException e) {
            err(parent, "Invalid amount. Enter numbers only (e.g. 1000)."); return -1;
        }
    }

    private JPanel centeredOuter() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(C_BG);
        return p;
    }

    private JPanel card(int w, int h) {
        JPanel p = new JPanel();
        p.setOpaque(true);
        p.setBackground(C_WHITE);
        p.setPreferredSize(new Dimension(w, h));
        p.setBorder(BorderFactory.createCompoundBorder(
                new RoundedShadowBorder(),
                new EmptyBorder(0, 0, 0, 0)
        ));
        return p;
    }

    private JPanel formPanel(String title, String subtitle) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(C_WHITE);
        p.setBorder(new EmptyBorder(24, 32, 24, 32));
        JLabel t = new JLabel(title);
        t.setFont(F_HEAD); t.setForeground(C_NAVY); t.setAlignmentX(LEFT_ALIGNMENT);
        JLabel s = new JLabel(subtitle);
        s.setFont(F_SMALL); s.setForeground(C_MUTED); s.setAlignmentX(LEFT_ALIGNMENT);
        JSeparator sep = new JSeparator();
        sep.setForeground(C_BORDER);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep.setAlignmentX(LEFT_ALIGNMENT);
        p.add(t); p.add(vgap(3)); p.add(s); p.add(vgap(13)); p.add(sep); p.add(vgap(16));
        return p;
    }

    private JDialog dlg(String title, int w, int h) {
        JDialog d = new JDialog(this, title, true);
        d.setSize(w, h);
        d.setLocationRelativeTo(this);
        d.getContentPane().setBackground(C_WHITE);
        return d;
    }

    private JButton btn(String text, Color bg, int w, int h) {
        JButton b = new JButton(text);
        b.setPreferredSize(new Dimension(w, h));
        b.setMaximumSize(new Dimension(w, h));
        b.setBackground(bg);
        b.setForeground(C_WHITE);
        b.setFont(F_BTN);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setOpaque(true);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        Color hover = bg.darker();
        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { b.setBackground(hover); }
            public void mouseExited(MouseEvent e)  { b.setBackground(bg);   }
        });
        return b;
    }

    private JButton dashBtn(String text, Color bg, String sub) {
        JButton b = new JButton("<html><center>" + text +
                "<br><font size='2'>" + sub + "</font></center></html>");
        b.setBackground(bg);
        b.setForeground(C_WHITE);
        b.setFont(new Font("SansSerif", Font.BOLD, 13));
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setOpaque(true);
        b.setPreferredSize(new Dimension(200, 66));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        Color hover = bg.darker();
        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { b.setBackground(hover); }
            public void mouseExited(MouseEvent e)  { b.setBackground(bg);   }
        });
        return b;
    }

    private JTextField field(String ph) {
        JTextField f = new JTextField(16);
        f.setFont(F_FIELD);
        f.setForeground(C_MUTED);
        f.setBackground(C_WHITE);
        f.setText(ph);
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(C_BORDER, 1, true),
                new EmptyBorder(5, 8, 5, 8)
        ));
        f.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (f.getText().equals(ph)) { f.setText(""); f.setForeground(C_TEXT); }
            }
            public void focusLost(FocusEvent e) {
                if (f.getText().isEmpty()) { f.setText(ph); f.setForeground(C_MUTED); }
            }
        });
        return f;
    }

    private JPasswordField passField(String ph) {
        JPasswordField f = new JPasswordField(16);
        f.setFont(F_FIELD);
        f.setForeground(C_MUTED);
        f.setBackground(C_WHITE);
        f.setEchoChar((char) 0);
        f.setText(ph);
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(C_BORDER, 1, true),
                new EmptyBorder(5, 8, 5, 8)
        ));
        f.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (String.valueOf(f.getPassword()).equals(ph)) {
                    f.setText(""); f.setEchoChar('●'); f.setForeground(C_TEXT);
                }
            }
            public void focusLost(FocusEvent e) {
                if (f.getPassword().length == 0) {
                    f.setEchoChar((char) 0); f.setText(ph); f.setForeground(C_MUTED);
                }
            }
        });
        return f;
    }

    private JLabel lbl(String text) {
        JLabel l = new JLabel(text + ":");
        l.setFont(F_LABEL);
        l.setForeground(C_TEXT);
        l.setHorizontalAlignment(SwingConstants.RIGHT);
        return l;
    }

    private <T extends JLabel> T centered(T l) {
        l.setAlignmentX(CENTER_ALIGNMENT);
        l.setHorizontalAlignment(SwingConstants.CENTER);
        return l;
    }

    private JPanel btnRow(JButton... btns) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        p.setOpaque(false);
        p.setAlignmentX(LEFT_ALIGNMENT);
        for (JButton b : btns) p.add(b);
        return p;
    }

    private Component vgap(int h) { return Box.createVerticalStrut(h); }

    private JPanel legendDot(Color c, String label) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        p.setOpaque(false);
        JLabel dot = new JLabel("●");
        dot.setFont(new Font("SansSerif", Font.PLAIN, 13));
        dot.setForeground(c);
        JLabel lbl = new JLabel(label);
        lbl.setFont(F_SMALL);
        lbl.setForeground(C_MUTED);
        p.add(dot); p.add(lbl);
        return p;
    }

    private void err(Component parent, String msg) {
        JOptionPane.showMessageDialog(parent, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void ok(Component parent, String msg) {
        JOptionPane.showMessageDialog(parent, msg, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    static class RoundedShadowBorder extends AbstractBorder {
        private static final int S = 5;
        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            for (int i = S; i >= 1; i--) {
                g2.setColor(new Color(70, 70, 100, (int)(16.0 * (S - i + 1) / S)));
                g2.drawRoundRect(x + i, y + i, w - 2 * i - 1, h - 2 * i - 1, 18, 18);
            }
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(x, y, w - S, h - S, 14, 14);
            g2.setColor(new Color(210, 215, 228));
            g2.drawRoundRect(x, y, w - S - 1, h - S - 1, 14, 14);
            g2.dispose();
        }
        public Insets getBorderInsets(Component c) { return new Insets(4, 4, S + 4, S + 4); }
        public Insets getBorderInsets(Component c, Insets i) { i.set(4, 4, S + 4, S + 4); return i; }
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) {}
        SwingUtilities.invokeLater(BankingAppGUI::new);
    }
}
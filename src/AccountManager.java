package BankingManagementSystem;

import java.sql.*;
import java.util.List;
import java.util.Scanner;

public class AccountManager {

    private Connection connection;
    private Scanner scanner;
    private TransactionLogger transactionLogger;

    AccountManager(Connection connection, Scanner scanner) {
        this.connection = connection;
        this.scanner = scanner;
        this.transactionLogger = new TransactionLogger(connection);
    }

    public boolean credit_moneyGUI(long account_number, double amount, String security_pin) {

        if (amount <= 0) {
            return false;
        }
        if (security_pin == null || security_pin.trim().isEmpty()) {
            return false;
        }

        try {
            connection.setAutoCommit(false);
            PreparedStatement checkPin = connection.prepareStatement(
                    "SELECT * FROM Accounts WHERE account_number = ? AND security_pin = ?"
            );
            checkPin.setLong(1, account_number);
            checkPin.setString(2, security_pin);
            ResultSet rs = checkPin.executeQuery();

            if (rs.next()) {
                PreparedStatement addMoney = connection.prepareStatement(
                        "UPDATE Accounts SET balance = balance + ? WHERE account_number = ?"
                );
                addMoney.setDouble(1, amount);
                addMoney.setLong(2, account_number);

                int rowsChanged = addMoney.executeUpdate();

                if (rowsChanged > 0) {

                    connection.commit();

                    transactionLogger.saveTransaction(
                            account_number,
                            "CREDIT",
                            amount,
                            "Cash deposit"
                    );

                    return true;  // success!
                }
            }


            connection.rollback();
            return false;

        } catch (SQLException e) {
            try { connection.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
            return false;
        } finally {
            // Always re-enable auto-commit when done
            try { connection.setAutoCommit(true); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    public String debit_moneyGUI(long account_number, double amount, String security_pin) {


        if (amount <= 0) {
            return "Amount must be greater than zero!";
        }
        if (security_pin == null || security_pin.trim().isEmpty()) {
            return "Security PIN cannot be empty!";
        }


        try {
            connection.setAutoCommit(false);

            // Step 1: Check PIN and fetch current balance
            PreparedStatement checkPin = connection.prepareStatement(
                    "SELECT * FROM Accounts WHERE account_number = ? AND security_pin = ?"
            );
            checkPin.setLong(1, account_number);
            checkPin.setString(2, security_pin);
            ResultSet rs = checkPin.executeQuery();

            if (rs.next()) {
                double currentBalance = rs.getDouble("balance");

                // Step 2: Make sure they have enough money
                if (amount > currentBalance) {
                    connection.rollback();
                    return "Insufficient Balance!";  // not enough money
                }

                // Step 3: Subtract the money
                PreparedStatement removeMoney = connection.prepareStatement(
                        "UPDATE Accounts SET balance = balance - ? WHERE account_number = ?"
                );
                removeMoney.setDouble(1, amount);
                removeMoney.setLong(2, account_number);

                int rowsChanged = removeMoney.executeUpdate();

                if (rowsChanged > 0) {
                    connection.commit();

                    // ── NEW LINE ───────────────────────────────────
                    transactionLogger.saveTransaction(
                            account_number,
                            "DEBIT",
                            amount,
                            "Cash withdrawal"
                    );
                    // ──────────────────────────────────────────────

                    return "SUCCESS";
                }

            } else {
                connection.rollback();
                return "Invalid Security Pin!";  // wrong PIN
            }

            connection.rollback();
            return "Transaction Failed!";

        } catch (SQLException e) {
            try { connection.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
            return "Database error!";
        } finally {
            try { connection.setAutoCommit(true); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    public String transfer_moneyGUI(long sender_account, long receiver_account,
                                    double amount, String security_pin) {


        if (amount <= 0) {
            return "Amount must be greater than zero!";
        }
        if (security_pin == null || security_pin.trim().isEmpty()) {
            return "Security PIN cannot be empty!";
        }
        if (sender_account == receiver_account) {
            return "Cannot transfer to your own account!";
        }


        try {
            connection.setAutoCommit(false);

            // Step 1: Check sender's PIN and balance
            PreparedStatement checkSender = connection.prepareStatement(
                    "SELECT * FROM Accounts WHERE account_number = ? AND security_pin = ?"
            );
            checkSender.setLong(1, sender_account);
            checkSender.setString(2, security_pin);
            ResultSet senderRS = checkSender.executeQuery();

            if (!senderRS.next()) {
                connection.rollback();
                return "Invalid Security Pin!";
            }

            double senderBalance = senderRS.getDouble("balance");
            if (amount > senderBalance) {
                connection.rollback();
                return "Insufficient Balance!";
            }

            // Step 2: Make sure the receiver account actually exists
            PreparedStatement checkReceiver = connection.prepareStatement(
                    "SELECT account_number FROM Accounts WHERE account_number = ?"
            );
            checkReceiver.setLong(1, receiver_account);
            ResultSet receiverRS = checkReceiver.executeQuery();

            if (!receiverRS.next()) {
                connection.rollback();
                return "Receiver account does not exist!";
            }

            // Step 3: Subtract from sender
            PreparedStatement debit = connection.prepareStatement(
                    "UPDATE Accounts SET balance = balance - ? WHERE account_number = ?"
            );
            debit.setDouble(1, amount);
            debit.setLong(2, sender_account);

            // Step 4: Add to receiver
            PreparedStatement credit = connection.prepareStatement(
                    "UPDATE Accounts SET balance = balance + ? WHERE account_number = ?"
            );
            credit.setDouble(1, amount);
            credit.setLong(2, receiver_account);

            int debitRows  = debit.executeUpdate();
            int creditRows = credit.executeUpdate();

            if (debitRows > 0 && creditRows > 0) {
                connection.commit();


                transactionLogger.saveTransaction(
                        sender_account,
                        "TRANSFER_SENT",
                        amount,
                        "Transfer to account " + receiver_account
                );

                transactionLogger.saveTransaction(
                        receiver_account,
                        "TRANSFER_RECEIVED",
                        amount,
                        "Transfer from account " + sender_account
                );


                return "SUCCESS";
            }

            connection.rollback();
            return "Transaction Failed!";

        } catch (SQLException e) {
            try { connection.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
            return "Database error!";
        } finally {
            try { connection.setAutoCommit(true); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    public Double getBalanceGUI(long account_number, String security_pin) {
        try (PreparedStatement stmt = connection.prepareStatement(
                "SELECT balance FROM Accounts WHERE account_number = ? AND security_pin = ?")) {

            stmt.setLong(1, account_number);
            stmt.setString(2, security_pin);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("balance");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;  // null means the PIN was wrong
    }

    public List<String[]> getTransactionHistoryGUI(long account_number) {
        return transactionLogger.getHistory(account_number);
    }

    public void credit_money(long account_number) throws SQLException {
        scanner.nextLine();
        System.out.print("Enter Amount: ");
        double amount = scanner.nextDouble();
        scanner.nextLine();
        System.out.print("Enter Security Pin: ");
        String security_pin = scanner.nextLine();

        try {
            connection.setAutoCommit(false);
            if (account_number != 0) {
                PreparedStatement ps = connection.prepareStatement(
                        "SELECT * FROM Accounts WHERE account_number = ? and security_pin = ?");
                ps.setLong(1, account_number); ps.setString(2, security_pin);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    PreparedStatement ps2 = connection.prepareStatement(
                            "UPDATE Accounts SET balance = balance + ? WHERE account_number = ?");
                    ps2.setDouble(1, amount); ps2.setLong(2, account_number);
                    if (ps2.executeUpdate() > 0) {
                        connection.commit();
                        transactionLogger.saveTransaction(account_number, "CREDIT", amount, "Cash deposit");
                        System.out.println("Rs." + amount + " credited Successfully");
                    } else { connection.rollback(); }
                } else { System.out.println("Invalid Security Pin!"); }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        connection.setAutoCommit(true);
    }

    public void debit_money(long account_number) throws SQLException {
        scanner.nextLine();
        System.out.print("Enter Amount: ");
        double amount = scanner.nextDouble();
        scanner.nextLine();
        System.out.print("Enter Security Pin: ");
        String security_pin = scanner.nextLine();

        try {
            connection.setAutoCommit(false);
            if (account_number != 0) {
                PreparedStatement ps = connection.prepareStatement(
                        "SELECT * FROM Accounts WHERE account_number = ? and security_pin = ?");
                ps.setLong(1, account_number); ps.setString(2, security_pin);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    double balance = rs.getDouble("balance");
                    if (amount <= balance) {
                        PreparedStatement ps2 = connection.prepareStatement(
                                "UPDATE Accounts SET balance = balance - ? WHERE account_number = ?");
                        ps2.setDouble(1, amount); ps2.setLong(2, account_number);
                        if (ps2.executeUpdate() > 0) {
                            connection.commit();
                            transactionLogger.saveTransaction(account_number, "DEBIT", amount, "Cash withdrawal");
                            System.out.println("Rs." + amount + " debited Successfully");
                        } else { connection.rollback(); }
                    } else { System.out.println("Insufficient Balance!"); }
                } else { System.out.println("Invalid Pin!"); }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        connection.setAutoCommit(true);
    }

    public void transfer_money(long sender_account_number) throws SQLException {
        scanner.nextLine();
        System.out.print("Enter Receiver Account Number: ");
        long receiver = scanner.nextLong();
        System.out.print("Enter Amount: ");
        double amount = scanner.nextDouble();
        scanner.nextLine();
        System.out.print("Enter Security Pin: ");
        String pin = scanner.nextLine();

        try {
            connection.setAutoCommit(false);
            PreparedStatement ps = connection.prepareStatement(
                    "SELECT * FROM Accounts WHERE account_number = ? AND security_pin = ?");
            ps.setLong(1, sender_account_number); ps.setString(2, pin);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                if (amount <= rs.getDouble("balance")) {
                    PreparedStatement d = connection.prepareStatement(
                            "UPDATE Accounts SET balance = balance - ? WHERE account_number = ?");
                    PreparedStatement c = connection.prepareStatement(
                            "UPDATE Accounts SET balance = balance + ? WHERE account_number = ?");
                    d.setDouble(1, amount); d.setLong(2, sender_account_number);
                    c.setDouble(1, amount); c.setLong(2, receiver);
                    if (d.executeUpdate() > 0 && c.executeUpdate() > 0) {
                        connection.commit();
                        transactionLogger.saveTransaction(sender_account_number, "TRANSFER_SENT",     amount, "Transfer to "   + receiver);
                        transactionLogger.saveTransaction(receiver,              "TRANSFER_RECEIVED", amount, "Transfer from " + sender_account_number);
                        System.out.println("Rs." + amount + " Transferred Successfully");
                    } else { connection.rollback(); }
                } else { System.out.println("Insufficient Balance!"); }
            } else { System.out.println("Invalid Security Pin!"); }
        } catch (SQLException e) { e.printStackTrace(); }
        connection.setAutoCommit(true);
    }

    public void getBalance(long account_number) {
        scanner.nextLine();
        System.out.print("Enter Security Pin: ");
        String pin = scanner.nextLine();
        try (PreparedStatement ps = connection.prepareStatement(
                "SELECT balance FROM Accounts WHERE account_number = ? AND security_pin = ?")) {
            ps.setLong(1, account_number); ps.setString(2, pin);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) System.out.println("Balance: " + rs.getDouble("balance"));
                else           System.out.println("Invalid Pin!");
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }
}
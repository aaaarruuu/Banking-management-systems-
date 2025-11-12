package BankingManagementSystem;

import java.sql.*;
import java.util.Scanner;

public class AccountManager {
    private Connection connection;
    private Scanner scanner;

    AccountManager(Connection connection, Scanner scanner) {
        this.connection = connection;
        this.scanner = scanner;
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
                PreparedStatement preparedStatement = connection.prepareStatement(
                        "SELECT * FROM Accounts WHERE account_number = ? and security_pin = ?");
                preparedStatement.setLong(1, account_number);
                preparedStatement.setString(2, security_pin);
                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    String credit_query = "UPDATE Accounts SET balance = balance + ? WHERE account_number = ?";
                    PreparedStatement preparedStatement1 = connection.prepareStatement(credit_query);
                    preparedStatement1.setDouble(1, amount);
                    preparedStatement1.setLong(2, account_number);
                    int rowsAffected = preparedStatement1.executeUpdate();
                    if (rowsAffected > 0) {
                        System.out.println("Rs." + amount + " credited Successfully");
                        connection.commit();
                        connection.setAutoCommit(true);
                        return;
                    } else {
                        System.out.println("Transaction Failed!");
                        connection.rollback();
                        connection.setAutoCommit(true);
                    }
                } else {
                    System.out.println("Invalid Security Pin!");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        connection.setAutoCommit(true);
    }

    public boolean credit_moneyGUI(long account_number, double amount, String security_pin) {
        try {
            connection.setAutoCommit(false);

            try (PreparedStatement verifyStmt = connection.prepareStatement(
                    "SELECT * FROM Accounts WHERE account_number = ? AND security_pin = ?")) {
                verifyStmt.setLong(1, account_number);
                verifyStmt.setString(2, security_pin);

                try (ResultSet resultSet = verifyStmt.executeQuery()) {
                    if (resultSet.next()) {
                        String credit_query = "UPDATE Accounts SET balance = balance + ? WHERE account_number = ?";
                        try (PreparedStatement creditStmt = connection.prepareStatement(credit_query)) {
                            creditStmt.setDouble(1, amount);
                            creditStmt.setLong(2, account_number);
                            int rowsAffected = creditStmt.executeUpdate();

                            if (rowsAffected > 0) {
                                connection.commit();
                                return true;
                            } else {
                                connection.rollback();
                                return false;
                            }
                        }
                    }
                }
            }
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
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
                PreparedStatement preparedStatement = connection.prepareStatement(
                        "SELECT * FROM Accounts WHERE account_number = ? and security_pin = ?");
                preparedStatement.setLong(1, account_number);
                preparedStatement.setString(2, security_pin);
                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    double current_balance = resultSet.getDouble("balance");
                    if (amount <= current_balance) {
                        String debit_query = "UPDATE Accounts SET balance = balance - ? WHERE account_number = ?";
                        PreparedStatement preparedStatement1 = connection.prepareStatement(debit_query);
                        preparedStatement1.setDouble(1, amount);
                        preparedStatement1.setLong(2, account_number);
                        int rowsAffected = preparedStatement1.executeUpdate();
                        if (rowsAffected > 0) {
                            System.out.println("Rs." + amount + " debited Successfully");
                            connection.commit();
                            connection.setAutoCommit(true);
                            return;
                        } else {
                            System.out.println("Transaction Failed!");
                            connection.rollback();
                            connection.setAutoCommit(true);
                        }
                    } else {
                        System.out.println("Insufficient Balance!");
                    }
                } else {
                    System.out.println("Invalid Pin!");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        connection.setAutoCommit(true);
    }

    public String debit_moneyGUI(long account_number, double amount, String security_pin) {
        try {
            connection.setAutoCommit(false);

            try (PreparedStatement verifyStmt = connection.prepareStatement(
                    "SELECT * FROM Accounts WHERE account_number = ? AND security_pin = ?")) {
                verifyStmt.setLong(1, account_number);
                verifyStmt.setString(2, security_pin);

                try (ResultSet resultSet = verifyStmt.executeQuery()) {
                    if (resultSet.next()) {
                        double current_balance = resultSet.getDouble("balance");

                        if (amount <= current_balance) {
                            String debit_query = "UPDATE Accounts SET balance = balance - ? WHERE account_number = ?";
                            try (PreparedStatement debitStmt = connection.prepareStatement(debit_query)) {
                                debitStmt.setDouble(1, amount);
                                debitStmt.setLong(2, account_number);
                                int rowsAffected = debitStmt.executeUpdate();

                                if (rowsAffected > 0) {
                                    connection.commit();
                                    return "SUCCESS";
                                } else {
                                    connection.rollback();
                                    return "Transaction Failed!";
                                }
                            }
                        } else {
                            connection.rollback();
                            return "Insufficient Balance!";
                        }
                    } else {
                        connection.rollback();
                        return "Invalid Security Pin!";
                    }
                }
            }
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            return "Database error occurred!";
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void transfer_money(long sender_account_number) throws SQLException {
        scanner.nextLine();
        System.out.print("Enter Receiver Account Number: ");
        long receiver_account_number = scanner.nextLong();
        System.out.print("Enter Amount: ");
        double amount = scanner.nextDouble();
        scanner.nextLine();
        System.out.print("Enter Security Pin: ");
        String security_pin = scanner.nextLine();

        try {
            connection.setAutoCommit(false);
            if (sender_account_number != 0 && receiver_account_number != 0) {
                PreparedStatement preparedStatement = connection.prepareStatement(
                        "SELECT * FROM Accounts WHERE account_number = ? AND security_pin = ?");
                preparedStatement.setLong(1, sender_account_number);
                preparedStatement.setString(2, security_pin);
                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    double current_balance = resultSet.getDouble("balance");
                    if (amount <= current_balance) {
                        String debit_query = "UPDATE Accounts SET balance = balance - ? WHERE account_number = ?";
                        String credit_query = "UPDATE Accounts SET balance = balance + ? WHERE account_number = ?";

                        PreparedStatement creditPreparedStatement = connection.prepareStatement(credit_query);
                        PreparedStatement debitPreparedStatement = connection.prepareStatement(debit_query);

                        creditPreparedStatement.setDouble(1, amount);
                        creditPreparedStatement.setLong(2, receiver_account_number);
                        debitPreparedStatement.setDouble(1, amount);
                        debitPreparedStatement.setLong(2, sender_account_number);

                        int rowsAffected1 = debitPreparedStatement.executeUpdate();
                        int rowsAffected2 = creditPreparedStatement.executeUpdate();

                        if (rowsAffected1 > 0 && rowsAffected2 > 0) {
                            System.out.println("Transaction Successful!");
                            System.out.println("Rs." + amount + " Transferred Successfully");
                            connection.commit();
                            connection.setAutoCommit(true);
                            return;
                        } else {
                            System.out.println("Transaction Failed");
                            connection.rollback();
                            connection.setAutoCommit(true);
                        }
                    } else {
                        System.out.println("Insufficient Balance!");
                    }
                } else {
                    System.out.println("Invalid Security Pin!");
                }
            } else {
                System.out.println("Invalid account number");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        connection.setAutoCommit(true);
    }

    public String transfer_moneyGUI(long sender_account_number, long receiver_account_number,
                                    double amount, String security_pin) {
        try {
            connection.setAutoCommit(false);

            if (sender_account_number == 0 || receiver_account_number == 0) {
                return "Invalid account number!";
            }

            try (PreparedStatement verifyStmt = connection.prepareStatement(
                    "SELECT * FROM Accounts WHERE account_number = ? AND security_pin = ?")) {
                verifyStmt.setLong(1, sender_account_number);
                verifyStmt.setString(2, security_pin);

                try (ResultSet resultSet = verifyStmt.executeQuery()) {
                    if (resultSet.next()) {
                        double current_balance = resultSet.getDouble("balance");

                        if (amount <= current_balance) {
                            try (PreparedStatement checkReceiver = connection.prepareStatement(
                                    "SELECT account_number FROM Accounts WHERE account_number = ?")) {
                                checkReceiver.setLong(1, receiver_account_number);
                                try (ResultSet receiverResult = checkReceiver.executeQuery()) {
                                    if (!receiverResult.next()) {
                                        connection.rollback();
                                        return "Receiver account does not exist!";
                                    }
                                }
                            }

                            String debit_query = "UPDATE Accounts SET balance = balance - ? WHERE account_number = ?";
                            String credit_query = "UPDATE Accounts SET balance = balance + ? WHERE account_number = ?";

                            try (PreparedStatement debitStmt = connection.prepareStatement(debit_query);
                                 PreparedStatement creditStmt = connection.prepareStatement(credit_query)) {

                                debitStmt.setDouble(1, amount);
                                debitStmt.setLong(2, sender_account_number);

                                creditStmt.setDouble(1, amount);
                                creditStmt.setLong(2, receiver_account_number);

                                int rowsAffected1 = debitStmt.executeUpdate();
                                int rowsAffected2 = creditStmt.executeUpdate();

                                if (rowsAffected1 > 0 && rowsAffected2 > 0) {
                                    connection.commit();
                                    return "SUCCESS";
                                } else {
                                    connection.rollback();
                                    return "Transaction Failed!";
                                }
                            }
                        } else {
                            connection.rollback();
                            return "Insufficient Balance!";
                        }
                    } else {
                        connection.rollback();
                        return "Invalid Security Pin!";
                    }
                }
            }
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            return "Database error occurred!";
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void getBalance(long account_number) {
        scanner.nextLine();
        System.out.print("Enter Security Pin: ");
        String security_pin = scanner.nextLine();

        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT balance FROM Accounts WHERE account_number = ? AND security_pin = ?")) {
            preparedStatement.setLong(1, account_number);
            preparedStatement.setString(2, security_pin);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    double balance = resultSet.getDouble("balance");
                    System.out.println("Balance: " + balance);
                } else {
                    System.out.println("Invalid Pin!");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Double getBalanceGUI(long account_number, String security_pin) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT balance FROM Accounts WHERE account_number = ? AND security_pin = ?")) {
            preparedStatement.setLong(1, account_number);
            preparedStatement.setString(2, security_pin);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getDouble("balance");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
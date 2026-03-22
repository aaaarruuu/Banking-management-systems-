package BankingManagementSystem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TransactionLogger {

    private Connection connection;
    public TransactionLogger(Connection connection) {
        this.connection = connection;
    }

    public void saveTransaction(long accountNumber, String type, double amount, String description) {

        double balanceAfter = getCurrentBalance(accountNumber);

        String sql = "INSERT INTO Transactions " +
                "(account_number, type, amount, balance_after, description) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {

            // Fill in the ? marks in the same order they appear
            stmt.setLong(1, accountNumber);   // ? 1 = account number
            stmt.setString(2, type);          // ? 2 = CREDIT / DEBIT / etc.
            stmt.setDouble(3, amount);        // ? 3 = amount
            stmt.setDouble(4, balanceAfter);  // ? 4 = balance snapshot
            stmt.setString(5, description);   // ? 5 = short note

            // Run the insert
            stmt.executeUpdate();

        } catch (SQLException e) {

            System.err.println("Warning: Could not save transaction record — " + e.getMessage());
        }
    }

    public List<String[]> getHistory(long accountNumber) {

        List<String[]> rows = new ArrayList<>();

        String sql = "SELECT transaction_date, type, amount, balance_after, description " +
                "FROM Transactions " +
                "WHERE account_number = ? " +
                "ORDER BY transaction_date DESC " +  // newest on top
                "LIMIT 50";                           // max 50 rows

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setLong(1, accountNumber);

            try (ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {

                    String[] oneRow = new String[5];

                    oneRow[0] = rs.getString("transaction_date");  // "2024-11-16 13:45:00"
                    oneRow[1] = rs.getString("type");              // "CREDIT"

                    oneRow[2] = "Rs. " + String.format("%.2f", rs.getDouble("amount"));
                    oneRow[3] = "Rs. " + String.format("%.2f", rs.getDouble("balance_after"));

                    String desc = rs.getString("description");
                    oneRow[4] = (desc != null) ? desc : "-";

                    rows.add(oneRow);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return rows;
    }

    private double getCurrentBalance(long accountNumber) {

        String sql = "SELECT balance FROM Accounts WHERE account_number = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setLong(1, accountNumber);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("balance");  // return the balance
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0.0;
    }
}
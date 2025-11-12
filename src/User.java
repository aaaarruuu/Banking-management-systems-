package BankingManagementSystem;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class User {
    private Connection connection;
    private Scanner scanner;

    public User(Connection connection, Scanner scanner) {
        this.connection = connection;
        this.scanner = scanner;
    }

    // Console-based register method
    public void register() {
        scanner.nextLine();
        System.out.print("Full Name: ");
        String full_name = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        if (user_exist(email)) {
            System.out.println("User already exists for this email address!");
            return;
        }

        String hashedPassword = hashPassword(password);
        if (hashedPassword == null) {
            System.out.println("Error processing password. Registration failed.");
            return;
        }

        String register_query = "INSERT INTO User(full_name, email, password) VALUES(?, ?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(register_query)) {
            preparedStatement.setString(1, full_name);
            preparedStatement.setString(2, email);
            preparedStatement.setString(3, hashedPassword);

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Registration Successful!");
            } else {
                System.out.println("Registration Failed!");
            }
        } catch (SQLException e) {
            System.out.println("Database error during registration: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // GUI-based register method
    public boolean registerGUI(String full_name, String email, String password) {
        if (user_exist(email)) {
            return false;
        }

        String hashedPassword = hashPassword(password);
        if (hashedPassword == null) {
            return false;
        }

        String register_query = "INSERT INTO User(full_name, email, password) VALUES(?, ?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(register_query)) {
            preparedStatement.setString(1, full_name);
            preparedStatement.setString(2, email);
            preparedStatement.setString(3, hashedPassword);

            int affectedRows = preparedStatement.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Console-based login method
    public String login() {
        scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        String hashedPassword = hashPassword(password);
        if (hashedPassword == null) {
            System.out.println("Error processing password.");
            return null;
        }

        String login_query = "SELECT * FROM User WHERE email = ? AND password = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(login_query)) {
            preparedStatement.setString(1, email);
            preparedStatement.setString(2, hashedPassword);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return email;
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            System.out.println("Database error during login: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    // GUI-based login method
    public String loginGUI(String email, String password) {
        String hashedPassword = hashPassword(password);
        if (hashedPassword == null) {
            return null;
        }

        String login_query = "SELECT * FROM User WHERE email = ? AND password = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(login_query)) {
            preparedStatement.setString(1, email);
            preparedStatement.setString(2, hashedPassword);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return email;
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean user_exist(String email) {
        String query = "SELECT * FROM User WHERE email = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, email);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Hash password using SHA-256
     * Note: For production, use BCrypt or Argon2 instead
     */
    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes());

            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
}
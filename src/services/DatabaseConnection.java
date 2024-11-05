package services;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static Connection connection;

    public static void initialize() {
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/BankSysPro", "root", "Ka@12veesha");
            System.out.println("Database connected successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to connect to the database.");
        }
    }

    public static Connection getConnection() {
        return connection;
    }
}

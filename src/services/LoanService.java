package services;

import models.Loan;
import services.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LoanService {

    private Connection connection;

    public LoanService() {
        this.connection = DatabaseConnection.getConnection();
    }

    // Applies for a new loan and inserts the loan details into the database
    public boolean applyForLoan(Loan loan) {
        String sql = "INSERT INTO Loans (customer_id, loan_amount, interest_rate, loan_term, status, created_at) VALUES (?, ?, ?, ?, ?, NOW())";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, loan.getCustomerId());
            pstmt.setDouble(2, loan.getLoanAmount());
            pstmt.setDouble(3, loan.getInterestRate());
            pstmt.setInt(4, loan.getLoanTerm());
            pstmt.setString(5, loan.getStatus());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Approves a loan by updating its status in the database
    public boolean approveLoan(int loanId) {
        String sql = "UPDATE Loans SET status = 'Approved' WHERE loan_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, loanId);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Repays a loan by reducing its remaining amount and updating the status if fully paid
    public boolean repayLoan(int loanId, double payment) {
        Loan loan = getLoanById(loanId);
        if (loan != null && "Approved".equals(loan.getStatus())) {
            double remainingAmount = loan.getLoanAmount() - payment;
            if (remainingAmount <= 0) {
                loan.setStatus("Paid");
            }
            loan.setLoanAmount(remainingAmount);
            return updateLoan(loan);
        }
        return false;
    }

    // Retrieves a list of loans for a specific user
    public List<Loan> getLoansByUserId(int userId) {
        String sql = "SELECT * FROM Loans WHERE customer_id = ?";
        List<Loan> loans = new ArrayList<>();
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                loans.add(new Loan(
                        rs.getInt("loan_id"),
                        rs.getInt("customer_id"),
                        rs.getDouble("loan_amount"),
                        rs.getDouble("interest_rate"),
                        rs.getInt("loan_term"),
                        rs.getString("status"),
                        rs.getString("created_at")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return loans;
    }

    // Retrieves a loan by its ID
    public Loan getLoanById(int loanId) {
        String sql = "SELECT * FROM Loans WHERE loan_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, loanId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Loan(
                        rs.getInt("loan_id"),
                        rs.getInt("customer_id"),
                        rs.getDouble("loan_amount"),
                        rs.getDouble("interest_rate"),
                        rs.getInt("loan_term"),
                        rs.getString("status"),
                        rs.getString("created_at")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Updates the loan details in the database
    public boolean updateLoan(Loan loan) {
        String sql = "UPDATE Loans SET loan_amount = ?, interest_rate = ?, loan_term = ?, status = ? WHERE loan_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setDouble(1, loan.getLoanAmount());
            pstmt.setDouble(2, loan.getInterestRate());
            pstmt.setInt(3, loan.getLoanTerm());
            pstmt.setString(4, loan.getStatus());
            pstmt.setInt(5, loan.getLoanId());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Retrieves all pending loans from the database
    public List<Loan> getPendingLoans() {
        String sql = "SELECT * FROM Loans WHERE status = 'Pending'";
        List<Loan> loans = new ArrayList<>();
        try (PreparedStatement pstmt = connection.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                loans.add(new Loan(
                        rs.getInt("loan_id"),
                        rs.getInt("customer_id"),
                        rs.getDouble("loan_amount"),
                        rs.getDouble("interest_rate"),
                        rs.getInt("loan_term"),
                        rs.getString("status"),
                        rs.getString("created_at")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return loans;
    }

    // Retrieves all loans from the database
    public List<Loan> getAllLoans() {
        String sql = "SELECT * FROM Loans";
        List<Loan> loans = new ArrayList<>();
        try (PreparedStatement pstmt = connection.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                loans.add(new Loan(
                        rs.getInt("loan_id"),
                        rs.getInt("customer_id"),
                        rs.getDouble("loan_amount"),
                        rs.getDouble("interest_rate"),
                        rs.getInt("loan_term"),
                        rs.getString("status"),
                        rs.getString("created_at")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return loans;
    }

    // Deletes a loan from the database
    public boolean deleteLoan(int loanId) {
        String sql = "DELETE FROM Loans WHERE loan_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, loanId);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}

import gui.LoginGUI;
import services.DatabaseConnection;

public class Main {
    public static void main(String[] args) {
        // Initialize the application
        DatabaseConnection.initialize();
        LoginGUI login = new LoginGUI();
        login.setVisible(true);
    }
}

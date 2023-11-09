package application;


import application.news.User;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import serverConection.ConnectionManager;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextField;


public class LoginController {
//TODO Add all attribute and methods as needed 
	private LoginModel loginModel = new LoginModel();
	
	private User loggedUsr = null;
	
	private ConnectionManager connectionManager;
	
	@FXML
	private TextField usernameBox;

	@FXML
	private TextField passwordBox;

	public LoginController (){
	
		//Uncomment next sentence to use data from server instead dummy data
		loginModel.setDummyData(false);
	}
	
	User getLoggedUsr() {
		return loggedUsr;
		
	}
		
	void setConnectionManager (ConnectionManager connection) {
		this.connectionManager = connection;
		this.loginModel.setConnectionManager(connection);
	}
	
	private boolean isValidLogin(String username, String password) {
	    // Implement your login verification logic here
	    // Return true if the login is valid, false otherwise
	    // You might check against a database, hardcoded credentials, etc.
	    return false;
	}

	private void showAlert(String message) {
	    Alert alert = new Alert(AlertType.ERROR);
	    alert.setTitle("Login Error");
	    alert.setHeaderText(null);
	    alert.setContentText(message);
	    alert.showAndWait();
	}
	
	@FXML
	public void onLogin(ActionEvent event) {
	    String username = usernameBox.getText();
	    String password = passwordBox.getText();

	    try {
	        loginModel.setConnectionManager(this.connectionManager);
	        
	        
	        connectionManager.login(username, password);
	        
	        User user = loginModel.validateUser(username, password);
	        
	        if (user != null) {
	            // Login successful, you can now close the login window and proceed with the application
	            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
	            stage.close();

	            // Store the logged-in user (if needed)
	            loggedUsr = user;
	        } else {
	            showAlert("Wrong credentials");
	        }
	    } catch (Exception e) {
	        // Handle any exceptions related to the login process
	    	System.out.println(e.getMessage());
	        showAlert("Login error: " + e.getMessage());
	    }
	}


	@FXML
    public void onExit(ActionEvent event) {
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
    	stage.close();
    }
}
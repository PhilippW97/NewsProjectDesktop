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
	
	@FXML
	private TextField usernameBox;

	@FXML
	private TextField passwordBox;

	public LoginController (){
	
		//Uncomment next sentence to use data from server instead dummy data
		//loginModel.setDummyData(false);
	}
	
	User getLoggedUsr() {
		return loggedUsr;
		
	}
		
	void setConnectionManager (ConnectionManager connection) {
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
	    
	    if (isValidLogin(username, password)) {
	    	Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
	    	stage.close();
	    }
	    else {
	    	
	    	showAlert("Wrong credentials");
	    }
    }

	@FXML
    public void onExit(ActionEvent event) {
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
    	stage.close();
    }
}
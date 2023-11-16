package application;


import application.news.User;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.Window;
import serverConection.ConnectionManager;
import serverConection.exceptions.AuthenticationError;

import java.io.IOException;
import java.util.Properties;

import static application.AppScenes.READER;


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

/**
 * @author Guilherme Garrido
 *
 */
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

			/*
			 * username: DEV_TEAM_06
			 * password: 123006@3
			 */

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

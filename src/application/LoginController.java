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


public class LoginController {
//TODO Add all attribute and methods as needed
	private LoginModel loginModel = new LoginModel();

	private static User loggedUsr = null;
	@FXML
	TextField username;

	@FXML
	TextField password;


	@FXML
	Button login;

	public static NewsReaderController newsReaderController;
	public LoginController (){

		//Uncomment next sentence to use data from server instead dummy data
		//loginModel.setDummyData(false);

	}


	@FXML
	public void Login() throws AuthenticationError, IOException {
		Properties prop = Main.buildServerProperties();
		ConnectionManager connection = new ConnectionManager(prop);
		//Connecting as public (anonymous) for your group
		connection.setAnonymousAPIKey("ANON06_340"/*Put your group API Key here*/);
		//Login whitout login form:
		loginModel.setDummyData(false);
		loginModel.setConnectionManager(connection);
		User user = loginModel.validateUser(username.getText(), password.getText());
//		connection.login("DEV_TEAM_06", "123006@3"); //User: Reader2 and password "reader2"
//		loggedUsr  = new User ("DEV_TEAM_06",
//				Integer.parseInt(connection.getIdUser()));
		loggedUsr=user;
		if(loggedUsr!=null){
			Alert alert = new Alert(Alert.AlertType.INFORMATION);
			alert.setTitle("Successful landing");
			alert.setHeaderText("Successful landing");
			alert.setContentText("User id: "+loggedUsr.getIdUser());
			alert.showAndWait();
			loginModel.setDummyData(true);
			Stage stage= (Stage) login.getScene().getWindow();
			//newsReaderController.initialize();
			newsReaderController.setUsr(user);
			newsReaderController.setLoggedIn(true);
			newsReaderController.updatePermissions();
			newsReaderController.setConnectionManager(connection);
			stage.close();
		}else {
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setTitle("Login");
			alert.setHeaderText("Login failure");
			alert.setContentText("Check the user name and password!");
			alert.showAndWait();
		}

	}

	public static User getLoggedUsr() {
		return loggedUsr;

	}

	void setConnectionManager (ConnectionManager connection) {
		this.loginModel.setConnectionManager(connection);
	}
}

package application;

import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.function.UnaryOperator;
import java.util.logging.Level;
import java.util.logging.Logger;


//import org.omg.CORBA.portable.InputStream;

import application.news.Article;
import application.news.Categories;
import application.news.User;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import serverConection.ConnectionManager;
import serverConection.exceptions.AuthenticationError;
import serverConection.exceptions.ServerCommunicationError;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;


public class Main extends Application {
    private double xOffset = 0;
    private double yOffset = 0;

	@Override
	public void start(Stage primaryStage) {
		try {
			//This start method allow us to load a Scene (only one).
			//Uncomment the desire to load scene and comment the other ones
			/*
			 * We use an instance of Pane, so we don't worry about what kind of pane is used
			 * in the FXML file. Pane is the father of all container (BorderPane,
			 * FlowPane, AnchorPane ...
			 */
			/*Pane root = FXMLLoader.load(getClass().getResource(
					AppScenes.NEWS_DETAILS.getFxmlFile()));*/
			/*Pane root = FXMLLoader.load(getClass().getResource(
					AppScenes.IMAGE_PICKER.getFxmlFile()));*/
			//Code for reader main window
			Pane root = loadReaderMainWindow();


			Scene scene = new Scene(root,900,600);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setTitle("Nifty News - fastest news for you!");
			primaryStage.getIcons().add(new Image("file:images/newsIcon.png"));
			primaryStage.initStyle(StageStyle.DECORATED);
			primaryStage.setScene(scene);
			/**
			 * Next tow sentences are used to allow moving window by dragging primary mouse button.
			 */
			root.setOnMousePressed(new EventHandler<MouseEvent>() {
	            @Override
	            public void handle(MouseEvent event) {
	            	if (event.getButton() == MouseButton.PRIMARY) {
	            		xOffset = event.getSceneX();
		                yOffset = event.getSceneY();
	            	}
	            }
	        });
	        root.setOnMouseDragged(new EventHandler<MouseEvent>() {
	            @Override
	            public void handle(MouseEvent event) {
	            	if (event.getButton() == MouseButton.PRIMARY) {
	            		primaryStage.setX(event.getScreenX() - xOffset);
		                primaryStage.setY(event.getScreenY() - yOffset);
	            	}

	            }
	        });
			primaryStage.show();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}



	//reader main window
	final  Pane loadReaderMainWindow() throws IOException {
		Pane root = null;
		try {
			FXMLLoader loader = new FXMLLoader (getClass().getResource(
					AppScenes.READER.getFxmlFile()));
			root = loader.load();
			NewsReaderController controller = loader.<NewsReaderController>getController();

			//Create properties for server connection
			Properties prop = buildServerProperties();
			ConnectionManager connection = new ConnectionManager(prop);
			//Connecting as public (anonymous) for your group
//			connection.setAnonymousAPIKey("ANON06_340"/*Put your group API Key here*/);
//			//Login whitout login form:
//				connection.login("DEV_TEAM_06", "123006@3"); //User: Reader2 and password "reader2"
//			    User user = new User ("DEV_TEAM_06",
//				Integer.parseInt(connection.getIdUser()));
//				controller.setUsr(user);
			controller.setConnectionManager(connection);

			//end code for main window reader

		} catch(AuthenticationError e) {
			Logger.getGlobal().log(Level.SEVERE, "Error in loging process");
			e.printStackTrace();
		}
		return root;
	}

	final static Properties buildServerProperties() {
		Properties prop = new Properties();
		prop.setProperty(ConnectionManager.ATTR_SERVICE_URL, "https://sanger.dia.fi.upm.es/pui-rest-news/");
		prop.setProperty(ConnectionManager.ATTR_REQUIRE_SELF_CERT, "TRUE");

		/* For http & https proxy
		prop.setProperty(ConnectionManager.ATTR_PROXY_HOST, "http://proxy.fi.upm.es");
		prop.setProperty(ConnectionManager.ATTR_PROXY_PORT, "80");
		*/
		/* For proxy or apache password auth
		prop.setProperty(ConnectionManager.ATTR_PROXY_USER, "...");
		prop.setProperty(ConnectionManager.ATTR_PROXY_PASS, "...");
		*/
		return prop;
	}

}

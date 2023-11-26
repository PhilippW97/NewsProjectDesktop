/**
 *
 */
package application;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import javax.json.JsonObject;


import application.news.Article;
import application.news.Categories;
import application.news.User;
import application.utils.JsonArticle;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.web.HTMLEditor;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.*;
import serverConection.ConnectionManager;
import serverConection.exceptions.AuthenticationError;
import serverConection.exceptions.ServerCommunicationError;
import javafx.scene.web.HTMLEditor;

/**
 * @author Jiaqi, Deng, 233412
 *
 */
public class ArticleEditController {
	/**
	 * Connection used to send article to server after editing process
	 */
    private ConnectionManager connection;

    /**
     * Instance that represent an article when it is editing
     */
	private ArticleEditModel editingArticle;
	/**
	 * User whose is editing the article
	 */
	private User usr;
	//TODO add attributes and methods as needed

	@FXML
	private TextField Title;

	@FXML
	private TextField Subtitle;

	@FXML
	private ImageView image;

	@FXML
	private ChoiceBox<Categories> category;

	@FXML
	private Label Abstract_Body;

	@FXML
	private WebView body;

	@FXML
	private Label Type;
	Article article;

	NewsReaderController newsReaderController;

	public void setNewsReaderController(NewsReaderController newsReaderController) {
		this.newsReaderController = newsReaderController;
	}
	@FXML
	void onImageClicked(MouseEvent event) {
		if (event.getClickCount() >= 2) {
			Scene parentScene = ((Node) event.getSource()).getScene();
			FXMLLoader loader = null;
			try {
				loader = new FXMLLoader(getClass().getResource(AppScenes.IMAGE_PICKER.getFxmlFile()));
				Pane root = loader.load();
				Scene scene = new Scene(root);
				scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
				Window parentStage = parentScene.getWindow();
				Stage stage = new Stage();
				stage.initOwner(parentStage);
				stage.setScene(scene);
				stage.initStyle(StageStyle.UNDECORATED);
				stage.initModality(Modality.WINDOW_MODAL);
				stage.showAndWait();
				ImagePickerController controller = loader.<ImagePickerController>getController();
				Image image = controller.getImage();
				if (image != null) {
					editingArticle.setImage(image);
					this.image.setImage(image);
					//TODO Update image on UI
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

	/**
	 * Send and article to server,
	 * Title and category must be defined and category must be different to ALL
	 * @return true if only if article was been correctly send
	 */
	private boolean send() {
		String titleText = editingArticle.getTitle();
		Categories category = editingArticle.getCategory();

		if (titleText == null || category == null || titleText.trim().isEmpty()) {
			// Show an error message if title or category is empty
			Alert alert = new Alert(AlertType.ERROR, "Title is mandatory.", ButtonType.OK);
			alert.showAndWait();
			return false;
		}

		try {
			// Commit any pending changes before sending
			editingArticle.commit();

			// Send the article using the connection manager
			int articleId = connection.saveArticle(editingArticle.getArticleOriginal());

			if (articleId > 0) {
				// Server successfully saved the article, update UI or handle as needed
				newsReaderController.updatePermissions();
				return true;
			} else {
				// Server failed to save the article
				Alert alert = new Alert(AlertType.ERROR, "Failed to send the article to the server.", ButtonType.OK);
				alert.showAndWait();
				return false;
			}
		} catch (ServerCommunicationError e) {
			// Handle server communication error
			Alert alert = new Alert(AlertType.ERROR, "Failed to communicate with the server.", ButtonType.OK);
			alert.showAndWait();
			return false;
		}
	}

	/**
	 * This method is used to set the connection manager which is
	 * needed to save a news
	 * @param connection connection manager
	 */
	void setConnectionManager(ConnectionManager connection) {
		this.connection = connection;
		// Enable send and back button or perform any other UI updates as needed
	}

	/**
	 *
	 * @param usr the usr to set
	 */
	void setUsr(User usr) {
		this.usr = usr;
		// Update UI and controls or perform any other actions as needed
	}

	/**
	 * Get the article without changes since last commit
	 * @return article without changes since last commit
	 */
	Article getArticle() {
		Article result = null;
		if (this.editingArticle != null) {
			result = this.editingArticle.getArticleOriginal();
		}
		return result;
	}

	/**
	 * Save an article to a file in a JSON format.
	 * The article must have a title.
	 */
	private void write() {
		// Consolidate all changes
		this.editingArticle.commit();

		// Removes special characters not allowed for filenames
		String name = this.editingArticle.getTitle().replaceAll("\\||/|\\\\|:|\\?", "");
		String fileName = "saveNews//" + name + ".news";
		JsonObject data = JsonArticle.articleToJson(this.editingArticle.getArticleOriginal());
		try (FileWriter file = new FileWriter(fileName)) {
			file.write(data.toString());
			file.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * PRE: User must be set
	 *
	 * @param article the article to set
	 */
	void setArticle(Article article) {
		this.editingArticle = (article != null) ? new ArticleEditModel(article) : new ArticleEditModel(usr);

		if (article != null) {
			this.article = article;
			Title.setText(article.getTitle());
			WebEngine webEngine = body.getEngine();
			webEngine.loadContent(article.getAbstractText());
			category.getSelectionModel().select(Categories.valueOf(article.getCategory().toUpperCase(Locale.ENGLISH)));
			image.setImage(article.getImageData());
			Subtitle.setText(article.getSubtitle());
		}
	}


	@FXML
	void initialize() {
		image.setImage(new Image("file:resources/1.png"));
		Type.setText("Type:Txt");
		article = new Article();
		HTMLEditor htmlEditor = new HTMLEditor();
		htmlEditor.setHtmlText(article.getAbstractText());
		article.setAbstractText("");
		ArrayList<Categories> categories = new ArrayList<>();
		categories.add(Categories.ECONOMY);
		categories.add(Categories.INTERNATIONAL);
		categories.add(Categories.SPORTS);
		categories.add(Categories.NATIONAL);
		categories.add(Categories.TECHNOLOGY);
		category.setItems(FXCollections.observableList(categories));
	}

	@FXML
	void onBack(ActionEvent event) {
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		stage.close();
	}

	@FXML
	void Send_and_Back(Event event) {
		editingArticle.commit();

		try {
			if (editingArticle.getTitle() == null || editingArticle.getTitle().equals("") || editingArticle.getCategory() == null || editingArticle.getCategory().equals("")) {
				Alert alert = new Alert(AlertType.ERROR, "It's impossible to send the article! Title and category are mandatory.", ButtonType.OK);
				alert.showAndWait();
			} else {
				if (send()) {
					int i = connection.saveArticle(editingArticle.getArticleOriginal());
					newsReaderController.updatePermissions();
					Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
					stage.close();
				}
			}
		} catch (Exception e) {
			Alert alert = new Alert(AlertType.ERROR, "It's impossible to send the article! Title and category are mandatory.", ButtonType.OK);
			alert.showAndWait();
		}
	}

	@FXML
	void Save_to_File() {
		editingArticle.commit();

		try {
			if (editingArticle.getTitle() == null || editingArticle.getTitle().equals("")) {
				Alert alert = new Alert(AlertType.ERROR, "It's impossible to save the article! Title is mandatory.", ButtonType.OK);
				alert.showAndWait();
			} else {
				write();
				Alert alert = new Alert(AlertType.CONFIRMATION, "Successfully saved the article in the \"saveNews\" folder inside the project.", ButtonType.OK);
				alert.showAndWait();
			}
		} catch (Exception e) {
			Alert alert = new Alert(AlertType.ERROR, "It's impossible to save the article! Title is mandatory.", ButtonType.OK);
			alert.showAndWait();
		}
	}

	@FXML
	void html_or_text() {
		if (editingArticle.isHtml()) {
			body.getEngine().loadContent(editingArticle.getBodyText(), "text/plain");
			Type.setText("Type:Html");
		} else {
			body.getEngine().loadContent(editingArticle.getBodyText(), "text/html");
			Type.setText("Type:Txt");
		}

		editingArticle.toggleHtml();
	}

	@FXML
	void Abstract_or_Body() {

		System.out.println("Abstract Text: " + editingArticle.getAbstractText());
		System.out.println("Body Text: " + editingArticle.getBodyText());

		WebEngine webEngine = body.getEngine();
		if (editingArticle.isAbstract()) {
			webEngine.loadContent(editingArticle.bodyTextProperty().get());
			Abstract_Body.setText("Body");
		} else {
			webEngine.loadContent(editingArticle.abstractTextProperty().get());
			Abstract_Body.setText("Abstract");
		}

		editingArticle.toggleAbstract();
	}
}

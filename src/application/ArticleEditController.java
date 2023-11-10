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
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import serverConection.ConnectionManager;
import serverConection.exceptions.AuthenticationError;
import serverConection.exceptions.ServerCommunicationError;

/**
 * @author ÁngelLucas
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
	private TextArea body;

	@FXML
	private ImageView image;

	@FXML
	private Button back;

	@FXML
	private Button Send_and_Back;

	@FXML
	private Button Save_to_file;

	@FXML
	private Button Text_or_html;

	@FXML
	private Button Abstract_or_Body;

	@FXML
	private ChoiceBox<Categories> category;



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
		String titleText = getArticle().getTitle(); // TODO Get article title
		Categories category = Categories.valueOf(getArticle().getCategory().toUpperCase(Locale.ENGLISH)); //TODO Get article cateory
		if (titleText == null || category == null ||
				titleText.equals("") || category == Categories.ALL) {
			Alert alert = new Alert(AlertType.ERROR, "Imposible send the article!! Title and categoy are mandatory", ButtonType.OK);
			alert.showAndWait();
			return false;
		}
//TODO prepare and send using connection.saveArticle( ...)

		return true;
	}

	/**
	 * This method is used to set the connection manager which is
	 * needed to save a news
	 * @param connection connection manager
	 */
	void setConnectionMannager(ConnectionManager connection) {
		this.connection = connection;
		//TODO enable send and back button
	}

	/**
	 *
	 * @param usr the usr to set
	 */
	void setUsr(User usr) {
		this.usr = usr;
		//TODO Update UI and controls

	}



	@FXML
	void Save_to_File(){

		Article article=new Article();
		article.setTitle(Title.getText());
		article.setCategory(category.getValue().toString());
		article.setImageData(image.getImage());
		article.setSubtitle(Subtitle.getText());
		article.setBodyText(body.getText());
		article.setIdUser(usr.getIdUser());
		this.setArticle(article);
		write();
	}

	@FXML
	void Send_and_Back(Event event) throws AuthenticationError, ServerCommunicationError {
		Article article=new Article();
		article.setTitle(Title.getText());
		article.setCategory(category.getValue().toString());
		article.setImageData(image.getImage());
		article.setSubtitle(Subtitle.getText());
		article.setBodyText(body.getText());
		article.setAbstractText(body.getText());
		article.setIdUser(usr.getIdUser());
		this.setArticle(article);
		if(send()){
			int i = connection.saveArticle(article);
			Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			stage.close();


		}
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
	 * PRE: User must be set
	 *
	 * @param article
	 *            the article to set
	 */
	void setArticle(Article article) {
		this.editingArticle = (article != null) ? new ArticleEditModel(article) : new ArticleEditModel(usr);
		if(article!=null) {
			Title.setText(article.getTitle());
			body.setText(article.getAbstractText());
			body.setWrapText(true);
			category.getSelectionModel().select(Categories.valueOf(article.getCategory().toUpperCase(Locale.ENGLISH)));
			image.setImage(article.getImageData());
			Subtitle.setText(article.getSubtitle());
		}



		//TODO update UI
	}

	@FXML
	void onBack(ActionEvent event) {
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		stage.close();
	}

	/**
	 * Save an article to a file in a json format
	 * Article must have a title
	 */
	private void write() {
		//TODO Consolidate all changes
		this.editingArticle.commit();
		//Removes special characters not allowed for filenames
		String name = this.getArticle().getTitle().replaceAll("\\||/|\\\\|:|\\?","");
		String fileName ="saveNews//"+name+".news";
		JsonObject data = JsonArticle.articleToJson(this.getArticle());
		  try (FileWriter file = new FileWriter(fileName)) {
	            file.write(data.toString());
	            file.flush();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	}
	@FXML
	void initialize() {
	image.setImage(new Image("file:resources/1.png"));
		ArrayList<Categories> strings=new ArrayList<>();
		strings.add(Categories.ECONOMY);
		strings.add(Categories.INTERNATIONAL);
		strings.add(Categories.SPORTS);
		strings.add(Categories.NATIONAL);
		strings.add(Categories.TECHNOLOGY);
	category.setItems(FXCollections.observableList(strings));
	}
}

/**
 *
 */
package application;


import application.news.Article;
import application.news.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.awt.*;

/**
 * @author ÁngelLucas
 *
 */
public class ArticleDetailsController {
	//TODO add attributes and methods as needed
	/**
	 * Registered user
	 */
	@FXML
	private Label userIdLabel;
	@FXML
	private Label titleLabel;
	@FXML
	private Label subtitleLabel;
	@FXML
	private Label categoryLabel;
	@FXML
	private ImageView imageView;
	@FXML
	private WebView webView;
	private User usr;

	/**
	 * Article to be shown
	 */
	private Article article;

	/**
	 * @param usr the usr to set
	 */
	void setUsr(User usr) {
		this.usr = usr;
		if (usr == null) {
			return; //Not logged user
		}
		//TODO Update UI information
		else {
			userIdLabel.setText(usr.toString());
		}
	}

	/**
	 * @param article the article to set
	 */
	void setArticle(Article article) {
		this.article = article;
		//TODO complete this method
		// Update UI with article information
		this.titleLabel.setText(article.getTitle());
		this.subtitleLabel.setText(article.getSubtitle());
		this.categoryLabel.setText(article.getCategory());
		// Load the HTML content into WebView
		WebEngine webEngine = webView.getEngine();
		System.out.println("hier wird geloggt "+ this.article);
		webEngine.loadContent(article.getBodyText(), "text/html");
		// Similarly, update other UI components with article details as needed
	}

	@FXML
	void onBack(ActionEvent event) {
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		stage.close();
	}

	@FXML
	void onBody(ActionEvent event) {
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
	}

	@FXML
	void initialize() {
		assert userIdLabel != null : "fx:id=\"userid\" was not injected: check your FXML file ''.";
		assert titleLabel != null : "fx:id=\"titleLabel\" was not injected: check your FXML file ''.";
		assert subtitleLabel != null : "fx:id=\"subtitleLabel\" was not injected: check your FXML file ''.";
		assert categoryLabel != null : "fx:id=\"categoryLabel\" was not injected: check your FXML file ''.";
		assert imageView != null : "fx:id=\"imageView\" was not injected: check your FXML file ''.";
		assert webView != null : "fx:id=\"webView\" was not injected: check your FXML file ''.";
	}
}

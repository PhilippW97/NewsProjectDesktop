/**
 * 
 */
package application;

import java.io.File;
import java.io.IOException;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

import com.jfoenix.controls.JFXButton;

import application.news.Article;
import application.news.User;
import application.utils.JsonArticle;
import application.utils.exceptions.ErrorMalFormedArticle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import serverConection.ConnectionManager;

/**
 * @author ÁngelLucas
 *
 */
public class NewsReaderController {

	private NewsReaderModel newsReaderModel = new NewsReaderModel();
	private User usr;
	private boolean articleSelected=false;
	private boolean loggedIn=false;
	private Article currentArticle;
	
	private ObservableList<Article> allArticles;
	@FXML
	private ListView<Article> articleList;
    @FXML
    private TextField textFilter;
    @FXML
    private Label articleSelectedLabel;
    @FXML
    private WebView abstractViewer;
    @FXML
    private ImageView imageViewer;
    @FXML
    private MenuButton categoryMenu;
    @FXML
    private JFXButton articleDetailsButton;
    @FXML
    private JFXButton articleEditButton;
    @FXML
    private JFXButton articleDeleteButton;
    @FXML
    private JFXButton articleNewButton;
    @FXML
    private Label userWelcomeLabel;
    
    
    
    private FilteredList<Article> filteredData;
    


	public NewsReaderController() {
		//TODO
		//Uncomment next sentence to use data from server instead dummy data
		//newsReaderModel.setDummyData(false);
		//Get text Label
		
	}

		

	private void getData() {
		//TODO retrieve data and update UI
		//The method newsReaderModel.retrieveData() can be used to retrieve data  
		newsReaderModel.retrieveData();
		this.allArticles=newsReaderModel.getArticles();
		
	}
	private Predicate<Article> createCombinedFilter(String categoryText, String filterText) {
	    return article -> {
	        // Check category filter
	        boolean categoryFilterPass = (categoryText == null || categoryText.isEmpty()) || categoryText.equals("All") ||article.getCategory().equals(categoryText);

	        // Check text filter
	        boolean textFilterPass = (filterText == null || filterText.isEmpty()) || article.getTitle().toLowerCase().contains(filterText.toLowerCase());

	        // Combine both filters with an AND condition
	        return categoryFilterPass && textFilterPass;
	    };
	}
	
	public void updatePermissions() {
		if(loggedIn) {
		    articleNewButton.setDisable(false);
		    articleNewButton.setVisible(true);
		}
		else {
		    articleNewButton.setDisable(true);
		    articleNewButton.setVisible(false);
		}
	}
	
	
	@FXML
    void initialize() {
		//dummy user
		usr=new User("testLogin",1);
		userWelcomeLabel.setText("Welcome, "+usr.getLogin()+"!");
		articleSelected=false;
		getData();
		articleEditButton.setDisable(true);
	    articleDeleteButton.setDisable(true);
	    articleDetailsButton.setDisable(true);
	    articleNewButton.setDisable(true);
	    articleEditButton.setVisible(false);
	    articleDeleteButton.setVisible(false);
	    articleDetailsButton.setVisible(false);
	    articleNewButton.setVisible(false);
        assert articleList != null : "fx:id=\"articleList\" was not injected: check your FXML file 'FirstWindow.fxml'.";
        assert textFilter != null : "fx:id=\"textFilter\" was not injected: check your FXML file 'FirstWindow.fxml'.";
        assert articleSelectedLabel != null : "fx:id=\"articleSelected\" was not injected: check your FXML file 'FirstWindow.fxml'.";
        filteredData = new FilteredList<>(allArticles, article -> true);
        this.articleList.setItems(filteredData);
        
        UnaryOperator<TextFormatter.Change> filterValidationFormatter = change -> {
        	if (change.isDeleted()) { //For deleted and replace (replace is a deleted operation with new text)
        		if (!change.getText().matches("[a-zA-ZÁ-ÿ0-9 ]+")) { //If is replace operation and text is not a character
        			change.setText("");	 //text is number or symbol
        		}
        	}else

            if(!change.getText().matches("[a-zA-ZÁ-ÿ0-9 ]+")){
                change.setText(""); //else make no change
                change.setRange(    //don't remove any selected text either.
                change.getRangeStart(),
                change.getRangeStart()
                );
            }
            return change;

           };
       textFilter.setTextFormatter(new TextFormatter<String>(filterValidationFormatter));
     // Add listener to articleList control whenever an article is selected
     this.articleList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Article>() {
		@Override
		/**
		 * When the selected element is changed this event handler is called
		 */
		public void changed(ObservableValue<? extends Article> observable, Article oldArticle, Article newArticle) {
			if (newArticle != null){
				articleSelected=true;
				currentArticle=newArticle;
				// Title
				articleSelectedLabel.setText(currentArticle.toString());
				// Abstract
				WebEngine webEngine= abstractViewer.getEngine();
				webEngine.loadContent(currentArticle.getAbstractText(),"text/html");
				// Image
				imageViewer.setImage(currentArticle.getImageData());
				System.out.println("current user id: "+usr.getIdUser()+" user id of article "+newArticle.getIdUser());
				// Handle button access ability based on permissions
				articleDetailsButton.setDisable(false);
				articleDetailsButton.setVisible(true);
				if(usr.getIdUser()!=currentArticle.getIdUser()) {
					articleEditButton.setDisable(true);
				    articleDeleteButton.setDisable(true);
				    articleEditButton.setVisible(false);
				    articleDeleteButton.setVisible(false);
				}
				if(loggedIn) {
					articleEditButton.setVisible(true);
					articleDeleteButton.setVisible(true);
					if(usr.getIdUser()==currentArticle.getIdUser()) {
						articleEditButton.setDisable(false);
						articleDeleteButton.setDisable(false);						
					}
				}
				
			}
			else { //Nothing selected
				articleSelectedLabel.setText("");
				WebEngine webEngine = abstractViewer.getEngine();
			    webEngine.loadContent(""); // Clear the WebView
			    imageViewer.setImage(null);
			}
		}
    
     });
    }
	
	@FXML
	public void handleCategorySelection(ActionEvent event) {
	    MenuItem selectedCategory = (MenuItem) event.getTarget();
	    String categoryText = selectedCategory.getText();
	    categoryMenu.setText(categoryText);

	    // Get the text associated with the key pressed
	    String filterText = textFilter.getText();

	    // Update the filter with the combined predicate
	    filteredData.setPredicate(createCombinedFilter(categoryText, filterText));
	}
	
	public void loadNewWindow(ActionEvent event, String fxmlFilename,String title) {
    	Window parentWindow = ((Node) event.getSource()).getScene().getWindow();
    	Pane root;
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFilename));
			root= (Pane) loader.load();
			if(fxmlFilename.equals("ArticleDetails.fxml")){
				ArticleDetailsController controller = loader.<ArticleDetailsController>getController();
	            if (currentArticle != null) {
	                controller.setArticle(currentArticle);
					System.out.println(currentArticle.getBodyText());
	            }
			}
			// Article Edit and Create are handled in the same controller
			else if(fxmlFilename.equals("ArticleEdit.fxml")) {
				ArticleEditController controller = loader.<ArticleEditController>getController();
				System.out.println("subtitle: "+currentArticle.getSubtitle());
				if(title.equals("Article Edit")) {
		            if (currentArticle != null) {
		                controller.setArticle(currentArticle);
		            }
				}
				// don't send article when you create a new one
				else if(title.equals("Article Create")) {
					controller.setArticle(null);
				}
			}
            
			Scene nextScene = new Scene(root);
			Stage nextWindow = new Stage();
		 	nextWindow.initStyle(StageStyle.DECORATED);
            nextWindow.setTitle(title);
            nextWindow.setScene(nextScene);
            // Specifies the modality for new window.
            nextWindow.initModality(Modality.WINDOW_MODAL);
            // Specifies the owner Window (parent) for new window
            nextWindow.initOwner(parentWindow);
            nextWindow.show();
           
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@FXML
	public void viewArticle(ActionEvent event) {
		if(articleSelected) {
			loadNewWindow(event, "ArticleDetails.fxml","Article Dedtails");
		}
	}
	
	@FXML
	public void editArticle(ActionEvent event) {
		loadNewWindow(event,"ArticleEdit.fxml","Article Edit");
	}
	@FXML
	public void deleteArticle(ActionEvent event) {
		if(articleSelected) {
			this.newsReaderModel.deleteArticle(currentArticle);
		}
	}
	@FXML
	public void createArticle(ActionEvent event) {
		//TODO: send to edit article page whether logged in or not, so Deng can decide whether to save to a file or server
		loadNewWindow(event,"ArticleEdit.fxml","Article Create");
	}
	@FXML
	public void loadArticleFromFile(ActionEvent event) {
		Window parentWindow = ((Node) event.getSource()).getScene().getWindow();
		ExtensionFilter ex1 = new ExtensionFilter("Text Files", "*.txt");
		FileChooser fileChooser = new FileChooser();
		fileChooser.getExtensionFilters().add(ex1);
		fileChooser.setTitle("Choose Article File");
		
		//fileChooser.setInitialDirectory(new File("C:/Files"));
		
		File selectedFile = fileChooser.showOpenDialog(parentWindow);
		if (selectedFile != null) {
			System.out.println("Open File");
			System.out.println(selectedFile.getPath());
			System.out.println(JsonArticle.readFile(selectedFile.getPath()));
			try {
				Article loadedArticle= JsonArticle.jsonToArticle(JsonArticle.readFile(selectedFile.getPath()));
				currentArticle=loadedArticle;
				editArticle(event);
				
			} catch (ErrorMalFormedArticle e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
	}
	@FXML
	public void handleLogin(ActionEvent event) {
		loggedIn=!loggedIn;
		if(loggedIn) {
			System.out.print("is logged in");
		}
		else {
			System.out.println("not logged in");
		}
		updatePermissions();
		
	}

	@FXML
	void onNewChar(KeyEvent event) {
	    // Get the text associated with the key pressed
	    String filterText = textFilter.getText();

	    // Get the selected category
	    String categoryText = categoryMenu.getText();

	    // Update the filter with the combined predicate
	    filteredData.setPredicate(createCombinedFilter(categoryText, filterText));
	}

	

	/**
	 * @return the usr
	 */
	User getUsr() {
		return usr;
	}

	void setConnectionManager (ConnectionManager connection){
		this.newsReaderModel.setDummyData(false); //System is connected so dummy data are not needed
		this.newsReaderModel.setConnectionManager(connection);
		this.getData();
	}
	
	/**
	 * @param usr the usr to set
	 */
	void setUsr(User usr) {
		
		this.usr = usr;
		//Reload articles
		this.getData();
		//TODO Update UI
	}


}

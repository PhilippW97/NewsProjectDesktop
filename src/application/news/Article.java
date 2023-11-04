package application.news;

import java.awt.image.BufferedImage;
import java.util.Date;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
/**
 * This class represents an article. An article is formed by:
 * - A title
 * - A subtitle
 * - An Image
 * - HTML text for the abstract
 * - HTML text for the body
 * - An article could be mark as deleted (only administrator users can see an modify it)
 * - An article belong to one category
 * @author agonzalez
 */
public class Article {
	//Binding attributes: getters and setters methods are providing in order to hiding the use of properties
	private BooleanProperty isDeleted;
	private StringProperty  abstractText;
	private StringProperty  bodyText;
	private StringProperty title;
	private StringProperty  subtitle;
	private Date publicationDate;
	
	//Author id User
	private int idUser;
	//Article Image
	private Image imageData;
	//IDs. These ids are needed for storing the information in the server
	private int idArticle = 0; //0 -> New Article, so don't exists in the BD
	private int idImage = 0; //0 -> New Image, so don't exists in the BD
	
	private String  category = Categories.ALL.name();
	//if needBeSaved is true the article was modified by a set method or by a property 
	private boolean needBeSaved = false;
	/**
	 * Copy constructor
	 * @param org article to be copied
	 */
	public Article (Article org){
		this(org.getTitle(), org.idUser, org.category, org.getAbstractText(),
				org.getBodyText(),	org.publicationDate, null);
		this.setDeleted(org.getDeleted());
		this.setSubtitle(org.getSubtitle());
		this.setImageData(org.getImageData());
	}
	
	public Article(String title, int idUser, String category) {
		this(); //Calling default constructor
		this.title.setValue(title);
		this.idUser = idUser;
		this.category = category;
	}
	
	public Article(String title, int idUser, String category, String abstractText, String bodyText, 
			Date publicationDate,  String urlImage) {
		this(title, idUser, category,abstractText,urlImage);
		this.bodyText.setValue(bodyText);
		if (publicationDate!=null){
			this.publicationDate = (Date)publicationDate.clone();
		}
		else{
			this.publicationDate = publicationDate;
		}
	}
	
	public Article(String title, int idUser, String category, String abstractText) {
		this( title, idUser, category);
		this.abstractText.setValue(abstractText);
	}
	
	public Article(String title, int idUser, String category,
			String abstractText,String urlImage) {
		this(title, idUser, category,abstractText);
		this.setUrlImage(urlImage);
	}
	
	/**
	 * Default constructor
	 */
	public Article()
	{
		abstractText =  new SimpleStringProperty(this, "abstractText","");
		bodyText =  new SimpleStringProperty(this, "bodyText","");
		title = new SimpleStringProperty(this, "title","");
		subtitle = new SimpleStringProperty(this, "subtitle","");
		isDeleted= new SimpleBooleanProperty (this,"isDeleted", false);
		needBeSaved = false;
	}
	
	/**
	 * Return a string with the article title
	 */
	public String toString()
	{
		return this.title.getValue();
	}
	
	//Getter and setters
	
	public boolean getDeleted(){
	 return isDeleted.getValue();	
	}
	
	public void setDeleted(boolean deleted){
	 isDeleted.setValue(deleted);	
	}
	
	
	public boolean isDeleted(){
		return isDeleted.getValue();
	}
	
	/**
	 * Provide access to isDeleted property. 
	 * Useful for binding operations
	 * @return isDeletd property
	 */
	public BooleanProperty isDeletedProperty(){
		return isDeleted;
	}
	
	/**
	 * @return the abstractText
	 */
	public String getAbstractText() {
		return abstractText.getValue();
	}
	
	/**
	 * @param string the abstractText to set
	 */
	public void setAbstractText(String string) {
		this.abstractText.setValue(string);
	}
	
	/**
	 * Provide access to abstractText property. 
	 * Useful for binding operations
	 * @return abstract property
	 */
	public StringProperty abstractTextProperty(){
		return abstractText;
	}
	
	
	/**
	 * @return the bodyText
	 */
	public String getBodyText() {
		return bodyText.getValue();
	}
	
	/**
	 * @param bodyText the bodyText to set
	 */
	public void setBodyText(String bodyText) {
		this.bodyText.setValue(bodyText);
	}
	
	/**
	 * Provide access to bodyText property. 
	 * Useful for binding operations
	 * @return body property
	 */
	public StringProperty bodyTextProperty(){
		return bodyText;
	}
	
	/**
	 * @return the subtitle
	 */
	public String getSubtitle() {
		return subtitle.getValue();
	}
	/**
	 * @param subtitle the subtitle to set
	 */
	public void setSubtitle(String subtitle) {
		this.subtitle.setValue(subtitle);
	}
	
	
	/**
	 * Provide access to subtitle property. 
	 * Useful for binding operations
	 * @return title property
	 */
	public StringProperty subtitleProperty(){
		return subtitle;
	}
	
	/**
	 * @return the title
	 */
	public String getTitle() {
		return title.getValue();
	}
	
	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title.setValue(title);
	}
	
	/**
	 * Provide access to title property. 
	 * Useful for binding operations
	 * @return title property
	 */
	public StringProperty titleProperty(){
		return title;
	}
	
	/**
	 * @return the publicationDate
	 */
	public Date getPublicationDate() {
		return publicationDate;
	}
	
	/**
	 * @param publicationDate the publicationDate to set
	 */
	public void setPublicationDate(Date publicationDate) {
		this.publicationDate = publicationDate;
	}
	
	/**
	 * @return the idUser
	 */
	public int getIdUser() {
		return idUser;
	}
	/**
	 * @param idUser the idUser to set
	 */
	public void setIdUser(int idUser) {
		this.idUser = idUser;
	}
	
	/**
	 * @param urlImage the urlImage to set
	 */
	public void setUrlImage(String urlImage) {
		if (urlImage!=null){
			imageData = new Image(urlImage, false);	
			this.setNeedBeSaved(true);
		}
		
	}
	
	/**
	 * 
	 * @return the image data
	 */
	public Image getImageData(){
		return this.imageData;
	}
	
	/**
	 * Set the image data
	 * @param data data for the image
	 */
	public void setImageData(BufferedImage data){
		this.imageData = SwingFXUtils.toFXImage(data, null);
		this.setNeedBeSaved(true);
	}
	
	/**
	 * Set the image data
	 * @param data image to use in the article
	 */
	public void setImageData(Image data){
		this.imageData = data;
		this.setNeedBeSaved(true);
	}
	
	/**
	 * @return the category
	 */
	public String getCategory() {
		return category;
	}
	
	/**
	 * @param category the category to set
	 */
	public void setCategory(String category) {
		this.category = category;
		this.setNeedBeSaved(true);
	}

	/**
	 * @return the idArticle
	 */
	public int getIdArticle() {
		return idArticle;
	}

	/**
	 * @param idArticle the idArticle to set
	 */
	public void setIdArticle(int idArticle) {
		this.idArticle = idArticle;
	}

	/**
	 * @return the idImage
	 */
	public int getIdImage() {
		return idImage;
	}

	/**
	 * @param idImage the idImage to set
	 */
	public void setIdImage(int idImage) {
		this.idImage = idImage;
	}
	

	/**
	 * @return the needBeSaved
	 */
	public boolean isNeedBeSaved() {
		return needBeSaved;
	}	
	
	public void setNeedBeSaved(boolean need){
		this.needBeSaved = need;
	}
}
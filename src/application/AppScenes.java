/**
 * 
 */
package application;

/**
 * Contain all app scenes
 * @author ÁngelLucas
 *
 */
public enum AppScenes {
 LOGIN("Login.fxml"), READER("NewsReader.fxml"), 
 NEWS_DETAILS ("ArticleDetails.fxml"),
 EDITOR("ArticleEdit.fxml"), ADMIN("AdminNews.fxml")
		 ,IMAGE_PICKER("ImagePicker.fxml")
		 /*,IMAGE_PICKER("ImagePickerMaterialFX.fxml")*/
		 /*,IMAGE_PICKER("ImagePickerMaterailDesignJfoneix.fxml")*/; 
 private String fxmlFile;
 
 private AppScenes (String file){
	 this.fxmlFile = file;
 }
 
 /**
  * Get the FXML file associated to form 
  * @return
  */
 public String getFxmlFile()
 {
	 return this.fxmlFile;
 }
 
}

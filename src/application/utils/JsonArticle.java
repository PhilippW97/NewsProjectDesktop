/**
 * 
 */
package application.utils;

import java.util.HashMap;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Base64;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import application.news.Article;
import application.utils.exceptions.ErrorMalFormedArticle;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

/**
 * This class provide services for translate an article to JSon and viceversa
 * 
 * @author agonzalez
 *
 */
public class JsonArticle {
	private static Map<String, String> keys;

	/**
	 * Build a JsonObject for the given article
	 * 
	 * @param article article to be transformed
	 * @return json data
	 */
	public static JsonObject articleToJson(Article article) {
		JsonObject result = null;
		JsonObjectBuilder buildJSon = Json.createObjectBuilder();
		initKeys();
		// Extract article data
		String data = article.getTitle();
		data = (data == null) ? "" : data;
		buildJSon.add(keys.get("Title"), data);
		data = article.getSubtitle();
		data = (data == null) ? "" : data;
		buildJSon.add(keys.get("Subtitle"), data);
		data = article.getBodyText();
		data = (data == null) ? "" : data;
		data = extractBody(data); 
		buildJSon.add(keys.get("Body"), data);
		data = article.getAbstractText();
		data = (data == null) ? "" : data;
		data = extractBody(data); 
		buildJSon.add(keys.get("Abstract"), data);
		Image image = article.getImageData();
		// Converting image data into a String
		String stringData = "";
		if (image != null) {
			stringData = imagetToString(image);
		}
		buildJSon.add(keys.get("Image"), stringData);
		int idArticle = article.getIdArticle();
		if (idArticle > 0) { // Article has a valid ID so it must be added
			buildJSon.add(keys.get("idArticle"), "" + idArticle);
		}

		int idUser = article.getIdUser();
		if (idUser > 0) {// Article has a valid user so it must be added
			buildJSon.add(keys.get("idUser"), "" + idUser);
		}

		// buildJSon.add (keys.get("Publish"), article.isPublish());
		buildJSon.add(keys.get("Category"), article.getCategory());
		result = buildJSon.build();
		return result;
	}

	/**
	 * Create an article from a JsonObject
	 * 
	 * @param articleData json data
	 * @return the article
	 * @throws ErrorMalFormedArticle an article was found without title or category
	 */
	public static Article jsonToArticle(JsonObject articleData) throws ErrorMalFormedArticle {
		Article result = null;
		initKeys();
		String title = articleData.getString(keys.get("Title"), "");
		String subtitle = articleData.getString(keys.get("Subtitle"), "");
		String abstractText = articleData.getString(keys.get("Abstract"), "");
		String bodyText = articleData.getString(keys.get("Body"), "");
		String category = articleData.getString(keys.get("Category"), "");
		String deleted = articleData.getString(keys.get("Deleted"), "0");
		Boolean isDeleted = deleted.equals("1");
		// Boolean isPublish = articleData.getBoolean(keys.get("Publish"));
		if (title.equals("") || category.equals("")) {
			// Not is a valid news
			throw new ErrorMalFormedArticle((title.equals("") ? "title " : "category ") + "is requiered");
		}
		int idUser = -1;
		if (articleData.containsKey(keys.get("idUser"))) {
			String idUserAux = articleData.getString(keys.get("idUser"));
			idUser = Integer.parseInt(idUserAux);
		}

		result = new Article(title, idUser, category, abstractText, bodyText, null, null);
		result.setSubtitle(subtitle);
		result.setDeleted(isDeleted);
		// Be careful. If key dosen't exists a null pointer exception will be raised
		String imageData = articleData.getString(keys.get("Image"), null);
		//2022-09-29 If image is not present we try with thumbnail_image
		if (imageData == null  ) {
			//look for thumbnail
			imageData = articleData.getString(keys.get("thumbnail"), null);
		}
		// Sometimes server return "null" as imageData -> a bug server
		if (imageData != null && !imageData.equals("null")) {
			BufferedImage img = StringToBufferedImage(imageData);
			if (img == null) {
				System.err.println(
						"Img da null con los siguentes datos: " + imageData + " id Articulo: " + result.getIdArticle());
			} else {
				result.setImageData(img);
			}
		}

		if (articleData.containsKey(keys.get("idArticle"))) {
			String id = articleData.getString(keys.get("idArticle"));
			result.setIdArticle(Integer.parseInt(id));
		}

		return result;
	}

	/**
	 * Convert a image data given in an string into an BufferedImage 
	 * @param imageData String with image data
	 * @return the corresponding image
	 */
	private static BufferedImage StringToBufferedImage(String imageData) {
		BufferedImage img = null;
		ByteArrayInputStream arrayData = new ByteArrayInputStream(Base64.getDecoder().decode(imageData));
		try {
			img = ImageIO.read(arrayData);
			arrayData.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return img;
	}

	/**
	 * This method load a file that contains json data into a jsonObject If file
	 * can't be read this method return null
	 * 
	 * @param fileName File to read
	 * @return jsonObject 
	 */
	public static JsonObject readFile(String fileName) {
		JsonObject result = null;
		try (FileReader in = new FileReader(fileName)) {
			JsonReader reader = Json.createReader(in);
			result = reader.readObject();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * this method transform an image into a String in order to attach it 
	 * @param image to be converted
	 * @return string corresponding to the given image
	 */
	private static String imagetToString(Image image) {// imageToString
		BufferedImage imageData = SwingFXUtils.fromFXImage(image, null);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			ImageIO.write(imageData, "png", bos);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		byte[] imageBytes = bos.toByteArray();
		String stringData = Base64.getEncoder().encodeToString(imageBytes);
		return stringData;
	}// imageToString

	/**
	 * This method return body tag from html string. If there is'nt body the original
	 * text is returned
	 * 
	 * @param text html text in wich the body tag is looked for
	 * @return return body tag from html string. If there is'nt body the original text is returned
	 */
	private static String extractBody(String text) {
		String result = text;
		// the pattern we want to search for
		Pattern pattern = Pattern.compile("<body[^>]*>(.*)</body>");
		Matcher matcher = pattern.matcher(text);
		if (matcher.find()) {
			// there is at least one body. Only the first body will be returned
			result = matcher.group(1);
		}
		return result;
	}

	/**
	 * This method initialize the hashmap with the corresponding json fields
	 */
	private static void initKeys() {
		if (keys != null)
			return; // Init not needed
		keys = new HashMap<>();
		String[] keyList = { "Title", "Subtitle", "Category", "Body", "Abstract", "thumbnail", "Image",
				"Publish", "idArticle", "idUser", "Deleted" };
		String[] valueList = { "title", "subtitle", "category", "body", "abstract", "thumbnail_image", "image_data",
				"publish", "id", "id_user", "is_deleted" };
		for (int i = 0; i < keyList.length; i++) {
			keys.put(keyList[i], valueList[i]);
		}
	}
}

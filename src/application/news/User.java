/**
 * 
 */
package application.news;

/**
 * @author ÁngelLucas
 *
 */
public class User {
	private String login;
	private int idUser;
	private boolean isAdmin = false;
	public User(String login, int idUser){
		this.login = login;
		this.idUser = idUser;
	}
	
	/**
	 * @return the isAdmin
	 */
	public boolean isAdmin() {
		return isAdmin;
	}
	
	/**
	 * @param isAdmin the isAdmin to set
	 */
	public void setAdmin(boolean isAdmin) {
		this.isAdmin = isAdmin;
	}
	
	/**
	 * @return the login
	 */
	public String getLogin() {
		return login;
	}
	
	/**
	 * @return the idUser
	 */
	public int getIdUser() {
		return idUser;
	}
	
}

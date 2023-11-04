package application.news;

public enum Categories {
	 ALL("All"), ECONOMY ("Economy"), INTERNATIONAL("International"), NATIONAL("National"),
	 SPORTS("Sports"), TECHNOLOGY("Technology");
	private String name;
	private Categories (String name){
		this.name= name;
	}
	
	public String toString(){
		return name;
	}
}

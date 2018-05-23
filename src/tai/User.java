package tai;

public class User {
	private String name;
	
	private String password;
	
	private String type;
	
	public String getName() {
		return name;
	}
	
	public String getPassword() {
		return password;
	}
	
	public String getType() {
		return type;
	}
	
	User(String name, String password, String type){
		this.name = name;
		this.password = password;
		this.type = type;
	}
}

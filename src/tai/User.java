package tai;

public class User {
	private Long id;
	
	private String name;
	
	private String password;
	
	private String type;
	
	User(String name, String password){
		this.name = name;
		this.password = password;
		this.type = "user";
	}
}

package mybean.data;

public class User {
	private String account;
	private String password;
	private String name;
	private int exp;
	
	public User() {
		super();
	}
	
	public User(String account, String password) {
		super();
		this.account = account;
		this.password = password;
		this.name = "User" + account;
		this.exp = 0;
	}

	public User(String account, String password, String name, int exp) {
		super();
		this.account = account;
		this.password = password;
		this.name = name;
		this.exp = exp;
	}
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getExp() {
		return exp;
	}
	public void setExp(int exp) {
		this.exp = exp;
	}
	
	@Override
	public String toString() {
		String y = "\"";
		return "{" + y + "account" 		+ y + ":" + y + account 	+ y + ","
				   + y + "password" 	+ y + ":" + y + password 	+ y + ","
				   + y + "name" 		+ y + ":" + y + name 		+ y + ","
				   + y + "exp" 			+ y + ":" + y + exp 		+ y
				   + "}";
	}
}

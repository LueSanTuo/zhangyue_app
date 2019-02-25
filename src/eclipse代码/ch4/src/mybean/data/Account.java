package mybean.data;

public class Account {

	String actnum;
	String password;
	String name;
	int exp;
	
	public Account() {
		super();
	}
	public Account(String actnum, String password, String name, int exp) {
		super();
		this.actnum = actnum;
		this.password = password;
		this.name = name;
		this.exp = exp;
	}
	public String getActnum() {
		return actnum;
	}
	public void setActnum(String actnum) {
		this.actnum = actnum;
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
		return "Account [actnum=" + actnum + ", password=" + password + ", name=" + name + ", exp=" + exp + "]";
	}
}

package mybean.data;

/**  È«© */
public class BookMark {
	
	private String account;
	private	String bookId;
	private	String chapterId;
	private	String firstLine;
	private String process;
	private	String date;
	
	public BookMark() {
		super();
	}

	public BookMark(String account, String bookId, String chapterId, String firstLine, String process, String date) {
		super();
		this.account = account;
		this.bookId = bookId;
		this.chapterId = chapterId;
		this.firstLine = firstLine;
		this.process = process;
		this.date = date;
	}
	
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public String getBookId() {
		return bookId;
	}
	public void setBookId(String bookId) {
		this.bookId = bookId;
	}
	public String getChapterId() {
		return chapterId;
	}
	public void setChapterId(String chapterId) {
		this.chapterId = chapterId;
	}
	public String getFirstLine() {
		return firstLine;
	}
	public void setFirstLine(String firstLine) {
		this.firstLine = firstLine;
	}
	public String getProcess() {
		return process;
	}
	public void setProcess(String process) {
		this.process = process;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	
	@Override
	public String toString() {
		String y = "\"";
		return "{" + y + "account" 		+ y + ":" + y + account 	+ y + ","
				   + y + "bookId" 		+ y + ":" + y + bookId 		+ y + ","
				   + y + "chapterId" 	+ y + ":" + y + chapterId 	+ y + ","
				   + y + "firstLine" 	+ y + ":" + y + firstLine 	+ y + ","
				   + y + "process" 		+ y + ":" + y + process 	+ y + ","
				   + y + "date" 		+ y + ":" + y + date 		+ y
				   + "}";
	}
	
}

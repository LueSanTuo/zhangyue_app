package mybean.data;
/** ÔÄ¶Á¼ÇÂ¼  */
public class ReadRecord {
	
	private String account;
	private	String bookId;
	private	String chapterId;
	private String process;


	public ReadRecord() {
		super();
	}
	public ReadRecord(String account, String bookId, String chapterId, String process) {
		super();
		this.account = account;
		this.bookId = bookId;
		this.chapterId = chapterId;
		this.process = process;
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

	public String getProcess() {
		return process;
	}
	public void setProcess(String process) {
		this.process = process;
	}
	
	@Override
	public String toString() {
		String y = "\"";
		return "{" + y + "account" 		+ y + ":" + y + account 	+ y + ","
				   + y + "bookId" 		+ y + ":" + y + bookId 		+ y + ","
				   + y + "chapterId" 	+ y + ":" + y + chapterId 	+ y + ","
				   + y + "process" 		+ y + ":" + y + process 	+ y 
				   + "}";
	}
	
	

}

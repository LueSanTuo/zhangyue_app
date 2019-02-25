package mybean.data;

public class Book {
	
	private String bookId;
	private String bookName;
	private String bookAuthor;
	public String getBookId() {
		return bookId;
	}
	public void setBookId(String bookId) {
		this.bookId = bookId;
	}
	public String getBookName() {
		return bookName;
	}
	public void setBookName(String bookName) {
		this.bookName = bookName;
	}
	public String getBookAuthor() {
		return bookAuthor;
	}
	public void setBookAuthor(String bookAuthor) {
		this.bookAuthor = bookAuthor;
	}
	
	@Override
	public String toString() {
		String y = "\"";
		return "{" + y + "bookId" 		+ y + ":" + y + bookId 		+ y + ","
				   + y + "bookName" 	+ y + ":" + y + bookName 	+ y + ","
				   + y + "bookAuthor" 	+ y + ":" + y + bookAuthor 	+ y + "}";
	}
	
	
	
}

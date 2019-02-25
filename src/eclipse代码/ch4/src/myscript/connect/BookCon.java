package myscript.connect;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import mybean.data.Book;

public class BookCon extends ConMgr {
	
	/** 连接数据库 */
	public void getCon () {
		this.conn = this.getConn(DEFAULT_DB_NAME, DEFAULT_ACCOUNT, DEFAULT_PASSWORD);
	}
		
	/** 获取书籍列表 */
	public List<Book> getBooks (){
		List<Book> bookList = new ArrayList<>();
		String sql = "select * from Book";
		try{
			ResultSet rs = this.executeQuery(sql,null);
			while(rs.next()){
				Book book = new Book();
				book.setBookId(rs.getString("id"));
				book.setBookName(rs.getString("name"));
				book.setBookAuthor(rs.getString("author"));
				bookList.add(book);
			}

		}catch(SQLException e) {
	            e.printStackTrace();
		} finally {
			this.closeAll();
		}
		return bookList;
	}
	    
	/** 根据书籍id获取书籍 如果找不到则返回null */
	public Book getBookById (String id){
		Book book = null;
		String sql = "select * from book where id= ?";
		try {            
			ResultSet rs = this.executeQuery(sql, new String[]{id});
			if(rs.next()){
				book = new Book();
				book.setBookId(rs.getString("id"));
				book.setBookName(rs.getString("name"));
				book.setBookAuthor(rs.getString("author"));
	        }
		}catch(SQLException e){
			e.printStackTrace();
		}finally{
			this.closeAll();
		}
		return book;
	}
	    
	/** 根据名称获取书籍 */
	public List<Book> getBookByame(String name){
		List<Book> bookList = new ArrayList<>();
		String sql = "select * from book where name= ?";
		try{            
			ResultSet rs = this.executeQuery(sql, new String[]{name});
			if(rs.next()){
				Book book = new Book();
				book.setBookId(rs.getString("id"));
				book.setBookName(rs.getString("name"));
				book.setBookAuthor(rs.getString("author"));
				bookList.add(book);
			}
		}catch(SQLException e){
			e.printStackTrace();
		}finally{
			this.closeAll();
		}
		return bookList;
	}
	    

    /** 修改书籍信息 */
    public boolean editBook (Book book){
        boolean r = false;
        String sql = "update book set name= ?,author= ? where id= ?";
        try{
            int num = this.executeUpdate(sql, new String[]{book.getBookName(), book.getBookAuthor(), book.getBookId()});
            if (num > 0) {
                r = true;
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            this.closeAll();
        }
        return r;
    }
}

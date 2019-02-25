package myscript.connect;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import mybean.data.BookMark;

/** 书签连接器 */
public class BookMarkCon extends ConMgr {
	
	public String table = "book_marks";
			
	/** 连接数据库 */
	public void getCon () {
		this.conn = this.getConn(DEFAULT_DB_NAME, DEFAULT_ACCOUNT, DEFAULT_PASSWORD);
	}
		
	/** 获取用户的书籍列表 */
	public List<BookMark> getBookMarksByAccount (String account){
		List<BookMark> bookMarkList = new ArrayList<>();
		String sql = "select * from " + table + " where account=?";
		try{
			ResultSet rs = this.executeQuery(sql, new String[]{account});
			while(rs.next()){
				BookMark bookMark = new BookMark();
				bookMark.setAccount(rs.getString("account"));
				bookMark.setBookId(rs.getString("bookId"));
				bookMark.setChapterId(rs.getString("chapterId"));
				bookMark.setFirstLine(rs.getString("firstLine"));
				bookMark.setProcess(rs.getString("process"));
				bookMark.setDate(rs.getString("date"));
				bookMarkList.add(bookMark);
			}
		}catch(SQLException e) {
	            e.printStackTrace();
		} finally {
			this.closeAll();
		}
		return bookMarkList;
	}
	
	/** 用户添加书签 */
	public boolean addBookMark (BookMark bm){
        boolean r = false;
        String sql = "insert into  " + table + " (account,bookId,chapterId,firstLine,process,date) values(?,?,?,?,?,?)";
        try{
            int num = this.executeUpdate(sql, new String[]{bm.getAccount(), bm.getBookId(), bm.getChapterId(),
            		bm.getFirstLine(), bm.getProcess(), bm.getDate()});
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
	
	/** 用户删除所有书签 */
	public boolean delBookMark (String account){
        boolean r = false;
        String sql = "delete from " + table + " where account=?";
        try {
            int num = this.executeUpdate(sql, new String[]{account});
            if(num > 0){
                r = true;
            }
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            this.closeAll();
        }
        return r;
    }
	
}

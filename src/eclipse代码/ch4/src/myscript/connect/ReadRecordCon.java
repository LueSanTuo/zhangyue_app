package myscript.connect;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import mybean.data.BookMark;

/** 阅读记录连接器 */
public class ReadRecordCon extends ConMgr {
	
	/** 连接数据库 */
	public void getCon () {
		this.conn = this.getConn(DEFAULT_DB_NAME, DEFAULT_ACCOUNT, DEFAULT_PASSWORD);
	}
	
	/** 获取用户的阅读记录 */
	public List<BookMark> getReadRecordsByAccount (String account){
		List<BookMark> bookMarkList = new ArrayList<>();
		String sql = "select * from user_read_record where account=?";
		try{
			ResultSet rs = this.executeQuery(sql, new String[]{account});
			while(rs.next()){
				BookMark bookMark = new BookMark();
				bookMark.setAccount(rs.getString("account"));
				bookMark.setBookId(rs.getString("bookId"));
				bookMark.setChapterId(rs.getString("chapterId"));
				bookMark.setFirstLine("firstLine");
				bookMark.setProcess(rs.getString("process"));
				bookMark.setDate("date");
				bookMarkList.add(bookMark);
			}
		}catch(SQLException e) {
	            e.printStackTrace();
		} finally {
			this.closeAll();
		}
		return bookMarkList;
	}
	
	/** 用户添加阅读记录 */
	public boolean addReadRecord (BookMark bm){
        boolean r = false;
        String sql = "insert into user_read_record(account,bookId,chapterId,process) values(?,?,?,?,?,?)";
        try{
            int num = this.executeUpdate(sql, new String[]{bm.getAccount(), bm.getBookId(), bm.getChapterId(), bm.getProcess()});
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
	
	/** 用户删除所有阅读记录 */
	public boolean delReadRecord (String account){
        boolean r = false;
        String sql = "delete from user_read_record where account=?";
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

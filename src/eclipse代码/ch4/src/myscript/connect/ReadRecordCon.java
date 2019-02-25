package myscript.connect;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import mybean.data.BookMark;

/** �Ķ���¼������ */
public class ReadRecordCon extends ConMgr {
	
	/** �������ݿ� */
	public void getCon () {
		this.conn = this.getConn(DEFAULT_DB_NAME, DEFAULT_ACCOUNT, DEFAULT_PASSWORD);
	}
	
	/** ��ȡ�û����Ķ���¼ */
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
	
	/** �û�����Ķ���¼ */
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
	
	/** �û�ɾ�������Ķ���¼ */
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

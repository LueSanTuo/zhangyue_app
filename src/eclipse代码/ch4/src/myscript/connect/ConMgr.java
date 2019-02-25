package myscript.connect;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/** ���ӹ����� */
public class ConMgr {
	
	/** Ĭ�����ݿ����� */
	public static final String DEFAULT_DB_NAME = "zhangyue";
	/** Ĭ���˻� */
	public static final String DEFAULT_ACCOUNT = "root";
	/** Ĭ������ */
	public static final String DEFAULT_PASSWORD = "123456";
	
	public Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    
    /** ������ */
    public Connection getConn (String dbname, String dbuser, String dbpwd) {      
        String DRIVER = "com.mysql.jdbc.Driver";
        String URL = "jdbc:mysql://localhost/" + dbname + "?user=" + dbuser + "&password=" + dbpwd + "&characterEncoding=utf-8";
        try{
            Class.forName(DRIVER);  
            conn = DriverManager.getConnection(URL);
        } catch(Exception e){
            e.printStackTrace();
        }
        return conn;
    }
    
    /** �ر����� */
    public void closeAll () {
        try{
            if(rs != null){
                rs.close();
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }finally {
            try {
                if(ps != null){
                    ps.close();
                }
            } catch(SQLException e) {
                e.printStackTrace();
            } finally{
            	try {
               		if(conn != null) {
               	    	conn.close();
               		}
           	 	} catch (SQLException e) {
              	  e.printStackTrace();
            	}
            }
        }

    }
    
    /** ִ��sql��䣬���Խ��в�ѯ */
    public ResultSet executeQuery (String preparedSql,String []param) {
        try{
            ps = conn.prepareStatement(preparedSql);
            if(param != null){
                for (int i = 0; i < param.length; i++) {
                    ps.setString(i + 1, param[i]);
                }
            }
            rs = ps.executeQuery();
        }catch(SQLException e) {
            e.printStackTrace();
        }       
        return rs;

    }
    
    /** ִ��sql��䣬���ӣ��޸ģ�ɾ�� */
    public int executeUpdate (String preparedSql,String[]param) {
        int num = 0;
        try{
            ps = conn.prepareStatement(preparedSql);
            if(ps != null){
                for (int i = 0; i < param.length; i++) {
                    ps.setString(i + 1, param[i]);
                }
            }
            num = ps.executeUpdate();
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return num;
    }
}

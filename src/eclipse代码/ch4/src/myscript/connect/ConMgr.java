package myscript.connect;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/** 连接管理器 */
public class ConMgr {
	
	/** 默认数据库名称 */
	public static final String DEFAULT_DB_NAME = "zhangyue";
	/** 默认账户 */
	public static final String DEFAULT_ACCOUNT = "root";
	/** 默认密码 */
	public static final String DEFAULT_PASSWORD = "123456";
	
	public Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    
    /** 打开连接 */
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
    
    /** 关闭连接 */
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
    
    /** 执行sql语句，可以进行查询 */
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
    
    /** 执行sql语句，增加，修改，删除 */
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

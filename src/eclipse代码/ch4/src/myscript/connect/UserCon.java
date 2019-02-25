package myscript.connect;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import mybean.data.User;

/** 用户连接器 */
public class UserCon extends ConMgr {
	/** 连接数据库 */
	public void getCon () {
		this.conn = this.getConn(DEFAULT_DB_NAME, DEFAULT_ACCOUNT, DEFAULT_PASSWORD);
	}
	
	/** 获取用户列表 */
    public List<User> getAct(){
        List<User> userList = new ArrayList<>();
        String sql = "select * from user";
        try{
            ResultSet rs = this.executeQuery(sql,null);
            while(rs.next()){
            	User user = new User();
                user.setAccount(rs.getString("account"));
                user.setPassword(rs.getString("password"));
                user.setName(rs.getString("name"));
                user.setExp(rs.getInt("exp"));
                userList.add(user);
            }

        }catch(SQLException e) {
            e.printStackTrace();
        } finally {
            this.closeAll();
        }
        return userList;
    }
    
    /** 根据账号获取用户 */
    public User getUserByAccount (String account){
        User user = null;
        String sql = "select * from user where account= ?";
        try{            
            ResultSet rs = this.executeQuery(sql, new String[]{account});
            if(rs.next()){
                user = new User();
                user.setAccount(rs.getString("account"));
                user.setPassword(rs.getString("password"));
                user.setName(rs.getString("name"));
                user.setExp(rs.getInt("exp"));
            }
        }catch(SQLException e){
            e.printStackTrace();
        }finally{
            this.closeAll();
        }
        return user;
    }
    
    /** 修改用户信息 */
    public boolean editUser (User user){
        boolean r = false;
        String sql = "update user set password= ?,name= ?,exp= ? where account= ?";
        try{
            int num = this.executeUpdate(sql, new String[]{user.getPassword(), user.getName(),
            		"" + user.getExp(), user.getAccount()});
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
    
    /** 添加用户 */
    public boolean addUser (User user){
        boolean r = false;
        String sql = "insert into user(account,password,name,exp) values(?,?,?,?)";
        try{
            int num = this.executeUpdate(sql, new String[]{user.getAccount(), user.getPassword(), user.getName(),
            		String.format("%d", user.getExp())});
            if(num > 0){
                r = true;
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            this.closeAll();
        }
        return r;
    }
    
    /** 删除指定用户 */
    public boolean delUser (String account){
        boolean r = false;
        String sql = "delete from user where account= ?";
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
    
    /** 判断账户是否存在 */
    public int isUserExist (String account){
        int n = 0;
        String sql = "select * from user where account= ?";
        try{            
            ResultSet rs = this.executeQuery(sql, new String[]{account});
            rs.last();
            n = rs.getRow();
        }catch(SQLException e){
            e.printStackTrace();
        }finally{
            this.closeAll();
        }
        return n;
    }
}

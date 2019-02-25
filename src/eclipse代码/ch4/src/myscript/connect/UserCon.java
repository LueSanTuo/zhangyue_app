package myscript.connect;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import mybean.data.User;

/** �û������� */
public class UserCon extends ConMgr {
	/** �������ݿ� */
	public void getCon () {
		this.conn = this.getConn(DEFAULT_DB_NAME, DEFAULT_ACCOUNT, DEFAULT_PASSWORD);
	}
	
	/** ��ȡ�û��б� */
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
    
    /** �����˺Ż�ȡ�û� */
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
    
    /** �޸��û���Ϣ */
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
    
    /** ����û� */
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
    
    /** ɾ��ָ���û� */
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
    
    /** �ж��˻��Ƿ���� */
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

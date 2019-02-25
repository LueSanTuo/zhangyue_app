package myscript.connect;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import mybean.data.*;

public class AccountCon extends ConMgr {
	
	
	// 连接数据库
	public void getCon () {
		this.conn = this.getConn(DEFAULT_DB_NAME, DEFAULT_ACCOUNT, DEFAULT_PASSWORD);
	}
	
	// 获取用户列表
    public List<Account> getAct(){
        List<Account> actList = new ArrayList<Account>();
        String sql = "select * from account";
        try{
            ResultSet rs = this.executeQuery(sql,null);
            while(rs.next()){
            	Account act = new Account();
                act.setActnum(rs.getString("actnum"));
                act.setPassword(rs.getString("password"));
                act.setName(rs.getString("name"));
                act.setExp(rs.getInt("exp"));
                actList.add(act);
            }

        }catch(SQLException e) {
            e.printStackTrace();
        } finally {
            this.closeAll();
        }
        return actList;
    }
    
    // 根据账号获取用户
    public Account getAccountByActnum (String actnum){
        Account act = null;
        String sql = "select * from account where actnum= ?";
        try{            
            ResultSet rs = this.executeQuery(sql, new String[]{actnum});
            if(rs.next()){
                act = new Account();
                act.setActnum(rs.getString("actnum"));
                act.setPassword(rs.getString("password"));
                act.setName(rs.getString("name"));
                act.setExp(rs.getInt("exp"));
            }
        }catch(SQLException e){
            e.printStackTrace();
        }finally{
            this.closeAll();
        }
        return act;
    }
    
    // 根据名称获取用户
    public Account getAccountByStore(String name){
        Account act = null;
        String sql = "select * from account where store= ?";
        try{            
            ResultSet rs = this.executeQuery(sql, new String[]{name});
            if(rs.next()){
                act = new Account();
                act.setActnum(rs.getString("actnum"));
                act.setPassword(rs.getString("password"));
                act.setName(rs.getString("name"));
                act.setExp(rs.getInt("exp"));
            }
        }catch(SQLException e){
            e.printStackTrace();
        }finally{
            this.closeAll();
        }
        return act;
    }
    
    // 根据经验值区间获取用户
    public List<Account> getAccountByExpRange(int minExp, int maxExp){
    	List<Account> actList = new ArrayList<Account>();
        Account act = null;
        String sql = "select * from account where exp>= ? and exp <= ?";
        try{            
            ResultSet rs = this.executeQuery(sql, new String[]{"" + minExp,  "" + maxExp});
            while(rs.next()){
                act = new Account();
                act.setActnum(rs.getString("actnum"));
                act.setPassword(rs.getString("password"));
                act.setName(rs.getString("name"));
                act.setExp(rs.getInt("exp"));
                actList.add(act);
            }
        }catch(SQLException e){
            e.printStackTrace();
        }finally{
            this.closeAll();
        }
        return actList;
    }
    
    // 修改用户信息
    public boolean editAccount (Account act){
        boolean r = false;
        String sql = "update account set password= ?,name= ?,exp= ? where actnum= ?";
        try{
            int num = this.executeUpdate(sql, new String[]{act.getPassword(), act.getName(),
            		"" + act.getExp(), act.getActnum()});
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
    
    // 添加用户
    public boolean addAccount (Account act){
        boolean r = false;
        String sql = "insert into account(actnum,password,name,exp) values(?,?,?,?)";
        try{
            int num = this.executeUpdate(sql, new String[]{act.getActnum(), act.getPassword(), act.getName(),
            		String.format("%d", act.getExp())});
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
    
    // 删除指定用户
    public boolean delAccount (String actnum){
        boolean r = false;
        String sql = "delete from account where actnum= ?";
        try {
            int num = this.executeUpdate(sql, new String[]{actnum});
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
    
    // 判断账户是否存在
    public int isAccountExist (String actnum){
        int n = 0;
        String sql = "select * from account where actnum= ?";
        try{            
            ResultSet rs = this.executeQuery(sql, new String[]{actnum});
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

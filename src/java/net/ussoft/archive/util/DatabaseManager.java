package net.ussoft.archive.util;

/**
 * 数据库操作的基类
 */
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class DatabaseManager {
	
    private Connection m_conn;

    private Statement m_stmt;
    
    String driver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";

    String url = "jdbc:sqlserver://192.168.1.224:2115; DatabaseName=hkarchive";

    String uName = "sa";

    String uPwd = "sa";
    
    public final String OADB_URL = "jdbc.properties";
    
    public DatabaseManager() {
    	
        this.setDriver(getOadb("DRIVERLOAD"));
        this.setConnection(getOadb("URL"), getOadb("USER"), getOadb("PWD"));
    }

    public DatabaseManager(String driver, String url, String userName, String userPWD) {
        try {
            m_conn = DriverManager.getConnection(url, userName, userPWD);
            m_stmt = m_conn.createStatement();
        } catch (Exception e) {
        	e.printStackTrace();
        }
    }
    
    public boolean setDriver(String driver) {
        try {
            Class.forName(driver);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean setConnection(String url, String userName, String userPWD) {
        try {
            m_conn = DriverManager.getConnection(url, userName, userPWD);
            m_stmt = m_conn.createStatement();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean isTheShipExist(String imo, String tablename){
    	PreparedStatement ps = null;
    	ResultSet res = null;
    	String sql = " select count(1) as total from "+tablename+" where shipimo = '" +imo+"' ";
    	try{
    		ps = this.m_conn.prepareStatement(sql);
    		res = ps.executeQuery(sql);
    		while(res.next()){
    			int total = res.getInt("total");
    			if (total>0){
    				return true;
    			}else{
    				return false;
    			}
    		}
    		
    		return false;
    	}catch(Exception ex){
    		return false;
    	}finally{
    		try{
    			if (res!=null){
    				res.close();
    			}
    			if (ps!=null){
    				ps.close();
    			}
    		}catch(Exception ex){
    			res = null;
    			ps = null;
    		}
    	}
    }
    public boolean isTheCompanyExist(String imo, String tablename){
    	PreparedStatement ps = null;
    	ResultSet res = null;
    	String sql = " select count(1) as total from "+tablename+" where companyimo = '" +imo+"' ";
    	try{
    		ps = this.m_conn.prepareStatement(sql);
    		res = ps.executeQuery(sql);
    		while(res.next()){
    			int total = res.getInt("total");
    			if (total>0){
    				return true;
    			}else{
    				return false;
    			}
    		}
    		
    		return false;
    	}catch(Exception ex){
    		return false;
    	}finally{
    		try{
    			if (res!=null){
    				res.close();
    			}
    			if (ps!=null){
    				ps.close();
    			}
    		}catch(Exception ex){
    			res = null;
    			ps = null;
    		}
    	}
    }
    private String getColumnSql(List columns){
    	StringBuffer result = new StringBuffer();
    	result.append("( ");
    	for(int index=0; index<columns.size(); index++){
    		result.append(columns.get(index));
    		result.append(",");
    	}
    	
    	return result.toString().replaceAll(",$", "")+" )";
    }
    
    private String getValuesParamSql(List columns){
    	StringBuffer result = new StringBuffer();
    	result.append(" values ( ");
    	for(int index=0; index<columns.size(); index++){
    		result.append("?");
    		result.append(",");
    	}
    	
    	return result.toString().replaceAll(",$", "")+" )";
    }    
    
    private String getTableName(String classname){
    	int pos = classname.lastIndexOf(".");
    	if (-1!=pos){
    		return classname.substring(pos+1);
    	}else{
    		return classname;
    	}
    }
    public ResultSet preExecuteSelect(String sql,Object paramValues[]){
		ResultSet set=null;
		PreparedStatement st=null;
	    try {
			st= this.m_conn.prepareStatement(sql);
			for(int i=0;i<paramValues.length;i++){
				st.setObject(i+1, paramValues[i]);
			}
			set=st.executeQuery();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return set;
	}
    /**
     * 
     * @param sql
     * @param param
     * @return
     */
    public List<Map> queryForList(String sql, Object[] param){
    	List result = new ArrayList();
    	PreparedStatement ps = null;
    	ResultSet rs = null;
    	try{
    		ps = this.m_conn.prepareStatement(sql);
    		for(int index=0; index<param.length; index++){
    			ps.setObject(index+1, param[index]);
    		}
    		rs = ps.executeQuery();
    		ResultSetMetaData metaData = rs.getMetaData();
    		
    		String[] columns = this.getColumnNames(metaData);
    		while(rs.next()){
    			Map unit = new LinkedHashMap();
    			for(int index=0; index<columns.length; index++){
    				unit.put(columns[index], rs.getObject(index+1));
    			}
    			result.add(unit);
    		}
    		return result;
    	}catch(Exception ex){
    		ex.printStackTrace();
    		return new ArrayList();
    	}finally{
    		if (null!=rs){
    			try{
    				rs.close();
    			}catch(Exception ex){
    				ex.printStackTrace();
    			}
    		}
    		
    		if (null!=ps){
    			try{
    				ps.close();
    			}catch(Exception ex){
    				ex.printStackTrace();
    			}
    		}
    		
    	}
    }
    public void deleteObject(String sql, Object[] param){
    	PreparedStatement ps = null;
    	try{
    		ps = this.m_conn.prepareStatement(sql);
    		for(int index=0; index<param.length; index++){
    			ps.setObject(index+1, param[index]);
    		}
    		ps.executeUpdate();
    	}catch(Exception ex){
    		ex.printStackTrace();
    	}finally{
    		if (null!=ps){
    			try{
    				ps.close();
    			}catch(Exception ex){
    				ex.printStackTrace();
    			}
    		}
    	}   	
    }
    public boolean insertObject(String sql, Object[] param){
    	PreparedStatement ps = null;
    	try{
    		ps = this.m_conn.prepareStatement(sql);
    		for(int index=0; index<param.length; index++){
    			ps.setObject(index+1, param[index]);
    		}
    		ps.executeUpdate();
    		return true;
    	}catch(Exception ex){
    		ex.printStackTrace();
    		return false;
    	}finally{
    		if (null!=ps){
    			try{
    				ps.close();
    			}catch(Exception ex){
    				ex.printStackTrace();
    			}
    		}
    	}
    }
    public boolean insertList(String sql, List<Object[]> params){
    	PreparedStatement ps = null;
    	try{
    		ps = this.m_conn.prepareStatement(sql);
    		for(int index=0; index<params.size(); index++){
    			Object[] objects = params.get(index);
    			for(int index1=0; index1<objects.length; index1++){
    				ps.setObject(index1+1, objects[index1]);
    			}
    			ps.addBatch();
    		}
    		ps.executeBatch();
    		return true;
    	}catch(Exception ex){
    		ex.printStackTrace();
    		return false;
    	}finally{
    		if (null!=ps){
    			try{
    				ps.close();
    			}catch(Exception ex){
    				ex.printStackTrace();
    			}
    		}
    	}
    }
    private String[] getColumnNames(ResultSetMetaData meta) throws Exception{
    	String[] columns = new String[meta.getColumnCount()];
    	for(int index=0; index<columns.length; index++){
    		columns[index] = meta.getColumnName(index+1);
    	}
    	return columns;
    }

    public boolean updateObject(String sql, Object[] param){
    	PreparedStatement ps = null;
    	try{
    		ps = this.m_conn.prepareStatement(sql);
    		for(int index=0; index<param.length; index++){
    			ps.setObject(index+1, param[index]);
    		}
    		ps.executeUpdate();
    		return true;
    	}catch(Exception ex){
    		ex.printStackTrace();
    		return false;
    	}finally{
    		if (null!=ps){
    			try{
    				ps.close();
    			}catch(Exception ex){
    				ex.printStackTrace();
    			}
    		}
    	}
    }
//    // 处理查询
//    public ResultSet sendQuery(String sql) {
//        try {
//        	
//            ResultSet m_rs = m_stmt.executeQuery(sql);
//            return m_rs;
//        } catch (SQLException e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
//
//    // 处理数据更新
//    public int sendUpdate(String sql) {
//        try {
//            return m_stmt.executeUpdate(sql);
//        } catch (SQLException e) {
//            e.printStackTrace();
//			System.out.println(sql);
//			System.out.println("----------------------------------------------------------------");
//            return -1;
//        }
//    }
    
    
    public Connection getConnection(){
    	return m_conn;
    }
    
    /**
     * 手动开启事务
     */
    private final static ThreadLocal local = new ThreadLocal();
    public void startTransaction(){
    	Transaction tr = (Transaction)local.get();
    	if(tr==null){
    		tr = new Transaction();
    		Connection con = getConnection();
    		try {
    			con.setAutoCommit(false);
    		} catch (SQLException e) {
    			e.printStackTrace();
    		}
    		tr.setConnection(con);
    		tr.setCommitCount(0);
    		tr.setTransCount(1);
    		tr.setTransDeep(1);
		   
    		local.set(tr);
    	}else{
    		//事务已经开启，将嵌套层次深度加一，将事务次数加一
		   tr.setTransCount(tr.getTransCount() + 1);
		   tr.setTransDeep(tr.getTransDeep() + 1);
    	}
    }
    
    /**
     * 提交事务
     */
    public void commitTransaction(){
    	Transaction tr = (Transaction)local.get();
    	//如果事务属于嵌套，则不提交数据，直接将层次数减一。
    	  if(tr.getTransDeep() > 1){
	    	   tr.setTransDeep(tr.getTransDeep() - 1);
	    	   tr.setCommitCount(tr.getCommitCount() + 1);
	    	   return;
    	  }
    	  Connection con = tr.getConnection();
    	  try {
	    	   if(tr.hasFullExecute()){
	    		   con.commit();   
	    	   }
    	  }catch(SQLException e){
    		 e.printStackTrace();
    	  }
    }
    
    /**
     * 结束事务
     */
    public void endTransaction() {
		Transaction tr = (Transaction) local.get();
		// 如果存在嵌套事务
		if (tr.getTransDeep() > 1) {
			tr.setTransDeep(tr.getTransDeep() - 1);
			return;
		}
		// 当前事务已经结束，清空ThreadLocal变量，防止下一次操作拿到已经关闭的Connection对象。
		local.set(null);
		
		Connection con = tr.getConnection();
		try {
			if (!tr.hasFullExecute()) {
				con.rollback();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				con.close();
			} catch (SQLException se) {
			}
		}
	}
    
    public String getSetMethodName(String str){
    	return "set"+ str.substring(0, 1).toUpperCase()+ str.substring(1);
    }
    
    public List queryList(String sql,String className){
    	List result = null;
    	PreparedStatement ps = null;
    	ResultSet rs = null;
    	String recordValue = ""; //构造返回的结果集
    	try{
    		Object obj = null;
    		result = new ArrayList();
    		ps = this.m_conn.prepareStatement(sql);
    		rs = ps.executeQuery();
    		ResultSetMetaData rsmd = rs.getMetaData();
    		int columnCount = rsmd.getColumnCount(); //列的总数
    		while(rs.next()){
    			obj = Class.forName(className).newInstance(); //类的实例化
    			for(int i=1;i<=columnCount;i++){
    				if(rs.getString(rsmd.getColumnName(i)) != null){
    					recordValue = rs.getString(rsmd.getColumnName(i));
	 				}else{
						recordValue = "";
					}
					Method m = obj.getClass().getMethod(getSetMethodName(rsmd.getColumnName(i)),new Class[] {recordValue.getClass()});
					m.invoke(obj, new Object[] {recordValue});
    			}
    			result.add(obj);
    		}
    		return result;
    	}catch(Exception ex){
    		ex.printStackTrace();
    		return null;
    	}finally{
    		if (null!=rs){
    			try{
    				rs.close();
    			}catch(Exception ex){
    				ex.printStackTrace();
    			}
    		}
    		if (null!=ps){
    			try{
    				ps.close();
    			}catch(Exception ex){
    				ex.printStackTrace();
    			}
    		}
    	}
    }
    
	/**
	 * 读取oadb.properties
	 * */
	public String getOadb(String key){
		String result = getValue(key, OADB_URL);
		return result;
	}
	/**
	 * 通过key获取.properties中的value
	 * @param key
	 * @return value
	 * */
	public String getValue(String key,String properties_file){
		String value="";
        try {
        	InputStream in = this.getClass().getClassLoader().getResourceAsStream(properties_file);
            Properties properties = new Properties();
			properties.load(in);
			value = properties.getProperty(key);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return value;
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		DatabaseManager manager = new DatabaseManager();
//		System.out.println(manager.getSetMethodName("shipName"));
	}

}

package it.tdlight.telegrambackup.db;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Logger;

public class Connection {
	private static final Logger LOG = Logger.getLogger("Connection");

	public static final String DB_FILE = "users.db";
	
	private final static int CLEAR_CONN_ID = -10;
	private final static int SHARED_CONN_ID = -100;
	
	private java.sql.Connection[] connections;
	private java.sql.Connection fallbackConnection;
	
	private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
	private final Lock readLock = readWriteLock.readLock();
	private final Lock writeLock = readWriteLock.writeLock();
	
	private LinkedList<Integer> freeConns = new LinkedList<Integer>();
	
	
	public Connection(int connectionNo) {
		connectionNo -= 1;
		connections = new java.sql.Connection[connectionNo];
		fallbackConnection = getConnection();
		for(int i = 0; i < connectionNo; i++) {
			connections[i] = getConnection();
			freeConns.add(i);
		}
	}
	
	public synchronized java.sql.Connection getConnection() {
		java.sql.Connection c = null;
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:file:" + DB_FILE, "SA", "");
		} catch (Exception e) {
			LOG.warning("Unable to init DataBase. Fatal Error.");
		}
		return c;
	}
	
	public synchronized void close() {
		for(java.sql.Connection c : connections) {
			try {
				c.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		try {
			fallbackConnection.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		freeConns.clear();
	}
	
	public PreparedStatement prepareStatement(String sql, boolean write) throws SQLException {
		return new PreparedStatement(sql, write, this);
	}
	
	public static class Handle {
		private int connId;
		private boolean write;
		private Connection orgConn;
		private java.sql.Connection sqlConn;
		private Handle(int connId, boolean write, Connection orgConn, java.sql.Connection sqlConn) {
			this.connId = connId;
			this.write = write;
			this.orgConn = orgConn;
			this.sqlConn = sqlConn;
		}
		public void unlock() {
			if(connId == CLEAR_CONN_ID)
				throw new RuntimeException("Handle was already cleared!");
			boolean shoudUnlock = false;
			synchronized(orgConn.freeConns) {
				if(connId < 0 || !orgConn.freeConns.contains(connId)) {
					if(connId >= 0) {
						orgConn.freeConns.add(connId);
					} else {
					}
					shoudUnlock = true;
				}
			}
			if(shoudUnlock) {
				if(write)
					orgConn.writeLock.unlock();
				else
					orgConn.readLock.unlock();
			}
			connId = CLEAR_CONN_ID;
			orgConn = null;
			sqlConn = null;
		}
		public java.sql.Connection getConnection(){
			return sqlConn;
		}
	}
	
	public Handle getHandle(boolean write) {
		return getHandle(write, false);
	}
	public Handle getHandle(boolean write, boolean forceSharedConnection) {
		if(write)
			writeLock.lock();
		else
			readLock.lock();
		
		int connId = SHARED_CONN_ID;
		java.sql.Connection c = fallbackConnection;
		if(!forceSharedConnection) {
			synchronized(freeConns) {
				if(!freeConns.isEmpty()) {			
					connId = freeConns.poll();
					c = connections[connId];
				} else {
					LOG.warning("Falling back on shared sqlite3 connection");
				}
			}
		}
		return new Handle(connId, write, this, c);
	}
	
	public static void closeStuff(Handle h, java.sql.Statement ps, ResultSet rs) {
		if(rs != null) try {if(!rs.isClosed()) rs.close(); rs = null;} catch (Exception ex) {ex.printStackTrace();}
		if(ps != null) try {if(!ps.isClosed()) ps.close(); ps = null;} catch (Exception ex) {ex.printStackTrace();}
		if(h != null) try {h.unlock();} catch (Exception ex) {ex.printStackTrace();}
	}
}
package it.tdlight.telegrambackup.db;

import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;


import it.tdlight.jni.TdApi;
import it.tdlight.telegrambackup.db.Connection.Handle;

public class Db {
	private static final Logger LOG = Logger.getLogger("Connection");
	private static final int CONNECTION_NO = 4;
	
	private static Connection c = null;

	public static void closeConnection() {
		try {
			c.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static synchronized Connection getConn() {
		if(c == null) {
			try {
				c = new Connection(CONNECTION_NO);
			} catch (Exception e) {
				LOG.warning("Unable to init DataBase. Fatal Error.");
			}
		}
		return c;
	}


	public static void createTables() {
		createTable("Update", "id BIGINT PRIMARY KEY AUTOINCREMENT, data TEXT");
		createTable("Chat", "id BIGINT, name TEXT, description TEXT");
		createTable("Propic", "id BIGINT AUTOINCREMENT, chatId BIGINT REFERENCES Chat(id), idForUser INTEGER, media TEXT REFERENCES Media(id)");
		createTable("Message", "id BIGINT PRIMARY KEY, chatId BIGINT REFERENCES Chat(id), updateId BIGINT REFERENCES Update(id), data TEXT");
		createTable("Media", "id TEXT, path TEXT");
		createIndex("Media_id", "Media", "id");
		createTable("StickerPack", "id BIGINT PRIMARY KEY, name TEXT, title TEXT");
		createTable("Poll", "id BIGINT, question TEXT");
		createTable("PollOption", "id BIGINT AUTOINCREMENT, text TEXT, voterCount INTEGER, pollId BIGINT REFERENCES Poll(id), idInPoll INTEGER");
	}
	private static void createIndex(String indexName, String tableName, String key) {
		Statement stmt = null;
		Connection.Handle h = null;
		try {
			h = c.getHandle(true, true);
			stmt = h.getConnection().createStatement();
			stmt.execute("CREATE UNIQUE INDEX " + indexName + " ON " + tableName + " (" + key + ");");
			stmt.close();
		} catch (Exception e) {
			//e.printStackTrace();
		} finally {
			Connection.closeStuff(h, stmt, null);
		}
	}
	private static void createTable(String tableName, String keys) {
		Connection.Handle h = null;
		java.sql.PreparedStatement p = null;
		ResultSet rs = null;
		try {
			Class.forName("org.sqlite.JDBC");
			h = c.getHandle(true, true);
			java.sql.Connection c = h.getConnection();
			DatabaseMetaData dbm = c.getMetaData();
			rs = dbm.getTables(null, null, tableName, null);
			if (!rs.next()) {
				p = c.prepareStatement("CREATE TABLE \"" + tableName + "\" (" + keys + ");");
				p.execute();
				p.close();
			}
		} catch (Exception e) {
			LOG.warning("FATAL ERROR WHILE CREATING TABLE " + tableName + ": " + e);
			e.printStackTrace();
		} finally {
			Connection.closeStuff(h, p, rs);
		}
	}

}

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
			if(c != null) c.close();
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
		createTable("Chat", "id BIGINT PRIMARY KEY,"
						  //+ "name TEXT NOT NULL,"
						  //+ "description TEXT,"
						  + "upgradeFrom BIGINT REFERENCES Chat(id),"
						  + "upgradeTo BIGINT REFERENCES Chat(id)");
		createTable("Update", "id INTEGER PRIMARY KEY AUTOINCREMENT,"
							+ "data TEXT NOT NULL,"
							+ "chatId BIGINT REFERENCS Chat(id),"
							+ "messageId BIGINT,"
							+ "timestamp BIGINT,"
							+ "updateType INTEGER");
		//createTable("Propic", "id INTEGER AUTOINCREMENT, chatId BIGINT REFERENCES Chat(id), idForUser INTEGER, media TEXT REFERENCES Media(id)");
		//createTable("Message", "id BIGINT PRIMARY KEY, chatId BIGINT REFERENCES Chat(id), updateId BIGINT REFERENCES Update(id), data TEXT");
		createTable("Media", "id TEXT PRIMARY KEY,"
						   + "path TEXT NOT NULL,"
						   + "BIGINT timestamp NOT NULL");
		//createIndex("Media_id", "Media", "id");
		createTable("StickerPack", "id BIGINT PRIMARY KEY,"
								 + "name TEXT,"
								 + "title TEXT");
		createTable("Sticker", "id BIGINT PRIMARY KEY,"
							 + "emoji TEXT,"
							 + "mediaId TEXT REFERENCS Media(id)");
		createTable("Poll", "id BIGINT PRIMARY KEY,"
						  + "question TEXT NOT NULL");
		createTable("PollOption", "id INTEGER PRIMARY KEY AUTOINCREMENT,"
								+ "text TEXT NOT NULL,"
								+ "voterCount INTEGER,"
								+ "pollId BIGINT REFERENCES Poll(id) NOT NULL,"
								+ "idInPoll INTEGER NOT NULL");
	}
	private static void createIndex(String indexName, String tableName, String key) {
		Statement stmt = null;
		Connection.Handle h = null;
		try {
			h = getConn().getHandle(true, true);
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
			h = getConn().getHandle(true, true);
			java.sql.Connection c = h.getConnection();
			DatabaseMetaData dbm = c.getMetaData();
			rs = dbm.getTables(null, null, tableName, null);
			if (!rs.next()) {
				String statement = "CREATE TABLE \"" + tableName + "\" (" + keys + ");";
				System.out.println(statement);
				p = c.prepareStatement(statement);
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

package it.tdlight.telegrambackup.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetProvider;

public class PreparedStatement {
	
	private static enum SetType {
		SET_INT, SET_LONG, SET_BOOLEAN, SET_BYTE, SET_STRING, SET_SHORT, SET_OBJECT, SET_DOUBLE
	};
	private static enum ExecuteType {
		EXEC, EXEC_QUERY, EXEC_UPDATE, GENERATED_KEYS
	};
	private class SetData {
		private SetType type;
		private Object[] args;
		private SetData(SetType type, Object... args) {
			this.type = type;
			this.args = args;
		}
	}
	
	private Map<Integer, SetData> setList = new HashMap<Integer, SetData>();
	
	private String sql;
	private boolean write;
	private Connection orgConn;
	private ResultSet returnedResultSet;
	
	public PreparedStatement(String sql, boolean write, Connection orgConn) {
		this.sql = sql;
		this.write = write;
		this.orgConn = orgConn;
		
		//orgConn.getSharedConnection(true, null).prepareStatement("").;
	}
	
	public void close() {
		Connection.closeStuff(null, null, returnedResultSet);
	}
	private void closeStuff(Connection.Handle h, java.sql.PreparedStatement ps, ResultSet rs) {
		Connection.closeStuff(h, ps, rs);		
		setList.forEach((i, sd) -> sd.args = null);
		setList.clear();
		orgConn = null;
		sql = null;
	}
	private Object _internal_exec(ExecuteType execType) throws SQLException {
		Object ret = null;
		Connection.Handle h = orgConn.getHandle(write);
		java.sql.PreparedStatement ps = null;
		ResultSet tempRs = null;
		try {
			ps = h.getConnection().prepareStatement(sql);
			for(Entry<Integer, SetData> e : setList.entrySet()) {
				SetData data = e.getValue();
				Object[] args = data.args;
				int index = e.getKey();
				switch(data.type) {
					case SET_BOOLEAN: {
						ps.setBoolean(index, (Boolean) args[0]);
						break;
					}
					case SET_BYTE: {
						ps.setByte(index, (Byte) args[0]);
						break;
					}
					case SET_INT: {
						ps.setInt(index, (Integer) args[0]);
						break;
					}
					case SET_LONG: {
						ps.setLong(index, (Long) args[0]);
						break;
					}
					case SET_SHORT: {
						ps.setShort(index, (Short) args[0]);
						break;
					}
					case SET_STRING: {
						ps.setString(index, (String) args[0]);
						break;
					}
					case SET_DOUBLE: {
						ps.setDouble(index, (Double) args[0]);
						break;
					}
					case SET_OBJECT: {
						ps.setObject(index, args[0]);
						break;
					}
				}
			}
			
			switch(execType) {
				case EXEC: {
					ps.execute();
					break;
				}
				case EXEC_QUERY: {
					tempRs = ps.executeQuery();
					break;
				}
				case EXEC_UPDATE: {
					ret = ps.executeUpdate();
					break;
				}
				case GENERATED_KEYS: {
					ps.execute();
					tempRs = ps.getGeneratedKeys();
					break;
				}
			}
			
			if(tempRs != null) {
				CachedRowSet crs = RowSetProvider.newFactory().createCachedRowSet();
				crs.populate(tempRs);
				returnedResultSet = crs;
				ret = returnedResultSet;
			}
			
			closeStuff(h, ps, tempRs);
		} catch (SQLException t) {
			closeStuff(h, ps, tempRs);
			throw t;
		} catch (Throwable t) {
			closeStuff(h, ps, tempRs);
			t.printStackTrace();
		}
		return ret;
	}
	
	public void execute() throws SQLException {
		_internal_exec(ExecuteType.EXEC);
	}
	public ResultSet executeQuery() throws SQLException {
		return (ResultSet) _internal_exec(ExecuteType.EXEC_QUERY);
	}
	public int executeUpdate() throws SQLException {
		return (Integer) _internal_exec(ExecuteType.EXEC_UPDATE);
	}
	public ResultSet getGeneratedKeys() throws SQLException {
		return (ResultSet) _internal_exec(ExecuteType.GENERATED_KEYS);
	}
	
	public PreparedStatement setInt(int parameterIndex, int x) {
		setList.put(parameterIndex, new SetData(SetType.SET_INT, x));
		return this;
	}
	public PreparedStatement setLong(int parameterIndex, long x) {
		setList.put(parameterIndex, new SetData(SetType.SET_LONG, x));
		return this;
	}
	public PreparedStatement setBoolean(int parameterIndex, boolean x) {
		setList.put(parameterIndex, new SetData(SetType.SET_BOOLEAN, x));
		return this;
	}
	public PreparedStatement setByte(int parameterIndex, byte x) {
		setList.put(parameterIndex, new SetData(SetType.SET_BYTE, x));
		return this;
	}
	public PreparedStatement setString(int parameterIndex, String x) {
		setList.put(parameterIndex, new SetData(SetType.SET_STRING, x));
		return this;
	}
	public PreparedStatement setShort(int parameterIndex, short x) {
		setList.put(parameterIndex, new SetData(SetType.SET_SHORT, x));
		return this;
	}
	public PreparedStatement setObject(int parameterIndex, Object x) {
		setList.put(parameterIndex, new SetData(SetType.SET_OBJECT, x));
		return this;
	}
	public PreparedStatement setDouble(int parameterIndex, double x) {
		setList.put(parameterIndex, new SetData(SetType.SET_DOUBLE, x));
		return this;
	}
}

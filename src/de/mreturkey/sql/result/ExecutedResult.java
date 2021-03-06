package de.mreturkey.sql.result;

import java.sql.ResultSet;
import java.sql.SQLException;

import de.mreturkey.sql.query.Query;
import de.mreturkey.sql.query.QueryType;

public class ExecutedResult implements Result {

	private final ResultSet resultSet;
	private final Query query;
	private final ResultState state;
	private Object[][] values = null;
	
	/**
	 * A instance of data representing a database result, which is generated by executing a statement that queries the database.
	 * @param resultSet - cannot be null
	 * @param query - a null as query means that this Result was executed without using the Query API
	 * @throws SQLException 
	 */
	public ExecutedResult(ResultSet resultSet, Query query) throws SQLException {
		this.resultSet = resultSet;
		this.query = query;
		if(this.isNull()) this.state = ResultState.NO_RESULTS;
		else this.state = ResultState.SUCCESSFUL;
	}

	@Override
	public final boolean isCached() {
		return false;
	}

	@Override
	public QueryType getQueryType() {
		return query.getType();
	}

	@Override
	public ResultSet getResultSet() {
		return resultSet;
	}

	@Override
	public Query getExecutedQuery() {
		return query;
	}

	@Override
	public Object[][] getValues() throws SQLException {
		if(!resultSet.last()) return null;
		if(values != null) return values;
		final int columnCount = resultSet.getMetaData().getColumnCount();
		Object[][] vals = new Object[resultSet.getRow()][columnCount];
		resultSet.beforeFirst();
		
		while(resultSet.next()) {
			for(int i = 0; i < columnCount; i++) {
				vals[(resultSet.getRow() - 1)][i] = resultSet.getObject((i + 1));
			}
		}
		this.values = vals;
		return vals;
	}

	@Override
	public ResultState getState() {
		return state;
	}

	@Override
	public boolean isNull() throws SQLException {
		if(resultSet == null) return true;
		if(resultSet.getType() != ResultSet.TYPE_FORWARD_ONLY) {
			if(resultSet.first()) {
				resultSet.beforeFirst();
				return false;
			} else {
				resultSet.beforeFirst();
				return true;
			}
		}
		return false;
	}
	
}

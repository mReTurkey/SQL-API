package de.mreturkey.sql.query.builder;

import java.sql.SQLException;
import java.sql.SQLTimeoutException;
import java.util.ArrayList;

import de.mreturkey.sql.clausel.OrderBy;
import de.mreturkey.sql.clausel.WhereClause;
import de.mreturkey.sql.operator.LogicalOperator;
import de.mreturkey.sql.provider.Connection;
import de.mreturkey.sql.query.SelectQuery;
import de.mreturkey.sql.result.Result;
import de.mreturkey.sql.util.OrderByEntry;

public class SelectBuilder {

	private String table;
	private ArrayList<String> columns;
	private WhereClause whereClausel;
	private int offset = -1, limit = -1;
	private OrderByEntry orderBy;
	
	protected SelectBuilder() {} //only Builder can access
	
	public String getTable() {
		return table;
	}
	public ArrayList<String> getColumns() {
		return columns;
	}
	public WhereClause getWhereClausel() {
		return whereClausel;
	}
	public int getOffset() {
		return offset;
	}
	public int getLimit() {
		return limit;
	}
	public OrderByEntry getOrderBy() {
		return orderBy;
	}
	
	public SelectBuilder from(String table) {
		this.table = table;
		return this;
	}
	
	public SelectBuilder column(String column) {
		if(columns == null) this.columns = new ArrayList<>();
		this.columns.add(column);
		return this;
	}
	
	public SelectBuilder columns(String... columns) {
		if(this.columns == null) this.columns = new ArrayList<>();
		for(String c : columns) this.columns.add(c);
		return this;
	}
	
	public <V> SelectBuilder where(String column, String operator, V value) {
		if(whereClausel == null) whereClausel = new WhereClause();
		whereClausel.add(column, operator, value, null);
		return this;
	}
	
	public <V> SelectBuilder where(String column, String operator, V value, LogicalOperator logicalOperator) {
		if(whereClausel == null) whereClausel = new WhereClause();
		whereClausel.add(column, operator, value, logicalOperator);
		return this;
	}
	
	public SelectBuilder limit(int limit) {
		this.limit = limit;
		return this;
	}
	
	public SelectBuilder offset(int offset) {
		this.offset = offset;
		return this;
	}
	
	public SelectBuilder orderBy(String column, OrderBy orderBy) {
		this.orderBy = new OrderByEntry(column, orderBy);
		return this;
	}
	
	public Result execute(Connection connection) throws SQLTimeoutException, SQLException {
		final SelectQuery sq = new SelectQuery();
		
		sq.setTable(table);
		sq.setColumns(columns);
		sq.setLimit(limit);
		sq.setOffset(offset);
		sq.setOrderBy(orderBy);
		sq.setWhereClausel(whereClausel);
		
		return connection.select(sq);
	}
}

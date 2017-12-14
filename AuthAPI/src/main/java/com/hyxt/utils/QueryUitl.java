/**
 * 
 */
package com.hyxt.utils;

import org.apache.commons.dbutils.QueryRunner;

import javax.sql.DataSource;

/**
 * @author hadoop
 * 
 */
public class QueryUitl {

	static private DataSource datasource;

	public static void setDataSource(DataSource ds) {
		datasource = ds;
	}

	public static QueryRunner getQueryRunner() {
		return new QueryRunner(datasource);
	}
	

}

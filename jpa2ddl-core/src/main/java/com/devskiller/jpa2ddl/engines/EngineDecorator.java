package com.devskiller.jpa2ddl.engines;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import org.hibernate.dialect.MySQLDialect;
import org.hibernate.dialect.Oracle8iDialect;
import org.hibernate.dialect.PostgreSQL81Dialect;
import org.hibernate.dialect.SQLServerDialect;

public abstract class EngineDecorator {

	public static EngineDecorator getEngineDecorator(String dialect) throws ClassNotFoundException {
		Class<?> dialectClass = Class.forName(dialect);
		if (MySQLDialect.class.isAssignableFrom(dialectClass)) {
			return new MySQLDecorator();
		} else if (PostgreSQL81Dialect.class.isAssignableFrom(dialectClass)) {
			return new PostgreSQLDecorator();
		} else if (Oracle8iDialect.class.isAssignableFrom(dialectClass)) {
			return new OracleDecorator();
		} else if (SQLServerDialect.class.isAssignableFrom(dialectClass)) {
			return new SQLServerDecorator();
		}
		return new NoOpDecorator();

	}

	public String decorateConnectionString(String connectionString) {
		return connectionString;
	}

	public void decorateDatabaseInitialization(Connection connection) throws IOException, SQLException {
	}

}

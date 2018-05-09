package com.devskiller.jpa2ddl.engines;

import java.sql.Connection;
import java.sql.SQLException;

class OracleDecorator extends EngineDecorator {

	private static final String SEQUENCES_VIEW = "CREATE VIEW ALL_SEQUENCES(SEQUENCE_NAME, SEQUENCE_OWNER) AS SELECT SEQUENCE_NAME, '' FROM INFORMATION_SCHEMA.SEQUENCES;";
	private static final String EMPTY_SYNONYMS = "CREATE TABLE ALL_SYNONYMS (SYNONYM_NAME VARCHAR2(30), TABLE_OWNER VARCHAR2(30), TABLE_NAME VARCHAR2(30));";

	@Override
	public String decorateConnectionString(String connectionString) {
		return connectionString + ";MODE=Oracle";
	}

	@Override
	public void decorateDatabaseInitialization(Connection connection) throws SQLException {
		connection.prepareStatement(SEQUENCES_VIEW + "\n" + EMPTY_SYNONYMS).execute();
	}
}

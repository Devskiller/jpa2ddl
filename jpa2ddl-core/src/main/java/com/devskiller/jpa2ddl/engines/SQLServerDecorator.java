package com.devskiller.jpa2ddl.engines;

class SQLServerDecorator extends EngineDecorator {

	@Override
	public String decorateConnectionString(String connectionString) {
		return connectionString + ";MODE=MSSQLServer";
	}
}

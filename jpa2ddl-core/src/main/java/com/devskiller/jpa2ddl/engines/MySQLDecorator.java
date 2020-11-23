package com.devskiller.jpa2ddl.engines;

class MySQLDecorator extends EngineDecorator {

	@Override
	public String decorateConnectionString(String connectionString) {
		return connectionString + ";MODE=MYSQL;DATABASE_TO_UPPER=FALSE";
	}

}

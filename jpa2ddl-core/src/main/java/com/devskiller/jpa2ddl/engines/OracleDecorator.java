package com.devskiller.jpa2ddl.engines;

class OracleDecorator extends EngineDecorator {

	@Override
	public String decorateConnectionString(String connectionString) {
		return connectionString + ";MODE=Oracle";
	}
}

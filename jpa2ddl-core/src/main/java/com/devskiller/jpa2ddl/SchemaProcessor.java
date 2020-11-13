package com.devskiller.jpa2ddl;

import java.sql.Connection;
import java.util.Properties;

public interface SchemaProcessor {

	void postProcess(Connection connection, Properties properties);

}

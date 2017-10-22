package com.devskiller.jpa2ddl;

public enum GenerationMode {

	/**
	 * Generation based on setting up embedded database and dumping the schema
	 */
	DATABASE,

	/**
	 * Generation based on static metadata
	 */
	METADATA
}

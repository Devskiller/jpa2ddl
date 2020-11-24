package com.devskiller.jpa2ddl;

public enum GenerationMode {

	/**
	 * Generation based on setting up embedded database and dumping the schema
	 */
	EMBEDDED_DATABASE,

	/**
	 * Generation based on setting up container database and dumping the schema
	 */
	CONTAINER_DATABASE,

	/**
	 * Generation based on static metadata
	 */
	METADATA
}

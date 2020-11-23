package com.devskiller.jpa2ddl.querydsl;

import com.devskiller.jpa2ddl.SchemaProcessor;
import com.querydsl.sql.codegen.MetaDataExporter;

import java.io.File;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Properties;

public class QueryDslSchemaProcessor implements SchemaProcessor {

	@Override
	public void postProcess(Connection connection, Properties properties) {
		String queryDslOutputPath = properties.getProperty("queryDslOutputPath");
		String queryDslOutputPackage = properties.getProperty("queryDslOutputPackage");

		if (queryDslOutputPath == null || queryDslOutputPath.length() == 0) {
			throw new IllegalArgumentException("queryDslOutputPath must be set");
		}

		try {
			DatabaseMetaData metaData = connection.getMetaData();
			MetaDataExporter metaDataExporter = new MetaDataExporter();
			metaDataExporter.setTargetFolder(new File(queryDslOutputPath));
			if (queryDslOutputPackage != null) {
				metaDataExporter.setPackageName(queryDslOutputPackage);
			}
			metaDataExporter.export(metaData);
		} catch (SQLException e) {
			throw new IllegalStateException(e);
		}
	}

}

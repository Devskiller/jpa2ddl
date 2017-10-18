package com.devskiller.hbm2ddl;

import org.hibernate.tool.hbm2ddl.SchemaExport;

public enum Action {
	CREATE(SchemaExport.Action.CREATE, "create"), DROP(SchemaExport.Action.DROP, "drop"), DROP_AND_CREATE(SchemaExport.Action.BOTH, "drop-and-create");

	private final SchemaExport.Action schemaExportAction;
	private final String schemaGenerationAction;

	Action(SchemaExport.Action schemaExportAction, String schemaGenerationAction) {
		this.schemaExportAction = schemaExportAction;
		this.schemaGenerationAction = schemaGenerationAction;
	}

	public SchemaExport.Action toSchemaExportAction() {
		return schemaExportAction;
	}

	public String toSchemaGenerationAction() {
		return schemaGenerationAction;
	}
}

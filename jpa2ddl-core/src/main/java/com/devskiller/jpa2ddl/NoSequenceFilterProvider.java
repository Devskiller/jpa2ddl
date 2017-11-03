package com.devskiller.jpa2ddl;

import org.hibernate.boot.model.relational.Sequence;
import org.hibernate.mapping.Table;
import org.hibernate.tool.schema.internal.DefaultSchemaFilter;
import org.hibernate.tool.schema.spi.SchemaFilter;
import org.hibernate.tool.schema.spi.SchemaFilterProvider;

public class NoSequenceFilterProvider implements SchemaFilterProvider {

	@Override
	public SchemaFilter getCreateFilter() {
		return NoSequenceSchemaFilter.INSTANCE;
	}

	@Override
	public SchemaFilter getDropFilter() {
		return NoSequenceSchemaFilter.INSTANCE;
	}

	@Override
	public SchemaFilter getMigrateFilter() {
		return NoSequenceSchemaFilter.INSTANCE;
	}

	@Override
	public SchemaFilter getValidateFilter() {
		return NoSequenceSchemaFilter.INSTANCE;
	}

	private static class NoSequenceSchemaFilter extends DefaultSchemaFilter {

		private static final SchemaFilter INSTANCE = new NoSequenceSchemaFilter();

		@Override
		public boolean includeTable(Table table) {
			return !isIdentifierTable(table);
		}

		private boolean isIdentifierTable(Table table) {
			return !table.getInitCommands().isEmpty();
		}

		@Override
		public boolean includeSequence(Sequence sequence) {
			return false;
		}
	}
}

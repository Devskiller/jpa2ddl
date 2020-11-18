package com.devskiller.jpa2ddl.dialects;

import org.hibernate.dialect.PostgreSQL95Dialect;
import org.hibernate.tool.schema.extract.internal.SequenceInformationExtractorH2DatabaseImpl;
import org.hibernate.tool.schema.extract.spi.SequenceInformationExtractor;

public class H2PostgreSQL95Dialect extends PostgreSQL95Dialect {

	@Override
	public SequenceInformationExtractor getSequenceInformationExtractor() {
		return new SequenceInformationExtractorH2DatabaseImpl();
	}
}

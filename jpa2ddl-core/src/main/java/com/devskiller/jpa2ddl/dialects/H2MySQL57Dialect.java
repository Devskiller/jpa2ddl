package com.devskiller.jpa2ddl.dialects;

import org.hibernate.dialect.MySQL57Dialect;
import org.hibernate.tool.schema.extract.internal.SequenceInformationExtractorH2DatabaseImpl;
import org.hibernate.tool.schema.extract.spi.SequenceInformationExtractor;

public class H2MySQL57Dialect extends MySQL57Dialect {

	@Override
	public SequenceInformationExtractor getSequenceInformationExtractor() {
		return new SequenceInformationExtractorH2DatabaseImpl();
	}
}

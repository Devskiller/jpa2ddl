package com.devskiller.jpa2ddl.dialects;

import org.hibernate.dialect.MySQL8Dialect;
import org.hibernate.tool.schema.extract.internal.SequenceInformationExtractorH2DatabaseImpl;
import org.hibernate.tool.schema.extract.spi.SequenceInformationExtractor;

public class H2MySQL8Dialect extends MySQL8Dialect {

	@Override
	public SequenceInformationExtractor getSequenceInformationExtractor() {
		return new SequenceInformationExtractorH2DatabaseImpl();
	}
}

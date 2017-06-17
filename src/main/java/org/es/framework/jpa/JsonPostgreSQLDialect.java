package org.es.framework.jpa;

import java.sql.Types;

import org.hibernate.dialect.PostgreSQL9Dialect;
import org.hibernate.type.descriptor.sql.BinaryTypeDescriptor;
import org.hibernate.type.descriptor.sql.SqlTypeDescriptor;

public class JsonPostgreSQLDialect extends PostgreSQL9Dialect {
	public JsonPostgreSQLDialect() {
		super();
		registerColumnType(Types.JAVA_OBJECT, "jsonb");
		registerColumnType(Types.BLOB, "bytea");
	}

	@Override
	public SqlTypeDescriptor remapSqlTypeDescriptor(SqlTypeDescriptor sqlTypeDescriptor) {
		if (sqlTypeDescriptor.getSqlType() == Types.BLOB) {
			return BinaryTypeDescriptor.INSTANCE;
		}
		return super.remapSqlTypeDescriptor(sqlTypeDescriptor);
	}
}

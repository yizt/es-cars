package org.es.framework.jpa;

import java.io.IOException;
import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.apache.commons.lang3.StringUtils;
import org.es.framework.util.json.JsonUtils;
import org.es.framework.util.json.PojoMapper;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.usertype.UserType;
import org.postgresql.util.PGobject;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JSONObjectUserType implements UserType {

	@Override
	public int[] sqlTypes() {
		return new int[] { Types.JAVA_OBJECT };
	}

	@Override
	public Class returnedClass() {
		return JsonNode.class;
	}

	@Override
	public boolean equals(Object x, Object y) throws HibernateException {
		if (x == null) {
			return y == null;
		}
		return x.equals(y);
	}

	@Override
	public int hashCode(Object x) throws HibernateException {
		return JsonUtils.toJson(x).hashCode();
	}

	@Override
	public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner)
			throws HibernateException, SQLException {
		if (!StringUtils.isEmpty(rs.getString(names[0]))) {
			try {
				ObjectMapper mapper = PojoMapper.getObjectMapper();
				return mapper.readTree(rs.getString(names[0]));
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		return null;

	}

	@Override
	public void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor session)
			throws HibernateException, SQLException {
		PGobject jsonObject = new PGobject();
		jsonObject.setType("jsonb");
		jsonObject.setValue(JsonUtils.toJson(value));
		st.setObject(index, jsonObject);
	}

	@Override
	public Object deepCopy(Object value) throws HibernateException {
		if (value == null)
			return value;
		try {
			ObjectMapper mapper = PojoMapper.getObjectMapper();
			return mapper.readTree(JsonUtils.toJson(value));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public boolean isMutable() {
		return true;
	}

	@Override
	public Serializable disassemble(Object value) throws HibernateException {
		return ((JsonNode) value).asText();
	}

	@Override
	public Object assemble(Serializable cached, Object owner) throws HibernateException {
		return this.deepCopy(cached);
	}

	@Override
	public Object replace(Object original, Object target, Object owner) throws HibernateException {
		return original;
	}

}

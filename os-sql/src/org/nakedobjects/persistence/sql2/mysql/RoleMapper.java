package org.nakedobjects.persistence.sql2.mysql;

import org.nakedobjects.object.NakedClass;
import org.nakedobjects.object.NakedClassManager;
import org.nakedobjects.object.NakedObject;
import org.nakedobjects.object.NakedObjectManager;
import org.nakedobjects.object.ObjectStoreException;
import org.nakedobjects.object.SimpleOid;
import org.nakedobjects.object.UnsupportedFindException;
import org.nakedobjects.security.Role;
import org.nakedobjects.utility.NotImplementedException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import org.apache.log4j.Logger;

public class RoleMapper extends NameBasedMapper {
	private static final String columns = "name, description, id";
	private static final Logger LOG = Logger.getLogger(RoleMapper.class);
	private static final String table = "no_role";

	public void createObject(NakedObject object) throws ObjectStoreException {
		Role user = (Role) object;
		long id = primaryKey(user.getOid());
		db.update("Insert into " + table + " (" + columns + ") values ('"
				+ user.getName().stringValue() + "', '"
				+ user.getDescription().stringValue() + "'," + id + ")");
	}

	protected void createTables() throws ObjectStoreException {
		db
				.update("create table "
						+ table
						+ " (id INTEGER, name VARCHAR(255), description VARCHAR(1024))");
	}

	public Vector getInstances(NakedClass cls) throws ObjectStoreException {
		String statement = "select " + columns + " from " + table;
		return getInstances(statement);
	}

	public Vector getInstances(NakedObject pattern)
			throws ObjectStoreException, UnsupportedFindException {
		LOG.debug("loading user: " + pattern);
		String statement = "select " + columns + " from " + table
				+ " where name = '" + ((Role) pattern).getName().stringValue()
				+ "'";
		return getInstances(statement);
	}

	private Vector getInstances(String statement) throws ObjectStoreException {
		ResultSet rs = db.select(statement);
		NakedObjectManager manager = NakedObjectManager.getInstance();
		Vector instances = new Vector();
		try {
			while (rs.next()) {
				int id = rs.getInt("id");
				SimpleOid oid = new SimpleOid(id);
				LOG.debug("  instance  " + oid);
				Role instance;

				if (loadedObjects.isLoaded(oid)) {
					instance = (Role) loadedObjects.getLoadedObject(oid);
				} else {
					instance = (Role) NakedClassManager.getInstance()
							.getNakedClass(Role.class.getName())
							.acquireInstance();
					instance.setOid(oid);
					instance.getName().setValue(rs.getString("name"));
					instance.getDescription().setValue(
							rs.getString("description"));

					instance.setResolved();
					loadedObjects.loaded(instance);
				}
				instances.addElement(instance);
			}
			return instances;
		} catch (SQLException e) {
			throw new ObjectStoreException(e);
		}
	}

	protected boolean needsTables() throws ObjectStoreException {
		return !db.hasTable(table);
	}

	public void resolve(NakedObject object) throws ObjectStoreException {
		throw new NotImplementedException(object.toString());
	}

	public void save(NakedObject object) throws ObjectStoreException {
		Role user = (Role) object;
		db.update("update " + table + " set name='"
				+ user.getName().stringValue() + "', description='"
				+ user.getDescription().stringValue() + "' where id = "
				+ ((SimpleOid) user.getOid()).getSerialNo());
	}

	protected String table(NakedClass cls) {
		return table;
	}
}

/*
 * Naked Objects - a framework that exposes behaviourally complete business
 * objects directly to the user. Copyright (C) 2000 - 2004 Naked Objects Group
 * Ltd
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 * The authors can be contacted via www.nakedobjects.org (the registered address
 * of Naked Objects Group is Kingsway House, 123 Goldworth Road, Woking GU21
 * 1NR, UK).
 */
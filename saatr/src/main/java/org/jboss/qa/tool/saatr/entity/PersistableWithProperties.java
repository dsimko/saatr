package org.jboss.qa.tool.saatr.entity;

import java.util.Set;

import org.bson.types.ObjectId;
import org.jboss.qa.tool.saatr.entity.Build.PropertyData;

public interface PersistableWithProperties extends Persistable<ObjectId> {

    Set<PropertyData> getProperties();
}

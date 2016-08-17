package org.jboss.qa.tool.saatr.entity;

import java.io.Serializable;
import java.util.Set;

import org.jboss.qa.tool.saatr.entity.Build.PropertyData;

public interface WithProperties extends Serializable {

    Set<PropertyData> getProperties();
}

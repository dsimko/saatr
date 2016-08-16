package org.jboss.qa.tool.saatr.entity;

import java.io.Serializable;
import java.util.List;

import org.jboss.qa.tool.saatr.entity.Build.PropertyData;

public interface WithProperties extends Serializable {

    List<PropertyData> getProperties();
}

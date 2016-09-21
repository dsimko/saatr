
package org.jboss.qa.tool.saatr.domain;

import java.io.Serializable;
import java.util.Set;

import org.jboss.qa.tool.saatr.domain.build.BuildDocument.PropertyData;

public interface DocumentWithProperties<T extends Serializable> extends Serializable {

    T getId();

    Set<PropertyData> getProperties();
}

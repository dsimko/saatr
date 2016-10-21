
package org.jboss.qa.tool.saatr.domain;

import java.io.Serializable;
import java.util.Set;

import org.jboss.qa.tool.saatr.domain.build.BuildProperty;


public interface DocumentWithProperties<T extends Serializable> extends Serializable {

    T getId();

    Set<BuildProperty> getProperties();
}

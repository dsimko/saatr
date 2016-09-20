package org.jboss.qa.tool.saatr.domain;

import java.io.Serializable;

public interface DocumentWithID<ID extends Serializable> extends Serializable {

	ID getId();

	void setId(ID id);
}

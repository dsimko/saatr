package org.jboss.qa.tool.saatr.entity;

import java.io.Serializable;

public interface Persistable<ID extends Serializable> extends Serializable {

	ID getId();

	void setId(ID id);
}

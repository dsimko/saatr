
package org.jboss.qa.tool.saatr.repo.config;

import java.util.Iterator;

import org.jboss.qa.tool.saatr.domain.DocumentWithProperties;
import org.jboss.qa.tool.saatr.domain.config.ConfigDocument;
import org.jboss.qa.tool.saatr.web.comp.config.ConfigProvider.ConfigFilter;

/**
 * The interface for repository functionality that will be implemented manually.
 * 
 * @author dsimko@redhat.com
 */
interface ConfigRepositoryCustom {

    void prefillValues(ConfigDocument config, DocumentWithProperties<?> persistable);

    Iterator<ConfigDocument> query(long first, long count, ConfigFilter filter);

    long count(ConfigFilter filter);
}

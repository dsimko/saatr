
package org.jboss.qa.tool.saatr.repo.config;

import java.util.List;

/**
 * The interface for repository functionality that will be implemented manually.
 * 
 * @author dsimko@redhat.com
 */

public interface QueryRepositoryCustom {

    List<String> findDistinctCategories();

}

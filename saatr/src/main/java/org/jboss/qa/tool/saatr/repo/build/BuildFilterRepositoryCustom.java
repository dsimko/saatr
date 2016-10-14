
package org.jboss.qa.tool.saatr.repo.build;

import org.jboss.qa.tool.saatr.domain.build.BuildFilter;

/**
 * The interface for repository functionality that will be implemented manually.
 * 
 * @author dsimko@redhat.com
 */
interface BuildFilterRepositoryCustom {

    BuildFilter findAndUpdateLastUsed(String id);

}
